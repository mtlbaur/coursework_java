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

abstract class LivingObject
{
	int nTips;
	double iRadius;
	double oRadius;
	double orient;
	int xPos;
	int yPos;
	Color c;
	double xSpeed;
	double ySpeed;
	long lastUpdateTime;
	long curTime;
	double angVel;
	long lifeSpan;
	long deltaTime;
	double xAccel;
	double yAccel;
	double angle;

	MortalityListener ml;

	static Random r = new Random();

	Vector<Star> livingObjects;

	DrawingPanel dPanel;

	Vector<MortalityListener> listenerList;

	public void update(long curMillis, JCheckBox gravityCB, JCheckBox deathSpiralCB, JCheckBox accelerateCB, JCheckBox mortalityCB, double value_GS, double value_IEL, double value_AS)
	{
		System.out.println("Calling update in LivingObject: THIS IS NOT SUPPOSED TO HAPPEN");
	}

	public abstract void draw(Graphics g1);

	public void updateLinearVelocity(double value_GS)
	{
		//ySpeed = (ySpeed + 1);

		if (yPos < dPanel.getHeight() - oRadius)
			ySpeed = (ySpeed + value_GS);
	}

	public void updateAngularVelocity(double value_AS)
	{
		angVel = (angVel + (deltaTime)/10) * value_AS;
	}

	public void updatePositionAccelerate()
	{
		//xSpeed = (xSpeed * value_AS);
		//ySpeed = (ySpeed * value_AS);

		xSpeed = (xSpeed + xAccel);
		ySpeed = (ySpeed + yAccel);

		xPos = (int) (xPos + xSpeed);

		yPos = (int) (yPos + ySpeed);
	}

	public void updatePosition(double value_AS)
	{
		//double angle;
		//angle = java.lang.Math.atan2(xSpeed, ySpeed);
		//System.out.println(angle);

		//xSpeed = (xSpeed * value_AS);
		//ySpeed = (ySpeed * value_AS);

		xPos = (int) (xPos + xSpeed * value_AS);

		yPos = (int) (yPos + ySpeed * value_AS);
	}

	public void updateOrientation(double value_AS)
	{
		orient = (orient + (angVel * value_AS));
	}

	public void reflectOffVWallLeft(double value_IEL)
	{
		//xSpeed = -xSpeed * 0.85;

		xSpeed = -xSpeed * value_IEL;
		xPos = (int)(1 + oRadius);
	}

	public void reflectOffVWallRight(double value_IEL)
	{
		//xSpeed = -xSpeed * 0.85;

		xSpeed = -xSpeed * value_IEL;
		xPos = (int)(dPanel.getWidth() - oRadius - 1);
	}

	public void reflectOffHWallTop(double value_IEL)
	{
		//ySpeed = -ySpeed * 0.85;

		ySpeed = -ySpeed * value_IEL;
		yPos = (int)(1 + oRadius);
	}

	public void reflectOffHWallBottom(double value_IEL)
	{
		//ySpeed = -ySpeed * 0.85;

		ySpeed = -ySpeed * value_IEL;
		yPos = (int)(dPanel.getHeight() - oRadius - 1);
	}

	public void updateVitality(JCheckBox mortalityCB, JCheckBox deathSpiralCB, double value_AS)
	{
		if (curTime > lifeSpan)
		{
			MortalityEvent e = new MortalityEvent(true, this);

			ml.mortalityChanged(e);
		}

		else if ((lifeSpan - curTime < 8000) && mortalityCB.isSelected() && deathSpiralCB.isSelected())
		{
			updateAngularVelocity(value_AS);
		}
	}

	public void addMortalityListener(MortalityListener ml)
	{
		listenerList.add(ml);
	}

	public LivingObject getSource()
	{
		return this;
	}
}