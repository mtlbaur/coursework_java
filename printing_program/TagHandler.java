import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

class TagHandler extends HTMLEditorKit.ParserCallback
{
	String baseDomain;
	Vector<String> fileNameList;

	public TagHandler(String baseDomain, Vector<String> fileNameList)
	{
		this.baseDomain = baseDomain;
		this.fileNameList = fileNameList;
	}

	public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attSet, int pos)
	{
		Enumeration<?> attributeEnum;

		if (tag == HTML.Tag.IMG)
		{
			attributeEnum = attSet.getAttributeNames();

			while(attributeEnum.hasMoreElements())
				if (attributeEnum.nextElement().equals(HTML.Attribute.SRC))
				{
					System.out.println(baseDomain + ":> " + attSet.getAttribute(HTML.Attribute.SRC).toString());

					fileNameList.add(attSet.getAttribute(HTML.Attribute.SRC).toString());
				}
		}
	}

	public void handleStartTag(HTML.Tag tag, MutableAttributeSet attSet, int pos)
	{
		Enumeration<?> attributeEnum;

		if (tag == HTML.Tag.A)
		{
			attributeEnum = attSet.getAttributeNames();

			while(attributeEnum.hasMoreElements())
				if (attributeEnum.nextElement().equals(HTML.Attribute.SRC))
				{
					System.out.println(baseDomain + ":> " + attSet.getAttribute(HTML.Attribute.SRC).toString());

					fileNameList.add(attSet.getAttribute(HTML.Attribute.SRC).toString());
				}
		}
	}
}