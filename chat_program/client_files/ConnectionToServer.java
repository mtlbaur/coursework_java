import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;

class ConnectionToServer
			implements Runnable
{
	Socket clientSocket;
	String domain = "127.0.0.1";
	int port = 7777;
	String username;
	String password;
	String type;

	Talker t;

	Client cl;

	FileReceiver fr;

	ConnectionToServer(Client cl, String username, String password, String type)
	{
		this.cl = cl;
		this.username = username;
		this.password = password;
		this.type = type;

		try
		{
			clientSocket = new Socket(domain, port);

			t = new Talker(clientSocket, username);

			new Thread(this).start();
		}

		catch (IOException e)
		{
			System.out.println("ConnectionToServer(): IOException");

			cl.actionPerformed(new ActionEvent(this, 0 , "SERVER_UNAVAILABLE"));
		}
	}

	public void send(String msg)
	{
		System.out.println("CTS: send()");

		try
		{
			t.send("!SEND:" + " " + msg);
		}

		catch (Exception e)
		{
			cl.actionPerformed(new ActionEvent(this, 0 , "CONNECTION_FAILED"));
		}
	}

	public void sendExactString(String exactMsg)
	{
		System.out.println("SENT EXACT STRING TO SERVER");

		try
		{
			t.send(exactMsg);
		}

		catch (IOException e)
		{
			System.out.println("EXCEPTION WHILE SENDING EXACT MESSAGE TO SERVER");
		}
	}

	public void logout()
	{
		try
		{
			t.send("!LOGOUT:" + " " + username);
		}

		catch (Exception e)
		{
			cl.actionPerformed(new ActionEvent(this, 0 , "CONNECTION_FAILED"));
		}
	}

	public void run()
	{
		// type is either LOGIN or REGISTER

		System.out.println("ConnectionToServer: RUN");

		boolean failure = false;

		try
		{
			if (type.equals("REGISTER"))
			{
				t.send("!REGISTER:" + " " + username + " " + password);

				System.out.println("MSG: " + "!REGISTER:" + " " + username + " " + password);

				String msg = t.receive();

				System.out.println("GOT PAST BLOCK");
				System.out.println(msg);

				if (msg.equals("REGISTER_FAILED"))
				{
					System.out.println("CLIENT REGISTER_FAILED");

					failure = true;

					cl.actionPerformed(new ActionEvent(this, 0 , "REGISTER_FAILED"));
				}

				else if (msg.equals("REGISTER_SUCCEEDED"))
				{
					System.out.println("CLIENT REGISTER_SUCCEEDED");

					cl.actionPerformed(new ActionEvent(this, 0 , "REGISTER_SUCCEEDED"));
				}

				else
				{
					System.out.println("CLIENT REGISTER_FAILED");

					failure = true;

					cl.actionPerformed(new ActionEvent(this, 0 , "REGISTER_CONNECTION_FAILED"));
				}
			}

			else if (type.equals("LOGIN"))
			{
				t.send("!LOGIN:" + " " + username + " " + password);

				String msg = t.receive();

				if (msg.equals("LOGIN_FAILED"))
				{
					System.out.println("CLIENT LOGIN_FAILED");

					failure = true;

					cl.actionPerformed(new ActionEvent(this, 0 , "LOGIN_FAILED"));
				}

				else if (msg.equals("LOGIN_SUCCEEDED"))
				{
					System.out.println("CLIENT LOGIN_SUCCEEDED");

					boolean loginFailed = false;

					t.send("REQUEST_FOR_FRIENDLIST");

					msg = t.receive();

					if (msg.startsWith("!FRIENDLIST:"))
					{
						String friendList[] = msg.split("/");

						System.out.println("TESTING RECEIVED FRIENDLIST RESULTS");

						for (String x : friendList)
							System.out.println(x);

						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								for (int x = 1; x < friendList.length; x = x + 2)
								{
									String name = friendList[x].substring(10);
									int y = x;
									y++;
									String status = friendList[y].substring(8);
									System.out.println("STATUS:" + status);

									cl.addFriend(name, status);
								}
							}
						});
					}

					else if (msg.equals("NO_FRIENDS"))
					{
						System.out.println(username + " HAS NO FRIENDS");
					}

					else
					{
						loginFailed = true;

						cl.actionPerformed(new ActionEvent(this, 0 , "LOGIN_CONNECTION_FAILED"));
					}

					if (!loginFailed)
						t.send("REQUEST_FOR_BUFFERED_MSGS");

					if (!loginFailed)
						cl.actionPerformed(new ActionEvent(this, 0 , "LOGIN_SUCCEEDED"));
				}

				else
				{
					System.out.println("CLIENT LOGIN_FAILED");

					failure = true;

					cl.actionPerformed(new ActionEvent(this, 0 , "LOGIN_CONNECTION_FAILED"));
				}
			}

			else
			{
				failure = true;

				cl.actionPerformed(new ActionEvent(this, 0 , "CONNECTION_FAILED"));
			}
		}

		catch (Exception e)
		{
			System.out.println("ConnectionToClient exception while trying to register/login");

			failure = true;

			cl.actionPerformed(new ActionEvent(this, 0 , "CONNECTION_FAILED"));
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

						for (String x : parts)
							System.out.println(x);

						if (parts.length == 4)
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									cl.printToChatDialog(parts[1], parts[3]);
								}
							});
						}

						else
						{
							System.out.println("CLIENT GOT MESSED UP MESSAGE");
						}
					}

					else if (msg.startsWith("!REQUEST_FRIEND:"))
					{
						System.out.println("CLIENT GOT FRIEND REQUEST");

						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								String parts[] = msg.split(" ");

								int decision = JOptionPane.showConfirmDialog(cl, "Accept friend request from: " + parts[1] + "?", "Friend Request", JOptionPane.YES_NO_OPTION);

								if (decision == 0)
								{
									boolean failure = false;

									try
									{
										t.send("!ACCEPTED_FRIEND: " + parts[1] + " " + username);
									}

									catch (Exception e)
									{
										failure = true;
									}

									if (!failure)
										cl.addFriend(parts[1], "1"); // MIGHT NOT ACTUALLY BE ONLINE
								}


								else
									System.out.println(cl.username + " denied friend request from: " + parts[1]);
							}
						});
					}

					else if (msg.startsWith("!ALREADY_FRIENDS:"))
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								String parts[] = msg.split(" ");

								JOptionPane.showMessageDialog(cl, "Already friends with " + parts[1] + "!", "Friend Request", JOptionPane.INFORMATION_MESSAGE);
							}
						});
					}

					else if (msg.startsWith("!REGISTERED_USERS:"))
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								String registeredUsers[] = msg.split("/");

								System.out.println("TESTING RECEIVED REGISTERED USERS");

								// ADD REGISTERED USERS TO DEFAULTLISTMODEL OF REG. USERS

								DefaultListModel<String> registeredUsersListModel = new DefaultListModel();

								//for (String y : registeredUsers)
								for (int x = 1; x < registeredUsers.length; x++)
								{
									String y = registeredUsers[x];
									System.out.println(y);

									if (!username.equals(y))
										registeredUsersListModel.addElement(y);
								}

								cl.setupFriendRequestDialog(registeredUsersListModel);
							}
						});
					}

					else if (msg.startsWith("!ACCEPTED_FRIEND:"))
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								String parts[] = msg.split(" ");

								cl.addFriend(parts[1], "1");
							}
						});
					}

					else if (msg.startsWith("!UPDATE_FRIEND_STATUS:"))
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								String parts[] = msg.split(" ");

								System.out.println(cl.username + " IS UPDATING STATUS OF FRIEND: " + parts[1] + " with new status of: " + parts[2]);

								cl.updateFriendStatus(parts[1], parts[2]);
							}
						});
					}

					else if (msg.startsWith("!REQUEST_FILE_TRANSFER:"))
					{
						String[] parts = msg.split("/");

						for (String x : parts)
							System.out.println(x);

						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								int decision = JOptionPane.showConfirmDialog(cl, "Accept file transfer request from: " + parts[3] + "?" + "\n" + "File Name: " + parts[1] + "\n" + "File Size: " + parts[2], "File Transfer Request", JOptionPane.YES_NO_OPTION);

								boolean failure = false;

								if (decision == 0)
								{
									try
									{
										fr = new FileReceiver("127.0.0.1", 7778, parts[1], Long.parseLong(parts[2]));

										t.send("!ACCEPTED_FILE_TRANSFER:/" + parts[1] + "/" + parts[2] + "/127.0.0.1" + "/7778/" + username + "/" + parts[3]);
									}

									catch (Exception e)
									{
										failure = true;
									}
								}


								else
									System.out.println(cl.username + " denied file transfer request from: " + parts[3]);
							}
						});
					}

					else if (msg.startsWith("!ACCEPTED_FILE_TRANSFER:"))
					{
						String[] parts = msg.split("/");

						for (String x : parts)
							System.out.println(x);

						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								try
								{
									Socket s = new Socket(parts[3], 7778);
									FileInputStream fis;
									DataOutputStream dos;

									byte bytes[] = new byte[256];
									fis = new FileInputStream(cl.fileToTransfer);
									dos = new DataOutputStream(s.getOutputStream());

									boolean eof = false;

									while (!eof)
									{
										fis.read(bytes);

										dos.write(bytes, 0, bytes.length);

										if (bytes.length < 256)
											eof = true;
									}

									dos.close();
									fis.close();

									s.close();
								}

								catch (Exception e)
								{
									e.printStackTrace();

									System.out.println("possibly normal exception while transferring file");
								}
							}
						});
					}
				}
			}

			catch (Exception e)
			{
				System.out.println("ConnectionToClient: run(): Exception");

				cl.actionPerformed(new ActionEvent(this, 0 , "CONNECTION_FAILED"));
			}
		}
	}
}