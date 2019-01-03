import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;
import javax.swing.text.html.*;
import javax.swing.text.*;

class FriendRequestDialog extends JFrame
			implements ActionListener, ListSelectionListener, MouseListener
{
	DefaultListModel<String> registeredUsers;
	JList registeredUsers_view;

	JButton send;
	JButton exit;

    JPanel buttonPanel;

	JScrollPane registeredUsers_scroller;

	Client cl;

    FriendRequestDialog(Client cl)
    {
		this.cl = cl;

		Container cp = getContentPane();

		registeredUsers = new DefaultListModel();
		registeredUsers_view = new JList(registeredUsers);
		registeredUsers_view.addListSelectionListener(this);
		registeredUsers_view.addMouseListener(this);
		registeredUsers_scroller = new JScrollPane(registeredUsers_view);

		send = new JButton("Send Friend Request");
		send.addActionListener(this);
		send.setActionCommand("SEND");
		send.setEnabled(false);
		getRootPane().setDefaultButton(send);

		exit = new JButton("Exit");
		exit.addActionListener(this);
		exit.setActionCommand("EXIT");

		buttonPanel = new JPanel();
		buttonPanel.add(send);
		buttonPanel.add(exit);

		cp.add(registeredUsers_scroller);
		cp.add(buttonPanel, BorderLayout.SOUTH);

		setupFrientRequestDialog();
    }

	public void setupFrientRequestDialog()
	{
		Toolkit tk;

		Dimension d;

		tk = Toolkit.getDefaultToolkit();

		d = tk.getScreenSize();

		setSize(d.width/2, d.height/2);

		setLocation(d.width/4, d.height/4);

		setTitle("Friend Request Dialog");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setVisible(true);
    }

    public void setDefaultListModel(DefaultListModel registeredUsersListModel)
    {
		Object[] users = registeredUsersListModel.toArray();

		for (Object x : users)
		{
			System.out.println("FriendRequestDialog: " + x.toString());

			if (!cl.friendTable.containsKey(x.toString()))
				registeredUsers.addElement((String)x);
		}

		registeredUsers_view.updateUI();
	}

    public void addRegisteredUsers()//TESTING CODE
    {
		for (int x = 50; x > 0; x--)
			registeredUsers.addElement("asdfasdf");
	}

    public void send()
    {
		System.out.println("FriendRequestDialog:(" + cl.username + "): SENT FRIEND REQUEST");

		int index = registeredUsers_view.getSelectedIndex();

		if (index != -1)
		{
			String friendToAdd = registeredUsers.elementAt(index);

			cl.sendFriendRequest(friendToAdd);
		}

		else
			System.out.println("FAILED TO GET CORRECT INDEX WHEN GETTING INDEX OF FRIEND TO ADD");
    }

    public void exit()
    {
		System.out.println("FriendRequestDialog: EXIT");

		this.dispose();
    }

    public void valueChanged(ListSelectionEvent e)
    {
		send.setEnabled(registeredUsers_view.getSelectedIndex() != -1);
	}

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		try
		{
			if (cmd.equals("SEND"))
			{
				send();
			}

        	else if (cmd.equals("EXIT"))
        	{
            	exit();
			}
		}

		catch (Exception x)
		{
			x.printStackTrace();

			System.out.println("FriendRequestDialog: actionPerformed(): Exception");
		}
	}

	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 2)
		{
			System.out.println("detected double click");

			int index = registeredUsers_view.getSelectedIndex();

			if (index != -1)
			{
				String friendToAdd = registeredUsers.elementAt(index);

				cl.sendFriendRequest(friendToAdd);
			}

			else
				System.out.println("FAILED TO GET CORRECT INDEX WHEN GETTING INDEX OF FRIEND TO ADD");
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

	public static void main(String[] x)
	{
		new FriendRequestDialog(new Client());
	}
}