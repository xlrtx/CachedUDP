
public interface CachedUDPDefs {
  
  
  // Server Time Out ( Clean expired cache interval )
  public static final int SERVER_TIME_OUT   = 1000;
  // Server Cache Time Out
  public static final int SERVER_CACHE_TTL  = 10000;
  // Client Time Out ( Detect packet loss )
  public static final int CLIENT_TIME_OUT   = 1000;
  // Client Retry Time
  public static final int CLIENT_TIME_OUT_RETRY_MAX = 3;
  // Data package size
  public static final int MAX_PACKET_SIZE   = 1024;
  
 
  
  
}
