import java.awt.*;
import javax.swing.*;

class drawPower extends JPanel {
	/*
	 * draws the power label and led
	 */
	static boolean power = false;
	public void togglePower() {
		if (power == false)
			power = true;
		else
			power = false;
	}
	public boolean getPower(){
		return power;
	}
	public void paintComponent (Graphics g) {
		// Power LED + Label
		g.setColor(Color.BLACK);
		g.drawString ("POWER", 0, 10);
		g.setColor(Color.GREEN);
		if (power == false)
			g.drawOval(15, 15, 10, 10);
		else
			g.fillOval(15, 15, 10, 10);
	}
}