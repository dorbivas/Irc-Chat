package Server;
import java.net.*;
import java.io.*;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Iterator;

public class ChatServer
{
	private ServerSocket service;
	private LinkedList<Connection> connections;
	private Hashtable <String, Boolean> nicks; 
	
	public static final int portNum = 6667;
	public static ChatServer server;
	
	/** removing connection from the iterator.
	 * @param con connection */
	public void connectionRemove(Connection con)
	{
		this.connections.remove(con);
	}
	
	/** returning ConnectionIterator. */
	public Iterator<Connection> getConnectionIterator()
	{
		return this.connections.iterator();
	}
	
	/** constructor of the server , building socket , connection list and nicks dictionary.*/
	ChatServer() throws IOException {
		this.service = new ServerSocket(portNum);
		this.connections = new LinkedList<Connection>();
		this.nicks = new Hashtable <String, Boolean>();
	}
	
	/** check if nick already used. if not add it.
	 * @param nick the new nick name to set */
	public boolean addNick (String nick)
	{
		if (this.nicks.get(nick) != null)
		{
			return false;
		}
		
		this.nicks.put(nick, true); // this value (true) has no meaning.
		return true;
	}
	
	/** remove nick which is no longer in use.
	 * @param nick the nick to remove*/
	public void removeNick (String nick)
	{
		this.nicks.remove(nick);
	}
	
	/** Listening to port and opening a socket */
	public static void main(String[] args) throws IOException
	{
		try
		{
			server = new ChatServer();
		} catch (IOException e)
		{
			System.err.println("Failed listening on port " + portNum + " (" + e + ")");
			return;
		}

		Socket soc = null;
		try
		{
			while (true)
			{
				soc = server.service.accept();
				Connection con = new Connection(soc);
				server.connections.add(con);
				con.start();
			}
		} catch (IOException e)
		{
			System.out.println(e);
		}

	}
}
