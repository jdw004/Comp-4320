public class Response {
  public int tml;          // total message length (in bytes)
  public int result;       
  public byte errorCode;   // 0=OK, 127=ERR
  public short requestId;  // starts at 1 and increments by 1

  public Response(int result, byte errorCode, short requestId) {
    this.result = result;
    this.errorCode = errorCode;
    this.requestId = requestId;
  }

  public Response() {}
}