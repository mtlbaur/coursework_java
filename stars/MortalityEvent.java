import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

class MortalityEvent
{
	boolean dead;
	LivingObject source;

	MortalityEvent(Star source)
	{
		dead = false;
		this.source = source.getSource();
	}

	MortalityEvent(boolean dead, LivingObject source)
	{
		this.dead = dead;
		this.source = source.getSource();
	}

	public LivingObject getSource()
	{
		return source;
	}
}