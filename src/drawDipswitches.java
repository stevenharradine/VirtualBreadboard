import java.awt.*;
import javax.swing.*;

class drawDipswitches extends JPanel {
	/*
	 * draws the dipswitchs and controls their on/off positions
	 */
	public void paintComponent (Graphics g){
		int[][] dipswitchPinArray = VirtualBreadboard.dipswitchPinArray;

		// dipswiches box
		g.setColor(Color.RED);
		g.fillRect(0, 0, 85, 25);

		// white rail for dipswitches
		g.setColor(Color.WHITE);
		for (int i = 0; i < 8; i++)
			g.fillRect(6 + (i * 10), 4, 4, 15);

		// dipswitches
		g.setColor(Color.BLACK);
		for (int i = 0; i < 8; i++)
			g.drawRect(dipswitchPinArray[0][i], dipswitchPinArray[1][i], 3, 3);

		// create white background for text
		g.setColor(Color.WHITE);
		g.fillRect(0, 25, 85, 40);

		// dipswiches's lables
		g.setColor(Color.BLACK);
		for (int i = 0; i < 8; i++)
			g.drawString(String.valueOf(i+1), 5 + (i * 10), 40);
	}
}