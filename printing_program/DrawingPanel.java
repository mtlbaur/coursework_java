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
import java.net.*;

class DrawingPanel extends JPanel
{
	ImageIcon imageIcon;

	URL imageURL;

	Graphics g;

	Image im;

	//Toolkit tk;

	//tk = Toolkit.getDefaultToolkit(); // wants an identifier

	DrawingPanel()
	{
		this.imageURL = null;

		imageIcon = null;
	}

	DrawingPanel(URL imageURL)
	{
		this.imageURL = imageURL;

		imageIcon = new ImageIcon(imageURL);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		this.g = g;

		g.setColor(Color.BLACK);

		Toolkit tk;
		Dimension d;

		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();

		if (imageIcon == null)
		{
			g.fillRect(0, 0, d.width, d.height);
		}
		else
		{
			//Image i = imageIcon.getImage();

			//Image i = tk.getImage(imageURL);

			//g.drawImage(imageIcon.getImage(), 0, 0, this); // behaves the same as the next line below

			//g.drawImage(tk.getImage(imageURL), 0, 0, this);

			im = tk.getImage(imageURL);

			g.fillRect(0, 0, d.width, d.height);

			g.drawImage(im, 0, 0, this);
		}

		updateUI();
	}
}