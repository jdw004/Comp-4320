import java.io.*;
import java.net.DatagramPacket;

public class ResponseDecoderBin {
  public Response decode(DatagramPacket p) throws IOException {
    // bais = Byte Array Input Stream
    ByteArrayInputStream bais = new ByteArrayInputStream(p.getData(), p.getOffset(), p.getLength());
    DataInputStream in = new DataInputStream(bais);

    int tml = in.readUnsignedByte(); // 1 byte
    int result = in.readInt();       // 4 bytes
    byte err = in.readByte();        // 1 bytes
    short rid = in.readShort();      // 2 bytes

    int consumed = 1 + 4 + 1 + 2;
    if (tml != consumed) throw new IOException("TML mismatch: got " + tml + " expected " + consumed);

    Response r = new Response();
    r.tml = tml;
    r.result = result;
    r.errorCode = err;
    r.requestId = rid;
    return r;
  }
}
