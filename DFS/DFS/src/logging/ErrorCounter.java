/*
 * Created on 25.05.2008
 * History for Danis FileSync:
 * 0.1: erste Version
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package logging;

/**
 * @author heida
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ErrorCounter {

	private int errors;


	/**
	 * 
	 */
	public ErrorCounter() {
		errors = 0;
	}

	public void increment() {
		errors++;
	}
	
	public int get() {
		return errors;
	}

}
