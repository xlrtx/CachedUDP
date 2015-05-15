import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;



public class CachedUDPClient implements CachedUDPDefs{
  private static boolean DEBUG = true;
  private DatagramSocket socket;
  private InetSocketAddress serverAddr;
  
  
  
  
  public CachedUDPClient(InetSocketAddress serverAddress) throws Exception {
    
    this.socket = new DatagramSocket();
    this.socket.setSoTimeout(CLIENT_TIME_OUT);
    this.serverAddr = serverAddress;
    
  }
  
  
  
  public void close() throws Exception{
    
    this.socket.close();
    
  }  
  

  
  public ByteBuffer request(ByteBuffer upLyrDataOut, boolean useCache, boolean closeSocket) throws Exception {

    
    // Generate pay load
    ByteBuffer dataOut = CachedUDPUtils.genPayLoad();
    
    
    // Fill pay load header
    int pktOpt;
    if ( useCache ){
      pktOpt = CachedUDPHeader.CU_REQ_CACHE;
    }else{
      pktOpt = CachedUDPHeader.CU_REQ_NOCAC;
    }
    CachedUDPHeader.putHeader(dataOut, pktOpt);
    
    
    // Fill pay load data
    dataOut.put(upLyrDataOut);
    dataOut.flip();
    

    // Transform data, prepare to send.
    byte[] dataOutArray = new byte[dataOut.remaining()];
    dataOut.put(dataOutArray);
    DatagramPacket sendPacket = 
        new DatagramPacket(dataOutArray, dataOutArray.length, 
            this.serverAddr.getAddress(), this.serverAddr.getPort());
    
    
    // Send packet and get response, if timeout then retry.
    int maxRetry        = CLIENT_TIME_OUT_RETRY_MAX;
    ByteBuffer dataIn   = null;
    do{
      
      // Send packet
      this.socket.send(sendPacket);
      
      
      // Receive and transform
      byte[] dataInArray = new byte[MAX_PACKET_SIZE];
      DatagramPacket receivePacket = new DatagramPacket(dataInArray, dataInArray.length);
      try {
        this.socket.receive(receivePacket);
      } catch (IOException e) {
        // Time out
        System.err.printf("Packet loss, retrying ( %d/%d )..",
            CLIENT_TIME_OUT_RETRY_MAX - maxRetry + 1, CLIENT_TIME_OUT_RETRY_MAX);
        continue;
      }
      dataIn = ByteBuffer.allocate(MAX_PACKET_SIZE);
      dataIn.put(receivePacket.getData());
      dataIn.flip();
      break;
      
      
    }while( maxRetry --> 0 );

    
    
    if ( maxRetry == 0 ){
      // Too many retry but no response.
      throw new Exception("Retry excess.\n");
    }
    
    
    // Got response, check header.
    CachedUDPHeader header = CachedUDPHeader.readHeader(dataIn);
    if ( header.isReq() || ( header.isCache() ^ useCache ) ){
      throw new Exception("Illigal response.\n");
    }
    
    
    // Return data to upper layer
    ByteBuffer upLyrDataIn =
        ByteBuffer.allocate(MAX_PACKET_SIZE - dataIn.position());
    upLyrDataIn.put(dataIn);
    upLyrDataIn.flip();
    return upLyrDataIn;
    
    
  }
  
}
