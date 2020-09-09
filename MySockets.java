import java.lang.System;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.*;
import java.util.Vector;

class MySocket 
{
	private Socket connection;
  private String Host;
  private int Port;
  private InetAddress ServerAddress;
  private String HostName;
  private String HostAddress;  
  private byte IPAddress[];  
  private int ServerPort;
  private int ClientPort;
  private boolean IsConnected;
  
  private BufferedReader inStream;
  private DataOutputStream outStream;
  
  public MySocket()   //Host, Port
  { Host = "127.0.0.1";
  	Port = 8196;
    IsConnected = false;
  }  


  public MySocket(String s, int i)   //Host, Port
  { 
    Host = s;
  	Port = i;
    IsConnected = false;
  }
  
  public boolean IsOpen()
  {
  	return IsConnected;
  }
  
  public int Open(String s, int i)
  {
  	Host = s;
  	Port = i;
    return Open();
  }
    
  public int Open()
  { 
    int err = 0;   
    if (IsConnected == false) 
    {
    	try 
    	{
      	connection = new Socket(Host, Port);
    	}
    	catch (IOException ex) 
    	{
    		err = -1;
    	}
      if (err == 0) 
      {
      	try 
      	{
        	inStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
          outStream = new DataOutputStream(connection.getOutputStream());
      	}
        catch (IOException ex)
        {
        	err = -3;
        }          
        if (err == 0)
        {
        	IsConnected = true;
          ServerAddress = connection.getInetAddress();
          HostName = ServerAddress.getHostName();
          HostAddress = ServerAddress.getHostAddress();
          IPAddress = ServerAddress.getAddress();
          ServerPort = connection.getPort();
          ClientPort = connection.getLocalPort();
        }      
      }
    }
    else 
    	err = -2;
    return err;    
  }  
  
  public String getHostName()
  { 
  	if (IsConnected)
      return HostName;
    else 
      return null;  
  }
  
  public String getHostAddress()
  { 
  	if (IsConnected)
      return HostAddress;
    else 
      return null;  
  }
  
  public int getServerPort()
  {
  	if (IsConnected)
     	return ServerPort;
    else 
      return 0;  
  }

  public int getClientPort()
  {
  	if (IsConnected)
      return ClientPort;
    else
      return 0;  
  }
  
  public int Send(String S)
  {
  	int err = 0;
  	try 
  	{
			outStream.flush();
			outStream.write(S.getBytes(),0,S.length());
  	}
    catch (IOException ex)
    {
    	err = -1;
    }
    return 0;
  }

//  public int Send(String S)
//  {
//  	int err = 0;
//  	try 
//  	{
//     	outStream.writeBytes(S);
//      outStream.write(13);
//      outStream.write(10);
//      outStream.flush();
//  	}
//    catch (IOException ex)
//    {
//    	err = -1;
//    }
//    return 0;
//  }
  
  public String Receive()
  {
  	int inByte = 0;
    String s = new String();    
    if (IsConnected)
    { 
      try 
    	{   	
        while ( ((inByte = inStream.read()) != '\n'))
        {           
          s = s + (char) inByte;
        }
        
      }    
      catch (IOException ex)
      {
    	  s = null;
      }
    }
    else 
    	s = null;
    return s;    
  }
  
  public int ReceiveInt()
  {
  	int data = 0;
    if (IsConnected)
    {
    	try
    	{
      	data = inStream.read();
    	} 
      catch (IOException ex)
      {
      	data = -1;
      }
    }
    return data;
  }
  

  public int Close()
  { 
  	int err = 0;
    if (IsConnected) 
    {
			try 
      {
      	connection.close();                
      }
      catch (IOException ex)
      {
    	  err = -1;
      }
    }
    else 
    	err = -1;
    if (err == 0)  
    	IsConnected = false;    
    return err;
  }  
}