package GUI;

import javax.swing.*;
import java.awt.Component;
/*
 * Created on 24.02.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Util {

	/** Open Directory Dialog
	 * @return true, if APPLY was pressed, CANCEL results in false
	 */
	public static boolean chooseFolder(StringBuffer output, String input, Component parent, 
			boolean saveWindow) {
		// folder chooser
		JFileChooser fc;

		if ( (input == null) || (input == "") ) input=System.getProperty("user.home"); // doppelt in DE_Filesync
		fc = new JFileChooser(input); // TODO hier kommt exception bei win2k??, warum nicht in Kaba??
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int status;
		if (saveWindow) status = fc.showSaveDialog(parent);
		else  status = fc.showOpenDialog(parent);
		
		if (status == JFileChooser.APPROVE_OPTION) {
			output.replace(0, 0, fc.getSelectedFile().getAbsolutePath());
			return true;
		} else { 
			return false;
		}
	}

	


}
