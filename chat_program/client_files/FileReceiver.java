import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;

class FileReceiver implements Runnable
{
	private Socket clientSocket;
	private ServerSocket serverSocket;
	String domain;
	int port;
	String fileName;
	long fileSize;

	FileReceiver (String domain, int port, String fileName, long fileSize) throws IOException
	{
		this.domain = domain;
		this.port = port;
		this.fileName = fileName;
		this.fileSize = fileSize;

		new Thread(this).start();
	}

	public void run()
	{
		try
		{
			long bytesRead = 0;

			serverSocket = new ServerSocket(port);

			clientSocket = serverSocket.accept();

			byte[] buffer = new byte[256];

			InputStream is = clientSocket.getInputStream();

			DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName));

			int numBytesRead = is.read(buffer);

			bytesRead += numBytesRead;

			while (numBytesRead > 255 && bytesRead < fileSize)
			{
				dos.write(buffer, 0, numBytesRead);

				numBytesRead = is.read(buffer);

				System.out.println("numBytesRead: " + numBytesRead);

				bytesRead += numBytesRead;

				System.out.println("bytesRead: " + bytesRead);
			}

			if (numBytesRead != 0)
				dos.write(buffer, 0, numBytesRead);

			dos.close();
			is.close();

			clientSocket.close();
			serverSocket.close();
		}

		catch (IOException e)
		{
			e.printStackTrace();

			System.out.println("SOMETHING WENT WRONG IN FILE RECEIVER");
		}
	}
}
