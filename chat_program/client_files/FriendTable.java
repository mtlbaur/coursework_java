import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;

class FriendTable extends Hashtable<String, Friend>
{
	Client cl;

	FriendTable(Client cl)
	{
		this.cl = cl;
	}

	FriendTable(Client cl, Vector<String> friendUsernames, Vector<String> friendStatus)
	{
		this.cl = cl;

		for (String username : friendUsernames)
			for (String status : friendStatus)
				this.put(username, new Friend(cl, username, status));
	}

	public void load(Vector<String> friendUsernames, Vector<String> friendStatus)
	{
		for (String username : friendUsernames)
			for (String status : friendStatus)
				this.put(username, new Friend(cl, username, status));
	}

	public void add(Friend newFriend)
	{
		this.put(newFriend.username, newFriend);
	}
}