import java.io.*;
import java.net.DatagramPacket;


public class RequestEncoderBin {
  private static final String ENC = "UTF-16"; // emits BOM (FE FF)

  public byte[] encode(Request r) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(baos);

    out.writeByte(0); //TML placeholder
    out.writeByte(r.opcode);
    out.writeInt(r.operand1);
    out.writeInt(r.operand2);
    out.writeShort(r.requestId);

    byte[] nameBytes = r.opName.getBytes(ENC);
    if (nameBytes.length > 255) {
      throw new IOException("opName too long");
    }
    
    out.writeByte(nameBytes.length);
    out.write(nameBytes);

    out.flush();
    byte[] arr = baos.toByteArray();
    arr[0] = (byte)(arr.length & 0xFF); // 1-byte TML
    return arr;
  }
}