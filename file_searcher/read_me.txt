This program utilizes the Entertainer class to provide a distraction while the Controller attempts to connect to a server (the Commander class) via the CommandHandler class.
When the Controller succeeds in connecting to the Commander, the Commander can then issue three different commands:
	1: Terminate: kills both the Commander and the Entertainer  
	2: Display message: displays the message "Sent file names." on the Entertainer's GUI  
	3: Search: causes the Controller to use the FileSearcher class to search the hosts computer for files that end with .m4a, mp4, and mp3.  
	           The names of these files are then sent to the server.  
	It should be noted that the fourth option on the Commander's GUI ("Exit") just kills the server - this causes the Controller to repeatedly attempt to connect to the server again.