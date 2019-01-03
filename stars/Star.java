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

class Star extends LivingObject
{
	Star()
	{
	}

	Star(int nTips, double iRadius, double oRadius, double orient, Color c, double xSpeed, double ySpeed, double angVel, double angle, long lifeSpan, DrawingPanel dPanel, MortalityListener ml)
	{
		lastUpdateTime = System.currentTimeMillis();

		this.nTips = nTips;
		this.iRadius = iRadius;
		this.oRadius = oRadius;
		this.orient = orient;
		this.dPanel = dPanel;
		this.xPos = ((int)oRadius + r.nextInt((int)(dPanel.getWidth() - (2*oRadius) + 1)));
		this.yPos = ((int)oRadius + r.nextInt((int)(dPanel.getHeight() - (2*oRadius) + 1)));
		this.c = c;
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.angVel = angVel;
		this.angle = angle;
		this.lifeSpan = lifeSpan;
		this.listenerList = new Vector();
		this.ml = ml;
		addMortalityListener(ml);
	}

	public void draw(Graphics g1)
	{
		int x;
		int y;

		Graphics2D g2;

		g2 = (Graphics2D) g1;

		Polygon poly = new Polygon();

		for (int k = 0; k < nTips; k++)
		{
			x = xPos + (int)(Math.cos(orient) * iRadius);
			y = yPos + (int)(Math.sin(orient) * iRadius);

			poly.addPoint(x, y);
			orient = orient + Math.PI/nTips;

			x = xPos + (int)(Math.cos(orient) * oRadius);
			y = yPos + (int)(Math.sin(orient) * oRadius);

			poly.addPoint(x, y);
			orient = orient + Math.PI/nTips;
		}

		g1.setColor(c);

		g1.fillPolygon(poly);

		//g1.setColor(Color.BLACK); // If this is uncommented then each Star will have a black outline. The width of this outline is determined by the BasicStroke object constructed on the following line.

		g2.setStroke(new BasicStroke(4));

		g2.drawPolygon(poly);
	}

	public void update(long curMillis, JCheckBox gravityCB, JCheckBox deathSpiralCB, JCheckBox accelerateCB, JCheckBox mortalityCB, double value_GS, double value_IEL, double value_AS)
	{
		curTime = curMillis;

		deltaTime = curTime - lastUpdateTime;

		lastUpdateTime = curTime;

		xAccel = xSpeed / deltaTime;
		yAccel = ySpeed / deltaTime;

		if(gravityCB.isSelected() == true)
			updateLinearVelocity(value_GS);

		if(accelerateCB.isSelected() == true)
		{
			updatePositionAccelerate();
		}

		else
			updatePosition(value_AS);

		updateOrientation(value_AS);

		if (xPos >= dPanel.getWidth() - oRadius)
			reflectOffVWallRight(value_IEL);

		else if (xPos - oRadius <= 0)
			reflectOffVWallLeft(value_IEL);

		if (yPos >= dPanel.getHeight() - oRadius)
			reflectOffHWallBottom(value_IEL);

		else if (yPos - oRadius <= 0)
			reflectOffHWallTop(value_IEL);

		updateVitality(mortalityCB, deathSpiralCB, value_AS);

		lastUpdateTime = System.currentTimeMillis();
	}

	public static Star getRandom(DrawingPanel dPanel, MortalityListener ml, long starLifeSpan)
	{
		Star randomStar;

		int nTips = 3 + r.nextInt(10);

		double iRadius = 25 * r.nextDouble() + 10;

		double oRadius = 50 * r.nextDouble() + 40;

		double orient = r.nextInt(361);

		Color c;

		int x;

		x = r.nextInt(8);

		if (x == 0)
			c = Color.BLACK;

		else if (x == 1)
			c = Color.BLUE;

		else if (x == 2)
			c = Color.CYAN;

		else if (x == 3)
			c = Color.GREEN;

		else if (x == 4)
			c = Color.MAGENTA;

		else if (x == 5)
			c = Color.RED;

		else if (x == 6)
			c = Color.YELLOW;

		else
			c = Color.GRAY;

		double xSpeed = ((2 * r.nextInt(2) -1) * (1+ r.nextInt(5)));

		double ySpeed = ((2 * r.nextInt(2) -1) * (1+ r.nextInt(5)));

		double angVel = 2 * r.nextDouble() + 1;

		double angle = 0;

		//long lifeSpan = (long)(1* r.nextDouble() + 10000);

		long lifeSpan = starLifeSpan;

		lifeSpan = lifeSpan + System.currentTimeMillis();

		return randomStar = new Star(nTips, iRadius, oRadius, orient, c, xSpeed, ySpeed, angVel, angle, lifeSpan, dPanel, ml);
	}

	public Star getSource()
	{
		return this;
	}
}