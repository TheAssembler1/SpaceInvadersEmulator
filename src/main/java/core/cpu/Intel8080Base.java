package core.cpu;

import core.memory.Mmu;

import java.nio.ByteBuffer;

public abstract class Intel8080Base extends Intel8080Strings{
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
        SIGN_FLAG((byte)7),
        ZERO_FLAG((byte)6),
        AUXILIARY_CARRY_FLAG((byte)4),
        PARITY_FLAG((byte)2),
        CARRY_FLAG((byte)0);

        byte bit;
        Flags(byte bit){
            this.bit = bit;
        }

        byte getBit(){
            return bit;
        }
    }

    protected enum FlagChoice{
        TRUE, FALSE, NULL
    }

    protected enum Operation{
        ADD, SUB, AND, OR, XOR
    }

    protected enum ValueSize {
        SHORT, BYTE
    }

    protected Registers registers = new Registers();
    protected long cycles = 0;
    protected Mmu mmu;

    public Intel8080Base(Mmu mmu) {
        this.mmu = mmu;

        //NOTE::Setting initial state of flags
        registers.f = 0b00000010;
    }

    private void setRegisterPairValue(Register regPair, short value) {
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
        }
    }

    private short getRegisterPairValue(Register regPair) {
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
        }
        return bb.getShort(0);
    }

    protected void checkSetSignFlag(byte value){
        //FIXME::This method needs to be tested
        if((((byte)value >> 0x7) & 1) == 1)
            setFlag(FlagChoice.TRUE, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL);
        else
            setFlag(FlagChoice.FALSE, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL);
    }

    protected void checkSetZeroFlag(short value){
        if(value == 0)
            setFlag(FlagChoice.NULL, FlagChoice.TRUE, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL);
        else
            setFlag(FlagChoice.NULL, FlagChoice.FALSE, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL);
    }

    protected void checkSetAuxiliaryCarryFlag(Operation operation, byte value1, byte value2){
        switch(operation){
            case ADD -> {
                if((((value1 & 0xF) + (value2 & 0xF)) & 0x10) == 0x10)
                    setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE, FlagChoice.NULL, FlagChoice.NULL);
                else
                    setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE, FlagChoice.NULL, FlagChoice.NULL);
            }
            case SUB -> {
                if((((value1 & 0xF) - (value2 & 0xF)) & 0x10) == 0x10)
                    setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE, FlagChoice.NULL, FlagChoice.NULL);
                else
                    setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE, FlagChoice.NULL, FlagChoice.NULL);
            }
        }
    }

    protected void checkSetParityFlag(byte value){
        //FIXME::This method needs to be tested
        int parity = 0;
        while(value >> 1 != 0){
            parity = (value & 1)^((value >> 1) & 1);
            value >>= 1;
        }

        if(parity % 2 == 0)
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE, FlagChoice.NULL);
        else
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE, FlagChoice.NULL);
    }

    protected void checkSetCarryFlag(Operation operation, byte value1, byte value2){
        byte result = 0;

        //FIXME::This method needs to be tested
        switch(operation){
            case ADD -> {
                result = (byte) (value1 + value2);
            }
            case SUB -> {
                result = (byte) (value1 - value2);
            }
            case AND -> {
                result = (byte) (value1 & value2);
            }
            case OR -> {
                result = (byte) (value1 | value2);
            }
            case XOR -> {
                result = (byte) (value1 ^ value2);
            }
        }

        if(result < 0)
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE);
        else
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

    protected byte getHighByte(short value){
        return (byte) (value >> 8);
    }

    protected short getRegisterValue(Register reg){
        return switch(reg){
            case AF -> getRegisterPairValue(Register.AF);
            case BC -> getRegisterPairValue(Register.BC);
            case DE -> getRegisterPairValue(Register.DE);
            case HL -> getRegisterPairValue(Register.HL);

            //NOTE::If we get this return then there is an error
            case M -> (short) 0;

            case A -> registers.a;
            case F -> registers.f;
            case B -> registers.b;
            case C -> registers.c;
            case D -> registers.d;
            case E -> registers.e;
            case H -> registers.h;
            case L -> registers.l;

            case SP -> registers.sp;
            case PC -> registers.pc;
        };
    }

    protected void setRegisterValue(Register reg, short value){
        switch(reg){
            case AF -> setRegisterPairValue(Register.AF, value);
            case BC -> setRegisterPairValue(Register.BC, value);
            case DE -> setRegisterPairValue(Register.DE, value);
            case HL -> setRegisterPairValue(Register.HL, value);

            case A -> registers.a = (byte)value;
            case F -> registers.f = (byte)value;
            case B -> registers.b = (byte)value;
            case C -> registers.c = (byte)value;
            case D -> registers.d = (byte)value;
            case E -> registers.e = (byte)value;
            case H -> registers.h = (byte)value;
            case L -> registers.l = (byte)value;

            case SP -> registers.sp = value;
            case PC -> registers.pc = value;
        }
    }

    public short getPCReg(){
        return registers.pc;
    }

    protected String registersToString(){
        String string = "";

        string = string.concat(String.format("AF: %4x | A: %2x | F: %2x\n", getRegisterPairValue(Register.AF), registers.a, registers.f));
        string = string.concat(String.format("BC: %4x | B: %2x | C: %2x\n", getRegisterPairValue(Register.BC), registers.b, registers.c));
        string = string.concat(String.format("DE: %4x | D: %2x | E: %2x\n", getRegisterPairValue(Register.DE), registers.d, registers.e));
        string = string.concat(String.format("HL: %4x | H: %2x | L: %2x\n", getRegisterPairValue(Register.HL), registers.h, registers.l));
        string = string.concat(String.format("SP: %4x\n", registers.sp));
        string = string.concat(String.format("PC: %4x", registers.pc));

        return string;
    }

    protected String flagsToString(){
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
}
