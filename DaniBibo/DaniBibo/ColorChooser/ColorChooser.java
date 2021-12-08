package ColorChooser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ColorChooser extends JPanel implements ActionListener
{
  JPanel   colorPanel, panel;
  JButton  chooserButton;
  JColorChooser colorChooser;
  JTextField  redField, greenField, blueField;  // Farbwerte
  
  ColorChooser(JApplet applet)
  {
    colorPanel = new JPanel();
    colorChooser = new JColorChooser();
    setLayout(new GridLayout(2, 1, 10, 10));
    colorPanel.setLayout(new BorderLayout(10, 5));
        
    add(chooserButton = new JButton("Öffne ColorChooser"));
    chooserButton.addActionListener(this);
    add(colorPanel);
    colorPanel.add("West", panel = new JPanel());
    panel.setLayout(new GridLayout(0, 1, 10, 5));
    panel.add(new JLabel("Rot:"));
    panel.add(new JLabel("Grün:"));
    panel.add(new JLabel("Blau:"));
    colorPanel.add("Center", panel = new JPanel());
    panel.setLayout(new GridLayout(0, 1, 0, 5));
    panel.add(redField = new JTextField("0"));
    panel.add(greenField = new JTextField("0"));
    panel.add(blueField = new JTextField("0"));
  }
  
  public void actionPerformed(ActionEvent e)
  {
    Color  c;
    int   r, g, b;

    r = Integer.parseInt(redField.getText());  
    g = Integer.parseInt(greenField.getText());  
    b = Integer.parseInt(blueField.getText());  
    c = JColorChooser.showDialog(this, "Ein ColorChooser-Beispiel",
      new Color(r, g, b));
    if (c!=null)
    {
      redField.setText(Integer.toString(c.getRed()));
      greenField.setText(Integer.toString(c.getGreen()));
      blueField.setText(Integer.toString(c.getBlue()));
    }
  }
}