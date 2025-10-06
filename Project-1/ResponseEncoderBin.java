import java.io.*;

public class ResponseEncoderBin {
  public byte[] encode(Response r) throws IOException {
    // baos = Byte Array Output Stream
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(baos);

    out.writeByte(0);            // TML placeholder
    out.writeInt(r.result);        // 4 bytes
    out.writeByte(r.errorCode);    // 1 bytes
    out.writeShort(r.requestId);   // 2 byte

    out.flush();
    byte[] arr = baos.toByteArray();
    arr[0] = (byte)(arr.length & 0xFF);
    return arr;
  }
}
