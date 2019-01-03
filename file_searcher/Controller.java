import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.filechooser.*;
import java.net.*;

class Controller
	implements ActionListener
{
	CommandHandler ch;

	Component host;

	Thread cht;

	Controller()
	{
		ch = new CommandHandler(this, host);
		cht = new Thread(ch);
		cht.start();
	}

	Controller(Component host)
	{
		this.host = host;

		ch = new CommandHandler(this, host);
		cht = new Thread(ch);
		cht.start();
	}

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("CONNECTION_FAILED"))
		{
			ch = new CommandHandler(this, host);
			cht = new Thread(ch);
			cht.start();
		}

		else if (cmd.equals("TERMINATE"))
		{
			System.exit(0);
		}
	}

	public static void main(String[] x)
	{
		new Controller();

		while(true)
		{
		}
	}
}