import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;

class Friend
{
	public String username;

	public String status;

	ChatDialog cd;

	Client cl;

	Friend(Client cl, String username, String status)
	{
		this.cl = cl;
		this.username = username;
		this.status = status;
	}

	public void constructChatDialog()
	{
		cd = new ChatDialog(cl, this, username);
	}

	public void disposeChatDialog()
	{
		if (cd != null)
			cd.exit();
	}

	public String toString()
	{
		return this.username;
	}
}