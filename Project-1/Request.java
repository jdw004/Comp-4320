public class Request {

    public int tml;          // total message length (in bytes)
    public byte opcode;      // 0 -, 1 +, 2 &, 3 |, 4 *, 5 /
    public int operand1;     
    public int operand2;     
    public short requestId;  // starts at 1 and increments by 1
    public byte opNameLen;   // length in bytes of opName
    public String opName;    // "addition", "subtraction", "and", "or", "multiplication", "division"
    

   public Request(byte opcode, int op1, int op2, short requestId, String opName) {
    this.opcode = opcode;
    this.operand1 = op1;
    this.operand2 = op2;
    this.requestId = requestId;
    this.opName = opName;
  }

public Request() {} // no-arg for decoder


}
