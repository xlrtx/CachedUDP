import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;



public class CachedUDPClient implements CachedUDPDefs{
  private static boolean DEBUG = true;
  private static boolean DEBUG_PACKET_LOSS = true;
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
  

  /**
   * Request server with given ByteBuffer.
   * ByteBuffer will be fully read (position will be changed) if send success.
   * @param upLyrDataOut    The data to be sent
   * @param useCache        Is cache allowed to use by the server?
   * @param closeSocket     Will the socket be closed after the request?
   * @return
   * @throws Exception
   */
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
    CachedUDPHeader dataOutHeader = 
        CachedUDPHeader.putHeader(dataOut, pktOpt);
    
    
    // Fill pay load data
    dataOut.put(upLyrDataOut);
    dataOut.flip();
    

    // Transform data, prepare to send.
    byte[] dataOutArray = new byte[dataOut.remaining()];
    dataOut.get(dataOutArray);
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
        if ( DEBUG && DEBUG_PACKET_LOSS && CachedUDPUtils.randInt(0,1) == 1 ){
          throw new SocketTimeoutException();
        }
        
      } catch (SocketTimeoutException  e) {
        // Time out
        System.err.printf("%x Expect to be lost, retry = %d..\n", dataOutHeader.getToken(), maxRetry);
        continue;
      }
      dataIn = ByteBuffer.allocate(MAX_PACKET_SIZE);
      dataIn.put(receivePacket.getData());
      dataIn.flip();
      break;
      
      
    }while( --maxRetry > 0 );

    
    
    if ( maxRetry == 0 ){
      // Too many retry but no response.
      throw new Exception("Retry excess.\n");
    }
    
    
    // Got response, check header.
    CachedUDPHeader header = CachedUDPHeader.readHeader(dataIn);
    if ( header.isReq() || ( useCache == false && header.isCache() ) ){
      throw new Exception("Illigal response.\n");
    }
    
   
    
    
    // Return data to upper layer
    ByteBuffer upLyrDataIn =
        ByteBuffer.allocate(MAX_PACKET_SIZE - dataIn.position());
    upLyrDataIn.put(dataIn);
    upLyrDataIn.flip();
    
    
    
    // Close the socket if said so
    if ( closeSocket ){
      this.socket.close();
    }
    
    
    
    // Lets see if the data is from cache
    if ( DEBUG ){
      
      byte[] data = new byte[upLyrDataIn.remaining()];
      upLyrDataIn.get(data);
      String replyStr = new String(data);
      
      
      if ( header.isCache() ){
        System.out.printf("%x [cache] %s\n\n", header.getToken(), replyStr);
      }else{
        System.out.printf("%x [fresh] %s\n\n", header.getToken(), replyStr);
      }
      
      upLyrDataIn.flip();
      
    }
    
    
    return upLyrDataIn;
    
    
  }
  
}
