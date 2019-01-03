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

class NotificationDialog extends JDialog
						implements WindowListener
{
	ActionListener frame;

	NotificationDialog(Folder mailboxFolder, ActionListener frame)
	{
		this.frame = frame;

		this.addWindowListener(this);

		try
		{
			add(new JLabel("<html>" + mailboxFolder.getNewMessageCount()
								+ " new message(s) since last update.<br><br>"
								+ mailboxFolder.getUnreadMessageCount()
								+ " unread message(s).</html>", SwingConstants.CENTER));

			repaint();
		}

		catch(MessagingException me)
		{
			System.out.println("MessagingException in NotificationDialog");
		}

		setupNotificationDialog();
	}

	public void setupNotificationDialog()
	{
		Toolkit tk;

		Dimension d;

		tk = Toolkit.getDefaultToolkit();

		d = tk.getScreenSize();

		setSize(d.width/6, d.height/6);

		setLocation(d.width/2, d.height/2);

		setTitle("New Message(s)");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setModalityType(Dialog.ModalityType.MODELESS);

		setVisible(true);
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
	}

	public void windowClosing(WindowEvent e)
	{
		System.out.println("windowClosing");

		ActionEvent ae = new ActionEvent(this, 0 , "DIALOG_CLOSED");

		frame.actionPerformed(ae);
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowOpened(WindowEvent e)
	{
	}
}