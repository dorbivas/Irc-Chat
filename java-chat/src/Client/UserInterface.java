package Client;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UserInterface extends JFrame implements ActionListener, KeyListener, WindowListener
{	
	
	private JTextField message;
	private JTextArea output;
	private ChatClient client;
	private JTextField host = new JTextField("localhost", 15);
	private JTextField port = new JTextField("6667", 4);
	private JButton connect;
	private JButton send;
	
	/**showing your  message on the output text area. */
	public void outputWriter (String line)
	{
		output.append(line + "\n");
	}
	
	/**showing the message on the output text area. */
	private void msgSendRun ()
	{
		if (message.getText().equals(""))
		{	
			return;
		}
			outputWriter("me: " + message.getText());
			client.PRIVMSG(message.getText());
			message.setText("");	
			output.setCaretPosition(output.getText().length());
	}
	
	/**sending connection request and and verify it happened only one time */
	private void msgConnectRun ()
	{
		output.append("Connecting..." + "\n");
		counterClick++;
		this.send.setEnabled(true);
		this.connect.setEnabled(false);
	}
	
	/** clearing the output*/
	public void Clear ()
	{
		output.setText("");
	}
	
	/** Graphic interface recipient the client*/
	public UserInterface(ChatClient client)
	{
		
		this.setTitle("Dor-Chat IRC");
		this.client = client;
		this.setSize(800, 400);
		this.setMinimumSize(new Dimension(600,170));
		
		Font font = new Font ("Ariel" , Font.BOLD ,12);
		
		output = new JTextArea(10, 20);
		output.setBackground(Color.white);
		output.setFont(font);
		output.setEditable(false);
		output.setLineWrap(true);
		output.setWrapStyleWord(true);
				
		JPanel msgPanel = new JPanel();
		JPanel conPanel = new JPanel();
		JPanel vertPanel = new JPanel();
		
	    JScrollPane scrollText = new JScrollPane(output);
		
		message = new JTextField(40);
		message.addKeyListener(this);
		message.setFont(font);
		message.setForeground(Color.BLUE);
		JButton stop = new JButton("Stop");
		stop.setActionCommand("stop");
		stop.addActionListener(this);
		stop.setToolTipText("Click this button to exit.");
		
		this.send = new JButton("Send");
		send.setActionCommand("send");
		send.addActionListener(this);
		send.setToolTipText("Click this button to send message.");
			
		this.connect = new JButton("Connect");
		connect.setActionCommand("connect");
		connect.addActionListener(this);
		connect.setToolTipText("Click this button to connect the server.");
		this.send.setEnabled(false);
		
		vertPanel.setLayout(new BoxLayout(vertPanel, BoxLayout.PAGE_AXIS));	
		
		Dimension MaximumSize = new Dimension (800 , send.getHeight());
		
		msgPanel.add(send);	
		msgPanel.add(new JLabel("Message:"));
		msgPanel.add(message);
		msgPanel.setMaximumSize(MaximumSize);
		
		conPanel.add(connect);
		conPanel.add(new JLabel("host:"));
		conPanel.add(host);
		conPanel.add(new JLabel("port:"));
		conPanel.add(port);
		conPanel.add(stop);
		conPanel.setMaximumSize(MaximumSize);
		
		vertPanel.add(scrollText);
		vertPanel.add(conPanel);
		vertPanel.add(msgPanel);
		
		add(vertPanel);

		setVisible(true);
		
		this.addWindowListener(this);
	}

	int counterClick = 0;
	
	/** Syncing the the action that the button send to the perform
	 * @param e -name of the action  */
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		
		if (command == "stop")
		{	
			client.shutdown();
			System.exit(0);
		}	
		
		if (command == "send")
		{
			msgSendRun();
		}
		
		if (command == "connect" && counterClick == 0)
		{
			if (client.connect(host.getText(),Integer.parseInt(port.getText())))
			{
				msgConnectRun ();
			}
		}
	}
	
	/** Syncing the the action that the event caused to the perform
	 * @param e - event key press  */
	@Override
	public void keyPressed(KeyEvent e)
	{
		
		if (e.getKeyChar() == KeyEvent.VK_ENTER && counterClick != 0)
		{
			msgSendRun();		
		}
		
		if (e.getKeyChar() == KeyEvent.VK_ESCAPE)
		{
			client.shutdown();
			System.exit(0);
		}
		
		if ((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) != 0  && (e.getKeyChar() == KeyEvent.VK_C && counterClick == 0)) 
			
		if (client.connect(host.getText(),Integer.parseInt(port.getText())))
		{
			msgConnectRun();
		}

	}
	
	/** Syncing the the action closing window to the shutdown
	 * @param e - event window closing  */
	public void windowClosing(WindowEvent e) 
	{
		client.shutdown();
		System.exit(0);
    }
    
	public void keyReleased(KeyEvent arg0) {} 

	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void windowActivated(WindowEvent arg0){}

	@Override
	public void windowClosed(WindowEvent arg0){}
	
	@Override
	public void windowDeactivated(WindowEvent arg0){}
	
	@Override
	public void windowDeiconified(WindowEvent arg0){}
	

	@Override
	public void windowIconified(WindowEvent arg0){}
	
	@Override
	public void windowOpened(WindowEvent arg0){}
	
}
