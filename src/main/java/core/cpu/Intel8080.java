package core.cpu;

import core.memory.Mmu;

public class Intel8080 extends Intel8080Base{
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
        opcodes[0x04] = () -> inrOpcode(Register.B);
        opcodes[0x14] = () -> inrOpcode(Register.D);
        opcodes[0x24] = () -> inrOpcode(Register.H);
        opcodes[0x34] = () -> inrOpcode(Register.M);
        opcodes[0x0C] = () -> inrOpcode(Register.C);
        opcodes[0x1C] = () -> inrOpcode(Register.E);
        opcodes[0x2C] = () -> inrOpcode(Register.L);
        opcodes[0x3C] = () -> inrOpcode(Register.A);
        //NOTE::DCR reg | 1 | 5 | S Z A P -
        opcodes[0x05] = () -> dcrOpcode(Register.B);
        opcodes[0x15] = () -> dcrOpcode(Register.D);
        opcodes[0x25] = () -> dcrOpcode(Register.H);
        opcodes[0x35] = () -> dcrOpcode(Register.M);
        opcodes[0x0D] = () -> dcrOpcode(Register.C);
        opcodes[0x1D] = () -> dcrOpcode(Register.E);
        opcodes[0x2D] = () -> dcrOpcode(Register.L);
        opcodes[0x3D] = () -> dcrOpcode(Register.A);
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

