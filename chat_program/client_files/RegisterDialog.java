import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.event.*;

class RegisterDialog extends JDialog
	implements ActionListener, WindowListener
{
	Client c;

	JButton register;
	JButton clear;
	JButton cancel;

	JTextField username;
	JTextField password;

	JLabel label_username;
	JLabel label_password;

	GroupLayout layout;

	JPanel buttonPanel;
	JPanel fieldPanel;
	JPanel mainPanel;

	RegisterDialog(Client c)
	{
		this.c = c;

		this.addWindowListener(this);

		register = new JButton("Register");
		register.addActionListener(this);
		register.setActionCommand("REGISTER");
		getRootPane().setDefaultButton(register);

		clear = new JButton("Clear");
		clear.addActionListener(this);
		clear.setActionCommand("CLEAR");

		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		cancel.setActionCommand("CANCEL");

		buttonPanel = new JPanel();
		buttonPanel.add(register);
		buttonPanel.add(clear);
		buttonPanel.add(cancel);

		add(buttonPanel, BorderLayout.SOUTH);

		username = new JTextField("");
		password = new JTextField("");

		label_username = new JLabel("Username:");
		label_password = new JLabel("Password:");

		fieldPanel = new JPanel();

		layout = new GroupLayout(fieldPanel);
		fieldPanel.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(label_username)
			.addComponent(label_password));

		hGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(username)
			.addComponent(password));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_username).addComponent(username));

		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label_password).addComponent(password));

        layout.setVerticalGroup(vGroup);

        add(fieldPanel, BorderLayout.NORTH);

        register.requestFocus();

		setupRegisterDialog();
	}

	public void setupRegisterDialog()
	{
		Toolkit tk;

		Dimension d;

		tk = Toolkit.getDefaultToolkit();

		d = tk.getScreenSize();

		setSize(d.width/2, d.height/2);

		setLocation(d.width/4, d.height/4);

		setTitle("Register Dialog");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

		setModal(true);

		setVisible(false);
	}

	public void register()
	{
		System.out.println("RegisterDialog: REGISTER");

		c.username = username.getText();
		c.password = password.getText();

		if (!c.username.equals(username.getText().trim()) || !c.password.equals(password.getText().trim()))
			JOptionPane.showMessageDialog(this, "Whitespace is not allowed.", "Illegal Character", JOptionPane.INFORMATION_MESSAGE);

		else if (c.username.contains("/") || c.username.contains("\\") || c.username.contains(" ") || c.password.contains("/") || c.password.contains("\\") || c.password.contains(" "))
			JOptionPane.showMessageDialog(this, "/ \\ and whitespace are not allowed.", "Illegal Character", JOptionPane.INFORMATION_MESSAGE);

		else if (c.username.contains("user_info_end") || c.password.contains("user_info_end"))
			JOptionPane.showMessageDialog(this, "This username is already taken.", "Alert", JOptionPane.INFORMATION_MESSAGE);

		else
			c.actionPerformed(new ActionEvent(this, 0, "ATTEMPT_REGISTER"));
	}

	public void clear()
	{
		username.setText("");
		password.setText("");

		username.requestFocus();
	}

	public void cancel()
	{
		System.out.println("RegisterDialog: CANCEL");

		clear();

		this.setVisible(false);
	}

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("REGISTER"))
		{
			register();
		}

		else if (cmd.equals("CLEAR"))
		{
			clear();
		}

		else if (cmd.equals("CANCEL"))
		{
			cancel();
		}

		else if (cmd.equals("FAILED"))
		{
			JOptionPane.showMessageDialog(this, "This username is already taken.", "Alert", JOptionPane.INFORMATION_MESSAGE);

			clear();
		}

		else if (cmd.equals("REGISTER_CONNECTION_FAILED"))
		{
			JOptionPane.showMessageDialog(this, "The connection to the server was lost!", "Alert", JOptionPane.INFORMATION_MESSAGE);

			clear();
		}
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
	}

	public void windowClosing(WindowEvent e)
	{
		System.out.println("RegisterDialog: windowClosing");
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