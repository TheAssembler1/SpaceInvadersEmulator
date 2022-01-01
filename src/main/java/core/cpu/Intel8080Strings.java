package core.cpu;

public abstract class Intel8080Strings {
    String[] opcodesStrings = new String[] {
            "NOP | 1  4 | - - - - -",
            "LXI B,d16 | 3  10 | - - - - -",
            "STAX B | 1  7 | - - - - -",
            "INX B | 1  5 | - - - - -",
            "INR B | 1  5 | S Z A P -",
            "DCR B | 1  5 | S Z A P -",
            "MVI B,d8 | 2  7 | - - - - -",
            "RLC | 1  4 | - - - - C",
            "*NOP | 1  4 | - - - - -",
            "DAD B | 1  10 | - - - - C",
            "LDAX B | 1  7 | - - - - -",
            "DCX B | 1  5 | - - - - -",
            "INR C | 1  5 | S Z A P -",
            "DCR C | 1  5 | S Z A P -",
            "MVI C,d8 | 2  7 | - - - - -",
            "RRC | 1  4 - - - - C" ,

            "*NOP | 1  4 | - - - - -",
            "LXI D,d16 | 3  10 | - - - - -",
            "STAX D | 1  7 | - - - - -",
            "INX D | 1  5 | - - - - -",
            "INR D | 1  5 | S Z A P -",
            "DCR D | 1  5 | S Z A P -",
            "MVI D,d8 | 2  7 | - - - - -",
            "RAL | 1  4 | - - - - C",
            "*NOP | 1  4 | - - - - -",
            "DAD D | 1  10 | - - - - C",
            "LDAX D | 1  7 | - - - - -",
            "DCX D | 1  5 | - - - - -",
            "INR E | 1  5 | S Z A P -",
            "DCR E | 1  5 | S Z A P -",
            "MVI E,d8 | 2  7 | - - - - -",
            "RAR | 1  4 | - - - - C"
    };

    protected String getOpcodeString(short opcode){
        return opcodesStrings[opcode];
    }
}
