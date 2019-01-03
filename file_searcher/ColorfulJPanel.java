import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class ColorfulJPanel extends JPanel
	implements MouseListener
{
	Random r;

	int x;

	ColorfulJPanel()
	{
		r = new Random();

		this.addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
		x = r.nextInt(8);

		if (x == 0)
			setBackground(Color.BLACK);

		else if (x == 1)
			setBackground(Color.BLUE);

		else if (x == 2)
			setBackground(Color.CYAN);

		else if (x == 3)
			setBackground(Color.GREEN);

		else if (x == 4)
			setBackground(Color.MAGENTA);

		else if (x == 5)
			setBackground(Color.RED);

		else if (x == 6)
			setBackground(Color.YELLOW);

		else
			setBackground(Color.GRAY);
	}

	public void mouseExited(MouseEvent e)
	{
	}
}
