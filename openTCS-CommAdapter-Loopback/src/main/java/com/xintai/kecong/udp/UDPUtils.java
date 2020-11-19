
package  com.xintai.kecong.udp;


import com.xintai.kecong.message.ByteQueue;
import com.xintai.kecong.message.ConnectionEventListener;
import com.xintai.kecong.message.KeCongCommandResponse;
import com.xintai.kecong.message.KeCongMessage;
import com.xintai.kecong.message.KeCongRequestMessage;
import com.xintai.kecong.message.ParseRecievedMessage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;


public class UDPUtils  {
    private static final int MESSAGE_LENGTH = 1024;
    private short nextTransactionId = 0;
    private DatagramSocket socket;
private String host;
private int port;
private int tries;
private int timeout;
private  ParseRecievedMessage parserecivedmessage;
  private ConnectionEventListener<KeCongCommandResponse> connectionEventListener;
   
    public UDPUtils(String  host,int port,int tries,int timeouts,ConnectionEventListener<KeCongCommandResponse> connectionEventListener) {
      try {
       this. connectionEventListener=connectionEventListener;
      this.port = port;
    }
    catch (Exception ex) {
      this.port = 4001;
    }
      this.host=host;   
      this.tries=tries;
      this.timeout=timeout;
        
    }
    protected short getNextTransactionId() {
        return nextTransactionId++;
    }
    private boolean initsucess;
  public boolean isInitsucess() {
    return initsucess;
  }
    public void init()  {   
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(timeout);
            initsucess=true;
        }
        catch (SocketException e) {
           // throw new Exception(e.toString());
        }
    
    }
    public void destroy() {
        socket.close();
    }
public KeCongCommandResponse sendImpl(KeCongRequestMessage  request) {       
   KeCongCommandResponse response=null;
   System.out.println(host+String.valueOf(port));
        try {
            int attempts = tries + 1;
            while (true){
                sendImpl1(request);
                  try {
                response = receiveImpl(request);
                }
                catch (SocketTimeoutException e) {
                attempts--;
                if (attempts > 0)
                continue;                
            //throw new Exception("UDPtilsErro");
                  System.out.println(e.getMessage());
                }
                break;
            }   
        }
        catch (Exception e) {
            //throw new Exception(e);
            System.out.println(e.getMessage());
        }
          connectionEventListener.onIncomingTelegram(response);
             return response;
    }

    private void sendImpl1(KeCongRequestMessage request) throws IOException  {
        byte[] data = request.creatMessage();
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(host),
                port);
        socket.send(packet);
        System.out.println(packet.getSocketAddress());
    }

     private KeCongCommandResponse receiveImpl(KeCongRequestMessage request) throws  Exception {
       DatagramPacket packet = new DatagramPacket(new byte[MESSAGE_LENGTH], MESSAGE_LENGTH);
       ByteQueue queue =null;
   //do{
      socket.receive(packet);
      queue = new ByteQueue(packet.getData(), 0, packet.getLength());
      parserecivedmessage = new  ParseRecievedMessage(queue);
  // }while(KeCongMessage.getCommunictaionSq()!=parserecivedmessage.getConsquence());
    KeCongCommandResponse response;
    try {
    response = parserecivedmessage.ParseResponseMessage();  
    }
    catch (Exception e) {
    throw new Exception(e);
    } 
   
    if (response == null)
    throw new Exception("Invalid response received");  
    
   return response;
    }
}
