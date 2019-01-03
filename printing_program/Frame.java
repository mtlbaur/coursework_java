import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import javax.swing.event.*;

class Frame extends JFrame
    implements ActionListener, MouseListener
{
	JPanel controlPanel;

	JPanel buttonPanel;

	JPanel mainPanel;

	JButton goButton;

	JButton printButton;

	JButton clearButton;

	JDialog dialog;

	JDialog errorDialog;

	JLabel label;

	Vector<String> fileNameList;

	JList list;

	JScrollPane scroller;

	JTextField inputField;

	DrawingPanel dPanel;

	Point p;

	URLPrinter printer;

	Toolkit tk;

	Dimension d;

	String domain;

    Frame()
	{
		Container cp;
		cp = getContentPane();

		controlPanel = new JPanel(new GridLayout(2, 1));
		buttonPanel = new JPanel();
		mainPanel = new JPanel(new GridLayout(2, 1));

		goButton = new JButton("Go");
		goButton.setActionCommand("GO");
		goButton.addActionListener(this);
		getRootPane().setDefaultButton(goButton);

		printButton = new JButton("Print");
		printButton.setActionCommand("PRINT");
		printButton.addActionListener(this);

		clearButton = new JButton("Clear");
		clearButton.setActionCommand("CLEAR");
		clearButton.addActionListener(this);

		inputField = new JTextField();

		fileNameList = new Vector();
		list = new JList(fileNameList);
		list.addMouseListener(this);

		scroller = new JScrollPane();
		scroller.getViewport().setView(list);

		dialog = new JDialog(this, "Image", Dialog.ModalityType.MODELESS);
		errorDialog = new JDialog(this, "Image", Dialog.ModalityType.MODELESS);
		label = new JLabel();

		controlPanel.add(inputField);
		buttonPanel.add(goButton);
		buttonPanel.add(printButton);
		buttonPanel.add(clearButton);
		controlPanel.add(buttonPanel);

		cp.add(scroller, BorderLayout.CENTER);

		cp.add(controlPanel, BorderLayout.SOUTH);

		setupFrame();
	}

    public void setupFrame()
	{
		//Toolkit tk;
		//Dimension d;

		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();

		p = new Point(d.width/4, d.height/4);

		errorDialog.setSize(d.width/2, d.height/2);
		dialog.setSize(d.width/2, d.height/2);

		setSize(d.width/2, d.height/2);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocation(d.width/4, d.height/4);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setTitle("URL Printer");

		setVisible(true);
	}

	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 2)
		{
			System.out.println("detected double click");

			try
			{
				//===========================================================================

				String url = list.getSelectedValue().toString();

				if (url.startsWith("http"))
					dPanel = new DrawingPanel(new URL(url));

				else
				{
					url = domain + url;

					System.out.println(url);

					dPanel = new DrawingPanel(new URL(url));
				}

				//===========================================================================

				dialog = new JDialog(this, "Image", Dialog.ModalityType.MODELESS);

				dialog.setSize(d.width/2, d.height/2);

				dialog.add(dPanel);

				dialog.repaint();

				dialog.setLocation(d.width/4, d.height/4);

				dialog.setVisible(true);
			}

			catch(MalformedURLException m)
			{
				System.out.println("trash url");

				//label.setHorizontalTextPosition(SwingConstants.CENTER); // DOES NOTHING

				//label = new JLabel("<html><div style='text-align: center;'> Malformed URL </div></html>"); DOES NOTHING

				//label = new JLabel("Malformed URL", SwingConstants.CENTER); // WORKS

				//label.setText("<html><div style='font-size: 200%;'><b> Malformed URL </b></div></html>"); // WORKS no center though

				dPanel = new DrawingPanel();

				label = new JLabel("<html><div style='font-size: 200%;'><b> Malformed URL </b></div></html>", SwingConstants.CENTER); // WORKS

				errorDialog = new JDialog(this, "Image", Dialog.ModalityType.MODELESS);

				errorDialog.setSize(d.width/2, d.height/2);

				errorDialog.add(label);

				errorDialog.repaint();

				errorDialog.setLocation(d.width/4, d.height/4);

				errorDialog.setVisible(true);
			}
		}
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
    }

	public void go()
	{
		System.out.println("GO");

		URL url;
		URLConnection urlConnection;
		InputStreamReader isr;
		TagHandler tagHandler;
		domain = inputField.getText();

		System.out.println(domain);

		try
		{
			url = new URL(domain);

			System.out.println(domain);

			urlConnection = url.openConnection();
			System.out.println("Last Modified: " + urlConnection.getHeaderField("last-modified"));

			isr = new InputStreamReader (urlConnection.getInputStream());

			tagHandler = new TagHandler(domain, fileNameList);
			new ParserDelegator().parse(isr, tagHandler, true);

			list.updateUI();
		}

		catch (MalformedURLException mue)
		{
			System.out.println("TKL: Malformed URL");

			label = new JLabel("<html><div style='font-size: 150%;'><b> Malformed Target URL </b></div></html>", SwingConstants.CENTER); // WORKS

			errorDialog = new JDialog(this, "Image", Dialog.ModalityType.MODELESS);

			errorDialog.setSize(d.width/2, d.height/2);

			errorDialog.setLocation(d.width/4, d.height/4);

			errorDialog.add(label);

			errorDialog.repaint();

			errorDialog.setVisible(true);
		}

		catch (IOException ioe)
		{
			System.out.println("TKL: IO Exception");

			label = new JLabel("<html><div style='font-size: 150%;'><b> IOException while processing URL </b></div></html>", SwingConstants.CENTER); // WORKS

			errorDialog = new JDialog(this, "Image", Dialog.ModalityType.MODELESS);

			errorDialog.setSize(d.width/2, d.height/2);

			errorDialog.setLocation(d.width/4, d.height/4);

			errorDialog.add(label);

			errorDialog.repaint();

			errorDialog.setVisible(true);
		}
	}

	public void print()
	{
		System.out.println("PRINT");

		printer = new URLPrinter(fileNameList);
		printer.printIt(printer);
	}

	public void clear()
	{
		inputField.setText("");

		fileNameList.removeAllElements();

		list.updateUI();
	}

    public void actionPerformed (ActionEvent e)
    {
		if (e.getActionCommand().equals("GO"))
			go();

		else if (e.getActionCommand().equals("PRINT"))
			print();

		else if (e.getActionCommand().equals("CLEAR"))
			clear();
	}
}