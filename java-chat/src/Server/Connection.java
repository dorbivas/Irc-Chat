package Server;

import java.net.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

class ParsedLine
{
	private String line;
	private String command;
	private String args;

	/**splits the line by white spaces, to fetch the command and arguments
	 * @param line	 */
	public ParsedLine(String line)
	{
		this.line = line;
		String[] parsedLine = this.line.split("[ \t]+", 2);
		if (parsedLine.length > 1)
			this.args = parsedLine[1];
		else
			this.args = "";

		if (parsedLine.length > 0)
			this.command = parsedLine[0];
		else
			this.command = "";
	}

	/** returning the command. */
	public String getCommand()
	{
		return this.command;
	}

	/** returning arguments */
	public String getArgs()
	{
		return this.args;
	}
}

public class Connection extends Thread
{
	private Socket soc;
	private Client client;

	/** constructor */
	public Connection(Socket soc)
	{
		this.soc = soc;
	}

	/** returning the client. */
	public Client getClient()
	{
		return this.client;
	}

	/** removing the connection from the list and the nick from the dictionary */
	public void shutdown()
	{

		ChatServer.server.connectionRemove(this);
		ChatServer.server.removeNick(client.getNick());
		try
		{
			if (!soc.isClosed())
				soc.close();

		} catch (IOException e)	{}
	}

	/** pretty print (the port) */
	public String toString()
	{
		return "[" + soc.getPort() + "]";
	}

	/** main loop of connection */
	@SuppressWarnings("deprecation")
	public void run()
	{
		try
		{
			CommandsTable commandsTable = new CommandsTable();
			InputStreamReader InTemp = new InputStreamReader(soc.getInputStream());
			BufferedReader input = new BufferedReader(InTemp);

			OutputStreamWriter outTemp = new OutputStreamWriter(soc.getOutputStream());
			BufferedWriter output = new BufferedWriter(outTemp);
			client = new Client(soc, output, this);

			while (true)
			{
				ParsedLine parsedLine;
				String line;

				try
				{
					line = input.readLine();
				} catch (java.net.SocketException e)
				{
					shutdown();
					break;
				}
				if (line == null)
				{
					System.err.println("Got null line");
					shutdown();
					break;
				}
				parsedLine = new ParsedLine(line);
				String s = soc.getPort() + ": <" + line + ">";
				System.out.println(s);
				s = soc.getPort() + ": <" + line + ">, command: <" + parsedLine.getCommand() + ">, args: <" + parsedLine.getArgs() + ">.";
				commandsTable.runCommand(client, parsedLine.getCommand(), parsedLine.getArgs());
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			shutdown();
		}
	}
}
