import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Entertainer extends JFrame
{
	JPanel mainPanel;

    Entertainer(int size)
    {
		new Controller(this);

		Container cp = getContentPane();

		mainPanel = new JPanel(new GridLayout(size, size));

		int newSize = size*size;

		for (; newSize > 0; newSize--)
		{
			ColorfulJPanel cjp = new ColorfulJPanel();
			mainPanel.add(cjp);
		}

		cp.add(mainPanel, BorderLayout.CENTER);

		setupEntertainer();
    }

    public void setupEntertainer()
    {
        Toolkit tk;

        Dimension d;

        tk = Toolkit.getDefaultToolkit();

        d = tk.getScreenSize();

        setSize(d.width/2, d.height/2);

        setLocation(d.width/4, d.height/4);

        setTitle("Entertainer");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

 		setVisible(true);
    }

	public static void main(String[] x)
	{
		new Entertainer(100);
	}
}