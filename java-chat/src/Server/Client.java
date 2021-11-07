package Server;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class Client
{
	private String nick;
	private String username;
	private Socket soc;
	private BufferedWriter output;
	private Connection con; 
	
	/** constructor*/
	public Client(Socket soc, BufferedWriter output, Connection con)
	{
		this.soc = soc;
		this.output = output;
		this.con = con;
	}
	/** returning if an Connection is my'n */
	public boolean IfMyCon (Connection con)
	{
		return con == this.con;
	}
	
	/** returning the nick name */
	public String getNick()
	{
		return this.nick;
	}
	/** set nick name if it does not exist. */
	public boolean setNick(String newNick)
	{
		newNick = newNick.toLowerCase();
		if (!ChatServer.server.addNick(newNick))
		{
			return false;
		}
		this.nick = newNick;
		return true;
	}
	/** returning the host name */
	public String getHostname()
	{
		return "myhost";
	}
	/** returning the user name */
	public String getUsername()
	{
		return this.username;
	}
	/** changing the user name */
	public void setUsername(String newUsername)
	{
		this.username = newUsername;
	}
	
	/** closing the socket */
	public void disconect ()  
	{
		this.con.shutdown();
	}
			
	/** prints a line to the client socket
	 * @param line the text to print  */		
	public void println(String line) throws IOException
	{
		output.write(line, 0, line.length());
		output.newLine();
		output.flush();
		System.out.println(this.soc.getPort() + ": " + line);
	}
}
