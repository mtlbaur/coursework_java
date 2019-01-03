import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.util.*;

class URLPrinter implements Printable
{
	Vector<String> fileNameList;

	URLPrinter(Vector<String> fileNameList)
	{
		this.fileNameList = fileNameList;

		//fileNameList.removeAllElements();

		//for (int i = 173; i > 0; i--)
			//fileNameList.add(Integer.toString(i) + "yyyyyyyyyyyyyyyyyyyyyyy");
	}

	static void printIt(Printable printable)
	{
		PrinterJob pj;
		PageFormat pfChanged;
		PageFormat defPageFormat;

		pj = PrinterJob.getPrinterJob();
		defPageFormat = pj.defaultPage();
		pfChanged = pj.pageDialog(defPageFormat);

		if (pfChanged != defPageFormat)
		{
			pj.setPrintable(printable, pfChanged);

			try
			{
				if (pj.printDialog())
					pj.print();
			}

			catch (PrinterException pe)
			{
				System.out.println("TKLFound printer exception.");
			}
		}

		else
			System.out.println("User canceled printing job.");
	}

	public int print(Graphics gg, PageFormat pf, int pageIndex)
	{
		Graphics2D g2;
		double imageableHeight = pf.getImageableHeight();
		int maxPageIndex = (int)(fileNameList.size()*12 / imageableHeight);
		int drawY = 12;
		int numPages = maxPageIndex + 1;
		int linesPerPage = (int)(imageableHeight / 12) - 1;
		int firstLineIndex = linesPerPage * pageIndex;
		int lastLineIndex = fileNameList.size() - 1;
		int maxIndexForCurrentPage = firstLineIndex + linesPerPage - 1;

		if(pageIndex > maxPageIndex || firstLineIndex > lastLineIndex)
			return java.awt.print.Printable.NO_SUCH_PAGE;

		g2 = (Graphics2D)gg;
		g2.translate(pf.getImageableX(), pf.getImageableY());
		g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
		g2.setPaint(Color.BLACK);

		String str;

		while (firstLineIndex <= lastLineIndex && firstLineIndex <= maxIndexForCurrentPage)
		{
			str = fileNameList.elementAt(firstLineIndex);

			g2.drawString(str, 12, drawY);

			drawY = drawY + 12;

			firstLineIndex = firstLineIndex + 1;
		}

		return java.awt.print.Printable.PAGE_EXISTS;
	}
}