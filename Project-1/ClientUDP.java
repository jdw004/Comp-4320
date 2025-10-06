import java.net.*;
import java.io.*;
import java.util.*;

public class ClientUDP {

  private static final Map<Integer,String> NAME = new HashMap<>();
  static {
    NAME.put(0, "subtraction");
    NAME.put(1, "addition");
    NAME.put(2, "and");
    NAME.put(3, "or");
    NAME.put(4, "multiplication");
    NAME.put(5, "division");
  }

  public static void main(String[] args) throws Exception {
    // Was not called correctly
    if (args.length != 2) {
      System.err.println("Usage: java ClientUDP <serverHost> <port>");
      System.err.println("Example: java ClientUDP tuxXX.cs.auburn.edu 10019");
      System.exit(1);
    }
    InetAddress server = InetAddress.getByName(args[0]);
    int port = Integer.parseInt(args[1]);
    
    // our portID is 10019
    if (port != 10010 + 9) {
      System.err.println("Reminder: per assignment, server uses UDP 10019 (TID=9). You're targeting " + port + ".");
    }

    // Since this is UDP, we use Datagram functions
    DatagramSocket sock = new DatagramSocket();
    sock.setSoTimeout(3000);

    RequestEncoderBin enc = new RequestEncoderBin();
    ResponseDecoderBin dec = new ResponseDecoderBin();
    // To read input
    Scanner sc = new Scanner(System.in);

    long minRtt = Long.MAX_VALUE, maxRtt = Long.MIN_VALUE, sumRtt = 0;
    int count = 0;
    short rid = 1;

    System.out.println("Enter: <opcode 0..5> <operand1 int> <operand2 int> (or 'q' to quit)");
    while (true) {
      System.out.print("> ");
      if (!sc.hasNext()) break;
      if (sc.hasNext("q|quit|exit")) break;

      // Fetch opcode and info
      int opcode = sc.nextInt();
      int a = sc.nextInt();
      int b = sc.nextInt();

      String opName = NAME.getOrDefault(opcode, "unknown");
      Request r = new Request((byte)opcode, a, b, rid, opName);
      byte[] outBytes = enc.encode(r);

      // Display request
      System.out.println("Request bytes (hex): " + HexDump.toHex(outBytes, 0, outBytes.length));

      DatagramPacket out = new DatagramPacket(outBytes, outBytes.length, server, port);

      long t0 = System.nanoTime();
      sock.send(out);

      byte[] inBuf = new byte[1024];
      DatagramPacket in = new DatagramPacket(inBuf, inBuf.length);

      try {
        sock.receive(in);
      } catch (SocketTimeoutException e) {
        System.out.println("Timed out waiting for server.");
        continue;
      }
      
      // to get our rtt
      long t1 = System.nanoTime();
      long rttMs = (t1 - t0) / 1_000_000;

      System.out.println("Response bytes (hex): " + HexDump.toHex(in.getData(), in.getOffset(), in.getLength()));

      Response resp = dec.decode(in);
      System.out.printf("Response (reqId=%d): result=%d, error=%s, RTT=%d ms%n",
          (int)(resp.requestId & 0xFFFF), resp.result, (resp.errorCode == 0 ? "OK" : "ERR"), rttMs);

      // calculate min/avg/max round-trip time for report
      minRtt = Math.min(minRtt, rttMs);
      maxRtt = Math.max(maxRtt, rttMs);
      sumRtt += rttMs;
      count++;
      rid++;

      System.out.println("Enter next request or 'q' to quit.");
    }

    // display round-trip time stats
    if (count > 0) {
      System.out.printf("RTT stats: min=%d ms, avg=%.2f ms, max=%d ms%n",
          minRtt, (sumRtt * 1.0 / count), maxRtt);
    }
    sock.close();
  }
}
