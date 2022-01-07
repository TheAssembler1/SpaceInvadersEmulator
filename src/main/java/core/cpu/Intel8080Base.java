package core.cpu;

import core.memory.Mmu;

import java.nio.ByteBuffer;

public abstract class Intel8080Base {
    private final Registers registers = new Registers();

    protected final Mmu mmu;
    protected int cycles = 0;

    protected boolean cpuStoppped = false;

    private static class Registers {
        byte a;
        byte f;

        byte b;
        byte c;

        byte d;
        byte e;

        byte h;
        byte l;

        short sp;
        short pc;
    }

    //NOTE::M stands for core.memory; needed for opcodes
    protected enum Register {
        AF, BC, DE, HL,
        A, F, B, C, D, E, H, L,
        SP, PC,
        M
    }

    protected enum Flags{
        SIGN_FLAG(7),
        ZERO_FLAG(6),
        AUXILIARY_CARRY_FLAG(4),
        PARITY_FLAG(2),
        CARRY_FLAG(0);

        int bit;
        Flags(int bit){
            this.bit = bit;
        }

        int getBit(){
            return bit;
        }
    }

    protected enum FlagChoice{
        TRUE, FALSE, NULL
    }

    protected enum Operation {
        ADD, SUB, AND, OR, XOR, NULL
    }

    private final int[] parityReference = {
        1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0,
        0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1,
        0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1,
        1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0,
        0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1,
        1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0,
        1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0,
        0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1
    };

