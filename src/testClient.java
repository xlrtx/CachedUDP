import java.net.InetSocketAddress;
import java.nio.ByteBuffer;


public class testClient {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    new testClient("127.0.0.1", 8888);
  }

  
  public testClient(String addr, int port){
    InetSocketAddress serverAddr = 
        new InetSocketAddress(addr, port);
    CachedUDPClient cuClient = null;
    try {
      cuClient = new CachedUDPClient(serverAddr);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      System.err.println("Failed to create cuClient");
      System.exit(1);
      e.printStackTrace();
    }
    
    
    int count = 10;
    ByteBuffer dataOut = ByteBuffer.allocate(1024);
    dataOut.put(new String("hello from client").getBytes());
    dataOut.flip();
    while( count-->0 ){
      try {
        cuClient.request(dataOut, true, false);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
  }
}
