import java.nio.ByteBuffer;
import java.util.Random;


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
  
  
  /**
   * Random number generator
   * @param min
   * @param max
   * @return
   */
  public static int randInt( int min, int max ) {

    Random rand = new Random();
    int randomNum = rand.nextInt((max - min) + 1) + min;
    return randomNum;
    
  }
}
