package cpu;

import memory.Mmu;

public class Intel8080 extends Intel8080Base{
    //NOTE::CHECK opcodes with these instructions http://www.emulator101.com/reference/8080-by-opcode.html
    //NOTE::CHECK opcodes with this spreadsheet https://pastraiser.com/cpu/i8080/i8080_opcodes.html

    private interface Opcode{
        void execute();
    }

    Opcode[] opcodes = new Opcode[0xFF];

    public Intel8080(Mmu mmu) {
        super(mmu);

        //NOTE::NOP | 1 | 4 | - - - - -
        opcodes[0x00] = this::nopOpcode;
        opcodes[0x10] = this::nopOpcode;
        opcodes[0x20] = this::nopOpcode;
        opcodes[0x30] = this::nopOpcode;
        opcodes[0x08] = this::nopOpcode;
        opcodes[0x18] = this::nopOpcode;
        opcodes[0x28] = this::nopOpcode;
        opcodes[0x38] = this::nopOpcode;
        //NOTE::LXI reg, d16 | 3 | 10 | - - - - -
        opcodes[0x01] = () -> lxiOpcode(Register.BC);
        opcodes[0x11] = () -> lxiOpcode(Register.DE);
        opcodes[0x21] = () -> lxiOpcode(Register.HL);
        opcodes[0x31] = () -> lxiOpcode(Register.SP);
        //NOTE::STAX reg | 1 | 7 | - - - - -
        opcodes[0x02] = () -> staxOpcode(Register.BC);
        opcodes[0x12] = () -> staxOpcode(Register.DE);
        //NOTE::SHLD a16 | 3 | 16 | - - - -
        opcodes[0x22] = this::shldOpcode;
        //NOTE::STA a16 | 3 | 13 | - - - -
        opcodes[0x32] = this::staOpcode;
        //NOTE::INX reg | 1 | 5 | - - - - -
        opcodes[0x03] = () -> inxOpcode(Register.BC);
        opcodes[0x13] = () -> inxOpcode(Register.DE);
        opcodes[0x23] = () -> inxOpcode(Register.HL);
        opcodes[0x33] = () -> inxOpcode(Register.SP);
        //NOTE::INR reg | 1 | 5 | S Z A P -
        opcodes[0x04] = () -> inrOpcode(ValueSize.BYTE, Register.B);
        opcodes[0x14] = () -> inrOpcode(ValueSize.BYTE, Register.D);
        opcodes[0x24] = () -> inrOpcode(ValueSize.BYTE, Register.H);
        opcodes[0x34] = () -> inrOpcode(ValueSize.SHORT, Register.M);
        opcodes[0x0C] = () -> inrOpcode(ValueSize.BYTE, Register.C);
        opcodes[0x1C] = () -> inrOpcode(ValueSize.BYTE, Register.E);
        opcodes[0x2C] = () -> inrOpcode(ValueSize.BYTE, Register.L);
        opcodes[0x3C] = () -> inrOpcode(ValueSize.BYTE, Register.A);
        //NOTE::DCR reg | 1 | 5 | S Z A P -
        opcodes[0x05] = () -> dcrOpcode(ValueSize.BYTE, Register.B);
        opcodes[0x15] = () -> dcrOpcode(ValueSize.BYTE, Register.D);
        opcodes[0x25] = () -> dcrOpcode(ValueSize.BYTE, Register.H);
        opcodes[0x35] = () -> dcrOpcode(ValueSize.SHORT, Register.M);
        opcodes[0x0D] = () -> dcrOpcode(ValueSize.BYTE, Register.C);
        opcodes[0x1D] = () -> dcrOpcode(ValueSize.BYTE, Register.E);
        opcodes[0x2D] = () -> dcrOpcode(ValueSize.BYTE, Register.L);
        opcodes[0x3D] = () -> dcrOpcode(ValueSize.BYTE, Register.A);
        //NOTE::MVI reg, d8 | 2 | 7 M = 10 | - - - - -
        opcodes[0x06] = () -> mviOpcode(Register.B);
        opcodes[0x16] = () -> mviOpcode(Register.D);
        opcodes[0x26] = () -> mviOpcode(Register.H);
        opcodes[0x36] = () -> mviOpcode(Register.M);
        opcodes[0x0E] = () -> mviOpcode(Register.C);
        opcodes[0x1E] = () -> mviOpcode(Register.E);
        opcodes[0x2E] = () -> mviOpcode(Register.L);
        opcodes[0x3E] = () -> mviOpcode(Register.A);
        //NOTE::HLT | 1 | 7 | - - - - -
        opcodes[0x76] = this::hltOpcode;
        //NOTE::MOV reg, reg | 1 | 5 M = 7 | - - - - -
        opcodes[0x40] = () -> movOpcode(Register.B, Register.B);
        opcodes[0x50] = () -> movOpcode(Register.D, Register.B);
        opcodes[0x60] = () -> movOpcode(Register.H, Register.B);
        opcodes[0x70] = () -> movOpcode(Register.M, Register.B);

        opcodes[0x41] = () -> movOpcode(Register.B, Register.C);
        opcodes[0x51] = () -> movOpcode(Register.D, Register.C);
        opcodes[0x61] = () -> movOpcode(Register.H, Register.C);
        opcodes[0x71] = () -> movOpcode(Register.M, Register.C);

        opcodes[0x42] = () -> movOpcode(Register.B, Register.D);
        opcodes[0x52] = () -> movOpcode(Register.D, Register.D);
        opcodes[0x62] = () -> movOpcode(Register.H, Register.D);
        opcodes[0x72] = () -> movOpcode(Register.M, Register.D);

        opcodes[0x43] = () -> movOpcode(Register.B, Register.E);
        opcodes[0x53] = () -> movOpcode(Register.D, Register.E);
        opcodes[0x63] = () -> movOpcode(Register.H, Register.E);
        opcodes[0x73] = () -> movOpcode(Register.M, Register.E);

        opcodes[0x44] = () -> movOpcode(Register.B, Register.H);
        opcodes[0x54] = () -> movOpcode(Register.D, Register.H);
        opcodes[0x64] = () -> movOpcode(Register.H, Register.H);
        opcodes[0x74] = () -> movOpcode(Register.M, Register.H);

        opcodes[0x45] = () -> movOpcode(Register.B, Register.L);
        opcodes[0x55] = () -> movOpcode(Register.D, Register.L);
        opcodes[0x65] = () -> movOpcode(Register.H, Register.L);
        opcodes[0x75] = () -> movOpcode(Register.M, Register.L);

        opcodes[0x46] = () -> movOpcode(Register.B, Register.M);
        opcodes[0x56] = () -> movOpcode(Register.D, Register.M);
        opcodes[0x66] = () -> movOpcode(Register.H, Register.M);

        opcodes[0x47] = () -> movOpcode(Register.B, Register.A);
        opcodes[0x57] = () -> movOpcode(Register.D, Register.A);
        opcodes[0x67] = () -> movOpcode(Register.H, Register.A);
        opcodes[0x77] = () -> movOpcode(Register.M, Register.A);

        opcodes[0x48] = () -> movOpcode(Register.C, Register.B);
        opcodes[0x58] = () -> movOpcode(Register.E, Register.B);
        opcodes[0x68] = () -> movOpcode(Register.L, Register.B);
        opcodes[0x78] = () -> movOpcode(Register.A, Register.B);

        opcodes[0x49] = () -> movOpcode(Register.C, Register.C);
        opcodes[0x59] = () -> movOpcode(Register.E, Register.C);
        opcodes[0x69] = () -> movOpcode(Register.L, Register.C);
        opcodes[0x79] = () -> movOpcode(Register.A, Register.C);

        opcodes[0x4A] = () -> movOpcode(Register.C, Register.D);
        opcodes[0x5A] = () -> movOpcode(Register.E, Register.D);
        opcodes[0x6A] = () -> movOpcode(Register.L, Register.D);
        opcodes[0x7A] = () -> movOpcode(Register.A, Register.D);

        opcodes[0x4B] = () -> movOpcode(Register.C, Register.E);
        opcodes[0x5B] = () -> movOpcode(Register.E, Register.E);
        opcodes[0x6B] = () -> movOpcode(Register.L, Register.E);
        opcodes[0x7B] = () -> movOpcode(Register.A, Register.E);

        opcodes[0x4C] = () -> movOpcode(Register.C, Register.H);
        opcodes[0x5C] = () -> movOpcode(Register.E, Register.H);
        opcodes[0x6C] = () -> movOpcode(Register.L, Register.H);
        opcodes[0x7C] = () -> movOpcode(Register.A, Register.H);

        opcodes[0x4D] = () -> movOpcode(Register.C, Register.L);
        opcodes[0x5D] = () -> movOpcode(Register.E, Register.L);
        opcodes[0x6D] = () -> movOpcode(Register.L, Register.L);
        opcodes[0x7D] = () -> movOpcode(Register.A, Register.L);

        opcodes[0x4E] = () -> movOpcode(Register.C, Register.M);
        opcodes[0x5E] = () -> movOpcode(Register.E, Register.M);
        opcodes[0x6E] = () -> movOpcode(Register.L, Register.M);
        opcodes[0x7E] = () -> movOpcode(Register.A, Register.M);

        opcodes[0x4F] = () -> movOpcode(Register.C, Register.A);
        opcodes[0x5F] = () -> movOpcode(Register.E, Register.A);
        opcodes[0x6F] = () -> movOpcode(Register.L, Register.A);
        opcodes[0x7F] = () -> movOpcode(Register.A, Register.A);

        //NOTE::ADD reg | 1 | 4 M = 7 | S Z A P C
        opcodes[0x80] = () -> addOpcode(Register.B);
        opcodes[0x81] = () -> addOpcode(Register.C);
        opcodes[0x82] = () -> addOpcode(Register.D);
        opcodes[0x83] = () -> addOpcode(Register.E);
        opcodes[0x84] = () -> addOpcode(Register.H);
        opcodes[0x85] = () -> addOpcode(Register.L);
        opcodes[0x86] = () -> addOpcode(Register.M);
        opcodes[0x87] = () -> addOpcode(Register.A);

        //NOTE::SUB reg | 1 | 4 M = 7 | S Z A P C
        opcodes[0x90] = () -> subOpcode(Register.B);
        opcodes[0x91] = () -> subOpcode(Register.C);
        opcodes[0x92] = () -> subOpcode(Register.D);
        opcodes[0x93] = () -> subOpcode(Register.E);
        opcodes[0x94] = () -> subOpcode(Register.H);
        opcodes[0x95] = () -> subOpcode(Register.L);
        opcodes[0x96] = () -> subOpcode(Register.M);
        opcodes[0x97] = () -> subOpcode(Register.A);

    }

