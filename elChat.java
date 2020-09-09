import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import MySocket;
import SwingWorker;

public class elChat extends Frame {
	public static int WIDTH = 400;
	public static int HEIGHT = 450;
	public static String TITLE = "elChat";
  MySocket cliente = new MySocket();
  thread2 t2 = new thread2();
	
	//swing components
	JTabbedPane tabbedPane = new JTabbedPane();    //grupo de componentes que cambien en cada tab
	JPanel ChatPanel = new JPanel(null);           //Panel del Chat 
	JPanel OptionsPanel = new JPanel(null);        //Panel de las opciones
	JPanel AboutPanel = new JPanel(null);          //Panel de About
	JPanel[] Panels = { ChatPanel, OptionsPanel, 
                      AboutPanel };              //Lista de los paneles
	
  Icon elChatIcon = new ImageIcon("elChat.gif"); //Imagen o icono
	
  JTextField TextField1 = new JTextField();                 //envia texto
  JTextField TextField2 = new JTextField("John Doe");       //Nickname
  JTextField TextField3 = new JTextField("192.168.0.1");    //Host
  JTextField TextField4 = new JTextField("8196");           //Puerto
  	
  JButton Button1 = new JButton("Send");
  JButton Button2 = new JButton("Connect");
  JButton Button3 = new JButton("Disconnect");
	
  JTextArea TextArea1 = new JTextArea();
  JTextArea TextArea2 = new JTextArea();
  
  JScrollPane DisplayScroll = new JScrollPane(TextArea1);
  JScrollPane ScrollTextArea2 = new JScrollPane(TextArea2);
	
	public elChat() {
		super(TITLE);
		addWindowListener(new WindowHandler());    
		buildGUI();
		setSize(WIDTH,HEIGHT);
		setBackground(Color.darkGray);
		show();
		TextArea1.setRows(15);
		TextArea2.setRows(5);
		try
		{
      t2.setPriority(2);
		}
		catch(IllegalArgumentException ex)
		{
			TextArea1.append("Error: Priority\n");
		}
    t2.start();
	}
	
	void buildGUI() {
		String[] Tabs = {"elChat", "Options", "About"};
		String[] TabTips = {"You can chat here...", "Configure here...", "About..."};
		//Icon[] TabIcons= { elChatIcon, null, null};
	
		for (int i=0;i<Tabs.length;++i)
		{ Panels[i].setBackground(Color.lightGray);
			Panels[i].setBorder(new BevelBorder(BevelBorder.LOWERED));
			tabbedPane.addTab(Tabs[i], null, Panels[i], TabTips[i]);
		}
		addComponentsToTabs();
		add("Center",tabbedPane);
	}
	
	void addComponentsToTabs() {
		setupChatPanel();
		setupOptionsPanel();
		setupAboutPanel();
	}
	
	void setupChatPanel() 
  {	
		TextArea1.setEditable(false);
		DisplayScroll.setBounds(20,30,340,280);
		ChatPanel.add(DisplayScroll);
			
		TextField1.setBounds(20,340,250,20);
    TextField1.addActionListener(new TextField1Handler());
		ChatPanel.add(TextField1);
		
		Button1.setBounds(290,338,70,25);		
		Button1.addActionListener(new ButtonHandler());
		ChatPanel.add(Button1);
	}
	
	void setupOptionsPanel() 
  {
  	JLabel OLabel1 = new JLabel("Nick:");
  	JLabel OLabel2 = new JLabel("Host:");
    JLabel OLabel3 = new JLabel("Port:");
    
    OLabel1.setBounds( WIDTH / 8, HEIGHT/6 - 30, 60, 10);
    OLabel2.setBounds( WIDTH / 8, HEIGHT/6 + 10, 60, 10);
    OLabel3.setBounds( WIDTH / 8, HEIGHT/6 + 50, 60, 10);
    
    TextField2.setBounds( WIDTH / 8 + 50, HEIGHT/6 - 35, 220, 20);
    TextField3.setBounds( WIDTH / 8 + 50, HEIGHT/6 +  5, 220, 20);
    TextField4.setBounds( WIDTH / 8 + 50, HEIGHT/6 + 45, 220, 20);

    Button2.setBounds(WIDTH / 2 - 125, HEIGHT / 6 + 100, 100, 25);
    Button3.setBounds(WIDTH / 2 +  25, HEIGHT / 6 + 100, 100, 25);
    Button2.addActionListener(new ButtonHandler());
    Button3.addActionListener(new ButtonHandler());

    TextArea2.setEditable(false);
    ScrollTextArea2.setBounds( WIDTH/8,  HEIGHT / 6 + 160, 300, 120);   

    OptionsPanel.add(OLabel1);
    OptionsPanel.add(OLabel2);
    OptionsPanel.add(OLabel3);
    
    OptionsPanel.add(TextField2);
    OptionsPanel.add(TextField3);
    OptionsPanel.add(TextField4);
    OptionsPanel.add(Button2);
    OptionsPanel.add(Button3);
    OptionsPanel.add(ScrollTextArea2);  	
	}
	