    private final String[] opcodesStrings = new String[]{
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
            "RRC | 1  4 | - - - - C",
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
            "RAR | 1  4 | - - - - C",
            "*NOP | 1  4 | - - - - -",
            "LXI H,d16 | 3  10 | - - - - -",
            "SHLD a16 | 3  16 | - - - - -",
            "INX H | 1  5 | - - - - -",
            "INR H | 1  5 | S Z A P -",
            "DCR H | 1  5 | S Z A P -",
            "MVI H,d8 | 2  7 | - - - - -",
            "DAA | 1  4 | S Z A P C",
            "*NOP | 1  4 | - - - - -",
            "DAD H | 1  10 | - - - - C",
            "LHLD a16 | 3  16 | - - - - -",
            "DCX H | 1  5 | - - - - -",
            "INR L | 1  5 | S Z A P -",
            "DCR L | 1  5 | S Z A P -",
            "MVI L,d8 | 2  7 | - - - - -",
            "CMA | 1  4 | - - - - -",
            "*NOP | 1  4 | - - - - -",
            "LXI SP,d16 | 3  10 | - - - - -",
            "STA a16 | 3  13 | - - - - -",
            "INX SP | 1  5 | - - - - -",
            "INR M | 1  10 | S Z A P -",
            "DCR M | 1  10 | S Z A P -",
            "MVI M,d8 | 2  10 | - - - - -",
            "STC | 1  4 | - - - - C",
            "*NOP | 1  4 | - - - - -",
            "DAD SP | 1  10 | - - - - C",
            "LDA a16 | 3  13 | - - - - -",
            "DCX SP | 1  5 | - - - - -",
            "INR A | 1  5 | S Z A P -",
            "DCR A | 1  5 | S Z A P -",
            "MVI A,d8 | 2  7 | - - - - -",
            "CMC | 1  4 | - - - - C",
            "MOV B,B | 1  5 | - - - - -",
            "MOV B,C | 1  5 | - - - - -",
            "MOV B,D | 1  5 | - - - - -",
            "MOV B,E | 1  5 | - - - - -",
            "MOV B,H | 1  5 | - - - - -",
            "MOV B,L | 1  5 | - - - - -",
            "MOV B,M | 1  7 | - - - - -",
            "MOV B,A | 1  5 | - - - - -",
            "MOV C,B | 1  5 | - - - - -",
            "MOV C,C | 1  5 | - - - - -",
            "MOV C,D | 1  5 | - - - - -",
            "MOV C,E | 1  5 | - - - - -",
            "MOV C,H | 1  5 | - - - - -",
            "MOV C,L | 1  5 | - - - - -",
            "MOV C,M | 1  7 | - - - - -",
            "MOV C,A | 1  5 | - - - - -",
            "MOV D,B | 1  5 | - - - - -",
            "MOV D,C | 1  5 | - - - - -",
            "MOV D,D | 1  5 | - - - - -",
            "MOV D,E | 1  5 | - - - - -",
            "MOV D,H | 1  5 | - - - - -",
            "MOV D,L | 1  5 | - - - - -",
            "MOV D,M | 1  7 | - - - - -",
            "MOV D,A | 1  5 | - - - - -",
            "MOV E,B | 1  5 | - - - - -",
            "MOV E,C | 1  5 | - - - - -",
            "MOV E,D | 1  5 | - - - - -",
            "MOV E,E | 1  5 | - - - - -",
            "MOV E,H | 1  5 | - - - - -",
            "MOV E,L | 1  5 | - - - - -",
            "MOV E,M | 1  7 | - - - - -",
            "MOV E,A | 1  5 | - - - - -",
            "MOV H,B | 1  5 | - - - - -",
            "MOV H,C | 1  5 | - - - - -",
            "MOV H,D | 1  5 | - - - - -",
            "MOV H,E | 1  5 | - - - - -",
            "MOV H,H | 1  5 | - - - - -",
            "MOV H,L | 1  5 | - - - - -",
            "MOV H,M | 1  7 | - - - - -",
            "MOV H,A | 1  5 | - - - - -",
            "MOV L,B | 1  5 | - - - - -",
            "MOV L,C | 1  5 | - - - - -",
            "MOV L,D | 1  5 | - - - - -",
            "MOV L,E | 1  5 | - - - - -",
            "MOV L,H | 1  5 | - - - - -",
            "MOV L,L | 1  5 | - - - - -",
            "MOV L,M | 1  7 | - - - - -",
            "MOV L,A | 1  5 | - - - - -",
            "MOV M,B | 1  7 | - - - - -",
            "MOV M,C | 1  7 | - - - - -",
            "MOV M,D | 1  7 | - - - - -",
            "MOV M,E | 1  7 | - - - - -",
            "MOV M,H | 1  7 | - - - - -",
            "MOV M,L | 1  7 | - - - - -",
            "HLT | 1  7 | - - - - -",
            "MOV M,A | 1  7 | - - - - -",
            "MOV A,B | 1  5 | - - - - -",
            "MOV A,C | 1  5 | - - - - -",
            "MOV A,D | 1  5 | - - - - -",
            "MOV A,E | 1  5 | - - - - -",
            "MOV A,H | 1  5 | - - - - -",
            "MOV A,L | 1  5 | - - - - -",
            "MOV A,M | 1  7 | - - - - -",
            "MOV A,A | 1  5 | - - - - -",
            "ADD B | 1  4 | S Z A P C",
            "ADD C | 1  4 | S Z A P C",
            "ADD D | 1  4 | S Z A P C",
            "ADD E | 1  4 | S Z A P C",
            "ADD H | 1  4 | S Z A P C",
            "ADD L | 1  4 | S Z A P C",
            "ADD M | 1  7 | S Z A P C",
            "ADD A | 1  4 | S Z A P C",
            "ADC B | 1  4 | S Z A P C",
            "ADC C | 1  4 | S Z A P C",
            "ADC D | 1  4 | S Z A P C",
            "ADC E | 1  4 | S Z A P C",
            "ADC H | 1  4 | S Z A P C",
            "ADC L | 1  4 | S Z A P C",
            "ADC M | 1  7 | S Z A P C",
            "ADC A | 1  4 | S Z A P C",
            "SUB B | 1  4 | S Z A P C",
            "SUB C | 1  4 | S Z A P C",
            "SUB D | 1  4 | S Z A P C",
            "SUB E | 1  4 | S Z A P C",
            "SUB H | 1  4 | S Z A P C",
            "SUB L | 1  4 | S Z A P C",
            "SUB M | 1  7 | S Z A P C",
            "SUB A | 1  4 | S Z A P C",
            "SBB B | 1  4 | S Z A P C",
            "SBB C | 1  4 | S Z A P C",
            "SBB D | 1  4 | S Z A P C",
            "SBB E | 1  4 | S Z A P C",
            "SBB H | 1  4 | S Z A P C",
            "SBB L | 1  4 | S Z A P C",
            "SBB M | 1  7 | S Z A P C",
            "SBB A | 1  4 | S Z A P C",
            "ANA B | 1  4 | S Z A P C",
            "ANA C | 1  4 | S Z A P C",
            "ANA D | 1  4 | S Z A P C",
            "ANA E | 1  4 | S Z A P C",
            "ANA H | 1  4 | S Z A P C",
            "ANA L | 1  4 | S Z A P C",
            "ANA M | 1  7 | S Z A P C",
            "ANA A | 1  4 | S Z A P C",
            "XRA B | 1  4 | S Z A P C",
            "XRA C | 1  4 | S Z A P C",
            "XRA D | 1  4 | S Z A P C",
            "XRA E | 1  4 | S Z A P C",
            "XRA H | 1  4 | S Z A P C",
            "XRA L | 1  4 | S Z A P C",
            "XRA M | 1  7 | S Z A P C",
            "XRA A | 1  4 | S Z A P C",
            "ORA B | 1  4 | S Z A P C",
            "ORA C | 1  4 | S Z A P C",
            "ORA D | 1  4 | S Z A P C",
            "ORA E | 1  4 | S Z A P C",
            "ORA H | 1  4 | S Z A P C",
            "ORA L | 1  4 | S Z A P C",
            "ORA M | 1  7 | S Z A P C",
            "ORA A | 1  4 | S Z A P C",
            "CMP B | 1  4 | S Z A P C",
            "CMP C | 1  4 | S Z A P C",
            "CMP D | 1  4 | S Z A P C",
            "CMP E | 1  4 | S Z A P C",
            "CMP H | 1  4 | S Z A P C",
            "CMP L | 1  4 | S Z A P C",
            "CMP M | 1  7 | S Z A P C",
            "CMP A | 1  4 | S Z A P C",
            "RNZ | 1  11/5 | - - - - -",
            "POP B | 1  10 | - - - - -",
            "JNZ a16 | 3  10 | - - - - -",
            "JMP a16 | 3  10 | - - - - -",
            "CNZ a16 | 3  17/11 | - - - - -",
            "PUSH B | 1  11 | - - - - -",
            "ADI d8 | 2  7 | S Z A P C",
            "RST 0 | 1  11 | - - - - -",
            "RZ | 1  11/5 | - - - - -",
            "RET | 1  10 | - - - - -",
            "JZ a16 | 3  10 | - - - - -",
            "*JMP a16 | 3  10 | - - - - -",
            "CZ a16 | 3  17/11 | - - - - -",
            "CALL a16 | 3  17 | - - - - -",
            "ACI d8 | 2  7 | S Z A P C",
            "RST 1 | 1  11 | - - - - -",
            "RNC | 1  11/5 | - - - - -",
            "POP D | 1  10 | - - - - -",
            "JNC a16 | 3  10 | - - - - -",
            "OUT d8 | 2  10 | - - - - -",
            "CNC a16 | 3  17/11 | - - - - -",
            "PUSH D | 1  11 | - - - - -",
            "SUI d8 | 2  7 | S Z A P C",
            "RST 2 | 1  11 | - - - - -",
            "RC | 1  11/5 | - - - - -",
            "*RET | 1  10 | - - - - -",
            "JC a16 | 3  10 | - - - - -",
            "IN d8 | 2  10 | - - - - -",
            "CC a16 | 3  17/11 | - - - - -",
            "*CALL a16 | 3  17 | - - - - -",
            "SBI d8 | 2  7 | S Z A P C",
            "RST 3 | 1  11 | - - - - -",
            "RPO | 1  11/5 | - - - - -",
            "POP H | 1  10 | - - - - -",
            "JPO a16 | 3  10 | - - - - -",
            "XTHL | 1  18 | - - - - -",
            "CPO a16 | 3  17/11 | - - - - -",
            "PUSH H | 1  11 | - - - - -",
            "ANI d8 | 2  7 | S Z A P C",
            "RST 4 | 1  11 | - - - - -",
            "RPE | 1  11/5 | - - - - -",
            "PCHL | 1  5 | - - - - -",
            "JPE a16 | 3  10 | - - - - -",
            "XCHG | 1  5 | - - - - -",
            "CPE a16 | 3  17/11 | - - - - -",
            "*CALL a16 | 3  17 | - - - - -",
            "XRI d8 | 2  7 | S Z A P C",
            "RST 5 | 1  11 | - - - - -",
            "RP | 1  11/5 | - - - - -",
            "POP PSW | 1  10 | S Z A P C",
            "JP a16 | 3  10 | - - - - -",
            "DI | 1  4 | - - - - -",
            "CP a16 | 3  17/11 | - - - - -",
            "PUSH PSW | 1  11 | - - - - -",
            "ORI d8 | 2  7 | S Z A P C",
            "RST 6 | 1  11 | - - - - -",
            "RM | 1  11/5 | - - - - -",
            "SPHL | 1  5 | - - - - -",
            "JM a16 | 3  10 | - - - - -",
            "EI | 1  4 | - - - - -",
            "CM a16 | 3  17/11 | - - - - -",
            "*CALL a16 | 3  17 | - - - - -",
            "CPI d8 | 2  7 | S Z A P C",
            "RST 7 | 1  11 | - - - - -"
    };

