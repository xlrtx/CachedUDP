import java.nio.ByteBuffer;


public class testServer implements CachedUDPServerCallback{
  
  public static void main( String args[] ){
    new testServer(8888);
  }
  
  
  public testServer(int port){
    CachedUDPServer cuServer = new CachedUDPServer(this, port);
    cuServer.start();
  }
  
  
  /**
   * A dummy application level function, consumes
   * the data passed by smart UDP layer, and generate
   * reply message
   * @param dataIn
   * @return
   * @throws Exception
   */
  public ByteBuffer consumeRequest(ByteBuffer dataIn) throws Exception{
    ByteBuffer dataOut = ByteBuffer.allocate(1024);
    dataOut.put(new String("hello from server").getBytes());
    dataOut.flip();
    return dataOut;
  }
}
