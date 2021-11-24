package cpu;

import memory.Mmu;

public class Intel8080 extends Intel8080Base{
    //TODO::Make parameters for methods that take in (value, address) be in that order
    //TODO::Currently on mov were setting the byte to the memory as a short. Check Correctness
    //TODO::rewrite opcodes with these instructions http://www.emulator101.com/reference/8080-by-opcode.html

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
        opcodes[0x04] = () -> inrOpcode(ValueSize.SHORT, Register.BC);
        opcodes[0x14] = () -> inrOpcode(ValueSize.SHORT, Register.DE);
        opcodes[0x24] = () -> inrOpcode(ValueSize.SHORT, Register.HL);
        opcodes[0x34] = () -> inrOpcode(ValueSize.SHORT, Register.M);
        opcodes[0x0C] = () -> inrOpcode(ValueSize.BYTE, Register.C);
        opcodes[0x1C] = () -> inrOpcode(ValueSize.BYTE, Register.E);
        opcodes[0x2C] = () -> inrOpcode(ValueSize.BYTE, Register.L);
        opcodes[0x3C] = () -> inrOpcode(ValueSize.BYTE, Register.A);
        //NOTE::DCR reg | 1 | 5 | S Z A P -
        opcodes[0x05] = () -> dcrOpcode(ValueSize.SHORT, Register.BC);
        opcodes[0x15] = () -> dcrOpcode(ValueSize.SHORT, Register.DE);
        opcodes[0x25] = () -> dcrOpcode(ValueSize.SHORT, Register.HL);
        opcodes[0x35] = () -> dcrOpcode(ValueSize.SHORT, Register.M);
        opcodes[0x0D] = () -> dcrOpcode(ValueSize.BYTE, Register.C);
        opcodes[0x1D] = () -> dcrOpcode(ValueSize.BYTE, Register.E);
        opcodes[0x2D] = () -> dcrOpcode(ValueSize.BYTE, Register.L);
        opcodes[0x3D] = () -> dcrOpcode(ValueSize.BYTE, Register.A);
        //NOTE::MVI reg, d8 | 2 | 7 M = 10 | - - - - -
        opcodes[0x06] = () -> mviOpcode(Register.BC);
        opcodes[0x16] = () -> mviOpcode(Register.DE);
        opcodes[0x26] = () -> mviOpcode(Register.HL);
        opcodes[0x36] = () -> mviOpcode(Register.M);
        opcodes[0x0E] = () -> mviOpcode(Register.C);
        opcodes[0x1E] = () -> mviOpcode(Register.E);
        opcodes[0x2E] = () -> mviOpcode(Register.L);
        opcodes[0x3E] = () -> mviOpcode(Register.A);
        //NOTE::HLT | 1 | 7 | - - - - -
        opcodes[0x76] = this::hltOpcode;
        //NOTE::MOV reg, reg | 1 | 5 M = 7 | - - - - -
        opcodes[0x40] = () -> movOpcode(Register.BC, Register.BC);
        opcodes[0x50] = () -> movOpcode(Register.DE, Register.BC);
        opcodes[0x60] = () -> movOpcode(Register.HL, Register.BC);
        opcodes[0x70] = () -> movOpcode(Register.M, Register.BC);
        opcodes[0x41] = () -> movOpcode(Register.BC, Register.C);
        opcodes[0x42] = () -> movOpcode(Register.DE, Register.C);
        opcodes[0x53] = () -> movOpcode(Register.HL, Register.C);
        opcodes[0x64] = () -> movOpcode(Register.M, Register.C);
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
        mmu.setData(getRegisterValue(Register.HL), getRegisterValue(reg));

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
        if(reg == Register.SP)
            setRegisterValue(Register.SP, (short) (getRegisterValue(Register.SP) + 1));
        else
            setRegisterValue(reg, (short) (getRegisterValue(reg) + 1));

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
        cycles += 5;
    }

    //NOTE::INR reg | 1 | 5 M = 10 | S Z A P -
    private void inrOpcode(ValueSize valueSize, Register reg){
        short result = 0;
        byte auxValue1 = 0;
        byte auxValue2 = 0;

        switch(valueSize){
            case BYTE -> {
                result = (short) (getRegisterValue(reg) + 1);

                auxValue1 = getHighByte(getRegisterValue(reg));
                auxValue2 = getHighByte(result);

                setRegisterValue(reg, result);
            }
            case SHORT -> {
                if(reg == Register.M) {
                    result = (short) (mmu.readShortData(getRegisterValue(Register.HL)) + 1);

                    auxValue1 = getHighByte(mmu.readShortData(getRegisterValue(Register.HL)));
                    auxValue2 = getHighByte(result);

                    mmu.setData(getRegisterValue(Register.HL), result);
                    cycles += 5;
                }else {
                    result = (short) (getRegisterValue(reg) + 1);

                    auxValue1 = (byte) getRegisterValue(reg);
                    auxValue2 = (byte) result;

                    setRegisterValue(reg, result);
                }
            }
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
        short result = 0;
        byte auxValue1 = 0;
        byte auxValue2 = 0;

        switch(valueSize){
            case BYTE -> {
                result = (short) (getRegisterValue(reg) - 1);

                auxValue1 = getHighByte(getRegisterValue(reg));
                auxValue2 = getHighByte(result);

                setRegisterValue(reg, result);
            }
            case SHORT -> {
                if(reg == Register.M) {
                    result = (short) (mmu.readShortData(getRegisterValue(Register.HL)) - 1);

                    auxValue1 = getHighByte(mmu.readShortData(getRegisterValue(Register.HL)));
                    auxValue2 = getHighByte(result);

                    mmu.setData(getRegisterValue(Register.HL), result);
                    cycles += 5;
                }else {
                    result = (short) (getRegisterValue(reg) - 1);

                    auxValue1 = (byte) getRegisterValue(reg);
                    auxValue2 = (byte) result;

                    setRegisterValue(reg, result);
                }
            }
        }

        checkSetSignFlag(valueSize, result);
        checkSetZeroFlag(result);
        checkSetAuxiliaryCarryFlag(Operation.SUB, auxValue1, auxValue2);
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
        if(reg2 == Register.M){
            setRegisterValue(reg1, mmu.readShortData(getRegisterValue(Register.HL)));
            cycles += 2;
        }else
            setRegisterValue(reg1, getRegisterValue(reg2));

        cycles += 5;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }


    //NOTE::MVI reg, d8 | 2 | 7 M = 10 | - - - - -
    void mviOpcode(Register reg){
        if(reg == Register.M) {
            mmu.setData(getRegisterValue(Register.HL), mmu.readShortData((short) (getRegisterValue(Register.PC) + 1)));
            cycles += 3;
        }
        else
            setRegisterValue(reg, mmu.readByteData((byte) (getRegisterValue(Register.PC) + 1)));

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
        cycles += 7;
    }

    @Override
    public String toString(){
        return super.toString();
    }
}
