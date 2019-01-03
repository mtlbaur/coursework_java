import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;

class ClientSpawner extends JFrame
	implements ActionListener, Runnable
{
	ClientSpawner()
	{
		Container cp = getContentPane();

		JButton spawn;
		JButton terminate;

		JPanel panel;

		spawn = new JButton("Spawn new client");
		spawn.addActionListener(this);
		spawn.setActionCommand("SPAWN");
		getRootPane().setDefaultButton(spawn);

		terminate = new JButton("Terminate");
		terminate.addActionListener(this);
		terminate.setActionCommand("TERMINATE");

		panel = new JPanel(new GridLayout(2, 1));

		panel.add(spawn);
		panel.add(terminate);

		cp.add(panel);

		setupClientSpawner();
	}

	public void setupClientSpawner()
	{
		Toolkit tk;

		Dimension d;

		tk = Toolkit.getDefaultToolkit();

		d = tk.getScreenSize();

		setSize(d.width/4, d.height/6);

		setLocation(d.width/12, d.height/12);

		setTitle("Client Spawner");

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setVisible(true);
	}


	public void createClient()
	{
		new Thread(this).start();
	}

	public void run()
	{
		new Client();
	}

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals("SPAWN"))
			createClient();

		else if (cmd.equals("TERMINATE"))
			System.exit(0);
	}


	public static void main(String[] x)
	{
		new ClientSpawner();
	}
}