import java.nio.ByteBuffer;


public interface CachedUDPServerCallback {
  ByteBuffer consumeRequest(ByteBuffer requestData) throws Exception;
}
