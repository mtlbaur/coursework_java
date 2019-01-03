import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.filechooser.*;
import java.net.*;

class Commander extends JFrame
				implements ActionListener, Runnable, ItemListener
{
	Talker t;

	JButton terminate;
	JButton display_msg;
	JRadioButton search;
	JButton exit;
	JPanel buttonPanel;

	String serverId;
	String clientId;

	Commander(String domain, int port, String id)
	{
		try
		{
			t = new Talker(domain, port, id);
		}

		catch (IOException e)
		{
			System.out.println("Commander(): IOException");

			System.exit(0);
		}

		serverId = id;

		terminate = new JButton("Terminate");
		terminate.addActionListener(this);
		terminate.setActionCommand("TERMINATE");

		display_msg = new JButton("Display Message");
		display_msg.addActionListener(this);
		display_msg.setActionCommand("DISPLAY_MSG");

		search = new JRadioButton("Search");
		search.addItemListener(this);
		search.setActionCommand("SEARCH");

		exit = new JButton("Exit");
		exit.addActionListener(this);
		exit.setActionCommand("EXIT");

		buttonPanel = new JPanel();
		buttonPanel.add(terminate);
		buttonPanel.add(display_msg);
		buttonPanel.add(search);
		buttonPanel.add(exit);

		add(buttonPanel, BorderLayout.CENTER);

		setupCommander();
	}

	public void setupCommander()
	{
		Toolkit tk;

		Dimension d;

		tk = Toolkit.getDefaultToolkit();

		d = tk.getScreenSize();

		setSize(d.width/2, d.height/2);

		setLocation(d.width/4, d.height/4);

		setTitle("Commander");

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setVisible(true);
    }

    public void itemStateChanged(ItemEvent e)
    {
		try
		{
			if (search.equals(e.getItem()))
			{
				System.out.println("search: " + e.getStateChange());

				t.send("SEARCH_" + e.getStateChange());
			}
		}

		catch (Exception x)
		{
			System.out.println("Commander: itemStateChanged(): Exception");
		}
	}

	public void run()
	{
		System.out.println("Commander: RUN");

		String msg;

		try
		{
			while (true)
			{
				msg = t.receive();

				if (msg.substring(0, 4).equals("!ID:"))
				{
					System.out.println("Commander: RECEIVED CLIENT ID");
					clientId = msg.substring(4);
					System.out.println("clientId = >>" + clientId + "<<");
					t.setClientId(clientId);
				}

				else if (msg.substring(0, 10).equals("!FILENAME:"))
				{
					System.out.println("Commander Received: >>" + msg + "<<" + " From: " + clientId);

					System.out.println(msg.substring(10));
				}

				else
				{
					System.out.println("Commander Received: >>" + msg + "<<" + " From: " + clientId);

					t.send(serverId + " Received Msg: >>" + msg + "<<");
				}
			}
		}

		catch (Exception e)
		{
			System.out.println("Commander: run(): Exception");
		}

		System.exit(0);
	}

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		try
		{
			if (cmd == "TERMINATE")
			{
				t.send("TERMINATE");

				System.out.println("Commander: TERMINATE");
			}

			else if (cmd == "DISPLAY_MSG")
			{
				t.send("DISPLAY_MSG");

				System.out.println("Commander: DISPLAY_MSG");
			}
/*
			else if (cmd == "SEARCH")
			{
				//t.send("SEARCH");

				//System.out.println("Commander: SEARCH");
			}
*/
			else if (cmd == "EXIT")
			{
				System.out.println("Commander: EXIT");

				System.exit(0);
			}
		}

		catch (Exception x)
		{
			System.out.println("Commander: actionPerformed(): Exception");
		}
	}

	public static void main(String[] x)
	{
		new Thread(new Commander("127.0.0.1", 7777, "Commander")).start();
	}
}