	void setupAboutPanel() 
  {
		Icon[] dibujos = { elChatIcon };
		JList IconList = new JList(dibujos);
    
    JLabel ALabel1 = new JLabel("elChat 0.3a");
    JLabel ALabel2 = new JLabel("Juan Jose Garcia Lau   97172");
    JLabel ALabel3 = new JLabel("Jose Esteban Maldonado 97199");
    JLabel ALabel4 = new JLabel("Universidad del Valle de Guatemala");
    JLabel ALabel5 = new JLabel("Proyecto de Redes");
    JLabel ALabel6 = new JLabel("Prof. Luis Fernando Barrera");
    JLabel ALabel7 = new JLabel("Guatemala, Junio 2000");
    
    IconList.setBounds(WIDTH / 4, HEIGHT / 4, 44, 29);
    ALabel1.setBounds( WIDTH / 4 + 50, HEIGHT /4 +  15, 200, 10);
    ALabel2.setBounds( WIDTH / 4, HEIGHT /4 +  50, 200, 10);
    ALabel3.setBounds( WIDTH / 4, HEIGHT /4 +  65, 200, 10);
    ALabel4.setBounds( WIDTH / 4, HEIGHT /4 + 100, 200, 10);
    ALabel5.setBounds( WIDTH / 4, HEIGHT /4 + 115, 200, 10);
    ALabel6.setBounds( WIDTH / 4, HEIGHT /4 + 130, 200, 10);
    ALabel7.setBounds( WIDTH / 4, HEIGHT /4 + 160, 200, 10);   
     
    AboutPanel.add(ALabel1);
    AboutPanel.add(ALabel2);
    AboutPanel.add(ALabel3);
    AboutPanel.add(ALabel4);
    AboutPanel.add(ALabel5);
    AboutPanel.add(ALabel6);
    AboutPanel.add(ALabel7);    
    AboutPanel.add(IconList);
	}
	
	public static void main(String[] args) {
		elChat app = new elChat();

	}
	
	public class WindowHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			if (cliente.IsOpen())
			{
				cliente.Send("#quit\n");
				cliente.Close();
			}
			System.exit(0);
		}
	}
	
	class ButtonHandler implements ActionListener {
 		public void actionPerformed(ActionEvent ev) {
			String s = ev.getActionCommand();    
			if (s.equals("Send")) 
      {
      	if(TextField1.getText() != null)
				 mandar();
			}
      else if (s.equals("Connect")) 
      {
      	connect();               	
//        Boolean t = new Boolean(t2.isDaemon());
//        TextArea2.append(t.toString());
	    }
      else if (s.equals("Disconnect")) 
      {
      	int err = 0;
				if(cliente.IsOpen())
				 cliente.Send("#quit\n");
        err = cliente.Close();
        if (err == 0) 
        {           
        	TextArea2.append("Disconnected...\n");
        }
        else if (err == -1) 
          TextArea2.append("Can't close port\n");
          
      }
		}
	}
  
  class TextField1Handler implements ActionListener 
  { 
  	public void actionPerformed(ActionEvent ev) 
  	{
    	mandar();
  	}  	
  }  
  
  private void mandar()
  {
  	if (cliente.IsOpen())
	  {
		  TextArea1.append(TextField2.getText() + "> " +
			                 TextField1.getText() + "\n");
			cliente.Send(TextField1.getText()+"\n");
	    TextField1.setText(null);  	  
	  }
  }
  
  class thread2 extends Thread
  {
    public void run() {      
      //int t;
      //while (cliente.IsOpen())
      String s = null;
      while (true)
      { 
        while (cliente.IsOpen())
        {
          s = cliente.Receive();
          if (s != null)
            TextArea1.append(s + "\n");
        }        
      }    
    }
  }
 
  private void connect()
  {
  	final SwingWorker thread1 = new SwingWorker() 
    {
  	  public Object construct() 
     	{
       	int er = 0;
        int port = 0;
        String s;        
        s = TextField3.getText();
        TextArea2.append("Connecting...\n");
        try 
        { 
         	port = Integer.valueOf(TextField4.getText()).intValue();
        }
        catch (NumberFormatException ex)
        {
         	TextArea2.append("Invalid port\n");
          er = -1;
        }                    
      	if (er == 0)
        {        	
          er = cliente.Open(s,port);
          if (er == 0)
          {
            TextArea2.append("Conectado a " + cliente.getHostAddress() + 
                             " en el puerto " + cliente.getServerPort() + "\n");                       
            TextArea2.append("Host name " + cliente.getHostName() + "\n");
            TextArea2.append("Puerto para el Cliente " + cliente.getClientPort() + "\n");
            cliente.Send(TextField2.getText()+"\n");            
          }          
          else if (er == -1)
           	TextArea2.append("Connection error\n");
        	else if (er == -2)
         	 	TextArea2.append("Already connected\n");
        }
        //TextArea2.append("El error = " + Integer.toString(er) + "\n");
     	  return null;        	      
    	}
    };
    thread1.start();
  }
    

}

