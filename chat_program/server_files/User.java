import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;

class User
{
	public String username;
	public String password;

	ConnectionToClient ctc;

	public Vector<String> friends;

	public Vector<String> msgBuffer;

	User(String username, String password)
	{
		this.username = username;
		this.password = password;
		friends = new Vector();
		msgBuffer = new Vector();
	}

	User(String username, String password, ConnectionToClient ctc)
	{
		this.username = username;
		this.password = password;
		this.ctc = ctc;
		friends = new Vector();
		msgBuffer = new Vector();
	}

	User(String username, String password, ConnectionToClient ctc, Vector friends)
	{
		this.username = username;
		this.password = password;
		this.ctc = ctc;
		this.friends = friends;
		msgBuffer = new Vector();
	}

	User(DataInputStream dis)
	{
		try
		{
			username = new String(dis.readUTF());
			password = new String(dis.readUTF());

			friends = new Vector();

			msgBuffer = new Vector();

			while (true)
			{
				String friend = dis.readUTF();

				if (!friend.equals("user_info_end"))
				{
					System.out.println(username + " adding friend: " + friend);

					friends.add(friend);
				}

				else
					break;
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();

			System.out.println("Error while constructing User from DataInputStream");
		}
	}

	public void store(DataOutputStream dos)
	{
		try
		{
			dos.writeUTF(username);
			dos.writeUTF(password);

			System.out.println(username + "'s friends's size(): " + friends.size());

			if (friends.size() != 0)
			{
				for (String friend : friends)
				{
					System.out.println("storing friend");

					dos.writeUTF(friend);
				}
			}

			dos.writeUTF("user_info_end");
		}

		catch (Exception e)
		{
			e.printStackTrace();

			System.out.println("Error saving User to DataOutputStream");
		}
	}
}