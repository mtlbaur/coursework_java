import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;

class UserTable extends Hashtable<String, User>
{
	DataInputStream dis;
	DataOutputStream dos;

	int numUsers;

	File users;

	UserTable()
	{
		try
		{
			users = new File("broadcaster_users.dat");

			if (!users.exists())
			{
				users.createNewFile();

				numUsers = 0;
			}

			else
			{
				dis = new DataInputStream(new FileInputStream(users));

				numUsers = dis.readInt();

				System.out.println("HASHTABLE: numUsers = " + numUsers);

				if (numUsers != 0)
				{
					for (int x = 0; x < numUsers; x++)
					{
						System.out.println("CONSTRUCTING USER FOR USERTABLE");

						User u = new User(dis);

						this.put(u.username, u);
					}
				}
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();

			System.out.println("Error while constructing UserTable.");
		}
	}

	public void store()
	{
		try
		{
			File users = new File("broadcaster_users.dat");

			//if (!users.exists())
			//{
				users.createNewFile();
			//}

			dos = new DataOutputStream(new FileOutputStream(users));

			dos.writeInt(this.size());

			Enumeration<User> userList = this.elements();

			while (userList.hasMoreElements())
			{
				userList.nextElement().store(dos);
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();

			System.out.println("Error while saving UserTable.");
		}
	}
}