    public Intel8080Base(Mmu mmu) {
        this.mmu = mmu;

        //NOTE::Setting initial state of flags
        setRegisterByteValue(Register.F, (byte) 0b00000010);
    }

    protected void setRegisterShortValue(Register regPair, short value) {
        switch (regPair) {
            case AF -> {
                registers.a = (byte) (value >> 8);
                registers.f = (byte) (value & 0x00FF);
            }
            case BC -> {
                registers.b = (byte) (value >> 8);
                registers.c = (byte) (value & 0x00FF);
            }
            case DE -> {
                registers.d = (byte) (value >> 8);
                registers.e = (byte) (value & 0x00FF);
            }
            case HL -> {
                registers.h = (byte) (value >> 8);
                registers.l = (byte) (value & 0x00FF);
            }
            case SP -> registers.sp = value;
            case PC -> registers.pc = value;
            case M -> mmu.setShortData(getRegisterShortValue(Register.HL), value);
            default -> System.out.println("ERROR::Invalid setRegisterPairValue");
        }
    }

    protected void setRegisterShortRelativeTo(Register regPair, short value){
        setRegisterShortValue(regPair, (short) (getRegisterShortValue(regPair) + value));
    }

    protected short getRegisterShortValue(Register regPair) {
        ByteBuffer bb = ByteBuffer.allocate(2);

        switch (regPair) {
            case AF -> {
                bb.put(registers.a);
                bb.put(registers.f);
            }
            case BC -> {
                bb.put(registers.b);
                bb.put(registers.c);
            }
            case DE -> {
                bb.put(registers.d);
                bb.put(registers.e);
            }
            case HL -> {
                bb.put(registers.h);
                bb.put(registers.l);
            }
            case SP -> {
                return registers.sp;
            }
            case PC -> {
                return registers.pc;
            }
            case M -> {
                return mmu.readShortData(getRegisterShortValue(Register.HL));
            }
        }
        return bb.getShort(0);
    }

