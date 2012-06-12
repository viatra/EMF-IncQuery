/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.base.itc.alg.kingopt;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcObserver;

public class TcRelation<V> {

	//TODO DEBUG miatt public
	public HashMap<V, HashMap<V, CountList>> tuplesForward = null;
	private HashMap<V, HashMap<V, CountList>> tuplesBackward = null;
	
	/**
	 * Maps level numbers to tuples on which those are starred.
	 */
	private HashMap<Integer, HashMap<V, HashSet<V>>> starredAtLevel = null;
	
	private ArrayList<ITcObserver<V>> observers;

	//private ArrayList<StarWork<V>> starWorkQueue;
	private TreeSet<StarWork<V>> starWorkQueue;
	
	private int levelCount;
	
	public TcRelation(int levelCount, ArrayList<ITcObserver<V>> observers) {
		//starWorkQueue = new ArrayList<StarWork<V>>();
		starWorkQueue = new TreeSet<StarWork<V>>();
		tuplesForward = new HashMap<V, HashMap<V, CountList>>();
		tuplesBackward = new HashMap<V, HashMap<V, CountList>>();
		starredAtLevel = new HashMap<Integer, HashMap<V, HashSet<V>>>();
		this.levelCount = levelCount;
		this.observers = observers;
	}
	
	//TODO DEBUG
	public void clearTc() {
		tuplesForward = new HashMap<V, HashMap<V,CountList>>();
		tuplesBackward = new HashMap<V, HashMap<V,CountList>>();
		starredAtLevel = new HashMap<Integer, HashMap<V,HashSet<V>>>();
		starWorkQueue = new TreeSet<StarWork<V>>();
		//starWorkQueue = new ArrayList<StarWork<V>>();
	}
	
	public void setLevelCount(int levelCount) {
		this.levelCount = levelCount;
	}
		
	/**
	 * This method derives the tc relation when an edge is inserted or deleted.
	 * 
	 * @param source the source of the node
	 * @param target the target of the node
	 * @param dir +1 when edge is inserted and -1 when edge is deleted
	 */
	public void deriveBaseTuple(V source, V target, int dir) {
		if (dir == 1) this.addTuple(source, target, 0);
		if (dir == -1) this.removeTuple(source, target, 0);
		doQueue();
	}
	
	public void clearQueue() {
		this.starWorkQueue.clear();
		//this.edgeWorkQueue.clear();
	}
		
	public void doQueue() {
		//System.out.println(starWorkQueue.size());
		
		while (!starWorkQueue.isEmpty()) {	
			//System.out.println(starWorkQueue.size());
			StarWork<V> sw = starWorkQueue.pollFirst();
			handleStars(sw.getSd(), sw.getSource(), sw.getTarget(), sw.getFromIdx(), sw.getToIdx());
		}
	}
	
	private void notifyObservers(V source, V target, int dir) {
		for (ITcObserver<V> to : observers) {
			if (dir == 1)
				to.tupleInserted(source, target);
			if (dir == -1)
				to.tupleDeleted(source, target);
		}
	}

