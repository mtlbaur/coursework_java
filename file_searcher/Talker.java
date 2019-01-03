import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;
import javax.mail.*;
import javax.mail.internet.*;

class Talker
{
	private DataOutputStream dos;
	private BufferedReader reader;
	private Socket clientSocket;
	String id;
	String clientId;

	Talker (Socket clientSocket, String id) throws IOException // client side
	{
		this.id = id;

		reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		dos = new DataOutputStream(clientSocket.getOutputStream());
	}

	Talker (String domain, int port, String id) throws IOException // server side
	{
		this.id = id;

		ServerSocket serverSocket = new ServerSocket(port);
		clientSocket = serverSocket.accept();
		reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		dos = new DataOutputStream(clientSocket.getOutputStream());
	}

	public void setClientId(String clientId) throws IOException // server side
	{
		//System.out.println("!ID: " + id);
		//dos.writeBytes("!ID:" + id);
		System.out.println("Talker: Trying to set client ID on Server");
		this.clientId = clientId;
	}

	public void sendClientId() throws IOException // client side
	{
		System.out.println("Talker: Trying to send client ID from Client");
		dos.writeBytes("!ID:" + id + "\n");
	}

	public void send(String msg) throws IOException
	{
		dos.writeBytes(msg + "\n");
		System.out.println(id + " Sent >>" + msg + "<<");
	}

	String receive() throws IOException
	{
		String msg = reader.readLine();
		System.out.println("Talker: " + id + " Received >>" + msg + "<<");
		return msg;
	}

	void expect(String strExpected) throws Exception, IOException
	{
		String strE = strExpected;

		String strR = receive();

		System.out.println("EXPECT: strE: " + strE);

		System.out.println("RECEIVE: strR: " + strR);

		if (!strE.equals(strR))
			throw new Exception("Expected Msg: " + strE+ " | Received Msg: " + strR);
	}
}