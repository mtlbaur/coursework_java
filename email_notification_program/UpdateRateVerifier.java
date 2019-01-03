import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.filechooser.*;
import java.text.*;

class UpdateRateVerifier extends InputVerifier
{
	UpdateRateVerifier()
	{
	}

	public boolean verify(JComponent c)
	{
		float testInt;

		JTextField tf = (JTextField) c;

		String testString = new String(tf.getText().trim());

		if (testString != "")
		{
			try
			{
				testInt = Integer.parseInt(testString);
			}

			catch(Exception e)
			{
				System.out.println("Exception in verify");
				return false;
			}

			if (testInt < 20 || testInt > 3600)
			{
				System.out.println("Int was not in right range");
				return false;
			}
		}

		else
		{
			System.out.println("String contained nothing.");
			return false;
		}

		System.out.println("Verify returned true");
		return true;
	}
}