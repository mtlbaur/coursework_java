import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.event.*;
import javax.mail.*;
import javax.mail.internet.*;
import com.sun.mail.imap.*;

class Controller
	implements ActionListener
{
	public static final String authSenderDomain = "smtp.gmx.com";
	public static final String portNumStr = "465"; // Alternative port # for some servers: 25
 	public static final String authSenderUsername = "_______@gmx.com"; // Some gmx email
 	public static final String authSenderPassword = "_______"; // Some password
	public static final String sentFrom = authSenderUsername;
	public static final String sentDate = new Date().toString();
	public static final String content = "";

	Properties props;
	File emailList;

	SettingsFrame settings;
	PasswordHandler pHandler;

	Session session;
	Message message;

	Controller()
	{
		props = new Properties();

		pHandler = new PasswordHandler(authSenderUsername, authSenderPassword);

		props = new Properties();
		props.put("mail.smtp.host", authSenderDomain);
		props.put("mail.smtp.socketFactory.port", portNumStr);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", portNumStr);
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.trasport.protocol", "smtps");
		props.setProperty("mail.user", authSenderUsername);
		props.setProperty("password", authSenderPassword);
		props.setProperty("sent_from", sentFrom);
		props.setProperty("sent_date", sentDate);

		session = Session.getDefaultInstance(props, pHandler);

		emailList = new File("emails.txt");

		if (emailList.exists() && emailList.canRead())
		{
			System.out.println("Controller: emailList exists and is readable");

			settings = new SettingsFrame(props, this);
		}

		else
		{
			JOptionPane.showMessageDialog(null, "\"emails.txt\" could not be found or could not be read.", "Error", JOptionPane.ERROR_MESSAGE);

			this.actionPerformed(new ActionEvent(this, 0 , "EXIT"));
		}
	}

	public void send()
	{
		System.out.println("FRAME: SEND");

		System.out.println(props.getProperty("mail.smtp.host"));
		System.out.println(props.getProperty("mail.smtp.socketFactory.port"));
		System.out.println(props.getProperty("mail.user"));
		System.out.println(props.getProperty("password"));
		System.out.println(props.getProperty("sent_from"));
		System.out.println(props.getProperty("sent_date"));
		System.out.println(props.getProperty("subject"));
		System.out.println(props.getProperty("content"));

		// Initial test
		try
		{
			message = new MimeMessage(session);
			message.setFrom(new InternetAddress(props.getProperty("sent_from")));
			message.setSubject(props.getProperty("subject"));
			message.setText(props.getProperty("content"));
			message.setSentDate(new Date(props.getProperty("sent_date"))); // new GregorianCalendar(2010, 1, 1).getTime()
		}

		catch (MessagingException e)
		{
			System.out.println("Controller: MessagingException");

			JOptionPane.showMessageDialog(null, "Message(s) could not be send.", "Error", JOptionPane.ERROR_MESSAGE);

			this.actionPerformed(new ActionEvent(this, 0 , "EXIT"));
		}

		catch (IllegalArgumentException lae)
		{
			System.out.println("Controller: IllegalArgumentException");

			JOptionPane.showMessageDialog(null, "Message properties could not be set.", "Error", JOptionPane.ERROR_MESSAGE);

			this.actionPerformed(new ActionEvent(this, 0 , "EXIT"));
		}

		catch (Exception q)
		{
			System.out.println("Some other exception.");

			JOptionPane.showMessageDialog(null, "Exception in Controller's send().", "Error", JOptionPane.ERROR_MESSAGE);

			this.actionPerformed(new ActionEvent(this, 0 , "EXIT"));
		}

		Vector<String> addresses = new Vector();

		try
		{
			BufferedReader br = new BufferedReader(new FileReader("emails.txt"));

			while(br.ready())
			{
				try
				{
					addresses.add(br.readLine().trim());
				}

				catch (Exception e)
				{
					break;
				}
			}
		}

		catch (Exception e)
		{
			System.out.println("Controller: \"emails.txt\" could not be read.");

			JOptionPane.showMessageDialog(null, "\"emails.txt\" could not be read.", "Error", JOptionPane.ERROR_MESSAGE);

			this.actionPerformed(new ActionEvent(this, 0 , "EXIT"));
		}

		File readableAddresses = new File("readable_addresses.txt");
		File unreadableAddresses = new File("unreadable_addresses.txt");

		PrintWriter pw;

		try
		{
			pw = new PrintWriter(readableAddresses);
			//pw.println("");
			pw.close();
			pw = new PrintWriter(unreadableAddresses);
			//pw.println("");
			pw.close();
		}

		catch (Exception x)
		{
			System.out.println("readable/unreadable addresses files could not be cleared.");

			JOptionPane.showMessageDialog(null, "readable/unreadable addresses files could not be cleared.", "Error", JOptionPane.ERROR_MESSAGE);

			this.actionPerformed(new ActionEvent(this, 0 , "EXIT"));
		}

		for(String address : addresses)
		{
			boolean goodAddress = true;

			try
			{
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address));

				Transport.send(message);

				System.out.println("Controller: Sent message.");
			}

			catch (MessagingException me)
			{
				goodAddress = false;

				try
				{
					System.out.println("unreadableAddress: " + address);

					me.printStackTrace();

					pw = new PrintWriter(new FileWriter(unreadableAddresses, true));

					pw.println(address);

					pw.close();
				}

				catch (Exception x)
				{
					System.out.println("Controller: unreadableAddresses file could not be processed.");
				}
			}

			if (goodAddress)
			{
				try
				{
					System.out.println("readableAddress: " + address);

					pw = new PrintWriter(new FileWriter(readableAddresses, true));

					pw.println(address);

					pw.close();
				}

				catch (Exception x)
				{
					System.out.println("Controller: unreadableAddresses file could not be processed.");
				}
			}
		}
	}

    public void actionPerformed (ActionEvent e)
    {
		String cmd = e.getActionCommand();

		if (cmd.equals("EXIT"))
		{
			System.out.println("EXIT");
			System.exit(0);
		}

		else if (cmd.equals("SEND"))
		send();
	}
}