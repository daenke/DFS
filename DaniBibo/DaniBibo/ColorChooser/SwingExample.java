package ColorChooser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class SwingExample extends JApplet
{
  public void init()
  {
    getContentPane().add(new ColorChooser(this));
  }
  
  // Erstellt ein Panel mit Rahmen
  public static void main(String[] args)
  {
    Frame frame = new Frame("SwingExample");
    
    WindowListener l = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {System.exit(0);}
    };
    frame.addWindowListener(l);
    
    SwingExample applet = new SwingExample();
    frame.add("Center", applet);
    applet.init();
    frame.pack();
    frame.show();
  }      
}