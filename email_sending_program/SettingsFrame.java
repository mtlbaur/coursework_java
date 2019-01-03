import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.filechooser.*;
import javax.mail.*;
import javax.mail.internet.*;
import com.sun.mail.imap.*;

class SettingsFrame extends JFrame
    implements ActionListener, WindowListener
{
	JTextField SMTP_host;
	JTextField SMTP_port;
	JTextField username;
	JPasswordField password;
	JTextField sentFrom;
	JTextField sentDate;
	JTextField subject;
	JTextArea content;

	JLabel label_SMTP_host;
	JLabel label_SMTP_port;
	JLabel label_username;
	JLabel label_password;
	JLabel label_sentFrom;
	JLabel label_sentDate;
	JLabel label_subject;
	JLabel label_content;

    JButton save;
    JButton clear;
    JButton cancel;

    GroupLayout layout;

    JPanel editPanel;
    JPanel buttonPanel;

    Properties props;

    File defaults;

	ActionListener controller;

	JScrollPane scroller;

    SettingsFrame(Properties props, ActionListener controller)
    {
		this.addWindowListener(this);

		this.props = props;

		//this.defaults = defaults;

		this.controller = controller;

		setupBasicGUI();

		save = new JButton("Send");
		save.addActionListener(this);
		save.setActionCommand("SEND");
		getRootPane().setDefaultButton(save);

		clear = new JButton("Clear");
		clear.addActionListener(this);
		clear.setActionCommand("CLEAR");

		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		cancel.setActionCommand("CANCEL");

		buttonPanel = new JPanel();

		buttonPanel.add(save);
		buttonPanel.add(clear);
		buttonPanel.add(cancel);

        add(buttonPanel, BorderLayout.SOUTH);

		SetupSettings();
    }

    public void setupBasicGUI()
    {
		if (props.getProperty("mail.smtp.host") == null)
        	SMTP_host = new JTextField("");

        else
        	SMTP_host = new JTextField(props.getProperty("mail.smtp.host"));

        label_SMTP_host = new JLabel("SMTP Host:");

//-----------------------------------------------------------------------------------------------------

		if (props.getProperty("mail.smtp.socketFactory.port") == null)
			SMTP_port = new JTextField("");

		else
			SMTP_port = new JTextField(props.getProperty("mail.smtp.socketFactory.port"));

        label_SMTP_port = new JLabel("SMTP Port:");

//-----------------------------------------------------------------------------------------------------

		if (props.getProperty("mail.user") == null)
        	username = new JTextField("");

        else
        	username = new JTextField(props.getProperty("mail.user"));

        label_username = new JLabel("Username:");

//-----------------------------------------------------------------------------------------------------

		if (props.getProperty("password") == null)
       		password = new JPasswordField("");

       	else
       		password = new JPasswordField(props.getProperty("password"));

        label_password = new JLabel("Password:");

//-----------------------------------------------------------------------------------------------------

        if (props.getProperty("sent_from") == null)
			sentFrom = new JTextField("");

		else
			sentFrom = new JTextField(props.getProperty("sent_from"));

		label_sentFrom = new JLabel("Sent From:");

//-----------------------------------------------------------------------------------------------------

		if (props.getProperty("sent_date") == null)
			sentDate = new JTextField("");

		else
			sentDate = new JTextField(props.getProperty("sent_date"));

		label_sentDate = new JLabel("Sent Date:");

//-----------------------------------------------------------------------------------------------------

		if (props.getProperty("subject") == null)
			subject = new JTextField("");

		else
			subject = new JTextField(props.getProperty("subject"));

		label_subject = new JLabel("Subject:");

//-----------------------------------------------------------------------------------------------------

		if (props.getProperty("content") == null)
			content = new JTextArea("");

		else
			content = new JTextArea(props.getProperty("content"));

		label_content = new JLabel("Message Content:");

		scroller = new JScrollPane();
		scroller.getViewport().setView(content);


        editPanel = new JPanel();

        layout = new GroupLayout(editPanel);

        editPanel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

        hGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(label_SMTP_host)
            .addComponent(label_SMTP_port)
            .addComponent(label_username)
            .addComponent(label_password)
            .addComponent(label_sentFrom)
            .addComponent(label_sentDate)
            .addComponent(label_subject)
            .addComponent(label_content));

        hGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(SMTP_host)
			.addComponent(SMTP_port)
			.addComponent(username)
			.addComponent(password)
			.addComponent(sentFrom)
			.addComponent(sentDate)
			.addComponent(subject)
			.addComponent(scroller));

        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_SMTP_host).addComponent(SMTP_host));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_SMTP_port).addComponent(SMTP_port));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_username).addComponent(username));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_password).addComponent(password));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_sentFrom).addComponent(sentFrom));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_sentDate).addComponent(sentDate));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_subject).addComponent(subject));

		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_content).addComponent(scroller));

        layout.setVerticalGroup(vGroup);

        add(editPanel, BorderLayout.CENTER);

        SMTP_host.requestFocus();
    }

    public void SetupSettings()
    {
        Toolkit tk;

        Dimension d;

        tk = Toolkit.getDefaultToolkit();

        d = tk.getScreenSize();

        setSize(d.width/2, d.height/2);

        setLocation(d.width/4, d.height/4);

        setTitle("Email Sender Settings");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

		//setModal(true);

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
		System.out.println("Settings: windowClosing");

		ActionEvent ae = new ActionEvent(this, 0 , "CANCEL");

		controller.actionPerformed(ae);
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

    public void send()
    {
		System.out.println("SETTINGS: SEND");

		boolean error = false;

		try
		{
			String test_SMTP_host = new String(SMTP_host.getText().trim());
			String test_SMTP_port = new String(SMTP_port.getText().trim());
			String test_username = new String(username.getText().trim());
			String test_password = new String(password.getPassword());
			test_password = test_password.trim();
			String test_sentFrom = new String(sentFrom.getText().trim());
			String test_sentDate = new String(sentDate.getText().trim());
			String test_subject = new String(subject.getText().trim());
			String test_content = new String(content.getText().trim());

			try
			{
				int testPortNum = Integer.parseInt(test_SMTP_port);

				if (testPortNum < 0 || testPortNum > 65535)
				{
					error = true;
					JOptionPane.showMessageDialog(null, "Port number of valid range (0 - 65535).", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}

			catch(Exception e)
			{
				error = true;
				JOptionPane.showMessageDialog(null, "Port number could not be parsed.", "Error", JOptionPane.ERROR_MESSAGE);
			}

		}

		catch(Exception e)
		{
			error = true;
			JOptionPane.showMessageDialog(null, "The content of the settings dialog could not be read.", "Error", JOptionPane.ERROR_MESSAGE);
		}

		if (error == true)
			System.out.println("Settings: ERROR IN SEND");

		else
		{
			props.setProperty("mail.smtp.host", SMTP_host.getText().trim());
			props.setProperty("mail.smtp.socketFactory.port", SMTP_port.getText().trim());
			props.setProperty("mail.user", username.getText().trim());
			props.setProperty("password", new String(password.getPassword()).trim());
			props.setProperty("sent_from", sentFrom.getText().trim());
			props.setProperty("sent_date", sentDate.getText().trim());
			props.setProperty("subject", subject.getText().trim());
			props.setProperty("content", content.getText().trim());

			ActionEvent e = new ActionEvent(this, 0 , "SEND");

			controller.actionPerformed(e);

			this.dispose();
		}
    }

    public void clear()
	{
		System.out.println("CLEAR");

		SMTP_host.setText("");

		SMTP_port.setText("");

		username.setText("");

		password.setText("");

		sentFrom.setText("");

		sentDate.setText("");

		subject.setText("");

		content.setText("");
	}

    public void cancel()
    {
		System.out.println("CANCEL");

		ActionEvent e = new ActionEvent(this, 0 , "EXIT");

		controller.actionPerformed(e);

        this.dispose();
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("SEND"))
            send();

		else if (e.getActionCommand().equals("CLEAR"))
            clear();

        else if (e.getActionCommand().equals("CANCEL"))
            cancel();
    }
}