        opcodes[0x4B] = () -> movOpcode(Register .C, Register.E);
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
        opcodes[0x80] = () -> addOpcode(Register.B, false);
        opcodes[0x81] = () -> addOpcode(Register.C, false);
        opcodes[0x82] = () -> addOpcode(Register.D, false);
        opcodes[0x83] = () -> addOpcode(Register.E, false);
        opcodes[0x84] = () -> addOpcode(Register.H, false);
        opcodes[0x85] = () -> addOpcode(Register.L, false);
        opcodes[0x86] = () -> addOpcode(Register.M, false);
        opcodes[0x87] = () -> addOpcode(Register.A, false);
        //NOTE::ADC reg | 1 | 4 M = 7 | S Z A P C
        opcodes[0x88] = () -> addOpcode(Register.B, true);
        opcodes[0x89] = () -> addOpcode(Register.C, true);
        opcodes[0x8A] = () -> addOpcode(Register.D, true);
        opcodes[0x8B] = () -> addOpcode(Register.E, true);
        opcodes[0x8C] = () -> addOpcode(Register.H, true);
        opcodes[0x8D] = () -> addOpcode(Register.L, true);
        opcodes[0x8E] = () -> addOpcode(Register.M, true);
        opcodes[0x8F] = () -> addOpcode(Register.A, true);
        //NOTE::SUB reg | 1 | 4 M = 7 | S Z A P C
        opcodes[0x90] = () -> subOpcode(Register.B, false);
        opcodes[0x91] = () -> subOpcode(Register.C, false);
        opcodes[0x92] = () -> subOpcode(Register.D, false);
        opcodes[0x93] = () -> subOpcode(Register.E, false);
        opcodes[0x94] = () -> subOpcode(Register.H, false);
        opcodes[0x95] = () -> subOpcode(Register.L, false);
        opcodes[0x96] = () -> subOpcode(Register.M, false);
        opcodes[0x97] = () -> subOpcode(Register.A, false);
        //NOTE::SBB reg | 1 | 4 M = 7 | S Z A P C
        opcodes[0x98] = () -> subOpcode(Register.B, true);
        opcodes[0x99] = () -> subOpcode(Register.C, true);
        opcodes[0x9A] = () -> subOpcode(Register.D, true);
        opcodes[0x9B] = () -> subOpcode(Register.E, true);
        opcodes[0x9C] = () -> subOpcode(Register.H, true);
        opcodes[0x9D] = () -> subOpcode(Register.L, true);
        opcodes[0x9E] = () -> subOpcode(Register.M, true);
        opcodes[0x9F] = () -> subOpcode(Register.A, true);
        //NOTE::ANA reg | 1 | 4 M = 7 | S Z A P C
        opcodes[0xA0] = () -> anaOpcode(Register.B);
        opcodes[0xA1] = () -> anaOpcode(Register.C);
        opcodes[0xA2] = () -> anaOpcode(Register.D);
        opcodes[0xA3] = () -> anaOpcode(Register.E);
        opcodes[0xA4] = () -> anaOpcode(Register.H);
        opcodes[0xA5] = () -> anaOpcode(Register.L);
        opcodes[0xA6] = () -> anaOpcode(Register.M);
        opcodes[0xA7] = () -> anaOpcode(Register.A);
        //NOTE::XRA reg | 1 | 4 M = 7 | S Z A P C
        opcodes[0xA8] = () -> xraOpcode(Register.B);
        opcodes[0xA9] = () -> xraOpcode(Register.C);
        opcodes[0xAA] = () -> xraOpcode(Register.D);
        opcodes[0xAB] = () -> xraOpcode(Register.E);
        opcodes[0xAC] = () -> xraOpcode(Register.H);
        opcodes[0xAD] = () -> xraOpcode(Register.L);
        opcodes[0xAE] = () -> xraOpcode(Register.M);
        opcodes[0xAF] = () -> xraOpcode(Register.A);
        //NOTE::ORA reg | 1 | 4 M = 7 | S Z A P C
        opcodes[0xB0] = () -> oraOpcode(Register.B);
        opcodes[0xB1] = () -> oraOpcode(Register.C);
        opcodes[0xB2] = () -> oraOpcode(Register.D);
        opcodes[0xB3] = () -> oraOpcode(Register.E);
        opcodes[0xB4] = () -> oraOpcode(Register.H);
        opcodes[0xB5] = () -> oraOpcode(Register.L);
        opcodes[0xB6] = () -> oraOpcode(Register.M);
        opcodes[0xB7] = () -> oraOpcode(Register.A);
        //NOTE::CMP reg | 1 | 4 M = 7 | S Z A P C
        opcodes[0xB8] = () -> cmpOpcode(Register.B);
        opcodes[0xB9] = () -> cmpOpcode(Register.C);
        opcodes[0xBA] = () -> cmpOpcode(Register.D);
        opcodes[0xBB] = () -> cmpOpcode(Register.E);
        opcodes[0xBC] = () -> cmpOpcode(Register.H);
        opcodes[0xBD] = () -> cmpOpcode(Register.L);
        opcodes[0xBE] = () -> cmpOpcode(Register.M);
        opcodes[0xBF] = () -> cmpOpcode(Register.A);
        //NOTE::JMP/JNZ/JNC/JPO/JP/JZ/JC/JPE/JM a16 | 3 | 10 | - - - - -
        opcodes[0xC2] = () -> jmpOpcode(Flags.ZERO_FLAG, FlagChoice.FALSE);
        opcodes[0xD2] = () -> jmpOpcode(Flags.CARRY_FLAG, FlagChoice.FALSE);
        opcodes[0xE2] = () -> jmpOpcode(Flags.PARITY_FLAG, FlagChoice.FALSE);
        opcodes[0xF2] = () -> jmpOpcode(Flags.SIGN_FLAG, FlagChoice.FALSE);

        opcodes[0xC3] = () -> jmpOpcode(Flags.ZERO_FLAG, FlagChoice.NULL);

        opcodes[0xCA] = () -> jmpOpcode(Flags.ZERO_FLAG, FlagChoice.TRUE);
        opcodes[0xDA] = () -> jmpOpcode(Flags.CARRY_FLAG, FlagChoice.TRUE);
        opcodes[0xEA] = () -> jmpOpcode(Flags.PARITY_FLAG, FlagChoice.TRUE);
        opcodes[0xFA] = () -> jmpOpcode(Flags.SIGN_FLAG, FlagChoice.TRUE);

