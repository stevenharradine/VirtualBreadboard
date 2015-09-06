import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class DialogBox extends JFrame implements ActionListener {
	/*
	 * create a user specified dialog box
	 */
	String message = "";
	String title = "";
	JFrame mainFrame;
	String type = "alert";

	public DialogBox (String newTitle, String newMessage) {
		this.message = newMessage;
		this.title = newTitle;
	}
	public DialogBox (String newTitle, String newMessage, String newType) {
		/*
		 * overloaded method if the programer needs another type of window other
		 * that the standard alert window
		 */
		this.message = newMessage;
		this.title = newTitle;
		this.type = newType;
	}
	public void show() {
		/*
		 * shows alert window
		 */
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();	// gets the screens size
		mainFrame = new JFrame(title);
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.getContentPane().setLayout(null);
		mainFrame.setSize(400, 175);
		mainFrame.setLocation ((int)Math.round(screenSize.getWidth() / 2) - (int)Math.round (mainFrame.getSize().getWidth() / 2),
							(int)Math.round(screenSize.getHeight() / 2) - (int)Math.round (mainFrame.getSize().getHeight() / 2));
		mainFrame.setResizable (false);

		ImageIcon warning = new ImageIcon("images\\" + type +".png");
		JLabel picLabel = new JLabel ();
		picLabel.setIcon(warning);
		mainFrame.getContentPane().add(picLabel);
		picLabel.setBounds (25, 30, 50, 50);


		JLabel messageLabel = new JLabel("<html>" + message + "</html>");	// the html allows word wraping
		JButton acceptButton = new JButton ("OK");
		acceptButton.addActionListener(this);

		mainFrame.getContentPane().add (messageLabel);
		messageLabel.setBounds(100, 5, mainFrame.getSize().width - 110, mainFrame.getSize().height - 70);

		mainFrame.getContentPane().add(acceptButton);
		acceptButton.setBounds ((int)Math.round(mainFrame.getWidth() / 2) - 35, mainFrame.getSize().height - 60, 70, 22);

		mainFrame.show();
	}
	public void actionPerformed (ActionEvent e) {
		if (e.getActionCommand().equals("OK"))
			mainFrame.dispose();
	}
}