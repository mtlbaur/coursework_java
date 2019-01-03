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

class Settings extends JDialog
    implements ActionListener, WindowListener
{
	JTextField serverName_IMAP;
	JTextField username;
	JPasswordField password;
	JTextField updateRate;
	JCheckBox soundCB;

	JLabel label_serverName_IMAP;
	JLabel label_username;
	JLabel label_password;
	JLabel label_updateRate;
	JLabel label_soundCB;

    JButton save;
    JButton clear;
    JButton cancel;

    GroupLayout layout;

    JPanel editPanel;
    JPanel buttonPanel;

    UpdateRateVerifier updateRateVerifier;

    Properties props;

    File defaults;

	ActionListener frame;

    Settings(Properties props, File defaults, ActionListener frame)
    {
		this.addWindowListener(this);

		this.props = props;
		this.defaults = defaults;
		this.frame = frame;

		updateRateVerifier = new UpdateRateVerifier();

		setupBasicGUI();

		save = new JButton("Save");
		save.addActionListener(this);
		save.setActionCommand("SAVE");
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
		if (props.getProperty("mail.host") == null)
        	serverName_IMAP = new JTextField("");

        else
        	serverName_IMAP = new JTextField(props.getProperty("mail.host"));

        label_serverName_IMAP = new JLabel("Server Name:");

		if (props.getProperty("mail.user") == null)
        	username = new JTextField("");

        else
        	username = new JTextField(props.getProperty("mail.user"));

        label_username = new JLabel("Username:");

		if (props.getProperty("password") == null)
       		password = new JPasswordField("");

       	else
       		password = new JPasswordField(props.getProperty("password"));

        label_password = new JLabel("Password:");

		if (props.getProperty("updateRate") == null)
       		updateRate = new JTextField("");

       	else
       		updateRate = new JTextField(props.getProperty("updateRate"));

       	updateRate.setInputVerifier(updateRateVerifier);

        label_updateRate = new JLabel("Update Rate:");

		if (props.getProperty("soundCB") == null)
        	soundCB = new JCheckBox();

        else
        {
			boolean boolean_result = Boolean.valueOf(props.getProperty("soundCB"));

        	soundCB = new JCheckBox();
        	soundCB.setSelected(boolean_result);
		}

        label_soundCB = new JLabel("Notification Sound:");

        editPanel = new JPanel();

        layout = new GroupLayout(editPanel);

        editPanel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

        hGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(label_serverName_IMAP)
            .addComponent(label_username)
            .addComponent(label_password)
            .addComponent(label_updateRate)
            .addComponent(label_soundCB));

        hGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(serverName_IMAP)
			.addComponent(username)
			.addComponent(password)
			.addComponent(updateRate)
			.addComponent(soundCB));

        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_serverName_IMAP).addComponent(serverName_IMAP));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_username).addComponent(username));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_password).addComponent(password));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_updateRate).addComponent(updateRate));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_soundCB).addComponent(soundCB));

        layout.setVerticalGroup(vGroup);

        add(editPanel, BorderLayout.CENTER);

        serverName_IMAP.requestFocus();
    }

    public void SetupSettings()
    {
        Toolkit tk;

        Dimension d;

        tk = Toolkit.getDefaultToolkit();

        d = tk.getScreenSize();

        setSize(d.width/2, d.height/2);

        setLocation(d.width/4, d.height/4);

        setTitle("Email Notifier Settings");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

		setModal(true);

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

    public void save()
    {
		System.out.println("SAVE");

		boolean error = false;

		try
		{
			String testServerName = new String(serverName_IMAP.getText().trim());
			String testUsername = new String(username.getText().trim());
			String testPassword = new String(password.getPassword());
			testPassword = testPassword.trim();
			Properties testProps = new Properties();

			try
			{
				Session testSession = Session.getInstance(testProps);
				Store testStore = testSession.getStore("imaps");
				testStore.connect(testServerName, testUsername, testPassword);
				testStore.close();
			}

			catch(Exception e)
			{
				System.out.println("Invalid input.");
				error = true;
			}

			if (!updateRateVerifier.verify(updateRate))
				error = true;
		}

		catch(Exception e)
		{
			System.out.println("Error in save()");
			e.printStackTrace();
			error = true;
		}

		if (error == true)
			JOptionPane.showMessageDialog(null, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);

		else
		{
			props.setProperty("mail.host", serverName_IMAP.getText().trim());

			props.setProperty("mail.user", username.getText().trim());

			props.setProperty("password", new String(password.getPassword()).trim());

			props.setProperty("updateRate", updateRate.getText());

			boolean boolean_result = soundCB.isSelected();

			String string_result = String.valueOf(boolean_result);

			props.setProperty("soundCB", string_result);

			try
			{
				FileOutputStream out = new FileOutputStream(defaults);

				props.store(out, "Defaults");

				out.close();
			}

			catch (IOException e)
			{
				System.out.println("Setting's save() failed.");
			}

			ActionEvent e = new ActionEvent(this, 0 , "SAVE");

			frame.actionPerformed(e);

			this.dispose();
		}
    }

    public void clear()
	{
		System.out.println("CLEAR");

		serverName_IMAP.setText("");

		username.setText("");

		password.setText("");

		updateRate.setText("");

		soundCB.setSelected(false);
	}

    public void cancel()
    {
		System.out.println("CANCEL");

		ActionEvent e = new ActionEvent(this, 0 , "CANCEL");

		frame.actionPerformed(e);

        this.dispose();
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("SAVE"))
            save();

		else if (e.getActionCommand().equals("CLEAR"))
            clear();

        else if (e.getActionCommand().equals("CANCEL"))
            cancel();
    }
}