	/**
	 * This method derives the tc relation when an edge is inserted.
	 * 
	 * @param source
	 * @param target
	 * @param levelNumber
	 */
	public void addTuple(V source, V target, int levelNumber) {
		if (!source.equals(target) && (levelNumber <= levelCount)) {
			notifyObservers(source, target, 1);
			
			HashMap<V, CountList> sMap = tuplesForward.get(source);
			HashMap<V, CountList> tMap = tuplesBackward.get(target);
			
			StarDir sd = StarDir.NONE;
			int fromIdx = -1, toIdx = -1;
			
			if (tMap != null) {
				if (tMap.get(source) == null) {
					CountList arrayTmp = new CountList();
					arrayTmp.addCount(levelNumber);
					tMap.put(source, arrayTmp);	
				}
				else {
					CountList arrayTmp = tMap.get(source);
					arrayTmp.addCount(levelNumber);
				}
			}
			else {
				HashMap<V, CountList> mapTmp = new HashMap<V, CountList>();
				CountList arrayTmp = new CountList();
				arrayTmp.addCount(levelNumber);
				mapTmp.put(source, arrayTmp);
				tuplesBackward.put(target, mapTmp);
			}
			
			if (sMap != null) {
				if (sMap.get(target) == null) {
					CountList arrayTmp = new CountList();
					arrayTmp.addCount(levelNumber);
					sMap.put(target, arrayTmp);

					toIdx = levelNumber;
					sd = StarDir.HOP_DOWN_INF;
				}
				else {
					CountList arrayTmp = sMap.get(target);
					int i = arrayTmp.getStarredLevel();
					arrayTmp.addCount(levelNumber);
					int j = arrayTmp.getStarredLevel();
					
					if (j < i) {
						fromIdx = i;
						toIdx = j;
						sd = StarDir.HOP_DOWN;
					}
				}
			}
			else {
				HashMap<V, CountList> mapTmp = new HashMap<V, CountList>();
				CountList arrayTmp = new CountList();
				arrayTmp.addCount(levelNumber);
				mapTmp.put(target, arrayTmp);
				tuplesForward.put(source, mapTmp);

				toIdx = levelNumber;
				sd = StarDir.HOP_DOWN_INF;
			}
			
			if (sd == StarDir.HOP_DOWN || sd == StarDir.HOP_DOWN_INF) {
				maintainStarredLevels(fromIdx, toIdx, source, target);
			}
			
			//System.out.println("insert "+source+" "+target+" "+sd+" fromIdx: "+fromIdx+" toIdx: "+toIdx);
			starWorkQueue.add(new StarWork<V>(sd, source, target, fromIdx, toIdx));
			//handleStars(sd, source, target, fromIdx, toIdx);
		}
	}
	
	/**
	 * This method dervies the tc relation when an edge is deleted.
	 * 
	 * @param source
	 * @param target
	 * @param levelNumber
	 */
	public void removeTuple(V source, V target, int levelNumber) {
		if (!source.equals(target)) {
			
			notifyObservers(source, target, -1);
			
			HashMap<V, CountList> sMap = tuplesForward.get(source);
			HashMap<V, CountList> tMap = tuplesBackward.get(target);
			
			StarDir sd = StarDir.NONE;
			int fromIdx = -1, toIdx = -1;
			
			if (tMap != null) {
				if (tMap.get(source) != null) {
					CountList arrayTmp = tMap.get(source);
					
					if (arrayTmp.deleteCount(levelNumber)) {
						tMap.remove(source);
						
						if (tMap.size() == 0) {
							tuplesBackward.remove(target);
						}
					}
				}
			}
			
			if (sMap != null) {
				if (sMap.get(target) != null) {
					CountList  arrayTmp = sMap.get(target);
					
					int i = arrayTmp.getStarredLevel();
					if (arrayTmp.deleteCount(levelNumber)) {
						sMap.remove(target);
						
						if (sMap.size() == 0) {
							tuplesForward.remove(source);
						}
						
						fromIdx = i;
						sd = StarDir.HOP_UP_INF;
					}
					else {
						int j = arrayTmp.getStarredLevel();
						if (i < j) {
							fromIdx = i;
							toIdx = j;
							sd = StarDir.HOP_UP;
						}
					}
				}
			}
			
			if (sd == StarDir.HOP_UP || sd == StarDir.HOP_UP_INF) {
				maintainStarredLevels(fromIdx, toIdx, source, target);
			}
			
			//System.out.println("delete "+source+" "+target+" "+sd+" fromIdx: "+fromIdx+" toIdx: "+toIdx);
			starWorkQueue.add(new StarWork<V>(sd, source, target, fromIdx, toIdx));
			//handleStars(sd, source, target, fromIdx, toIdx);
		}
	}

