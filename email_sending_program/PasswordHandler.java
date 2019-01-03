import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import javax.mail.*;

class PasswordHandler extends javax.mail.Authenticator
{
	javax.mail.PasswordAuthentication pA;


	PasswordHandler(String authSenderUsername, String authSenderPassword)
	{
		pA = new javax.mail.PasswordAuthentication(authSenderUsername, authSenderPassword);
	}

	PasswordHandler(Properties props)
	{
		pA = new javax.mail.PasswordAuthentication(props.getProperty("mail.user"), props.getProperty("password"));
	}

	protected PasswordAuthentication getPasswordAuthentication()
	{
		return pA;
	}
}