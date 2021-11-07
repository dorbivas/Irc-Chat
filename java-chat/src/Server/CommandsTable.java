package Server;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

public class CommandsTable
{
	private Hashtable<String, Command> table;
	
	public CommandsTable()
	{
		this.table = new Hashtable<String, Command>();
		this.table.put("NICK", new CommandNick());
		this.table.put("USER", new CommandUser());
		this.table.put("QUIT", new CommandQuit());
		this.table.put("JOIN", new CommandJoin());
		this.table.put("PING", new CommandPing());
		this.table.put("PRIVMSG", new CommandPrivmsg());
		this.table.put("MODE", new CommandIgnored());
	}	
	
	public void runCommand (Client client , String commandName , String args)
	{
		Command command = this.table.get(commandName);
		
		if (command == null)
		{
			System.err.println("Missing command <" + commandName + ">.");
			command = new CommandBad();
		}
		command.run(client, args);
	}
}

/** parent class for all command handling classes.*/
abstract class Command 
{
	protected void printStartBanner (Client client)
	{
		this.println(client, "001 " + client.getNick() + " :Welcome to Dor's ircd");
	}
	
	abstract public void run(Client client, String args);

	/** print line to the client
	 * @param client destination client 
	 * @param str the line */ 
	public void println(Client client, String str)
	{
		try {
			client.println(":" + client.getHostname() +" " + str);
		} catch (IOException e) {
			System.err.println("Failed to print to client socket: <" + str + "> (" + e + ")");
			client.disconect();
		}
	}

	/** send message to a client
	 * @param client
	 * @param str message */
	public void printUser(Client client, String str)
	{
		printUser (client ,client, str);
	}
	
	/** print to client a message from sender. 
	 * @param client destination client  
	 * @param sender client
	 * @param str message */
	public void printUser(Client client,Client sender, String str)
	{
		try {
			client.println(":" + sender.getNick() + "!" + sender.getUsername() + "@" + sender.getHostname() + " " + str);
		} catch (IOException e) {
			System.err.println("Failed to print to client socket: <" + str + "> (" + e + ")");
			client.disconect();
		}
	}

	/** Remove the first char from the input (this char is irelevant)*/
	public static String RemoveFirst(String st)
	{
		return st.substring(1 , st.length());
	}
}

/** sending message from a client to all other clients.
 * @param client, arguments */
class CommandPrivmsg extends Command
{
	public CommandPrivmsg(){}

	public void run(Client client, String args)
	{
		String []  starr = args.split("[ \t]+" ,  2);
		String line = RemoveFirst(starr[1]);
		String ports = "PRIVMSG port: ";
		
		Iterator<Connection> iter = ChatServer.server.getConnectionIterator();
		
		try
		{
			while (iter.hasNext())
			{
				Connection con = iter.next();

				if (client.IfMyCon(con))
				{
					continue;
				}
				ports += con.toString();
				this.printUser(con.getClient(), client, "PRIVMSG " + starr[0] + " :" + line);
			}
		}
		catch (java.util.ConcurrentModificationException e)
		{
			System.err.println("concurrency error, privmsg may not be fully delivered");
		}
		System.out.println("printed message to ports: " + ports);
	}
}

/** Command that setting the nick name of the client*/
class CommandNick extends Command
{
	public CommandNick() {}
	
	public void run(Client client, String args)
	{
		String nick = args;
		String oldNick = client.getNick();
		
		if (client.setNick(nick))
		{
			if (oldNick == null && client.getUsername() != null)
			{
				this.printStartBanner(client);		
			}
		}
		else
		{
			this.println(client, "433 * " +  nick + " :Nick alredy in use.");	
		}			
	}
}

/** Command that connecting the client to the server */
class CommandJoin extends Command
{
	public CommandJoin() {}

	public void run(Client client, String args)
	{
		// NOTE: parse args to channel names and save client state
		// but right now everybody is on a single channel
		this.printUser(client, "JOIN " + ":" + args);
		this.println(client, "332 " + client.getNick() + " " + args + " :Welcome to the single channel");
	}
}

/** Command that recipient the client to the irc*/
class CommandUser extends Command
{
	public void run(Client client, String args)
	{
		String[] argsArray = args.split("[ \t]+");
		client.setUsername(argsArray[0]);
		
		if (client.getNick() != null)
		{
			printStartBanner(client);
		}	
	}
}

/** Command that disconnect the client from the server*/
class CommandQuit extends Command
{
	public CommandQuit() {}
	
	public void run(Client client, String args)
	{
		client.disconect();
	}	
}

/**Input of unknown command*/
class CommandBad extends Command
{
	public void run(Client client, String args)
	{
		this.println(client, "421 " + client.getNick() + " " + args + " Unknown command.");
	}
}

/** A command to ignore */
class CommandIgnored extends Command
{
	public void run(Client client, String args)	{}
}

/** Pinging each few seconds to keep the connection alive*/
class CommandPing extends Command
{
	public CommandPing() {}
	
	public void run(Client client, String args)
	{
		this.println(client , "PONG " + args + " :" + client.getHostname());
	}
}
