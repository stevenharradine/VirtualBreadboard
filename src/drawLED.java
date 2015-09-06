import java.awt.*;
import javax.swing.*;

class drawLED extends JPanel {
	/*
	 * draws led of any colour
	 */
	boolean power = false;	// does the LED have power
	boolean show = true;	// should the LED be show as having power (for inputs)
	Color colour;

	public void togglePower() {
		if (power == false)
			power = true;
		else
			power = false;
	}
	public void setPowerHigh() {
		power = true;
	}
	public void setPowerLow() {
		power = false;
	}
	public void setColour(Color ledColour) {
		colour = ledColour;
	}
	public void setShow (boolean showable) {
		show = showable;
	}
	public void paintComponent (Graphics g) {
		g.setColor(colour);
		if (power == false || show == false)
			g.drawOval(10, 10, 10, 10);
		else
			g.fillOval(10, 10, 10, 10);
	}
}