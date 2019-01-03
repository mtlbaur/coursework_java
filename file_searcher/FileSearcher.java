import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.filechooser.*;

class FileSearcher
	implements Runnable
{
	Controller c;

	Talker t;

	boolean stop;

	FileSearcher(Controller c, Talker t)
	{
		this.c = c;
		this.t = t;
		stop = false;
	}

	public void stop()
	{
		stop = true;
	}

	public void sendFileName(String fileName) throws Exception
	{
		System.out.println("fileName" + fileName);
		//t.send("!FILENAME:" + fileName);
	}

	public void run()
	{
		File[] roots = File.listRoots();

		FileNameExtensionFilter fnef = new FileNameExtensionFilter("Multimedia Files", "m4a", "mp4", "mp3");

		if (roots != null)
		{
			for (File file : roots)
			{
				if (stop)
					break;

				ArrayDeque<File> q = new ArrayDeque();

				q.add(file);

				//System.out.println("CHECKING");

				//System.out.println("STATE: " + stop);

				while(!q.isEmpty() && !stop) //.m4a .mp4 .mp3
				{
					//System.out.println("CONTINUED");

					//System.out.println("STATE: " + stop);

					File tempFile = q.remove();
					File[] fileList = tempFile.listFiles();

					/*
					for (File f : fileList) // TESTING CODE
					{
						System.out.println(f.getName());
					}
					*/

					//System.out.println(fileList.toString());

					if (fileList != null)
					{
						for (File f : fileList)
						{
							if (stop)
								break;

							if (f.isDirectory())
							{
								//System.out.println("add f");
								q.add(f);
							}

							else if (fnef.accept(f))
							{
								try
								{
									t.send("!FILENAME:" + f.getName());
									//System.out.println("fileName: " + f.getName());
								}

								catch (Exception e)
								{
									c.actionPerformed(new ActionEvent(this, 0, "CONNECTION_FAILED"));
								}
							}
						}
					}
				}
			}
		}

		System.out.println("File Searching Complete");
	}
}