    protected void setRegisterByteValue(Register reg, byte value){
        switch(reg){
            case A -> registers.a = value;
            case F -> registers.f = value;
            case B -> registers.b = value;
            case C -> registers.c = value;
            case D -> registers.d = value;
            case E -> registers.e = value;
            case H -> registers.h = value;
            case L -> registers.l = value;
            case M -> mmu.setByteData(getRegisterShortValue(Register.HL), value);
            default -> System.out.println("ERROR::Invalid setRegisterByteValue");
        }
    }

    protected byte getRegisterByteValue(Register reg){
        switch(reg){
            case A -> {
                return registers.a;
            }
            case F -> {
                return registers.f;
            }
            case B -> {
                return registers.b;
            }
            case C -> {
                return registers.c;
            }
            case D -> {
                return registers.d;
            }
            case E -> {
                return registers.e;
            }
            case H -> {
                return registers.h;
            }
            case L -> {
                return registers.l;
            }
            case M -> {
                return mmu.readByteData(getRegisterShortValue(Register.HL));
            }
            default -> {
                System.out.println("ERROR::Invalid setRegisterByteValue");
                return -1;
            }
        }
    }

    protected void setRegisterByteRelativeTo(Register reg, byte value){
        setRegisterByteValue(reg, (byte) (getRegisterByteValue(reg) + value));
    }

