import java.net.*;
import java.io.*;

public class ServerUDP {

  private static String sym(byte code) {
    // Switch case for OP
    switch (code) {
      case 0: return "-";
      case 1: return "+";
      case 2: return "&";
      case 3: return "|";
      case 4: return "*";
      case 5: return "/";
      default: return "?";
    }
  }

  private static int compute(byte opcode, int a, int b) {
    // Fetch Results using OP
    switch (opcode) {
      case 0: return a - b;
      case 1: return a + b;
      case 2: return (a & b);
      case 3: return (a | b);
      case 4: return a * b;
      case 5: return a / b; // may throw /0
      default: throw new IllegalArgumentException("invalid opcode " + opcode);
    }
  }

  public static void main(String[] args) throws Exception {
    // 10010 + our group number (9)
    final int requiredPort = 10010 + 9; // TID=9 => 10019
    int port = requiredPort;

    if (args.length > 0) {
      int argPort = Integer.parseInt(args[0]);
      if (argPort != requiredPort) {
        System.err.println("Per assignment, server must bind to UDP " + requiredPort + " (got " + argPort + ")");
        System.exit(1);
      }
      port = argPort;
    }

    // Intialize socket
    DatagramSocket sock = new DatagramSocket(port);
    System.out.println("Server listening on UDP port " + port);

    RequestDecoderBin reqDec = new RequestDecoderBin();
    ResponseEncoderBin respEnc = new ResponseEncoderBin();

    byte[] buf = new byte[1024];
    while (true) {
      DatagramPacket pkt = new DatagramPacket(buf, buf.length);
      sock.receive(pkt);

      // Hex dump of incoming request
      System.out.println("Request bytes (hex): " + HexDump.toHex(pkt.getData(), pkt.getOffset(), pkt.getLength()));

      byte err = 0;
      int result = 0;
      short rid = 0;

      try {
        Request r = reqDec.decode(pkt);
        rid = r.requestId;
        System.out.printf("Request #%d: %d %s %d  (name: %s)%n",
            (int)(rid & 0xFFFF), r.operand1, sym(r.opcode), r.operand2, r.opName);

        result = compute(r.opcode, r.operand1, r.operand2);
      } catch (Throwable t) {
        err = (byte)127;
      }

      // Fetch response
      Response resp = new Response(result, err, rid);
      byte[] outBytes = respEnc.encode(resp);

      System.out.println("Response bytes (hex): " + HexDump.toHex(outBytes, 0, outBytes.length));
      DatagramPacket out = new DatagramPacket(outBytes, outBytes.length, pkt.getAddress(), pkt.getPort());
      sock.send(out);
    }
  }
}
