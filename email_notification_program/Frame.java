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

class Frame
	implements ActionListener, ItemListener
{
	String host;
	String username;
	String password;
	String protocolProvider = "imaps";

	Properties props;
	File defaults;
	FileInputStream in;
	FileOutputStream out;

	Settings settings;
	javax.swing.Timer timer;

	Session session;
	Store store;
	Folder inboxFolder;
	Message[] messageList;

	TrayIcon trayIcon;
	SystemTray tray;
	PopupMenu popupMenu;

	boolean initialSettings;

	boolean dialogExists;

	PasswordHandler pHandler;

	Settings settingsPointer;

	NotificationDialog nd;

	CheckboxMenuItem cbMenuItem;

	Frame()
	{
		props = new Properties();

		defaults = new File("email_notifier_defaults.txt");

		initialSettings = true;

		dialogExists = false;

		settingsPointer = null;

		if (defaults.exists())
		{
			System.out.println("defaults exists");

			try
			{
				in = new FileInputStream(defaults);
				props.load(in);
				in.close();
			}

			catch (IOException e)
			{
				System.out.println("Frame's \"props\" variable failed to load defaults.");
			}
		}

		else
		{
			settingsPointer = settings;

			settings = new Settings(props, defaults, this);
		}

		try
		{
			pHandler = new PasswordHandler(props);

			session = Session.getDefaultInstance(props, pHandler);

			store = session.getStore(protocolProvider);

			store.connect();

			System.out.println("Number of folders: " + store.getPersonalNamespaces().length);

			inboxFolder = store.getFolder("INBOX");

			checkForNewMessage(inboxFolder);

			inboxFolder.open(Folder.READ_WRITE);

			inboxFolder.close(false);

			store.close();
		}

		catch (NoSuchProviderException nspe)
		{
			System.out.println("No such email protocol provider: " + protocolProvider);
		}

		catch (MessagingException me)
		{
			System.out.println("Messaging Exception: ");
			me.printStackTrace();
		}

		catch (Exception x)
		{
			System.out.println("Exception in constructor");
		}

		MenuItem menuItem;

		popupMenu = new PopupMenu();
		menuItem = new MenuItem("Settings");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("SETTINGS");
		popupMenu.add(menuItem);

		boolean boolean_result = Boolean.valueOf(props.getProperty("soundCB"));

		cbMenuItem = new CheckboxMenuItem("Sound", boolean_result);
		cbMenuItem.addItemListener(this);
		popupMenu.add(cbMenuItem);

		menuItem = new MenuItem("Exit");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("EXIT");
		popupMenu.add(menuItem);

		if (SystemTray.isSupported())
		{
			tray = SystemTray.getSystemTray();
			trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("220px-Crateva_religiosa.jpg"), "Email Notifier");
			trayIcon.setImageAutoSize(true);
			trayIcon.setPopupMenu(popupMenu);

			try
			{
				tray.add(trayIcon);
			}

			catch (AWTException e)
			{
				System.out.println("TrayIcon could not be added.");
				return;
			}
		}

		initialSettings = false;

		timer = new javax.swing.Timer(Integer.parseInt(props.getProperty("updateRate"))*1000, this);
		timer.setActionCommand("CHECK");
		timer.start();
	}


	public void checkForNewMessage(Folder mailboxFolder) throws MessagingException
	{
		if (mailboxFolder.hasNewMessages())
		{
			int newMsgCount = mailboxFolder.getNewMessageCount();

			if (newMsgCount != -1)
			{
				System.out.println("New messages!");

				if (props.getProperty("soundCB") == "true")
					new NotificationSound().notifyUser();


				if (dialogExists == true)
				{
					nd.dispose();
					nd = new NotificationDialog(mailboxFolder, this);
				}

				else
				{
					dialogExists = true;
					nd = new NotificationDialog(mailboxFolder, this);
				}
			}

			else
				System.out.println("Number of new messages could not be obtained.");
		}

		System.out.println("Number of messages: " + mailboxFolder.getMessageCount());
		System.out.println("Number of new messages: " + mailboxFolder.getNewMessageCount());
		System.out.println("Number of unread messages: " + mailboxFolder.getUnreadMessageCount());
		System.out.println("=============================================================================");
	}

	public void itemStateChanged(ItemEvent e)
	{
		this.actionPerformed(new ActionEvent(this, 0, "SOUND"));
	}

    public void actionPerformed (ActionEvent e)
    {
		String cmd = e.getActionCommand();

		if (cmd.equals("EXIT"))
		{
			System.out.println("EXIT");
			System.exit(0);
		}

		else if (cmd.equals("SAVE"))
		{
			settingsPointer = null;

			if (!initialSettings)
			{
				System.out.println("NOT INITIAL SETTINGS");
				boolean boolean_result = Boolean.valueOf(props.getProperty("soundCB"));
				cbMenuItem.setState(boolean_result);
			}
		}

		else if (cmd.equals("CANCEL"))
		{
			if (initialSettings == true)
			{
				System.out.println("EXIT");
				System.exit(0);
			}

			else
			{
				System.out.println("NOT EXIT");
				settingsPointer = null;
			}
		}

		else if (cmd.equals("SETTINGS"))
		{
			System.out.println("SETTINGS");
			if (settingsPointer == null)
			{
				timer.stop();
				settingsPointer = settings;
				settings = new Settings(props, defaults, this);
				timer.start();
			}

			else
				settings.toFront();
		}

		else if (cmd.equals("SOUND"))
		{
			System.out.println("SOUND");

			boolean boolean_result = Boolean.valueOf(props.getProperty("soundCB"));
			boolean_result = !boolean_result;
			String string_result = String.valueOf(boolean_result);
			System.out.println(string_result);
			props.setProperty("soundCB", string_result);

			cbMenuItem.setState(boolean_result);

			try
			{
				FileOutputStream out = new FileOutputStream(defaults);

				props.store(out, "Defaults");

				out.close();
			}

			catch (IOException ioe)
			{
				System.out.println("SOUND: Failed to store updated props.");
			}
		}

		else if (cmd.equals("DIALOG_CLOSED"))
		{
			System.out.println("CLOSE IT");

			dialogExists = false;
		}

		else if (cmd.equals("CHECK"))
		{
			System.out.println("CHECK");

			try
			{
				pHandler = new PasswordHandler(props);

				session = Session.getDefaultInstance(props, pHandler);

				store = session.getStore(protocolProvider);

				store.connect();

				System.out.println("Number of folders: " + store.getPersonalNamespaces().length);

				inboxFolder = store.getFolder("INBOX");

				checkForNewMessage(inboxFolder);

				inboxFolder.open(Folder.READ_WRITE);

				inboxFolder.close(false);

				store.close();
			}

			catch (NoSuchProviderException nspe)
			{
				System.out.println("No such email protocol provider: " + protocolProvider);
			}

			catch (MessagingException me)
			{
				System.out.println("Messaging Exception: ");
				me.printStackTrace();
			}

			catch (Exception x)
			{
				System.out.println("Exception in actionPerformed");
			}
		}
	}
}