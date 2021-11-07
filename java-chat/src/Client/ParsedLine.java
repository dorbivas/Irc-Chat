package Client;
import java.lang.NumberFormatException;
import java.util.Hashtable;

public class ParsedLine
{
	private String line;
	private Hashtable <String, String> dict;
	
	/** constructor, returning dictionary. 
	 * @param line which is the text
	 * for example input:  ":sweetmorn.cohens.org.il 352 tzafrir #chat-dor ~tzafrir localhost"
	 * output: a dictionary with the following entries:
	 * - server: sweetmorn.cohens.org.il
	 * - command: 352
	 * - nick: tzafrir
	 * - message:  "#chat-dor ~tzafrir localhost"
	 */
	public ParsedLine(String line)
	{
		this.line = line;
		this.dict = new Hashtable <String, String>();
		
		String[] parsed = this.line.split("[ \t]", 3);
		
		int messageCode = -1;
		try {
			messageCode = Integer.getInteger(parsed[1], 10);
		} catch (NumberFormatException e) {};
		
		if (parsed[1].compareTo("PRIVMSG") == 0)
		{
			dict.put("user", RemoveFirst(parsed[0]));
			String[] parsedUser = dict.get("user").split("!", 2);
			dict.put("nick", parsedUser[0]);
			
			dict.put("command", parsed[1]);
			
			String[] parsedArgs = parsed[2].split("[ \t]", 2);
			dict.put("channel", parsedArgs[0]);
			dict.put("message",RemoveFirst(parsedArgs[1]));
		}
		else if (parsed[1].compareTo("NICK") == 0)
		{
			dict.put("user", RemoveFirst(parsed[0]));
			String[] parsedUser = dict.get("user").split("!", 2);
			dict.put("nick", parsedUser[0]);

			dict.put("command", parsed[1]);
			
			dict.put("newnick", RemoveFirst(parsed[2]));
		}
		
		else if (parsed[1].compareTo("JOIN") == 0)
		{
			dict.put("user", RemoveFirst(parsed[0]));
			String[] parsedUser = dict.get("user").split("!", 2);
			dict.put("nick", parsedUser[0]);

			dict.put("command", parsed[1]);
			
			dict.put("channel", parsed[2]);
		}
		
		else if (parsed[0].compareTo("PING") == 0)
		{
			dict.put("server", RemoveFirst(parsed[1]));
			dict.put("command", parsed[0]);
		}
		else if (messageCode > 0) {
			dict.put("server", RemoveFirst(parsed[0]));
			dict.put("command", parsed[1]);
			String[] parsedArgs = parsed[2].split("[ \t]", 2);
			dict.put("nick", parsedArgs[0]);
			dict.put("message", parsedArgs[1]); // FIXME: remove ":". starts at various places for various codes
		}
		else
			System.err.println("Notice: failed to parse message message: [" + line + "]");
	}
	
	/** getting the results from the dictionary
	@param property	the key to look for in the parsed request */
	public String get(String property)
	{
		return dict.get(property);
	}
	
	/** Remove the first char from the input (this char is irelevant). TODO: move to Common.*/
	public static String RemoveFirst(String st)
	{
		return st.substring(1 , st.length());
	}
}