    public void executeOpcode(short opcode){
        opcodes[opcode].execute();
    }

    //NOTE::NOP | 1 | 4 | - - - - -
    private void nopOpcode(){
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
        cycles += 4;
    }

    //NOTE::LXI reg, d16 | 3 | 10 | - - - - -
    private void lxiOpcode(Register reg){
        setRegisterValue(reg, mmu.readShortData((short) (getRegisterValue(Register.PC) + 1)));

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 2));
        cycles += 10;
    }

    //NOTE::STAX reg | 1 | 7 | - - - - -
    private void staxOpcode(Register reg) {
        mmu.setData(getRegisterValue(reg), getRegisterValue(Register.A));

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
        cycles += 7;
    }

    //NOTE::SHLD a16 | 3 | 16 | - - - - -
    private void shldOpcode(){
        mmu.setData(mmu.readShortData((short) (getRegisterValue(Register.PC) + 1)), getRegisterValue(Register.HL));

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 3));
        cycles += 16;
    }

    //NOTE::STA a16 | 3 | 13 | - - - - -
    private void staOpcode(){
        mmu.setData(mmu.readShortData((short) (getRegisterValue(Register.PC) + 1)), getRegisterValue(Register.A));

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 3));
        cycles += 16;
    }

    //NOTE::INX reg | 1 | 5 | - - - - -
    private void inxOpcode(Register reg){
        setRegisterValue(reg, (short) (getRegisterValue(reg) + 1));

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
        cycles += 5;
    }

    //NOTE::INR reg | 1 | 5 M = 10 | S Z A P -
    private void inrOpcode(ValueSize valueSize, Register reg){
        short result;
        byte auxValue1;
        byte auxValue2;

        if(reg == Register.M) {
            short addressHLValue = mmu.readShortData(getRegisterValue(Register.HL));
            result = (short) (addressHLValue + 1);

            auxValue1 = getHighByte(addressHLValue);
            auxValue2 = getHighByte(result);
            mmu.setData(getRegisterValue(Register.HL), result);

            cycles += 5;
        }else{
            result = (short) (getRegisterValue(reg) + 1);

            auxValue1 = (byte) getRegisterValue(reg);
            auxValue2 = (byte) result;
            setRegisterValue(reg, result);
        }

        checkSetSignFlag(valueSize, result);
        checkSetZeroFlag(result);
        checkSetAuxiliaryCarryFlag(Operation.ADD, auxValue1, auxValue2);
        checkSetParityFlag(result);

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
        cycles += 5;
    }

    //NOTE::DCR reg | 1 | 5 M = 10 | S Z A P -
    private void dcrOpcode(ValueSize valueSize, Register reg) {
        short result;
        byte auxValue1;
        byte auxValue2;

        if(reg == Register.M) {
            short addressHLValue = mmu.readShortData(getRegisterValue(Register.HL));
            result = (short) (addressHLValue - 1);

            auxValue1 = getHighByte(addressHLValue);
            auxValue2 = getHighByte(result);
            mmu.setData(getRegisterValue(Register.HL), result);

            cycles += 5;
        }else{
            result = (short) (getRegisterValue(reg) - 1);

            auxValue1 = (byte) getRegisterValue(reg);
            auxValue2 = (byte) result;
            setRegisterValue(reg, result);
        }

        checkSetSignFlag(valueSize, result);
        checkSetZeroFlag(result);
        checkSetAuxiliaryCarryFlag(Operation.ADD, auxValue1, auxValue2);
        checkSetParityFlag(result);

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
        cycles += 5;
    }

    //NOTE::HLT | 1 | 7 | - - - - -
    private void hltOpcode(){
        cycles += 7;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //NOTE::MOV reg, reg | 1 | 5 M = 7 | - - - - -
    private void movOpcode(Register reg1, Register reg2){
        if(reg1 == Register.M || reg2 == Register.M){
            if(reg1 == Register.M)
                mmu.setData(getRegisterValue(Register.HL), getRegisterValue(reg2));
            else
                setRegisterValue(reg1, mmu.readByteData(getRegisterValue(Register.HL)));

            cycles += 2;
        }
        setRegisterValue(reg1, getRegisterValue(reg2));

        cycles += 5;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }


    //NOTE::MVI reg, d8 | 2 | 7 M = 10 | - - - - -
    private void mviOpcode(Register reg){
        if(reg == Register.M) {
            mmu.setData(getRegisterValue(Register.HL), mmu.readByteData((short) (getRegisterValue(Register.PC) + 1)));
            cycles += 3;
        }
        else
            setRegisterValue(reg, mmu.readByteData((byte) (getRegisterValue(Register.PC) + 1)));

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
        cycles += 7;
    }

    //NOTE::ADD reg | 1 | 4 M = 7 | S Z A P C
    private void addOpcode(Register reg){
        byte result;
        byte prevValue;

        if(reg == Register.M) {
            result = (byte) (getRegisterValue(Register.A) + mmu.readShortData(getRegisterValue(Register.HL)));
            cycles += 3;
        }
        else
            result = (byte) (getRegisterValue(Register.A) + getRegisterValue(reg));

        prevValue = (byte) getRegisterValue(Register.A);

        setRegisterValue(Register.A, result);

        checkSetSignFlag(ValueSize.BYTE, result);
        checkSetZeroFlag(result);
        checkSetAuxiliaryCarryFlag(Operation.ADD, prevValue, result);
        checkSetParityFlag(result);
        checkSetCarryFlag(Operation.ADD, prevValue, result);

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
        cycles +=4;
    }

    //NOTE::SUB reg | 1 | 4 M = 7 | S Z A P C
    private void subOpcode(Register reg){
        byte result;
        byte prevValue;

        if(reg == Register.M) {
            result = (byte) (getRegisterValue(Register.A) - mmu.readShortData(getRegisterValue(Register.HL)));
            cycles += 3;
        }
        else
            result = (byte) (getRegisterValue(Register.A) - getRegisterValue(reg));

        prevValue = (byte) getRegisterValue(Register.A);

        setRegisterValue(Register.A, result);

        checkSetSignFlag(ValueSize.BYTE, result);
        checkSetZeroFlag(result);
        checkSetAuxiliaryCarryFlag(Operation.SUB, prevValue, result);
        checkSetParityFlag(result);
        checkSetCarryFlag(Operation.SUB, prevValue, result);

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
        cycles +=4;
    }


    @Override
    public String toString(){
        return super.toString();
    }
}