	/**
	 * HOP DOWN : toIdx is smaller than fromIdx and toIdx will be starred
	 * HOP UP : fromIdx is smaller than toIdx and toIdx will be starred
	 * 
	 * @param fromIdx
	 * @param toIdx
	 * @param source
	 * @param target
	 */
	private void maintainStarredLevels(int fromIdx, int toIdx, V source, V target) {
		
		//remove star from fromIdx level
		if (starredAtLevel.get(fromIdx) != null) {
			if (starredAtLevel.get(fromIdx).get(source) != null) {
				starredAtLevel.get(fromIdx).get(source).remove(target);
				if (starredAtLevel.get(fromIdx).get(source).size() == 0)
					starredAtLevel.get(fromIdx).remove(source);
			}
		}
		
		//put start to toIdx level
		if (starredAtLevel.get(toIdx) == null) {
			HashMap<V, HashSet<V>> eSet = new HashMap<V, HashSet<V>>();
			starredAtLevel.put(toIdx, eSet);
		}
		
		if (starredAtLevel.get(toIdx).get(source) == null) {
			//needs to create new set for targets
			HashSet<V> tSet = new HashSet<V>();
			tSet.add(target);
			starredAtLevel.get(toIdx).put(source, tSet);
		}
		else {
			starredAtLevel.get(toIdx).get(source).add(target);
		}
	}
	
	/**
	 * This method handles the movement of the stars on the tuples and computes the new tuples that need to be modified.
	 * 
	 * @param sd the direction of the star movement
	 * @param source the source of the tuple
	 * @param target the target of the tuple
	 * @param fromIdx the index of the level from which the star has been moved down or up
	 * @param toIdx the index of the level to which the star has been moved up or down
	 */
	private void handleStars(StarDir sd, V source, V target, int fromIdx, int toIdx) {
		
		HashMap<V, CountList> sMap = tuplesBackward.get(source);
		HashMap<V, CountList> tMap = tuplesForward.get(target);
		CountList arrayTmp = null;
		int starredLevel = -1;
		
		if (sd == StarDir.HOP_DOWN || sd == StarDir.HOP_DOWN_INF) {
			
			//in this case, toIdx < fromIdx
			
			if (tMap != null) {
				
				//tMap's keyset contains those nodes which are in the form of (target, b) 
				for (V t : tMap.keySet()) {
					
					arrayTmp = tuplesForward.get(target).get(t); 
					starredLevel = arrayTmp.getStarredLevel();
					
					//(a,b) X-> (b,c) | k is an element of [0,toIdx] : (a,c,toIdx+1) +1
					if ((starredLevel >= 0) && (starredLevel <= toIdx)) {
						//System.out.println("addTuple: ["+source+"] -> "+target+" -> ["+t+"] level: "+(toIdx+1));
						//edgeWorkQueue.add(new EdgeWork<V>(1, source, t, toIdx+1));
						this.addTuple(source, t, toIdx+1);
					}
					
					//(a,b) X-> (b,c) | k is an element of [0,fromIdx] : (a,c,fromIdx+1) -1
					if ((sd == StarDir.HOP_DOWN) && (starredLevel >= 0) && (starredLevel <= fromIdx)) {
						//System.out.println("removeTuple: ["+source+"] -> "+target+" -> ["+t+"] level: "+(fromIdx+1));
						//edgeWorkQueue.add(new EdgeWork<V>(-1, source, t, fromIdx+1));
						this.removeTuple(source, t, fromIdx+1);
					}
				}
			}
			
			//(c,a) | k is an element of [toIdx, fromIdx-1] X-> (a,b) : (c,b,k+1) +1 count
			if (sMap != null) {
				for (V s : sMap.keySet()) {
					arrayTmp = tuplesForward.get(s).get(source);
					starredLevel = arrayTmp.getStarredLevel();
					
					if (starredLevel >= toIdx) {
						//System.out.println("addTuple: ["+s+"] -> "+source+" -> ["+target+"] level: "+(starredLevel+1));
						//edgeWorkQueue.add(new EdgeWork<V>(1, s, target, starredLevel+1));
						this.addTuple(s, target, starredLevel+1);
					}
				}
			}
		}
		
		if (sd == StarDir.HOP_UP || sd == StarDir.HOP_UP_INF) {
			
			//in this case toIdx > fromIdx
			if (tMap != null) {
				for (V t : tMap.keySet()) {
					
					arrayTmp = tuplesForward.get(target).get(t); 
					starredLevel = arrayTmp.getStarredLevel();
					
					//(a,b) X-> (b,c) | k is an element of [0,toIdx] (a,c,toIdx+1) +1
					if ((sd == StarDir.HOP_UP) && (starredLevel >= 0) && (starredLevel <= toIdx)) {
						//System.out.println("addTuple: ["+source+"] -> "+target+" -> ["+t+"] level: "+(toIdx+1));
						//edgeWorkQueue.add(new EdgeWork<V>(1, source, t, toIdx+1));
						this.addTuple(source, t, toIdx+1);
					}
					
					//(a,b) X-> (b,c) | k is an element of [0,fromIdx] (a,c,fromIdx+1) -1
					if ((starredLevel >= 0) && (starredLevel <= fromIdx)) {
						//System.out.println("removeTuple: ["+source+"] -> "+target+" -> ["+t+"] level: "+(fromIdx+1));
						//edgeWorkQueue.add(new EdgeWork<V>(-1, source, t, fromIdx+1));
						this.removeTuple(source, t, fromIdx+1);
					}
				}
			}
			
			//(c,a) | k is an element of [fromIdx, toIdx-1] X-> (a,b) (c,b,k+1) -1
			if (sMap != null) {
				for (V s : sMap.keySet()) {
					arrayTmp = tuplesForward.get(s).get(source);
					starredLevel = arrayTmp.getStarredLevel();
					
					if (starredLevel >= fromIdx) {
						//System.out.println("removeTuple: ["+s+"] -> "+source+" -> ["+target+"] level: "+(starredLevel+1));
						//edgeWorkQueue.add(new EdgeWork<V>(-1, s, target, starredLevel+1));
						this.removeTuple(s, target, starredLevel+1);
					}
				}
			}
		}
	}

