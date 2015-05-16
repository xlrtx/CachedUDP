import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * This is the definition of CachedUDP header.
 * It also includes the function to create a
 * header by reading it from given ByteBuffer.
 * @author xlrtx
 *
 */

public class CachedUDPHeader {
  
  
  // Packet Identifier
  private static final int CU_PACKET_ID          = 0x00CAC5ED;
  // Packet Type
  public static final int CU_REQ_CACHE          = 1;
  public static final int CU_RSP_CACHE          = 2;
  public static final int CU_REQ_NOCAC          = 3;
  public static final int CU_RSP_NOCAC          = 4;
  

  private int packetOpt;
  private int packetToken;
  
  
  private CachedUDPHeader(int packetOpt, int packetToken){
    
    this.packetOpt      = packetOpt;
    this.packetToken    = packetToken;
    
  }
  
  
  
  
  
  /**
   * Put a header with given packet option in the ByteBuffer with a given token
   * @param dataIn
   * @param packetOpt
   * @param packetToken
   * @return Header
   * @throws PuttingCachedUDPHeaderToByteBufferException
   */
  public static CachedUDPHeader putHeader(ByteBuffer dataIn, int packetOpt, int packetToken) 
      throws PuttingCachedUDPHeaderToByteBufferException{
    
    
    if ( !isValidHeader(CU_PACKET_ID, packetOpt) ) {
      throw new PuttingCachedUDPHeaderToByteBufferException("Packet option is invalid.");
    }
    
    
    try{
      
      dataIn.putInt(CU_PACKET_ID);
      dataIn.putInt(packetOpt);
      dataIn.putInt(packetToken);
      
    }catch( Exception e ){
      
      throw new PuttingCachedUDPHeaderToByteBufferException("Packet option is invalid.");
      
    }
    
    return new CachedUDPHeader(packetOpt, packetToken);

  }
  
  
  
  
  /**
   * Put a header with given packet option in the ByteBuffer with a random token
   * @param dataIn
   * @param packetOpt
   * @param packetToken
   * @throws PuttingCachedUDPHeaderToByteBufferException
   */
  public static CachedUDPHeader putHeader(ByteBuffer dataIn, int packetOpt) 
      throws PuttingCachedUDPHeaderToByteBufferException{
    return putHeader(dataIn, packetOpt, randToken());
  }
  
  
  
  
  /**
   * Reads from ByteBuffer and create a CachedUDPHeader. 
   * Position of ByteBuffer will be increased by 8 bytes 
   * after header creation.
   * @param dataIn
   * @return On success, return CachedUDPHeader. On fail, throw exception.
   * @throws CachedUDPHeaderCreateUsingByteBufferException 
   */
  public static CachedUDPHeader readHeader(ByteBuffer dataIn) throws CachedUDPHeaderCreateUsingByteBufferException{
    
    
    int packetId;
    int packetOpt;
    int packetToken;
    
    try{
      
      packetId    = dataIn.getInt();
      packetOpt   = dataIn.getInt();
      packetToken = dataIn.getInt();
      
    }catch ( BufferUnderflowException e ) {
      
      // Data header is not complete
      throw new CachedUDPHeaderCreateUsingByteBufferException("Data header is not complete.");
      
    }
    
    
    if ( isValidHeader(packetId, packetOpt) ) {
      
      return new CachedUDPHeader(packetOpt, packetToken);
      
    }else{
      
      throw new CachedUDPHeaderCreateUsingByteBufferException("Not a valid header read from bytebuffer.");
      
    }
    
    
    
  }
  
  
  
  /**
   * Return if this packet header is valid, based on packet
   * id and packet option
   */
  private static boolean isValidHeader( int packetId, int packetOpt ){
    
    if ( packetId != CU_PACKET_ID ||
        ((packetOpt != CU_REQ_CACHE) && 
         (packetOpt != CU_RSP_CACHE) && 
         (packetOpt != CU_REQ_NOCAC) && 
         (packetOpt != CU_RSP_NOCAC)) ){
      
      
      // Not a CachedUDP packet
      return false;
      
      
    }else{
      
      
      return true;
      
      
    }
    
  }
  
  
  
  
  /**
   * Return if the cache flag is on
   * @return
   */
  public boolean isCache(){
    
    if ( this.packetOpt == CU_REQ_CACHE ||
         this.packetOpt == CU_RSP_CACHE) {
      
      return true;
      
    }else{
      
      return false;
    }
    
    
  }
  
  
  
  
  /**
   * Return if this packet is request
   */
  public boolean isReq(){
    
    
    if ( this.packetOpt == CU_REQ_CACHE ||
         this.packetOpt == CU_REQ_NOCAC ) {
      
      
      return true;
      
    }else{
      
      
      return false;
      
    }
    
    
  }
  
  
  
  
  
  /**
   * Return if this packet is response
   */
  public boolean isRsp(){
    
    
    if ( this.packetOpt == CU_RSP_CACHE ||
         this.packetOpt == CU_RSP_NOCAC ) {
     
     
     return true;
     
   }else{
     
     
     return false;
     
   }
    
    
  }
  
  
  
  
  /**
   * Get the token of this packet.
   * @return
   */
  public int getToken(){
    return this.packetToken;
  }
  
  
  
  
  /**
   * Random token generator
   * @param min
   * @param max
   * @return
   */
  private static int randToken() {

    Random rand = new Random();
    return rand.nextInt();
    
}
  

  
  
  
  /**
   * Self-defined exception.
   * @author xlrtx
   *
   */
  static class CachedUDPHeaderCreateUsingByteBufferException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public CachedUDPHeaderCreateUsingByteBufferException(String message) {
      super(message);
    }
  }



  
  /**
   * Self-defined exception.
   * @author xlrtx
   *
   */
  static class PuttingCachedUDPHeaderToByteBufferException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PuttingCachedUDPHeaderToByteBufferException(String message) {
      super(message);
    }
  }

}

