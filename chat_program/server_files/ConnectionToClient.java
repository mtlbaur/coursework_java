import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;

class ConnectionToClient
			implements Runnable
{
	Talker t;

	String serverId;
	String clientId;

	Server sv;

	ConnectionToClient(Server sv, String serverId, Socket clientSocket)
	{
		this.sv = sv;

		this.serverId = serverId;

		try
		{
			t = new Talker(clientSocket, serverId);

			new Thread(this).start();
		}

		catch (IOException e)
		{
			System.out.println("ConnectionToClient(): IOException");

			System.exit(0);
		}
	}

	public void sendExactString(String exactMsg)
	{
		System.out.println("SENT EXACT STRING TO CLIENT");

		try
		{
			exactMsg.replaceAll("/", "");

			t.send(exactMsg);
		}

		catch (IOException e)
		{
			System.out.println("EXCEPTION WHILE SENDING EXACT MESSAGE TO CLIENT");
		}
	}

	public void send(String msg, String sender)
	{
		System.out.println("SENT MESSAGE TO CLIENT");

		try
		{
			msg.replaceAll("/", "");

			t.send("!MESSAGE:" + "/" + msg + "/" + "!SENDER:" + "/" + sender);
		}

		catch (IOException e)
		{
			System.out.println("EXCEPTION WHILE SENDING MESSAGE TO CLIENT");
		}
	}

	public void run()
	{
		System.out.println("ConnectionToClient: RUN");

		boolean failure = false;

		boolean register = false;

		try
		{
			String msg = t.receive();

			System.out.println("MSG: " + msg);

			String[] parts;

			if (msg.startsWith("!REGISTER:"))
			{
				register = true;

				parts = msg.split(" ");

				if (parts.length == 3)
				{
					if (sv.userTable.containsKey(parts[1]))
					{
						System.out.println("ALREADY REGISTERED");

						failure = true;

						t.send("REGISTER_FAILED");
					}

					else
					{
						clientId = parts[1];

						User u = new User(parts[1], parts[2], this);

						sv.userTable.put(u.username, u);

						System.out.println("storing userTable");

						sv.userTable.store();

						sv.updateStatus(u, "1");

						t.send("REGISTER_SUCCEEDED");
					}
				}

				else
				{
					failure = true;

					t.send("REGISTER_FAILED");
				}
			}

			else if (msg.startsWith("!LOGIN:"))
			{
				parts = msg.split(" ");

				if (parts.length == 3)
				{
					System.out.println("parts[1]: " + parts[1]);
					System.out.println("parts[2]: " + parts[2]);

					User temp = sv.userTable.get(parts[1]);

					System.out.println("temp.username: " + temp.username);
					System.out.println("temp.password: " + temp.password);


					if (temp.username.equals(parts[1]) && temp.password.equals(parts[2]))
					{
						boolean alreadyLoggedIn = false;

						if (temp.ctc != null)
						{
							System.out.println("ALREADY LOGGED IN");
							alreadyLoggedIn = true;

							t.send("LOGIN_FAILED");
						}

						if (!alreadyLoggedIn)
						{
							clientId = temp.username;

							temp.ctc = this;

							t.send("LOGIN_SUCCEEDED");

							msg = t.receive();

							System.out.println(msg);

							if (msg.equals("REQUEST_FOR_FRIENDLIST"))
								sv.sendUserFriends(temp);

							else
								throw new Exception("FAILED TO SEND FRIENDLIST");

							sv.updateStatus(temp, "1");

							msg = t.receive();

							System.out.println(msg);

							if (msg.equals("REQUEST_FOR_BUFFERED_MSGS"))
							{
								if (temp.msgBuffer.size() != 0)
								{
									for (String x : temp.msgBuffer)
									{
										System.out.println("BUFFERED MSG SENT ON LOGIN: " + x);

										sendExactString(x);
									}

									temp.msgBuffer.removeAllElements();
								}
							}

							else
								throw new Exception("FAILED TO SEND BUFFERED MSGS");
						}
					}

					else
					{
						failure = true;

						t.send("LOGIN_FAILED");
					}
				}

				else
				{
					failure = true;

					t.send("LOGIN_FAILED");
				}
			}

			else
			{
				System.out.println("ConnectionToClient: run(): Initial message did not start correctly");

				failure = true;
			}
		}

		catch (Exception e)
		{
			System.out.println("ConnectionToClient: run(): Exception");

			e.printStackTrace();

			failure = true;

			try
			{
				t.send("LOGIN_FAILED");
			}

			catch (Exception y)
			{
				System.out.println("FAILED TO LOGIN AND FAILED TO SEND INFO MESSAGE TO CLIENT");
			}

			if (clientId != "" && register)
			{
				try
				{
					System.out.println("ATTEMPTING TO REMOVE CLIENT FROM HASHTABLE AFTER FAILED REGISTER");

					sv.userTable.remove(clientId);

					sv.userTable.store();
				}

				catch (Exception x)
				{
					e.printStackTrace();

					System.out.println("EXCEPTION WHILE REMOVING CLIENT FROM HASHTABLE AFTER FAILED REGISTER");
				}
			}
		}

		if (!failure)
		{
			try
			{
				while (true)
				{
					String msg = t.receive();

					if (msg.startsWith("!MESSAGE:"))
					{
						String parts[] = msg.split("/");

						System.out.println("TRYING TO CALL SERVER'S SEND()");

						sv.send(parts[1], parts[2], parts[3]);
					}

					else if (msg.startsWith("!LOGOUT:"))
						throw new Exception(clientId + " LOGGED OUT");


					else if (msg.equals("REQUEST_FOR_REGISTERED_USERS"))
								sv.sendRegisteredUsers(clientId);

					else if (msg.startsWith("!REQUEST_FRIEND:"))
					{
						System.out.println("CTC TRYING TO RESPOND TO FRIEND REQUEST");

						String[] parts = msg.split(" ");

						String friendToAdd = parts[1];
						String friendRequester = parts[2];

						System.out.println("CTC:friendToAdd: " + friendToAdd);
						System.out.println("CTC:friendRequester: " + friendRequester);

						if (!friendToAdd.equals(friendRequester))
						{
							System.out.println("CTC: IF NOT REQUESTING SELF AS FRIEND");

							User y = sv.userTable.get(friendToAdd);

							boolean alreadyFriends = false;

							for (String friendName : y.friends)
								if (friendName.equals(friendRequester))
									alreadyFriends = true;

							if (!alreadyFriends)
							{
								if (y.ctc != null)
								{
									y.ctc.sendExactString("!REQUEST_FRIEND:" + " " + friendRequester);
								}

								else
									y.msgBuffer.addElement("!REQUEST_FRIEND:" + " " + friendRequester);
							}

							else
							{
								y = sv.userTable.get(friendRequester);

								y.ctc.sendExactString("!ALREADY_FRIENDS: " + friendToAdd);
							}
						}
					}

					else if (msg.startsWith("!ACCEPTED_FRIEND:"))
					{
						System.out.println("CTC TRYING TO ACCEPT TO FRIEND REQUEST");

						String[] parts = msg.split(" ");

						User y = sv.userTable.get(parts[1]);

						y.friends.addElement(parts[2]);

						if (y.ctc != null)
						{
							y.ctc.sendExactString("!ACCEPTED_FRIEND: " + parts[2]);
						}

						else
							y.msgBuffer.addElement("!ACCEPTED_FRIEND: " + parts[2]);

						y = sv.userTable.get(parts[2]);

						y.friends.addElement(parts[1]);

						System.out.println("storing userTable");

						sv.userTable.store();
					}

					else if (msg.startsWith("!REQUEST_FILE_TRANSFER:"))
					{
						System.out.println("CTC HANDLING FILE TRANSFER REQUEST");

						String[] parts = msg.split("/");

						User y = sv.userTable.get(parts[3]);

						if (y.ctc != null)
						{
							y.ctc.sendExactString("!REQUEST_FILE_TRANSFER:/" + parts[1] + "/" + parts[2] + "/" + parts[4]);
						}

						else
						{
							System.out.println("store in buffer");

							y.msgBuffer.addElement("!REQUEST_FILE_TRANSFER:/" + parts[1] + "/" + parts[2] + "/" + parts[4]);
						}
					}

					else if (msg.startsWith("!ACCEPTED_FILE_TRANSFER:"))
					{
						String[] parts = msg.split("/");

						User y = sv.userTable.get(parts[6]);

						if (y.ctc != null)
						{
							y.ctc.sendExactString("!ACCEPTED_FILE_TRANSFER:/" + parts[1] + "/" + parts[2] + "/" + parts[3]+ "/" + parts[4] + "/" + parts[5]);
						}

						else
						{
							System.out.println("store in buffer");

							y.msgBuffer.addElement("!ACCEPTED_FILE_TRANSFER:/" + parts[1] + "/" + parts[2] + "/" + parts[3]+ "/" + parts[4] + "/" + parts[5]);
						}
					}
				}
			}

			catch (Exception e)
			{
				e.printStackTrace();

				System.out.println("ConnectionToClient: run(): Exception");

				User y = sv.userTable.get(clientId);

				System.out.println("LOGOUT: " + clientId);

				y.ctc = null;

				sv.updateStatus(y, "0");

				System.out.println("storing userTable");

				sv.userTable.store();
			}
		}
	}
}