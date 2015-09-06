import java.awt.*;
import javax.swing.*;

class powerSwitch extends JPanel {
	/*
	 * draws the power switch
	 */
	public void paintComponent (Graphics g) {
		int powerSwitchPosition = VirtualBreadboard.powerSwitchPosition;
		g.setColor(Color.RED);
		g.fillRect(0, 0, 15, 30);

		g.setColor(Color.WHITE);
		g.fillRect(5, 4, 5, 22);

		g.setColor(Color.BLACK);
		g.drawRect(5, powerSwitchPosition, 4, 4);
	}
}