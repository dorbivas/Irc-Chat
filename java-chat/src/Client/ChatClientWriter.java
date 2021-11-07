package Client;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatClientWriter extends Thread
{
	private ChatClient client;
	private Socket soc;
	private BufferedWriter output;
	private LinkedBlockingQueue<String> queue;

	public static Scanner reader = new Scanner(System.in);

	/** constructor 
	 * @param soc - the socket
	 * @param client - the CLient info */

	public ChatClientWriter(Socket soc ,ChatClient client)
	{
		this.soc = soc;
		this.client = client;
		this.queue = new LinkedBlockingQueue<String>();
	}

	/** add message to queue.  
	 * @param line which is the message */
	public void sendLine (String line) 
	{
		try
		{
			this.queue.put(line);
		} catch (InterruptedException e)
			
		{
			System.err.println("input line is null: " + e); 
		}
	}
	
	/** writing the line and making sure the package will be pushed on the stream  
	 * @param line and the client info */
	public void println(String line)
	{	
		try
		{
			this.output.write(line, 0, line.length());
			this.output.newLine();
			this.output.flush();
				
		} catch (IOException e)	{System.err.print("failed to print line" + e);}
		
	}
	
	/** Running the object and translate the output from bytes to letters*/	
	public void run()
	{
		try
		{
			OutputStreamWriter outTemp;
			String line;
			
			outTemp = new OutputStreamWriter(this.soc.getOutputStream());
			this.output = new BufferedWriter(outTemp);

			client.startup();
			
			while (true)
			{
				try
				{
					line = this.queue.take();
				} catch (InterruptedException e){line = "";}	
				

				System.out.println(line); 
				
				this.output.write(line, 0, line.length());
				this.output.newLine();
				this.output.flush();
			}

		} catch (IOException e)	{}

	}

}
