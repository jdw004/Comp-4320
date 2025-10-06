public class HexDump {

  // make the byte slice into a space-separated uppercase hex (like "11 03 FF")
  public static String toHex(byte[] a, int off, int len) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < len; i++) {
      sb.append(String.format("%02X", a[off + i]));
      if (i + 1 < len) sb.append(" ");
    }
    return sb.toString();
  }
}
