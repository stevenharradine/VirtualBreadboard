/*
 *      author:  Steven J. Harradine
 *
 *    filename:  QuickieForms.java
 *
 *     started:  20050313
 *    finished:  2005xxxx
 *
 * description:  This object will allow the programer to use short cuts when
 *               creating forms.
 *
 */
 
 import javax.swing.*;
 import java.awt.*;
 import java.awt.event.*;
 
 class QuickieForms {
 	
 	/*
     *     started:  20050313
     *    finished:  20050313
     *
     * description:  This allows you to create a button with a built in
     *               ActionListener.
 	 */
 	public JButton makeButtonWithAL (String text, ActionListener type) {
 		JButton button = new JButton(text);
		button.addActionListener(type);
		
		return button;
 	}
 	
 	/*
	* used to simplify the creation of menu items
	*/
 	public JMenuItem createMenuItem (String text, char mnemonic, ActionListener type) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.setMnemonic(mnemonic);
		menuItem.addActionListener(type);

		return menuItem;
	}
	public JMenuItem createMenuItem (String text, ActionListener type) {
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(type);

		return menuItem;
	}
	public JMenu createMenu (String text, char mnemonic) {
		JMenu menu = new JMenu(text);
		menu.setMnemonic(mnemonic);

		return menu;
	}
	
	/*
     *     started:  20050517
     *    finished:  20050517
     *
     * description:  This allows you to create JLabels and add them to a
     *               container.
	 */
	 public void createJLabel (String text, JPanel panel, int x1, int y1, int x2, int y2) {
	 	JLabel label = new JLabel (text);
	 	panel.add (label);
	 	label.setBounds (x1, y1, x2, y2);
	 }
	 public void createJLabel (String text, JPanel panel) {
	 	JLabel label = new JLabel (text);
	 	panel.add (label);
	 }
	 
	 /*
     *     started:  20050517
     *    finished:  20050519
     *
     * description:  This allows you to create JTextFields and add them to a
     *               container.
	 */
	 public JTextField createJTextField (String text, JPanel panel) {
	 	JTextField field = new JTextField (text);
	 	panel.add (field);
	 	
	 	return field;
	 }
 }