        opcodes[0xCB] = () -> jmpOpcode(Flags.ZERO_FLAG, FlagChoice.NULL);
        //NOTE::POP reg | 1 | 10 | - - - - - PSW =  S Z A P C
        opcodes[0xC1] = () -> popOpcode(Register.BC);
        opcodes[0xD1] = () -> popOpcode(Register.DE);
        opcodes[0xE1] = () -> popOpcode(Register.HL);
        opcodes[0xF1] = () -> popOpcode(Register.AF);
        //NOTE::PUSH reg | 1 | 11 | - - - -
        opcodes[0xC5] = () -> pushOpcode(Register.BC);
        opcodes[0xD5] = () -> pushOpcode(Register.DE);
        opcodes[0xE5] = () -> pushOpcode(Register.HL);
        opcodes[0xF5] = () -> pushOpcode(Register.AF);
        //NOTE::RF | 1 | 11/5 | - - - - -
        opcodes[0xC0] = () -> rtOpcode(Flags.ZERO_FLAG, false);
        opcodes[0xD0] = () -> rtOpcode(Flags.CARRY_FLAG, false);
        opcodes[0xE0] = () -> rtOpcode(Flags.PARITY_FLAG, false);
        opcodes[0xF0] = () -> rtOpcode(Flags.SIGN_FLAG, false);
        //NOTE::RT | 1 | 11/5 | - - - - -
        opcodes[0xC8] = () -> rtOpcode(Flags.ZERO_FLAG, true);
        opcodes[0xD8] = () -> rtOpcode(Flags.CARRY_FLAG, true);
        opcodes[0xE8] = () -> rtOpcode(Flags.PARITY_FLAG, true);
        opcodes[0xF8] = () -> rtOpcode(Flags.SIGN_FLAG, true);
        //NOTE::RET | 1 | 10 | - - - - -
        opcodes[0xC9] = () -> rtOpcode(null, false);
        opcodes[0xD9] = () -> rtOpcode(null, false);
        //CF a16 | 3 | 17/11 | - - - - -
        opcodes[0xC4] = () -> callOpcode(Flags.ZERO_FLAG, false);
        opcodes[0xD4] = () -> callOpcode(Flags.CARRY_FLAG, false);
        opcodes[0xE4] = () -> callOpcode(Flags.PARITY_FLAG, false);
        opcodes[0xF4] = () -> callOpcode(Flags.SIGN_FLAG, false);
        //CT a16 | 3 | 17/11 | - - - - -
        opcodes[0xCC] = () -> callOpcode(Flags.ZERO_FLAG, true);
        opcodes[0xDC] = () -> callOpcode(Flags.CARRY_FLAG, true);
        opcodes[0xEC] = () -> callOpcode(Flags.PARITY_FLAG, true);
        opcodes[0xFC] = () -> callOpcode(Flags.SIGN_FLAG, true);
        //CALL a16 | 3 | 7 | - - - -
        opcodes[0xCD] = () -> callOpcode(null, false);
        opcodes[0xDD] = () -> callOpcode(null, false);
        opcodes[0xED] = () -> callOpcode(null, false);
        opcodes[0xFD] = () -> callOpcode(null, false);
        //NOTE::LDAX reg | 1 | 7 | - - - - -
        opcodes[0x0A] = () -> ldaxOpcode(Register.BC);
        opcodes[0x1A] = () -> ldaxOpcode(Register.DE);
        //NOTE::LHLD a16 | 3 | 16 | - - - - -
        opcodes[0x2A] = this::lhldOpcode;
        //NOTE::LDA a16 | 3 | 13 | - - - - -
        opcodes[0x3A] = this::ldaOpcode;
        //ACI/SBI/XRI/CPI d8 | 2 | 7 | S Z A P C
        opcodes[0xCE] = () -> cpiOpcode(Operation.ADD);
        opcodes[0xDE] = () -> cpiOpcode(Operation.SUB);
        opcodes[0xEE] = () -> cpiOpcode(Operation.XOR);
        opcodes[0xFE] = () -> cpiOpcode(Operation.NULL);
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

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 3));
        cycles += 10;
    }

    //NOTE::STAX reg | 1 | 7 | - - - - -
    private void staxOpcode(Register reg) {
        mmu.setShortData(getRegisterValue(reg), getRegisterValue(Register.A));

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
        cycles += 7;
    }

    //NOTE::SHLD a16 | 3 | 16 | - - - - -
    private void shldOpcode(){
        mmu.setShortData(mmu.readShortData((short) (getRegisterValue(Register.PC) + 1)), getRegisterValue(Register.HL));

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 3));
        cycles += 16;
    }

    //NOTE::STA a16 | 3 | 13 | - - - - -
    private void staOpcode(){
        mmu.setShortData(mmu.readShortData((short) (getRegisterValue(Register.PC) + 1)), getRegisterValue(Register.A));

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
    private void inrOpcode(Register reg){
        byte prevValue; byte result;

        prevValue = (reg != Register.M) ? (byte) getRegisterValue(reg) : mmu.readByteData(getRegisterValue(Register.HL));

        if(reg != Register.M) { setRegisterValue(reg, (short) (getRegisterValue(reg) + 1)); }
        else{ mmu.setShortData(getRegisterValue(Register.HL), mmu.readShortData((short) (getRegisterValue(Register.HL) + 1))); cycles += 5; }

        result = (reg != Register.M) ? (byte) getRegisterValue(reg) : mmu.readByteData(getRegisterValue(Register.HL));

        checkSetSignFlag((short) Byte.toUnsignedInt(result));
        checkSetZeroFlag((short) Byte.toUnsignedInt(result));
        checkSetAuxiliaryCarryFlag(Operation.ADD, (short) Byte.toUnsignedInt(prevValue), (short) Byte.toUnsignedInt(result));
        checkSetParityFlag((short) Byte.toUnsignedInt(result));

        cycles += 5;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //NOTE::DCR reg | 1 | 5 M = 10 | S Z A P -
    private void dcrOpcode(Register reg) {
        byte prevValue; byte result;

        prevValue = (reg != Register.M) ? (byte) getRegisterValue(reg) : mmu.readByteData(getRegisterValue(Register.HL));

        if(reg != Register.M) { setRegisterValue(reg, (short) (getRegisterValue(reg) - 1)); }
        else{ mmu.setShortData(getRegisterValue(Register.HL), mmu.readShortData((short) (getRegisterValue(Register.HL) - 1))); cycles += 5; }

        result = (reg != Register.M) ? (byte) getRegisterValue(reg) : mmu.readByteData(getRegisterValue(Register.HL));

        checkSetSignFlag((short) Byte.toUnsignedInt(result));
        checkSetZeroFlag((short) Byte.toUnsignedInt(result));
        checkSetAuxiliaryCarryFlag(Operation.SUB, (short) Byte.toUnsignedInt(prevValue), (short) Byte.toUnsignedInt(result));
        checkSetParityFlag((short)Byte.toUnsignedInt(result));

        cycles += 5;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //NOTE::HLT | 1 | 7 | - - - - -
    private void hltOpcode(){
        cycles += 7;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //NOTE::MOV reg, reg | 1 | 5 M = 7 | - - - - -
    private void movOpcode(Register reg1, Register reg2){
        if(reg1 != Register.M && reg2 != Register.M) { setRegisterValue(reg1, getRegisterValue(reg2)); }
        else if(reg1 != Register.M) { setRegisterValue(reg1, mmu.readByteData(getRegisterValue(Register.HL))); cycles += 2; }
        else { mmu.setByteData(getRegisterValue(Register.HL), (byte) getRegisterValue(reg2)); cycles +=2; }

        cycles += 5;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //NOTE::MVI reg, d8 | 2 | 7 M = 10 | - - - - -
    private void mviOpcode(Register reg){
        if(reg != Register.M) { setRegisterValue(reg, mmu.readByteData((short) (getRegisterValue(Register.PC) + 1))); }
        else { mmu.setByteData(getRegisterValue(Register.HL), mmu.readByteData((short) (getRegisterValue(Register.PC) + 1))); cycles += 3; }

        cycles += 7;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 2));
    }

    //NOTE::ADD reg | 1 | 4 M = 7 | S Z A P C
    //NOTE::ADC reg | 1 | 4 M = 7 | S Z A P C
    private void addOpcode(Register reg, boolean carryFlag){
        byte prevValue; byte result;

        prevValue = (byte) getRegisterValue(Register.A);

        if(reg != Register.M ) { setRegisterValue(Register.A, (short) (getRegisterValue(Register.A) + getRegisterValue(reg))); }
        else { setRegisterValue(Register.A, (short) (getRegisterValue(Register.A) + mmu.readByteData(getRegisterValue(Register.HL)))); cycles += 3; }

        if(carryFlag) { setRegisterValue(Register.A, (short) (getRegisterValue(Register.A) + 1));}

        result = (byte) getRegisterValue(Register.A);

        checkSetSignFlag((short) Byte.toUnsignedInt(result));
        checkSetZeroFlag((short) Byte.toUnsignedInt(result));
        checkSetAuxiliaryCarryFlag(Operation.ADD, (short) Byte.toUnsignedInt(prevValue), (short) Byte.toUnsignedInt(result));
        checkSetParityFlag((short) Byte.toUnsignedInt(result));
        checkSetCarryFlag(Operation.ADD, (short) Byte.toUnsignedInt(prevValue), (short) Byte.toUnsignedInt(result));

        cycles += 4;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //NOTE::SUB reg | 1 | 4 M = 7 | S Z A P C
    //NOTE::SBB reg | 1 | 4 M = 7 | S Z A P C
    private void subOpcode(Register reg, boolean carryFlag){
        byte prevValue; byte result;

        prevValue = (byte) getRegisterValue(Register.A);

        if(reg != Register.M ) { setRegisterValue(Register.A, (short) (getRegisterValue(Register.A) - getRegisterValue(reg))); }
        else { setRegisterValue(Register.A, (short) (getRegisterValue(Register.A) - mmu.readByteData(getRegisterValue(Register.HL)))); cycles += 3; }

        if(carryFlag) { setRegisterValue(Register.A, (short) (getRegisterValue(Register.A) - 1));}

        result = (byte) getRegisterValue(Register.A);

        checkSetSignFlag((short) Byte.toUnsignedInt(result));
        checkSetZeroFlag((short) Byte.toUnsignedInt(result));
        checkSetAuxiliaryCarryFlag(Operation.SUB, (short) Byte.toUnsignedInt(prevValue), (short) Byte.toUnsignedInt(result));
        checkSetParityFlag((short) Byte.toUnsignedInt(result));
        checkSetCarryFlag(Operation.SUB, (short) Byte.toUnsignedInt(prevValue), (short) Byte.toUnsignedInt(result));

        cycles += 4;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //NOTE::ANA reg | 1 | 4 M = 7 | S Z A P C
    private void anaOpcode(Register reg){
        byte prevValue; byte result;

        prevValue = (byte) getRegisterValue(Register.A);

        if(reg != Register.M ) { setRegisterValue(Register.A, (short) (getRegisterValue(Register.A) & getRegisterValue(reg))); }
        else { setRegisterValue(Register.A, (short) (getRegisterValue(Register.A) & mmu.readByteData(getRegisterValue(Register.HL)))); cycles += 3; }

        result = (byte) getRegisterValue(Register.A);

        checkSetSignFlag((short) Byte.toUnsignedInt(result));
        checkSetZeroFlag((short) Byte.toUnsignedInt(result));
        checkSetAuxiliaryCarryFlag(Operation.AND, (short) Byte.toUnsignedInt(prevValue), (short) Byte.toUnsignedInt(result));
        checkSetParityFlag((short) Byte.toUnsignedInt(result));
        checkSetCarryFlag(Operation.AND, (short) Byte.toUnsignedInt(prevValue), (short) Byte.toUnsignedInt(result));

        cycles += 4;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //NOTE::XRA reg | 1 | 4 M = 7 | S Z A P C
    private void xraOpcode(Register reg){
        byte prevValue; byte result;

        prevValue = (byte) getRegisterValue(Register.A);

        if(reg != Register.M ) { setRegisterValue(Register.A, (short) (getRegisterValue(Register.A) ^ getRegisterValue(reg))); }
        else { setRegisterValue(Register.A, (short) (getRegisterValue(Register.A)  ^ mmu.readByteData(getRegisterValue(Register.HL)))); cycles += 3; }

        result = (byte) getRegisterValue(Register.A);

        checkSetSignFlag((short) Byte.toUnsignedInt(result));
        checkSetZeroFlag((short) Byte.toUnsignedInt(result));
        checkSetAuxiliaryCarryFlag(Operation.XOR, (short) Byte.toUnsignedInt(prevValue), (short) Byte.toUnsignedInt(result));
        checkSetParityFlag((short) Byte.toUnsignedInt(result));
        checkSetCarryFlag(Operation.XOR, prevValue, (short) Byte.toUnsignedInt(result));

        cycles += 4;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //NOTE::ORA reg | 1 | 4 M = 7 | S Z A P C
    private void oraOpcode(Register reg){
        byte prevValue; byte result;

        prevValue = (byte) getRegisterValue(Register.A);

        if(reg != Register.M ) { setRegisterValue(Register.A, (short) (getRegisterValue(Register.A) | getRegisterValue(reg))); }
        else { setRegisterValue(Register.A, (short) (getRegisterValue(Register.A)  | mmu.readByteData(getRegisterValue(Register.HL)))); cycles += 3; }

        result = (byte) getRegisterValue(Register.A);

        checkSetSignFlag((short) Byte.toUnsignedInt(result));
        checkSetZeroFlag((short) Byte.toUnsignedInt(result));
        checkSetAuxiliaryCarryFlag(Operation.OR, (short) Byte.toUnsignedInt(prevValue), (short) Byte.toUnsignedInt(result));
        checkSetParityFlag((short) Byte.toUnsignedInt(result));
        checkSetCarryFlag(Operation.OR, (short) Byte.toUnsignedInt(prevValue), (short) Byte.toUnsignedInt(result));

        cycles += 4;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //NOTE::CMP reg | 1 | 4 M = 7 | S Z A P C
    private void cmpOpcode(Register reg){
        byte prevValue; byte result;

        prevValue = (byte) getRegisterValue(Register.A);

        if(reg != Register.M ) {
            prevValue = (byte) getRegisterValue(reg);
            result = (byte) (getRegisterValue(Register.A) - getRegisterValue(reg));
        } else {
            prevValue = mmu.readByteData(getRegisterValue(Register.HL));
            result = (byte) (getRegisterValue(Register.A) - mmu.readByteData(getRegisterValue(Register.HL))); cycles += 3;
        }

        checkSetSignFlag((short) Byte.toUnsignedInt(result));
        checkSetZeroFlag((short) Byte.toUnsignedInt(result));
        checkSetAuxiliaryCarryFlag(Operation.SUB, (short) Byte.toUnsignedInt(prevValue), result);
        checkSetParityFlag((short) Byte.toUnsignedInt(result));
        checkSetCarryFlag(Operation.SUB, (short) Byte.toUnsignedInt(prevValue), (short) Byte.toUnsignedInt(result));

        cycles += 4;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //ACI/SBI/XRI/CPI d8 | 2 | 7 | S Z A P C
    private void cpiOpcode(Operation operation){
        byte prevValue = (byte) getRegisterValue(Register.A);
        byte readValue = mmu.readByteData((short) (getRegisterValue(Register.PC) + 1));
        byte result = readValue;

        switch (operation){
            case ADD -> result = (byte) (prevValue + readValue);
            case SUB -> result = (byte) (prevValue - readValue);
            case XOR -> result = (byte) (prevValue ^ readValue);
            //FIXME::This is a hack don't know fix this is the correct way for this to get done
            case NULL -> result = (byte) (prevValue - readValue);
        }

        checkSetSignFlag((short) Byte.toUnsignedInt(result));
        checkSetZeroFlag((short) Byte.toUnsignedInt(result));
        checkSetAuxiliaryCarryFlag(Operation.SUB, (short) Byte.toUnsignedInt(prevValue), result);
        checkSetParityFlag((short) Byte.toUnsignedInt(result));
        checkSetCarryFlag(Operation.SUB, (short) Byte.toUnsignedInt(prevValue), (short) Byte.toUnsignedInt(result));

        System.out.println("__________________");
        System.out.printf("PrevValue: %x\n", prevValue);
        System.out.printf("ReadValue: %x\n", readValue);
        System.out.println(flagsToString());

        cycles += 2;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 2));
    }

    //NOTE::JMP/JNZ/JNC/JPO/JP/JZ/JC/JPE/JM a16 | 3 | 10 | - - - - -
    private void jmpOpcode(Flags flag, FlagChoice flagChoice){
        if(flagChoice == FlagChoice.NULL || (getFlag(flag) && flagChoice == FlagChoice.TRUE) || (!getFlag(flag) && flagChoice == FlagChoice.FALSE)){
                setRegisterValue(Register.PC, (mmu.readShortData((short) (getRegisterValue(Register.PC) + 1))));
                cycles += 7;
        }
        else
            setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 3));

        cycles += 3;
    }

    //NOTE::POP reg | 1 | 10 | - - - - - PSW =  S Z A P C
    private void popOpcode(Register reg){
        setRegisterValue(reg, mmu.readShortData(getRegisterValue(Register.SP)));
        setRegisterValue(Register.SP, (short) (getRegisterValue(Register.SP) + 2));

        cycles += 10;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //NOTE::PUSH reg | 1 | 11 | - - - -
    private void pushOpcode(Register reg){
        mmu.setShortData(getRegisterValue(Register.SP), getRegisterValue(reg));

        cycles += 1;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //NOTE::RET | 1 | 10 | - - - - -
    //NOTE::RT | 1 | 11/5 | - - - - -
    //NOTE::RF | 1 | 11/5 | - - - - -
    private void rtOpcode(Flags flag, boolean flagTruth){
        cycles += 10;

        if (flag != null && ((!getFlag(flag) && flagTruth) || (getFlag(flag) && !flagTruth))){ cycles -= 5; return; } else { cycles += 10; }

        setRegisterValue(Register.SP, (short) (getRegisterValue(Register.SP) - 2));
        setRegisterValue(Register.PC, mmu.readShortData(getRegisterValue(Register.SP)));
    }

    //CALL a16 | 3 | 7 | - - - -
    //CT a16 | 3 | 17/11 | - - - - -
    //CF a16 | 3 | 17/11 | - - - - -
    private void callOpcode(Flags flag, boolean flagTruth) {
        //NOTE::We know we at least have to cycle this many times
        cycles += 7;

        //NOTE::Checking if we should not jump
        if (flag != null && ((!getFlag(flag) && flagTruth) || (getFlag(flag) && !flagTruth))){ cycles += 4; return; } else { cycles += 10; }

        //NOTE::Setting the stack pointer
        mmu.setShortData(getRegisterValue(Register.SP), (short) (getRegisterValue(Register.PC) + 3));
        setRegisterValue(Register.SP, (short) (getRegisterValue(Register.SP) + 2));

        setRegisterValue(Register.PC, mmu.readShortData((short) (getRegisterValue(Register.PC) + 1)));
    }

    //NOTE::LDAX reg | 1 | 7 | - - - - -
    private void ldaxOpcode(Register reg) {
        setRegisterValue(Register.A, mmu.readByteData(getRegisterValue(reg)));

        cycles += 7;
        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 1));
    }

    //NOTE::LHLD a16 | 3 | 16 | - - - - -
    private void lhldOpcode(){
        setRegisterValue(Register.HL, mmu.readShortData(mmu.readShortData((short) (getRegisterValue(Register.PC) + 1))));

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 3));
        cycles += 16;
    }

    //NOTE::LDA a16 | 3 | 13 | - - - - -
    private void ldaOpcode(){
        setRegisterValue(Register.A, mmu.readByteData(mmu.readShortData((short) (getRegisterValue(Register.PC) + 1))));

        setRegisterValue(Register.PC, (short) (getRegisterValue(Register.PC) + 3));
        cycles += 16;
    }

    public String registersToString(){
        return super.registersToString();
    }

    public String flagsToString(){
        return super.flagsToString();
    }

    public String opcodeToString(short opcode){
        return getOpcodeString(opcode);
    }
}
