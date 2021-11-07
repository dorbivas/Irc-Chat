package Client;
import java.io.*;
import java.net.Socket;

public class ChatClient
{
	private String nick;
	private String realname;
	private	ChatClientWriter writer;
	private	UserInterface ui;
	private Socket soc;
	private boolean isConnected = false;
	
	private static final String CHANNEL = "#Dor-chat";
	
	ChatClient() {
		this.soc = null;
	}
	
	/** returning the nick name */
	public String getNick ()
	{
		return this.nick;
	}
	/** returning the real name */
	public String getRealname ()
	{
		return this.realname;
	}
	/** changing the nick name *
	 * @param nick name the name to set*/
	public void setNick (String nick)
	{
		this.nick = nick;
	}
	/** changing the real name
	 * @param realname the name to set*/
	public void setRealname (String realname)
	{
		this.realname = realname;
	}
	
	/** mark client as connected */
	public void setConnected()
	{
		this.isConnected = true;
	}
	
	/** returning connection */
	public boolean getConnected()
	{
		return this.isConnected;
	}
	
	/** send a line to server */
	public void runSendline (String line)
	{
		if (!this.isConnected)
			return;
		writer.sendLine(line);
	}
	
	/** send line to server */
	private void earlySendline(String line)
	{
		writer.sendLine(line);
	}
	
	/** if the line is special command privmsg will handle it, else it will send the line as a privmsg.  
	 * @param line the line.*/
	public void PRIVMSG (String line)
	{
		String[] words = line.split("[ \t]", 2);
		if (words[0].equalsIgnoreCase("/QUOTE"))
		{
			runSendline(line.substring(7, line.length()));
		}
		
		else if(words[0].equalsIgnoreCase("/CLEAR"))
		{
			ui.Clear();
		}
		
		else
		{
			runSendline("PRIVMSG " + ChatClient.CHANNEL + " :" + line);
		}
	}
	
	/** checking for nick Collisions and handling them. */
	public void fixNickCollision()
	{
		String newNick = getNick() + new Integer((int)(Math.random()*1000)).toString();
		earlySendline("NICK " + newNick);
	}
	
	/** sending nick and user to the writer.*/
	public void startup() {
		earlySendline("NICK " + this.getNick());
		earlySendline("USER " + this.getNick() + " 0 * :" + this.getRealname());		
	}
	
	/** Initial setup with the server once the nick was set up*/
	public void joinChannel()
	{
		// 0: mode (nothing special). *: unused in RFC 2812, server name in RFC 1459
		this.setConnected(); // FIXME: Not sure yet if client is connected.
		runSendline("JOIN " + ChatClient.CHANNEL);
	
	}
	
	/** closing the socket*/
	public void shutdown()
	{
		if (soc != null)
		{
			try
			{
				soc.close();
			} 
			catch (IOException e) {}
		}
	}
	
	/** opening new connection
	 * @param host name.
	 * @param port number. */
	public boolean connect (String host, int port)
	{
		try
		{
			soc = new Socket(host, port);
			ChatClientReader reader = new ChatClientReader(this, soc, ui);
			
			this.writer = new ChatClientWriter(soc,this);

			reader.start();
			writer.start();
			
			return true;
			
		} catch (IOException e)
		{
			System.err.println("Failed connecting to server: " + host + ":" + port + " (" + e + ")");
			return false;
		}
	}
	
	public static void main(String[] args) throws IOException
	{
 		ChatClient client = new ChatClient();
		client.setNick("Dor");
		client.setRealname("Dor bivas");
		client.ui = new UserInterface(client);
	}
}
