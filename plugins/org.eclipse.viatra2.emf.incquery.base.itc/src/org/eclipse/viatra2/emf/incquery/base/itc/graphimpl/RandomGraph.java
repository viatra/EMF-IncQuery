package org.eclipse.viatra2.emf.incquery.base.itc.graphimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomGraph extends Graph<Integer> {

	private static final long serialVersionUID = -8888373780155316788L;
	private List<Integer> numbers = null;
	private int K;
	private int nodeCount;
	
	public RandomGraph(int nodeCount, int K) {
		this.nodeCount = nodeCount;
		this.K = K;
		
	}
	
	public void buildGraph() {
		numbers = new ArrayList<Integer>();
		int eInserted = 0;
		
		for (int i = 0;i< nodeCount;i++) {
			numbers.add(i);
			this.insertNode(i);
		}
		
		for (int i = 0;i< nodeCount;i++) {
			numbers.remove(i);
			
			Collections.shuffle(numbers);
			
			for (int j=0;j<K;j++) {
				this.insertEdge(i, numbers.get(j));
				eInserted++;
			}
			
			numbers.add(i);
		}
		
		//System.out.println("Edges inserted: "+eInserted);
	}

	@Override
	public Integer[] deleteRandomEdge() {
		Integer[] r = new Integer[2];
		Collections.shuffle(numbers);
		r[0] = numbers.get(0);
		for (int delT : this.getTargetNodes(r[0])) {
			r[1] = delT;
			break;
		}
		return r;
	}
	
	@Override
	public Integer[] insertRandomEdge() {
		Integer[] r = new Integer[2];
		Collections.shuffle(numbers);
		r[0] = numbers.get(0);
		r[1] = numbers.get(1);
		return r;
	}
	
}
