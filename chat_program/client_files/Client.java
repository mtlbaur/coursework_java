import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

class Client extends JFrame
			implements ActionListener, WindowListener, ListSelectionListener
{
	JButton login;
	JButton logout;
	JButton register;
	JButton addFriend;
	JButton exit;
	JButton message;

	FriendTable friendTable;

    JPanel buttonPanel;

    DefaultListModel<Friend> onlineFriends;
    DefaultListModel<Friend> offlineFriends;

	JList online_friends_view;
	JList offline_friends_view;

	JScrollPane online_friends_scroller;
	JScrollPane offline_friends_scroller;

	LoginDialog ld;
	RegisterDialog rd;
	ChatDialog cd;
	FriendRequestDialog frd;

	ConnectionToServer cts;

	protected String username;
	protected String password;

	boolean loggedIn;

	JPanel scrollerPanel;

	File fileToTransfer;

	JLabel onlineLabel;
	JLabel offlineLabel;

	JPanel friendsPanel;

	GroupLayout layout;

	boolean notify;

    Client()
    {
		loggedIn = false;

		Container cp = getContentPane();

		login = new JButton("Login");
		login.addActionListener(this);
		login.setActionCommand("LOGIN");

		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setActionCommand("LOGOUT");

		register = new JButton("Register");
		register.addActionListener(this);
		register.setActionCommand("REGISTER");

		exit = new JButton("Exit");
		exit.addActionListener(this);
		exit.setActionCommand("EXIT");

		addFriend = new JButton("Add Friend");
		addFriend.addActionListener(this);
		addFriend.setActionCommand("ADD_FRIEND");

		message = new JButton("Message");
		message.addActionListener(this);
		message.setActionCommand("MESSAGE");
		message.setEnabled(false);
		getRootPane().setDefaultButton(message);

		onlineFriends = new DefaultListModel();
		online_friends_view = new JList(onlineFriends);
		online_friends_view.addListSelectionListener(this);
		online_friends_scroller = new JScrollPane(online_friends_view);

		offlineFriends = new DefaultListModel();
		offline_friends_view = new JList(offlineFriends);
		offline_friends_view.addListSelectionListener(this);
		offline_friends_scroller = new JScrollPane(offline_friends_view);

		scrollerPanel = new JPanel(new GridLayout(1, 2));

		friendsPanel = new JPanel();

		onlineLabel = new JLabel("<html><div style='font-size: 110%;'><b>Online Friends</b></div></html>", SwingConstants.CENTER);
		offlineLabel = new JLabel("<html><div style='font-size: 110%;'><b>Offline Friends</b></div></html>", SwingConstants.CENTER);

		//***********************************************//

		layout = new GroupLayout(friendsPanel);

		friendsPanel.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(onlineLabel)
			.addComponent(online_friends_scroller));

		hGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(offlineLabel)
			.addComponent(offline_friends_scroller));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(onlineLabel).addComponent(offlineLabel));

		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(online_friends_scroller).addComponent(offline_friends_scroller));

		layout.setVerticalGroup(vGroup);

        cp.add(friendsPanel, BorderLayout.CENTER);

        //***********************************************//

		scrollerPanel.add(online_friends_scroller);
		scrollerPanel.add(offline_friends_scroller);

		friendTable = new FriendTable(this);

		login.setEnabled(true);
		logout.setEnabled(false);
		register.setEnabled(true);
		addFriend.setEnabled(false);
		message.setEnabled(false);

		buttonPanel = new JPanel();

		buttonPanel.add(message);
		buttonPanel.add(addFriend);
		buttonPanel.add(login);
		buttonPanel.add(logout);
		buttonPanel.add(register);
		buttonPanel.add(exit);

		//cp.add(online_friends_scroller);
		//cp.add(scrollerPanel);
        cp.add(buttonPanel, BorderLayout.SOUTH);

        ld = new LoginDialog(this);
        rd = new RegisterDialog(this);

        //TESTING
        //for (int x = 0; x < 50; x++)
        //{
       	//	onlineFriends.addElement(new Friend(this, "asdf;laksdjf;laksdjf", "1"));
		//}
        //TESTING

        notify = false;

		setupClient();
    }

	public void setupClient()
	{
		Toolkit tk;

		Dimension d;

		tk = Toolkit.getDefaultToolkit();

		d = tk.getScreenSize();

		setSize(d.width/4, d.height/4);

		setLocation(d.width/4, d.height/4);

		setTitle("Client");

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setVisible(true);
    }

    public void send(String msg)
    {
		System.out.println("Client: SEND");

		cts.sendExactString(msg);
	}

	public void message()
	{
		Friend friendToMessage = (Friend)online_friends_view.getSelectedValue();

		if (friendToMessage == null)
			friendToMessage = (Friend)offline_friends_view.getSelectedValue();

		if (friendToMessage.cd == null)
			friendToMessage.cd = new ChatDialog(this, friendToMessage, friendToMessage.username);
	}

	public void printToChatDialog(String msg, String sender)
	{
		System.out.println("Client: printToChatDialog");

		Friend initiatingFriend = friendTable.get(sender);

		if (initiatingFriend.cd == null)
		{
			initiatingFriend.cd = new ChatDialog(this, initiatingFriend, sender);

			notify = false;

			new NotificationSound().notifyUser();

			initiatingFriend.cd.startTimer();
		}

		else if (notify == true)
		{
			notify = false;

			new NotificationSound().notifyUser();

			initiatingFriend.cd.startTimer();
		}

		initiatingFriend.cd.displayMessage(msg, sender, "#0000FF");
	}

	public void addFriend(String name, String status)
	{
		System.out.println("CLIENT ATTEMPTING TO ADD FRIEND");

		System.out.println(name + " " + status);

		Friend newFriend = new Friend(this, name, status);

		System.out.println(newFriend.username + " " + newFriend.status);

		friendTable.put(newFriend.username, newFriend);

		if (status.equals("1"))
			onlineFriends.addElement(newFriend);

		else
			offlineFriends.addElement(newFriend);
	}

	public void updateFriendStatus(String name, String status)
	{
		Friend temp = friendTable.get(name);

		if (!temp.status.equals(status))
		{
			if (temp.status.equals("1"))
			{
				onlineFriends.removeElement(temp);
				offlineFriends.addElement(temp);
				temp.status = "0";
			}

			else if (temp.status.equals("0"))
			{
				offlineFriends.removeElement(temp);
				onlineFriends.addElement(temp);
				temp.status = "1";
			}
		}
	}

	public void requestFileTransfer(String fileName, int fileSize, String recipient)
	{
		cts.sendExactString("!REQUEST_FILE_TRANSFER:" + " " + fileName + " " + fileSize + " " + recipient + " " + username);
	}

	public void login()
	{
		System.out.println("Client: LOGIN");

		if (!loggedIn)
		{
			ld.setVisible(true);
		}
	}

 	public void logout()
	{
		System.out.println("Client: LOGOUT");

		cts.logout();
	}

	public void register()
	{
		System.out.println("Client: REGISTER");

		if (!loggedIn)
		{
			rd.setVisible(true);
		}
	}

    public void exit()
    {
		System.out.println("Client: EXIT");

		System.exit(0);
    }

    public void attempt_login()
    {
		System.out.println("Client: ATTEMPT_LOGIN");

		onlineFriends.removeAllElements();
		offlineFriends.removeAllElements();

		friendTable = new FriendTable(this);

		if (!loggedIn)
			cts = new ConnectionToServer(this, username, password, "LOGIN");

		else
			JOptionPane.showMessageDialog(this, "You are already logged in!", "Alert", JOptionPane.INFORMATION_MESSAGE);
	}

	public void attempt_register()
	{
		System.out.println("Client: ATTEMPT_REGISTER");

		onlineFriends.removeAllElements();
		offlineFriends.removeAllElements();

		friendTable = new FriendTable(this);

		if (!loggedIn)
			cts = new ConnectionToServer(this, username, password, "REGISTER");

		else
			JOptionPane.showMessageDialog(this, "You are already registered and logged in!", "Alert", JOptionPane.INFORMATION_MESSAGE);
	}

	public void sendFriendRequest(String friendToAdd)
	{
		cts.sendExactString("!REQUEST_FRIEND:" + " " + friendToAdd + " " + username);
	}

	public void setupFriendRequestDialog(DefaultListModel registeredUsers)
	{
		frd = new FriendRequestDialog(this);

		frd.setDefaultListModel(registeredUsers);
	}

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		try
		{
			if (cmd.equals("MESSAGE"))
				message();

			else if (cmd.equals("LOGIN"))
				login();

			else if (cmd.equals("LOGOUT"))
			{
				logout();

				login.setEnabled(true);
				logout.setEnabled(false);
				register.setEnabled(true);
				addFriend.setEnabled(false);
				message.setEnabled(false);

				loggedIn = false;
			}

			else if (cmd.equals("REGISTER"))
				register();

        	else if (cmd.equals("EXIT"))
        	{
				if (loggedIn)
				{
					logout();
				}

            	exit();
			}

            else if (cmd.equals("ATTEMPT_LOGIN"))
            	attempt_login();

            else if (cmd.equals("ATTEMPT_REGISTER"))
            	attempt_register();

            else if (cmd.equals("CONNECTION_FAILED"))
            {
				System.out.println("CONNECTION_FAILED");

				login.setEnabled(true);
				logout.setEnabled(false);
				register.setEnabled(true);
				addFriend.setEnabled(false);
				message.setEnabled(false);

            	loggedIn = false;

            	JOptionPane.showMessageDialog(this, "The connection to the server was lost!", "Alert", JOptionPane.INFORMATION_MESSAGE);
			}

			else if (cmd.equals("SERVER_UNAVAILABLE"))
			{
				ld.actionPerformed(new ActionEvent(this, 0, "CANCEL"));

				rd.actionPerformed(new ActionEvent(this, 0, "CANCEL"));

				JOptionPane.showMessageDialog(this, "Server is currently unavailable!", "Alert", JOptionPane.INFORMATION_MESSAGE);
			}

			else if (cmd.equals("LOGIN_CONNECTION_FAILED"))
			{
				System.out.println("LOGIN_CONNECTION_FAILED");

				login.setEnabled(true);
				logout.setEnabled(false);
				register.setEnabled(true);
				addFriend.setEnabled(false);
				message.setEnabled(false);

				loggedIn = false;

				ld.actionPerformed(new ActionEvent(this, 0, "LOGIN_CONNECTION_FAILED"));
			}

			else if (cmd.equals("REGISTER_CONNECTION_FAILED"))
			{
				System.out.println("REGISTER_CONNECTION_FAILED");

				login.setEnabled(true);
				logout.setEnabled(false);
				register.setEnabled(true);
				addFriend.setEnabled(false);
				message.setEnabled(false);

				loggedIn = false;

				rd.actionPerformed(new ActionEvent(this, 0, "REGISTER_CONNECTION_FAILED"));
			}

			else if (cmd.equals("LOGIN_SUCCEEDED"))
			{
				System.out.println("LOGIN_SUCCEEDED");

				login.setEnabled(false);
				logout.setEnabled(true);
				register.setEnabled(false);
				addFriend.setEnabled(true);

				loggedIn = true;

				ld.actionPerformed(new ActionEvent(this, 0, "CANCEL"));
			}

			else if (cmd.equals("REGISTER_SUCCEEDED"))
			{
				System.out.println("REGISTER_SUCCEEDED");

				login.setEnabled(false);
				logout.setEnabled(true);
				register.setEnabled(false);
				addFriend.setEnabled(true);

				loggedIn = true;

				rd.actionPerformed(new ActionEvent(this, 0, "CANCEL"));
			}

			else if (cmd.equals("LOGIN_FAILED"))
			{
				ld.actionPerformed(new ActionEvent(this, 0, "FAILED"));
			}

			else if (cmd.equals("REGISTER_FAILED"))
			{
				rd.actionPerformed(new ActionEvent(this, 0, "FAILED"));
			}

			else if (cmd.equals("ADD_FRIEND"))
			{
				cts.sendExactString("REQUEST_FOR_REGISTERED_USERS");
			}

			else if (cmd.equals("PLAY_NOTIFICATION_SOUND"))
			{
				System.out.println(username + " PLAY NOTIFICATION SOUND");

				notify = true;
			}
		}

		catch (Exception x)
		{
			x.printStackTrace();

			System.out.println("Client: actionPerformed(): Exception");
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
		System.out.println("Client: windowClosing");

		if (loggedIn)
		{
			logout();
		}
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

	public void valueChanged(ListSelectionEvent e)
    {
		if (loggedIn)
		{
			if (online_friends_view.getSelectedIndex() != -1 && offline_friends_view.getSelectedIndex() != -1)
			{
				if (e.getSource() != online_friends_view)
					online_friends_view.clearSelection();

				else if (e.getSource() != offline_friends_view)
					offline_friends_view.clearSelection();
			}

			if (online_friends_view.getSelectedIndex() != -1 || offline_friends_view.getSelectedIndex() != -1)
				message.setEnabled(true);
		}
	}

	public static void main(String[] x)
	{
		new Client();
	}
}