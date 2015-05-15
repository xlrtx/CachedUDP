import java.nio.ByteBuffer;


public class CachedUDPUtils implements CachedUDPDefs{
  
  /**
   * Generates CachedUDP pay load packet
   * @param type
   * @return
   */
  public static ByteBuffer genPayLoad(){
    ByteBuffer payload = ByteBuffer.allocate(MAX_PACKET_SIZE);
    return payload;
  }
}