    protected void checkSetSignFlag(byte result){
        if(((result >> 0x7) & 1) == 1)
            setFlag(FlagChoice.TRUE, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL);
        else
            setFlag(FlagChoice.FALSE, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL);
    }

    protected void checkSetZeroFlag(byte value){
        if(value == 0)
            setFlag(FlagChoice.NULL, FlagChoice.TRUE, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL);
        else
            setFlag(FlagChoice.NULL, FlagChoice.FALSE, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL);
    }

    protected void checkSetAuxiliaryCarryFlag(Operation operation, byte value1, byte value2){
        switch(operation){
            case ADD -> {
                if(((value1 & 0xF) + (value2 & 0xF)) > 0xF)
                    setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE, FlagChoice.NULL, FlagChoice.NULL);
                else
                    setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE, FlagChoice.NULL, FlagChoice.NULL);
            }
            case SUB -> {
                if(((value1 & 0xF) - (value2 & 0xF)) < 0xF)
                    setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE, FlagChoice.NULL, FlagChoice.NULL);
                else
                    setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE, FlagChoice.NULL, FlagChoice.NULL);
            }
        }
    }

    protected void checkSetParityFlag(byte value){
        if(parityReference[Byte.toUnsignedInt(value)] == 1)
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE, FlagChoice.NULL);
        else
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE, FlagChoice.NULL);
    }

    protected void checkSetCarryFlag(Operation operation, byte value1, byte value2){
        switch(operation){
            case ADD -> {
                if((Byte.toUnsignedInt(value1) + Byte.toUnsignedInt(value2)) > 0xFF)
                    setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE);
            }
            case SUB -> {
                if((Byte.toUnsignedInt(value1) - Byte.toUnsignedInt(value2)) < 0)
                    setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE);
            }
        }

        setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE);
    }

    protected void setFlag(FlagChoice signFlag, FlagChoice ZERO_FLAG, FlagChoice auxiliaryCarryFlag, FlagChoice parityFlag, FlagChoice carryFlag){
        switch(signFlag){
            case TRUE -> registers.f |= 1 << Flags.SIGN_FLAG.getBit();
            case FALSE -> registers.f &= ~(1 << Flags.SIGN_FLAG.getBit());
        }
        switch(ZERO_FLAG){
            case TRUE -> registers.f |= 1 << Flags.ZERO_FLAG.getBit();
            case FALSE -> registers.f &= ~(1 << Flags.ZERO_FLAG.getBit());
        }
        switch(auxiliaryCarryFlag){
            case TRUE -> registers.f |= 1 << Flags.AUXILIARY_CARRY_FLAG.getBit();
            case FALSE -> registers.f &= ~(1 << Flags.AUXILIARY_CARRY_FLAG.getBit());
        }
        switch(parityFlag){
            case TRUE -> registers.f |= 1 << Flags.PARITY_FLAG.getBit();
            case FALSE -> registers.f &= ~(1 << Flags.PARITY_FLAG.getBit());
        }
        switch(carryFlag){
            case TRUE -> registers.f |= 1 << Flags.CARRY_FLAG.getBit();
            case FALSE -> registers.f &= ~(1 << Flags.CARRY_FLAG.getBit());
        }
    }

    protected boolean getFlag(Flags flag){
        return switch (flag) {
            case SIGN_FLAG -> ((registers.f >> Flags.SIGN_FLAG.getBit()) & 1) == 1;
            case ZERO_FLAG -> ((registers.f >> Flags.ZERO_FLAG.getBit()) & 1) == 1;
            case AUXILIARY_CARRY_FLAG -> ((registers.f >> Flags.AUXILIARY_CARRY_FLAG.getBit()) & 1) == 1;
            case PARITY_FLAG -> ((registers.f >> Flags.PARITY_FLAG.getBit()) & 1) == 1;
            case CARRY_FLAG -> ((registers.f >> Flags.CARRY_FLAG.getBit()) & 1) == 1;
        };
    }

    protected boolean getBitOfByte(byte value, int pos){
        return ((value >> pos) & 1) == 1;
    }

    private byte setBitOfByte(byte value, int pos){
        return value |= 1 << pos;
    }

    private byte clearBitOfByte(byte value, int pos){
        return (byte) (value & ~(1 << pos));
    }

    protected byte getHighByteOfShort(short value){
        return (byte) ((value>>8) & 0xFF);
    }

    protected void swapAccumWithCarry(boolean rotRight, boolean bitOfAccum){
        if(rotRight){
            if(getFlag(Flags.CARRY_FLAG))
                setRegisterByteValue(Register.A, setBitOfByte(getRegisterByteValue(Register.A), 0));
            else
                setRegisterByteValue(Register.A, clearBitOfByte(getRegisterByteValue(Register.A), 0));
        }else{
            if(getFlag(Flags.CARRY_FLAG))
                setRegisterByteValue(Register.A, setBitOfByte(getRegisterByteValue(Register.A), 7));
            else
                setRegisterByteValue(Register.A, clearBitOfByte(getRegisterByteValue(Register.A), 7));
        }

        if(bitOfAccum)
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE);
        else
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE);
    }

    protected byte rotByteLeft(byte value){
        return (byte) ((value << 1) | (value >> (8 - 1)));
    }

    protected byte rotByteRight(byte value){
        return (byte) ((value >> 1) | (value << (8 - 1)));
    }

    public byte[] getPixelBuffer(){
        return mmu.getPixelBuffer();
    }

    public String registersToString(){
        String string = "";

        string = string.concat(String.format("AF: %4x | A: %2x | F: %2x\n", getRegisterShortValue(Register.AF), registers.a, registers.f));
        string = string.concat(String.format("BC: %4x | B: %2x | C: %2x\n", getRegisterShortValue(Register.BC), registers.b, registers.c));
        string = string.concat(String.format("DE: %4x | D: %2x | E: %2x\n", getRegisterShortValue(Register.DE), registers.d, registers.e));
        string = string.concat(String.format("HL: %4x | H: %2x | L: %2x\n", getRegisterShortValue(Register.HL), registers.h, registers.l));
        string = string.concat(String.format("SP: %4x\n", registers.sp));
        string = string.concat(String.format("PC: %4x", registers.pc));

        return string;
    }

    public String flagsToString(){
        String string = "";

        string = string.concat(String.format("%-12s", "Sign Flag"));
        string = (getFlag(Flags.SIGN_FLAG)) ? string.concat(": True\n") : string.concat(": False\n");

        string = string.concat(String.format("%-12s", "Zero Flag"));
        string = (getFlag(Flags.ZERO_FLAG)) ? string.concat(": True\n") : string.concat(": False\n");

        string = string.concat(String.format("%-12s", "Aux Flag"));
        string = (getFlag(Flags.AUXILIARY_CARRY_FLAG)) ? string.concat(": True\n") : string.concat(": False\n");

        string = string.concat(String.format("%-12s", "Parity Flag"));
        string = (getFlag(Flags.PARITY_FLAG)) ? string.concat(": True\n") : string.concat(": False\n");

        string = string.concat(String.format("%-12s", "Carry Flag"));
        string = (getFlag(Flags.CARRY_FLAG)) ? string.concat(": True") : string.concat(": False");

        return string;
    }

    public String getOpcodeString(short opcode){
        return opcodesStrings[opcode];
    }
}
