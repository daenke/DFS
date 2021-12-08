package GUI;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

/*
 * File: CloseableFrame.java
 * Created for Projekt: DaniBibo
 * Created on 07.11.2007 by Daniel Enke
 */

/**
 * Dies ist eine Klasse, die weiß, wie man auf WindowClosing-Events reagiert.
 * Sie ist da zur Codevereinfachung der geschriebenen JFrame-Klasse
 * @author Daniel Enke
 */
public class CloseableJFrame extends JFrame implements WindowListener {

	/**
	 * @throws java.awt.HeadlessException
	 */
	public CloseableJFrame() throws HeadlessException {
		super();
		this.addWindowListener(this);
	}

	/**
	 * @param gc
	 */
	public CloseableJFrame(GraphicsConfiguration gc) {
		super(gc);
		this.addWindowListener(this);
	}

	/**
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public CloseableJFrame(String title) throws HeadlessException {
		super(title);
		this.addWindowListener(this);
	}

	/**
	 * @param title
	 * @param gc
	 */
	public CloseableJFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		this.addWindowListener(this);
	}

	public void windowClosing(WindowEvent e) { this.dispose(); }
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}

}
