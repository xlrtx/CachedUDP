import java.nio.ByteBuffer;


public class Cache implements CachedUDPDefs{
  
  
  // Data
  private ByteBuffer data;
  // Time of creation
  private long timeStamp;
  
  
  public Cache(ByteBuffer data){
    
    this.data       = data;
    this.timeStamp  = System.currentTimeMillis();
    
  }
  
  
  /**
   * If the cache is expried
   * @return
   */
  public boolean isExpried(){
    
    return (System.currentTimeMillis() - this.timeStamp) > SERVER_CACHE_TTL;
    
  }
  
  
  /**
   * Get the cache data
   * @return
   */
  public ByteBuffer getData(){
    
    return this.data;
    
  }
}
