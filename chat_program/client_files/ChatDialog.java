import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;
import javax.swing.text.html.*;
import javax.swing.text.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

class ChatDialog extends JFrame
			implements ActionListener, WindowListener, KeyListener, ItemListener, DocumentListener, DropTargetListener
{
	DefaultListModel<String> receive_msg;
	//JList receive_msg_view;
	JTextArea send_msg;

	JButton send;
	JButton clear;
	JButton exit;

    JPanel buttonPanel;
    JPanel scrollerPanel;

	JScrollPane messages_scroller;
	JScrollPane send_scroller;

	protected String friend_username;

	Client cl;

	Friend f;

	JEditorPane edPane;

	DropTarget dt;

	javax.swing.Timer t;

    ChatDialog(Client cl, Friend f, String friend_username)
    {
		this.cl = cl;
		this.f = f;
		this.friend_username = friend_username;

		Container cp = getContentPane();

		edPane = new JEditorPane();
		edPane.setContentType("text/html");
		edPane.setEditable(false);
		messages_scroller = new JScrollPane();
		messages_scroller.getViewport().setView(edPane);

		send_msg = new JTextArea("");
		send_msg.addKeyListener(this);
		send_msg.getDocument().addDocumentListener(this);
		send_scroller = new JScrollPane();
		send_scroller.getViewport().setView(send_msg);

		send = new JButton("Send");
		send.addActionListener(this);
		send.setActionCommand("SEND");
		getRootPane().setDefaultButton(send);

		scrollerPanel = new JPanel(new GridLayout(2, 1));
		scrollerPanel.add(messages_scroller);
		scrollerPanel.add(send_scroller);

		clear = new JButton("Clear");
		clear.addActionListener(this);
		clear.setActionCommand("CLEAR");

		exit = new JButton("Exit");
		exit.addActionListener(this);
		exit.setActionCommand("EXIT");

		send.setEnabled(false);

		buttonPanel = new JPanel();

		buttonPanel.add(send);
		buttonPanel.add(clear);
		buttonPanel.add(exit);

        cp.add(scrollerPanel, BorderLayout.CENTER);
        cp.add(buttonPanel, BorderLayout.SOUTH);

        //send_msg.requestFocus();// doesn't work

        dt = new DropTarget(send_msg, this);

		t = new javax.swing.Timer(50000, cl);
		t.setActionCommand("PLAY_NOTIFICATION_SOUND");
		t.setRepeats(false);
		t.setInitialDelay(50000);

		setupChatDialog();
    }

	public void setupChatDialog()
	{
		Toolkit tk;

		Dimension d;

		tk = Toolkit.getDefaultToolkit();

		d = tk.getScreenSize();

		setSize(d.width/2, d.height/2);

		setLocation(d.width/4, d.height/4);

		setTitle(cl.username + "'s " + "Chat Dialog");

		//setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		//setDefaultCloseOperation(HIDE_ON_CLOSE);

		setVisible(true);
    }

    public void startTimer()
    {
		t.start();
	}

    public void addTextToEditorPane(String msg, String sender, String color)
    {
		try
		{
			HTMLDocument doc;

			Element rootElt;
			Element bodyElt;

			doc = (HTMLDocument)edPane.getDocument();
			rootElt = doc.getRootElements()[0];
			bodyElt = rootElt.getElement(0);
			doc.insertBeforeEnd(bodyElt, "<div alignment=left><font color ="+ color +">" + sender + ": " + msg + "</font></div>");
			edPane.repaint();
		}

		catch (Exception e)
		{
			System.out.println("COULD NOT addTextToEditorPane() for: " + cl.username);
		}

		messages_scroller.validate();

		JScrollBar scrollBar = messages_scroller.getVerticalScrollBar();

		scrollBar.setValue(scrollBar.getMaximum());
	}

    public void displayMessage(String msg, String sender, String color) // could have alignment arg here
    {
		System.out.println("DISPLAYING MESSAGE");

		addTextToEditorPane(msg, sender, color);

/*
		receive_msg.addElement("Received message from: " + sender);
		receive_msg.addElement(msg);

		int x = receive_msg.getSize();

		receive_msg_view.ensureIndexIsVisible(x - 1);
		*/
	}

    public void send()
    {
		System.out.println("ChatDialog:(" + cl.username + "): SEND");

		String msg = send_msg.getText().trim();

		if (msg != "")
		{
			String color = "#FF0000";

			msg = msg.replace("\n", "<br>");

			displayMessage(msg, cl.username, color);

			cl.send("!MESSAGE:" + "/" + friend_username + "/" + cl.username + "/" + msg);
		}

		startTimer();

		clear();
    }

    public void clear()
	{
		System.out.println("Client: CLEAR");

		send_msg.setText("");
		send_msg.requestFocus();
	}

    public void exit()
    {
		System.out.println("Client: EXIT");

		// TELL THE CLIENT USER EXITED

		this.dispose();

		f.cd = null;
    }

	public void checkTextArea()
	{
		String msg = send_msg.getText();

		//System.out.println(msg);
		//System.out.println(msg.trim());

		if (!msg.trim().equals(""))
		{
			send.setEnabled(true);
		}

		else
		{
			send.setEnabled(false);
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		try
		{
			if (cmd.equals("SEND"))
			{
				send();
			}

			else if (cmd.equals("CLEAR"))
			{
           		clear();
			}

        	else if (cmd.equals("EXIT"))
        	{
            	exit();
			}
		}

		catch (Exception x)
		{
			x.printStackTrace();

			System.out.println("Client: actionPerformed(): Exception");
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
		System.out.println("ChatDialog: windowClosing");

		this.dispose();

		f.cd = null;
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

	public void keyPressed(KeyEvent e)
	{
	}

	public void keyReleased(KeyEvent e)
	{
	}

	public void keyTyped(KeyEvent e)
	{
		char x = e.getKeyChar();
		String y = String.valueOf(x);

		if (!Character.isLetterOrDigit(x) && !Character.isWhitespace(x) && !(y.equals(".")) && !(y.equals(",")) && !(y.equals(":")) && !(y.equals(";")) && !(y.equals("!")) && !(y.equals("?")) && !(y.equals("\"")) && !(y.equals("\'")) && !(y.equals("(")) && !(y.equals(")")))
			e.consume();
	}

	public void itemStateChanged(ItemEvent e)
	{
	}

	public void changedUpdate(DocumentEvent e)
	{
	}

	public void insertUpdate(DocumentEvent e)
	{
		checkTextArea();
	}

	public void removeUpdate(DocumentEvent e)
	{
		checkTextArea();
	}

	public void dragEnter(DropTargetDragEvent x)
	{
	}

	public void dragExit(DropTargetEvent x)
	{
	}

	public void dragOver(DropTargetDragEvent x)
	{
	}

	public void drop(DropTargetDropEvent x)
	{
		System.out.println("GOT DROP EVENT");

		java.util.List<File> file;
		Transferable transferableData;
		transferableData = x.getTransferable();

		try
		{
			if (transferableData.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				x.acceptDrop(DnDConstants.ACTION_COPY);

				file = (java.util.List<File>)(transferableData.getTransferData(DataFlavor.javaFileListFlavor));

				cl.fileToTransfer = file.get(0);

				if (!cl.fileToTransfer.isDirectory())
				{
					System.out.println(cl.fileToTransfer);

					System.out.println(cl.fileToTransfer.getName());

					System.out.println(cl.fileToTransfer.length());

					cl.cts.sendExactString("!REQUEST_FILE_TRANSFER:" + "/" + cl.fileToTransfer.getName() + "/" + cl.fileToTransfer.length() + "/" + friend_username + "/" + cl.username);
				}

				else
					JOptionPane.showMessageDialog(this, "Folders cannot be transferred.", "Alert", JOptionPane.INFORMATION_MESSAGE);
			}

			else
				System.out.println("File list flavor not supported.");
		}

		catch(Exception e)
		{
			e.printStackTrace();

			System.out.println("Client: Exception in drop()");
		}
	}

	public void dropActionChanged(DropTargetDragEvent x)
	{
	}

	public static void main(String[] x)
	{
		new ChatDialog(new Client(), new Friend(new Client(), "ABC", "123"), "MyUsername");
	}
}