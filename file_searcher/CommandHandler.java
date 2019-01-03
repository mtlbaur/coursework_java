import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.filechooser.*;
import java.net.*;

class CommandHandler
		implements Runnable
{
	Socket clientSocket;
	String domain = "127.0.0.1";
	int port = 7777;
	String id = "Command Handler";

	Talker t;

	Controller c;

	Component host;

	FileSearcher fs;

	Thread fileSearcherThread;

	CommandHandler(Controller c, Component host)
	{
		this.c = c;
		this.t = t;
		this.host = host;
	}

	public void run()
	{
		boolean success = false;

		while (!success)
		{
			try
			{
				System.out.println("trying");

				clientSocket = new Socket(domain, port);

				t = new Talker(clientSocket, id);

				Thread.sleep(10000);

				success = true;
			}

			catch (Exception x)
			{
				System.out.println("failed");

				success = false;
			}
		}

		try
		{
			String msg;

			t.sendClientId();

			while (true)
			{
				msg = t.receive();

				System.out.println("CommandHandler Received: >>" + msg + "<<");

				if (msg.equals("TERMINATE"))
				{
					//c.actionPerformed(new ActionEvent(this, 0, "TERMINATE"));

					System.exit(0);
				}

				else if (msg.equals("DISPLAY_MSG"))
				{
					if (host != null)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								JOptionPane.showMessageDialog(host, "Sent file names.", "Alert", JOptionPane.INFORMATION_MESSAGE);
							}
						});
					}
				}

				else if (msg.startsWith("SEARCH"))
				{
					if (msg.endsWith("_1"))
					{
						fs = new FileSearcher(c, t);

						fileSearcherThread = new Thread(fs);
						fileSearcherThread.start();
					}

					else
					{
						System.out.println("SEARCH_2");

						fs.stop();
					}
				}
			}
		}

		catch (Exception e)
		{
			//e.printStackTrace();

			c.actionPerformed(new ActionEvent(this, 0, "CONNECTION_FAILED"));

			System.out.println("CommandHandler: run(): Exception");
		}
	}
}
