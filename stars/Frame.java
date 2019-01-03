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


class Frame extends JFrame
    implements ActionListener, MortalityListener, ChangeListener
{
	Vector<LivingObject> livingObjects;

	DrawingPanel dPanel;

	javax.swing.Timer timer;

	JPanel controlPanel;

	JPanel sliderPanel;

	JPanel subControlPanel;

	JPanel checkBoxPanel;

	JPanel buttonPanel;

	JPanel slider1;

	JPanel slider2;

	JPanel slider3;

	JPanel slider4;

	JPanel slider1s1;

	JPanel slider2s1;

	JPanel slider3s1;

	JPanel slider4s1;

	JPanel slider1s2;

	JPanel slider2s2;

	JPanel slider3s2;

	JPanel slider4s2;

	JButton button;

	JCheckBox gravityCB;

	JCheckBox trailsCB;

	JCheckBox accelerateCB;

	JCheckBox mortalityCB;

	JCheckBox deathSpiralCB;

	JSlider gravityStrength;

	JSlider impactEnergyLoss;

	JSlider animationSpeed;

	JSlider starLifeSpan;

	JLabel label;

	double value_GS;

	double value_IEL;

	double value_AS;

	long value_SLS;

    Frame()
	{
		Container cp;
		cp = getContentPane();

		livingObjects = new Vector();

		controlPanel = new JPanel(new GridLayout(2, 1));

		sliderPanel = new JPanel();

		subControlPanel = new JPanel();

		buttonPanel = new JPanel();

		checkBoxPanel = new JPanel();

		slider1 = new JPanel(new GridLayout(2, 1));

		slider2 = new JPanel(new GridLayout(2, 1));

		slider3 = new JPanel(new GridLayout(2, 1));

		slider4 = new JPanel(new GridLayout(2, 1));

		slider1s1 = new JPanel();
		slider1s2 = new JPanel();

		slider2s1 = new JPanel();
		slider2s2 = new JPanel();

		slider3s1 = new JPanel();
		slider3s2 = new JPanel();

		slider4s1 = new JPanel();
		slider4s2 = new JPanel();

		button = new JButton("Add Star");
		button.setActionCommand("ADD");
		button.addActionListener(this);

		buttonPanel.add(button);

		button = new JButton("Add 20 Stars");
		button.setActionCommand("ADD_20");
		button.addActionListener(this);

		buttonPanel.add(button);

		button = new JButton("Remove Star");
		button.setActionCommand("REMOVE");
		button.addActionListener(this);

		buttonPanel.add(button);

		button = new JButton("Remove All Stars");
		button.setActionCommand("REMOVE_ALL");
		button.addActionListener(this);

		buttonPanel.add(button);

		trailsCB = new JCheckBox("Star Trails", false);

		checkBoxPanel.add(trailsCB);

		gravityCB = new JCheckBox("Gravity", false);

		checkBoxPanel.add(gravityCB);

		accelerateCB = new JCheckBox("Accelerate", false);

		checkBoxPanel.add(accelerateCB);

		mortalityCB = new JCheckBox("Mortality", false);

		checkBoxPanel.add(mortalityCB);

		deathSpiralCB = new JCheckBox("Death Spiral", false);

		checkBoxPanel.add(deathSpiralCB);

		gravityStrength = new JSlider(0, 100, 50);
		//gravityStrength.setMajorTickSpacing(20);
		gravityStrength.setPaintTicks(true);
		gravityStrength.setPaintLabels(true);
		gravityStrength.addChangeListener(this);
		label = new JLabel("Gravity Strength");

		slider1s1.add(label);
		slider1s2.add(gravityStrength);

		slider1.add(slider1s1);
		slider1.add(slider1s2);
		sliderPanel.add(slider1);

		impactEnergyLoss = new JSlider(0, 100, 50);
		//impactEnergyLoss.setMajorTickSpacing(20);
		impactEnergyLoss.setPaintTicks(true);
		impactEnergyLoss.setPaintLabels(true);
		impactEnergyLoss.addChangeListener(this);
		label = new JLabel ("Impact Energy Loss");

		slider2s1.add(label);
		slider2s2.add(impactEnergyLoss);

		slider2.add(slider2s1);
		slider2.add(slider2s2);
		sliderPanel.add(slider2);

		animationSpeed = new JSlider(0, 100, 50);
		//animationSpeed.setMajorTickSpacing(20);
		animationSpeed.setPaintTicks(true);
		animationSpeed.setPaintLabels(true);
		animationSpeed.addChangeListener(this);
		label = new JLabel ("Animation Speed");

		slider3s1.add(label);
		slider3s2.add(animationSpeed);

		slider3.add(slider3s1);
		slider3.add(slider3s2);
		sliderPanel.add(slider3);

		starLifeSpan = new JSlider(0, 100, 50);
		//starLifeSpan.setMajorTickSpacing(20);
		starLifeSpan.setPaintTicks(true);
		starLifeSpan.setPaintLabels(true);
		starLifeSpan.addChangeListener(this);
		label = new JLabel ("Star Life Span");

		slider4s1.add(label);
		slider4s2.add(starLifeSpan);

		slider4.add(slider4s1);
		slider4.add(slider4s2);
		sliderPanel.add(slider4);

		value_GS = 0.85;
		value_IEL = 1;
		value_AS = 1;
		value_SLS = 50 * 1000;

		subControlPanel.add(buttonPanel);

		subControlPanel.add(checkBoxPanel);

		controlPanel.add(subControlPanel);

		controlPanel.add(sliderPanel);

		dPanel = new DrawingPanel(livingObjects, trailsCB);

		cp.add(dPanel, BorderLayout.CENTER);

		cp.add(controlPanel, BorderLayout.SOUTH);

		timer = new javax.swing.Timer(15, this);
		timer.setActionCommand("UPDATE");
		timer.start();

		setupFrame();
	}

    public void setupFrame()
	{
		Toolkit tk;
		Dimension d;

		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();

		setSize(d.width/2, d.height/2);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocation(d.width/4, d.height/4);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setTitle("Star Panel");

		setVisible(true);
	}

	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource().equals(gravityStrength))
		{
			value_GS = gravityStrength.getValue();

			value_GS = Math.pow((value_GS / 50), 3);
		}

		else if (e.getSource().equals(impactEnergyLoss))
		{
			value_IEL = impactEnergyLoss.getValue();

			value_IEL = (value_IEL / (long)58.8235294118);
		}

		else if (e.getSource().equals(animationSpeed))
		{
			value_AS = animationSpeed.getValue();

			value_AS = Math.pow((value_AS / 50), 3);
		}

		else if (e.getSource().equals(starLifeSpan))
		{
			value_SLS = starLifeSpan.getValue();

			value_SLS = (1000 * value_SLS);
		}
	}

	public void update()
	{
		Star starToUpdate;

		int size = livingObjects.size();

		for (int x = 0; x < size; x++)
		{
			starToUpdate = (Star)livingObjects.get(x);
			starToUpdate.update(System.currentTimeMillis(), gravityCB, deathSpiralCB, accelerateCB, mortalityCB, value_GS, value_IEL, value_AS);
			size = livingObjects.size();

			if (x < size)
				livingObjects.set(x, starToUpdate);
		}

		dPanel.repaint();
	}

	public void add()
	{
		Star newStar = Star.getRandom(dPanel, this, value_SLS); // PASS LIFESPANVALUE HERE

		livingObjects.add(newStar);
	}

	public void add20()
	{
		for (int x = 0; x < 20; x++)
			add();
	}

	public void remove()
	{
		if (livingObjects.size() != 0)
			livingObjects.remove(livingObjects.size()-1);
	}

	public void removeAll()
	{
		livingObjects.removeAllElements();
	}

	public void mortalityChanged(MortalityEvent e)
	{
		if (mortalityCB.isSelected())
		{
			if (e.dead)
			{
				Star deadStar = (Star)e.getSource();
				livingObjects.remove(deadStar);
			}
		}
	}

    public void actionPerformed (ActionEvent e)
    {
		if (e.getActionCommand().equals("UPDATE"))
			update();

		else if (e.getActionCommand().equals("ADD"))
			add();

		else if (e.getActionCommand().equals("ADD_20"))
			add20();

		else if (e.getActionCommand().equals("REMOVE"))
			remove();

		else if (e.getActionCommand().equals("REMOVE_ALL"))
			removeAll();
    }
}