	@Override
	public String toString() {
	
		String s = "TcRelation = ";

		for (V source : this.tuplesForward.keySet()) {
			for (V target : this.tuplesForward.get(source).keySet()) {
				s += "{(" + source + "," + target + "),"
						+ this.tuplesForward.get(source).get(target).toString() + "} ";
			}
		}
		return s;
	}
	
	public HashMap<V, HashSet<V>> getStarredAtLevel(int levelNumber) {
		return starredAtLevel.get(levelNumber);
	}
	
	public Set<V> getTupleStarts() {
		Set<V> t = tuplesForward.keySet();
		return (t == null) ? new HashSet<V>() : new HashSet<V>(t);
	}
	
	public Set<V> getTupleStarts(V target) {
		if (tuplesBackward.containsKey(target))
			return tuplesBackward.get(target).keySet();
		return null;
	}
	
	public Set<V> getTupleEnds(V source) {
		if (tuplesForward.containsKey(source))
			return tuplesForward.get(source).keySet();
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.TcRelation) {
		
			org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.TcRelation<V> aTR = (org.eclipse.viatra2.emf.incquery.base.itc.alg.dred.TcRelation<V>) obj;
			
			for (V source : aTR.getTuplesForward().keySet()) {
				for (V target : aTR.getTuplesForward().get(source)) {
					if (!this.containsTuple(source, target)) return false;
				}
			}
			
			for (V source : this.tuplesForward.keySet()) {
				for (V target : this.tuplesForward.get(source).keySet()) {
					if (!aTR.containsTuple(source, target)) return false;
				}
			}
			
			return true;
		}
		return false;
	}

	public boolean containsTuple(V source, V target) {
		if (tuplesForward.containsKey(source))
			if (tuplesForward.get(source).containsKey(target))
				return true;
		
		return false;
	}
}
	
