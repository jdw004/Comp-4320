import java.io.*;
import java.net.DatagramPacket;

public class RequestDecoderBin {
  private static final String ENC = "UTF-16";

  public Request decode(DatagramPacket p) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(p.getData(), p.getOffset(), p.getLength());
    DataInputStream in = new DataInputStream(bais);

    int tml = in.readUnsignedByte(); // 1 byte
    byte opcode = in.readByte();     // 1 byte
    int op1 = in.readInt();          // 4 bytes
    int op2 = in.readInt();          // 4 bytes
    short reqId = in.readShort();    // 2 bytes
    int nameLen = in.readUnsignedByte(); // 1 byte

    byte[] nameBytes = new byte[nameLen];
    in.readFully(nameBytes);         // nameLen

    int consumed = 1 + 1 + 4 + 4 + 2 + 1 + nameLen;
    
    if (tml != consumed){
      throw new IOException("TML mismatch: got " + tml + " expected " + consumed);
    }

    Request r = new Request();
    r.tml = tml;
    r.opcode = opcode;
    r.operand1 = op1;
    r.operand2 = op2;
    r.requestId = reqId;
    r.opNameLen = (byte)nameLen;
    r.opName = new String(nameBytes, ENC);
    return r;
  }
}
