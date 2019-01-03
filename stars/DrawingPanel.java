import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.image.*;

class DrawingPanel extends JPanel
	implements MouseMotionListener
{
	int preX;
	int preY;
	int curX;
	int curY;

	Vector<Star> livingObjects;

	JCheckBox trailsCB;

	BufferedImage im;
	Graphics2D imGraphics;

	Color savedColor;

	DrawingPanel(Vector livingObjects, JCheckBox trailsCB)
	{
		this.livingObjects = livingObjects;

		this.trailsCB = trailsCB;

		imGraphics = null;


	}

	public void mouseMoved(MouseEvent e)
	{
	}

	public void mouseDragged(MouseEvent e)
	{
		preX = curX;
		preY = curY;
		curX = e.getX();
		curY = e.getY();
		repaint();
	}

	@Override
	public void paintComponent(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;

		if (imGraphics == null)
		{
			Toolkit tk;
			tk = Toolkit.getDefaultToolkit();
			Dimension d;
			d = tk.getScreenSize();

			im = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);

			imGraphics = im.createGraphics();
		}

		if (!trailsCB.isSelected())
		{
			savedColor = imGraphics.getColor();
			imGraphics.setColor(Color.WHITE);
			imGraphics.fillRect(getX(), getY(), getWidth(), getHeight());
			imGraphics.setColor(savedColor);
		}

		Star starToDraw = new Star();

		for (int x = 0; x < livingObjects.size(); x++)
		{
			starToDraw = livingObjects.get(x);
			starToDraw.draw(imGraphics);
		}

		g.drawImage(im, 0, 0, this);

		updateUI();
	}
}