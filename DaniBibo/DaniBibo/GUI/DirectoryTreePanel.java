/*
 * Created on 20.05.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package GUI;

import javax.swing.*;
import javax.swing.tree.*;

import java.awt.*;
import java.io.*;

/** 
 * @author heida
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DirectoryTreePanel extends JPanel {
	
	public DirectoryTreePanel () {
		this ("/home/heida9/testIt/"); // TODO test ersma
		// this (System.getProperty("user.home"));
	}
	
	public DirectoryTreePanel (String directory) {
		// in eine Hierarchie organisierte Daten
		TreeNode root = maketree (new File (directory));
		
		// visualisieren
		JTree tree = new JTree (new DefaultTreeModel (root));
		JScrollPane scroll = new JScrollPane ();
		scroll.getViewport().add(tree);
		setLayout( new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		
		
	}

	/**
	 * @param file
	 * @return
	 */
	private DefaultMutableTreeNode maketree(File f) {
		DefaultMutableTreeNode t = new DefaultMutableTreeNode (f.getName());
		System.out.println("f.hashCode()= " + f.hashCode());
		if (f.canRead()) System.out.println("Verzeichnis lesbar");
		else System.out.println("Verzeichnis NICHT lesbar");

		if (f.isDirectory()) {
			String[] list = f.list();
			for (int i=0; i < list.length; i++) {
				t.add(maketree (new File (f, list[i])));
			} 
		}
		return t;
	}

}
