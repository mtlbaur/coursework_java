import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;

class Server extends JFrame
			implements ActionListener, Runnable, WindowListener, ItemListener
{
	JButton exit;

    JPanel buttonPanel;

	String domain = "127.0.0.1";
	int port = 7777;
	String id = "Server";

	ServerSocket serverSocket;

	protected UserTable userTable;

    Server()
    {
		userTable = new UserTable();

		Container cp = getContentPane();

		exit = new JButton("Exit");
		exit.addActionListener(this);
		exit.setActionCommand("EXIT");

		buttonPanel = new JPanel();

		buttonPanel.add(exit);

        cp.add(buttonPanel, BorderLayout.CENTER);

      	setupServer();

      	new Thread(this).start();
    }

	public void setupServer()
	{
		Toolkit tk;

		Dimension d;

		tk = Toolkit.getDefaultToolkit();

		d = tk.getScreenSize();

		setSize(d.width/4, d.height/4);

		setLocation(d.width/3, d.height/3);

		setTitle("Server");

		//setDefaultCloseOperation(EXIT_ON_CLOSE);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		setVisible(true);
    }

    public void send(String recipient, String initiator, String msg)
    {
		System.out.println("SERVER TRYING TO SEND: " + msg + " TO: " + recipient);
		System.out.println(recipient);
		System.out.println(initiator);
		System.out.println(msg);

		try
		{
			User y = userTable.get(recipient);

			if (y.ctc != null)
			{
				System.out.println("SENT MSG TO: " + y.username);

				y.ctc.send(msg, initiator);
			}

			else
			{
				System.out.println("BUFFERED MSG FOR: " + y.username);

				y.msgBuffer.addElement("!MESSAGE:" + "/" + msg + "/" + "!SENDER:" + "/" + initiator);
			}


		}

		catch (Exception e)
		{
			e.printStackTrace();

			System.out.println("EXCEPTION WHILE SENDING MESSAGE");
		}
	}

	public void sendUserFriends(User user)
	{
		String friendList = "";

		if (user.friends != null && user.friends.size() != 0)
		{
			for (String friendName : user.friends)
			{
				System.out.println("FRIEND OF " + user.username + ": " + friendName);

				User friend = userTable.get(friendName);

				if (friend.ctc != null)
				{
					System.out.println("ONLINEFRIEND: " + friend.username);

					friendList += "!USERNAME:" + friend.username + "/" + "!STATUS:" + "1/";
				}

				else
				{
					System.out.println("OFFLINEFRIEND: " + friend.username);

					friendList += "!USERNAME:" + friend.username + "/" + "!STATUS:" + "0/";
				}
			}

			user.ctc.sendExactString("!FRIENDLIST:/" + friendList);
		}

		else
			user.ctc.sendExactString("NO_FRIENDS");
	}

	public void sendRegisteredUsers(String clientId)
	{
		String registeredUsers = "";

		Enumeration<User> enumeration_registeredUsers = userTable.elements();

		while (enumeration_registeredUsers.hasMoreElements())
		{
			registeredUsers += enumeration_registeredUsers.nextElement().username + "/";
		}

		User u = userTable.get(clientId);

		u.ctc.sendExactString("!REGISTERED_USERS:/" + registeredUsers);
	}

	public void updateStatus(User u, String status)
	{
		for (String friendName : u.friends)
		{
			User friend = userTable.get(friendName);

			if (friend.ctc != null)
			{
				friend.ctc.sendExactString("!UPDATE_FRIEND_STATUS:" + " " + u.username + " " + status);
			}
		}
	}

	public void run()
	{
		System.out.println("Server: RUN");

		try
		{
			serverSocket = new ServerSocket(port);

			while (true)
			{
				Socket clientSocket = serverSocket.accept();

				new ConnectionToClient(this, id, clientSocket);
			}
		}

		catch (Exception e)
		{
			System.out.println("ConnectionToClient: run(): Exception");
		}
	}


    public void exit()
    {
		System.out.println("Server: EXIT");

		System.exit(0);
    }

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		try
		{
			if (cmd.equals("EXIT"))
			{
				System.out.println("storing userTable");
				userTable.store();
            	exit();
			}
		}

		catch (Exception x)
		{
			System.out.println("Server: actionPerformed(): Exception");
		}
	}

    public void windowActivated(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
	}

	public void windowClosing(WindowEvent e)
	{
		System.out.println("storing userTable");
		userTable.store();
		System.out.println("Server: windowClosing");
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowOpened(WindowEvent e)
	{
	}

	public void itemStateChanged(ItemEvent e)
	{
	}

	public static void main(String[] x)
	{
		new Server();
	}
}