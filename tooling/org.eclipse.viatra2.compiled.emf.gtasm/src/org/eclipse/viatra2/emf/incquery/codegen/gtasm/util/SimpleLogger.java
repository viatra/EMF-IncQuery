/*
 * Created on 2005.07.17.
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.viatra2.emf.incquery.codegen.gtasm.util;


import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Akoz
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimpleLogger  {
	static Logger cat = Logger.getAnonymousLogger();
	/* (non-Javadoc)
	 * @see alap.Logger4j#setLevel(int)
	 */
	public void setLevel(int l) {
		// TODO Auto-generated method stub
		
//		
//		switch(l)
//		{
//		case FATAL : 	cat.setPriority(Priority.FATAL); break;
//		case ERROR:		cat.setPriority(Priority.ERROR); break;
//		case WARNING:	cat.setPriority(Priority.WARN); break;
//		case INFO:		cat.setPriority(Priority.INFO); break;
//		case DEBUG:		cat.setPriority(Priority.DEBUG); break;
//		
//		}
//		
//		
	}

	/* (non-Javadoc)
	 * @see alap.Logger4j#debug(java.lang.String)
	 */
	public void debug(String s) {
		// TODO Auto-generated method stub
//		cat. .debug(s);
	}

	/* (non-Javadoc)
	 * @see alap.Logger4j#warning(java.lang.String)
	 */
	public void warning(String s) {
		// TODO Auto-generated method stub
	//	cat.warn(s);
	}

	/* (non-Javadoc)
	 * @see alap.Logger4j#error(java.lang.String)
	 */
	public void error(String s) {
		// TODO Auto-generated method stub
//		cat.error(s);
	}

	/* (non-Javadoc)
	 * @see alap.Logger4j#fatal(java.lang.String)
	 */
	public void fatal(String s) {
		// TODO Auto-generated method stub
	//	cat.fatal(s);
	}

	/* (non-Javadoc)
	 * @see alap.Logger4j#info(java.lang.String)
	 */
	public void info(String s) {
		// TODO Auto-generated method stub
		cat.info(s);
	}

	/* (non-Javadoc)
	 * @see alap.Logger4j#init(java.util.Properties)
	 */
	public void init(Properties p) {
		// TODO Auto-generated method stub
	//	DOMConfigurator.configure("log4jconfig.xml"); 
	}

}
