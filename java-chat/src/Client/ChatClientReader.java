package Client;
import java.net.Socket;
import java.io.*;

public class ChatClientReader extends Thread
{
	private ChatClient client;
	private Socket soc;
	private UserInterface ui;
	
	/** constructor
	 * @param client the chat client instance
	 * @param soc the socket
	 * @param ui the user interface
	 */
	public ChatClientReader (ChatClient client, Socket soc, UserInterface ui)
	{
		this.client = client;
		this.soc = soc;
		this.ui = ui;
	}
	
	/** Running the object and translate the input from bytes to letters*/	
	public void run()
	{
		try
		{
			String line;
	
			InputStreamReader inputTemp = new InputStreamReader(this.soc.getInputStream());
			BufferedReader input = new BufferedReader(inputTemp);
			
			while (true)
			{
				line = input.readLine(); 
				if (line == null)
				{
					soc.close();
					System.err.println("The line is empty");
					break;
				}

				System.out.println("Got line: [" + line + "]");
				
				ParsedLine parsed = new ParsedLine(line);

				if (parsed.get("command").compareTo("PRIVMSG") == 0)
				{
					ui.outputWriter(parsed.get("nick") + ": " + parsed.get("message"));
				}

				else if (parsed.get("command").compareTo("PING") == 0)
				{
					this.client.runSendline("PONG :me");
				}
				
				else if (!client.getConnected())
				{
					if (parsed.get("command").compareTo("001") == 0)
					{
						client.joinChannel();
						ui.outputWriter("You've conected succesefully to the server!");
					}
					
					else if (parsed.get("command").compareTo("433") == 0) {
						System.err.println("fixing nick collision");
						client.fixNickCollision();
					}
					
					ui.outputWriter("[" + line + "]");
				}

				else
				{
					ui.outputWriter("[" + line + "]");
				}				
			}
		} catch (IOException e)	{
			System.err.println(e);
			return;
		}
		
		

	}

}
