import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class NotificationSound implements LineListener
{
	static Clip audioClip;
	AudioInputStream audioInputStream;
	URL url;

	public void notifyUser()
	{
		try
		{
			url = this.getClass().getClassLoader().getResource("Windows Notify.wav");
			audioInputStream = AudioSystem.getAudioInputStream(url);
			audioClip = AudioSystem.getClip();
			audioClip.open(audioInputStream);
			audioClip.addLineListener(this);
			audioClip.start();
		}

		catch (UnsupportedAudioFileException uafe)
		{
			System.out.println("NotificationSound: UnsupportedAudioFileException");
		}

		catch (IOException ioe)
		{
			System.out.println("NotificationSound: IOException");
		}

		catch (LineUnavailableException lue)
		{
			System.out.println("NotificationSound: LineUnavailableException");
		}
	}

	public void update(LineEvent event)
	{
		System.out.println("Got a lineEvent...");

		if (event.getType() == LineEvent.Type.START)
			System.out.println("Got a START LineEvent");

		else if (event.getType() == LineEvent.Type.STOP)
		{
			System.out.println("Got a STOP LineEvent");
			event.getLine().close();
		}
	}
}