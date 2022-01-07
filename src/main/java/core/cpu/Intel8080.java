package core.cpu;

import core.memory.Mmu;
import debug.Debugger;

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
        //NOTE::RLC/RRC | 1 | 4 | - - - - C
        opcodes[0x07] = () -> rcOpcode(false);
        opcodes[0x0F] = () -> rcOpcode(true);
        //NOTE::RAL/RAR | 1 | 4 | - - - - C
        opcodes[0x17] = () -> raOpcode(false);
        opcodes[0x1F] = () -> raOpcode(true);
        //NOTE::DAA | 1 | 4  | S Z A P C
        opcodes[0x27] = this::daaOpcode;
        //NOTE::STC | 1 | 4 | - - - - C
        opcodes[0x37] = this::stcOpcode;
        //NOTE::DAD reg | 1 |  10 | - - - - C
        opcodes[0x09] = () -> dadOpcode(Register.BC);
        opcodes[0x19] = () -> dadOpcode(Register.DE);
        opcodes[0x29] = () -> dadOpcode(Register.HL);
        opcodes[0x39] = () -> dadOpcode(Register.SP);
        //NOTE::LDAX reg | 1 | 7 | - - - - -
        opcodes[0x0A] = () -> ldaxOpcode(Register.BC);
        opcodes[0x1A] = () -> ldaxOpcode(Register.DE);
        //NOTE::LHLD a16 | 3 | 16 | - - - - -
        opcodes[0x2A] = this::lhldOpcode;
        //NOTE::LDA a16 | 3 | 13 | - - - - -
        opcodes[0x3A] = this::ldaOpcode;
        //NOTE::DCX reg | 1 | 5 | - - - - -
        opcodes[0x0B] = () -> dcxOpcode(Register.BC);
        opcodes[0x1B] = () -> dcxOpcode(Register.DE);
        opcodes[0x2B] = () -> dcxOpcode(Register.HL);
        opcodes[0x3B] = () -> dcxOpcode(Register.SP);
        //NOTE::CMA | 1 | 4 | - - - - -
        opcodes[0x2F] = this::cmaOpcode;
        //NOTE::CMC | 1 | 4 | - - - - C
        opcodes[0x3F] = this::cmcOpcode;
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
        //NOTE::HLT | 1 | 7 | - - - - -
        opcodes[0x76] = this::hltOpcode;
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
        //NOTE::RF | 1 | 11/5 | - - - - -
        opcodes[0xC0] = () -> rtfOpcode(Flags.ZERO_FLAG, FlagChoice.FALSE);
        opcodes[0xD0] = () -> rtfOpcode(Flags.CARRY_FLAG, FlagChoice.FALSE);
        opcodes[0xE0] = () -> rtfOpcode(Flags.PARITY_FLAG, FlagChoice.FALSE);
        opcodes[0xF0] = () -> rtfOpcode(Flags.SIGN_FLAG, FlagChoice.FALSE);
        //NOTE::RT | 1 | 11/5 | - - - - -
        opcodes[0xC8] = () -> rtfOpcode(Flags.ZERO_FLAG, FlagChoice.TRUE);
        opcodes[0xD8] = () -> rtfOpcode(Flags.CARRY_FLAG, FlagChoice.TRUE);
        opcodes[0xE8] = () -> rtfOpcode(Flags.PARITY_FLAG, FlagChoice.TRUE);
        opcodes[0xF8] = () -> rtfOpcode(Flags.SIGN_FLAG, FlagChoice.TRUE);
        //NOTE::POP reg | 1 | 10 | - - - - - PSW =  S Z A P C
        opcodes[0xC1] = () -> popOpcode(Register.BC);
        opcodes[0xD1] = () -> popOpcode(Register.DE);
        opcodes[0xE1] = () -> popOpcode(Register.HL);
        opcodes[0xF1] = () -> popOpcode(Register.AF);
        //NOTE::JMP/JNZ/JNC/JPO/JP/JZ/JC/JPE/JM a16 | 3 | 10 | - - - - -
        opcodes[0xC2] = () -> jtfOpcode(Flags.ZERO_FLAG, FlagChoice.FALSE);
        opcodes[0xD2] = () -> jtfOpcode(Flags.CARRY_FLAG, FlagChoice.FALSE);
        opcodes[0xE2] = () -> jtfOpcode(Flags.PARITY_FLAG, FlagChoice.FALSE);
        opcodes[0xF2] = () -> jtfOpcode(Flags.SIGN_FLAG, FlagChoice.FALSE);

        opcodes[0xCA] = () -> jtfOpcode(Flags.ZERO_FLAG, FlagChoice.TRUE);
        opcodes[0xDA] = () -> jtfOpcode(Flags.CARRY_FLAG, FlagChoice.TRUE);
        opcodes[0xEA] = () -> jtfOpcode(Flags.PARITY_FLAG, FlagChoice.TRUE);
        opcodes[0xFA] = () -> jtfOpcode(Flags.SIGN_FLAG, FlagChoice.TRUE);
        //NOTE::JMP a16 | 3 | 10 | - - - - -
        opcodes[0xC3] = this::jmpOpcode;
        opcodes[0xCB] = this::jmpOpcode;
        //FIXME::Temp code for input
        //NOTE::OUT d8 | 2 | 10 | - - - -
        opcodes[0xD3] = this::outOpcode;
        //NOTE::XCHG/XTHL | 1 | 5/18 | - - - -
        opcodes[0xE3] = () -> xchOpcode(Register.SP);
        opcodes[0xEB] = () -> xchOpcode(Register.DE);
        //NOTE::DI/EI | 1 | 4 | - - - - -
        opcodes[0xF3] = () -> intOpcode(false);
        opcodes[0xFB] = () -> intOpcode(true);
        //CTF a16 | 3 | 17/11 | - - - - -
        opcodes[0xC4] = () -> ctfOpcode(Flags.ZERO_FLAG, FlagChoice.FALSE);
        opcodes[0xD4] = () -> ctfOpcode(Flags.CARRY_FLAG, FlagChoice.FALSE);
        opcodes[0xE4] = () -> ctfOpcode(Flags.PARITY_FLAG, FlagChoice.FALSE);
        opcodes[0xF4] = () -> ctfOpcode(Flags.SIGN_FLAG, FlagChoice.FALSE);

        opcodes[0xCC] = () -> ctfOpcode(Flags.ZERO_FLAG, FlagChoice.TRUE);
        opcodes[0xDC] = () -> ctfOpcode(Flags.CARRY_FLAG, FlagChoice.TRUE);
        opcodes[0xEC] = () -> ctfOpcode(Flags.PARITY_FLAG, FlagChoice.TRUE);
        opcodes[0xFC] = () -> ctfOpcode(Flags.SIGN_FLAG, FlagChoice.TRUE);
        //NOTE::PUSH reg | 1 | 11 | - - - -
        opcodes[0xC5] = () -> pushOpcode(Register.BC);
        opcodes[0xD5] = () -> pushOpcode(Register.DE);
        opcodes[0xE5] = () -> pushOpcode(Register.HL);
        opcodes[0xF5] = () -> pushOpcode(Register.AF);
        //NOTE::OPI d8 | 2 | 7 | S Z A P C
        opcodes[0xC6] = () -> opiOpcode(Operation.ADD, false);
        opcodes[0xD6] = () -> opiOpcode(Operation.SUB, false);
        opcodes[0xE6] = () -> opiOpcode(Operation.AND, false);
        opcodes[0xF6] = () -> opiOpcode(Operation.OR, false);

        opcodes[0xCE] = () -> opiOpcode(Operation.ADD, true);
        opcodes[0xDE] = () -> opiOpcode(Operation.SUB, true);
        opcodes[0xEE] = () -> opiOpcode(Operation.XOR, false);
        opcodes[0xFE] = () -> opiOpcode(Operation.NULL, false);
        //NOTE::RET | 1 | 10 | - - - - -
        opcodes[0xC9] = this::retOpcode;
        opcodes[0xD9] = this::retOpcode;
        //NOTE::PCHL/SPHL | 1 | 5 | - - - - -
        opcodes[0xE9] = () -> ldpcOpcode(Register.HL);
        opcodes[0xF9] = () -> ldpcOpcode(Register.PC);
        //FIXME::Temp code for input
        //NOTE::IN d8 | 2 | 10 | - - - -
        opcodes[0xDB] = this::inOpcode;
        //NOTE::CALL a16 | 3 | 17 | - - - - -
        opcodes[0xCD] = this::callOpcode;
        opcodes[0xDD] = this::callOpcode;
        opcodes[0xED] = this::callOpcode;
        opcodes[0xFD] = this::callOpcode;
    }

    public void executeOpcode(int opcode){
        opcodes[opcode].execute();
    }

    //NOTE::NOP | 1 | 4 | - - - - -
    private void nopOpcode(){
        setRegisterShortRelativeTo(Register.PC, (short) 1);
        cycles += 4;
    }

    //NOTE::LXI reg, d16 | 3 | 10 | - - - - -
    private void lxiOpcode(Register reg){
        setRegisterShortValue(reg, mmu.readShortData((getRegisterShortValue(Register.PC) + 1)));

        setRegisterShortRelativeTo(Register.PC, (short) 3);
        cycles += 10;
    }

    //NOTE::STAX reg | 1 | 7 | - - - - -
    private void staxOpcode(Register reg) {
        mmu.setShortData(getRegisterShortValue(reg), getRegisterShortValue(Register.AF));

        setRegisterShortRelativeTo(Register.PC, (short) 1);
        cycles += 7;
    }

    //NOTE::SHLD a16 | 3 | 16 | - - - - -
    private void shldOpcode(){
        mmu.setShortData(mmu.readShortData(getRegisterShortValue(Register.PC) + 1), getRegisterShortValue(Register.HL));

        setRegisterShortRelativeTo(Register.PC, (short)3);
        cycles += 16;
    }

    //NOTE::STA a16 | 3 | 13 | - - - - -
    private void staOpcode(){
        mmu.setByteData(mmu.readShortData(getRegisterShortValue(Register.PC) + 1), getRegisterByteValue(Register.A));

        setRegisterShortRelativeTo(Register.PC, (short) 3);
        cycles += 16;
    }

    //NOTE::INX reg | 1 | 5 | - - - - -
    private void inxOpcode(Register reg){
        setRegisterShortRelativeTo(reg, (short) 1);

        setRegisterShortRelativeTo(Register.PC, (short) 1);
        cycles += 5;
    }

    //NOTE::INR reg | 1 | 5 M = 10 | S Z A P -
    private void inrOpcode(Register reg){
        byte value1 = getRegisterByteValue(reg);
        byte value2 = 1;
        byte result = (byte) (value1 + value2);

        if(reg == Register.M)
            cycles += 5;

        setRegisterByteRelativeTo(reg, (byte) 1);

        checkSetSignFlag(result);
        checkSetZeroFlag(result);
        checkSetAuxiliaryCarryFlag(Operation.ADD, value1, value2);
        checkSetParityFlag(result);

        cycles += 5;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::DCR reg | 1 | 5 M = 10 | S Z A P -
    private void dcrOpcode(Register reg) {
        byte value1 = getRegisterByteValue(reg);
        byte value2 = 1;
        byte result = (byte) (value1 - value2);

        if(reg == Register.M)
            cycles += 5;

        setRegisterByteRelativeTo(reg, (byte) -1);

        checkSetSignFlag(result);
        checkSetZeroFlag(result);
        checkSetAuxiliaryCarryFlag(Operation.SUB, value1, value2);
        checkSetParityFlag(result);

        cycles += 5;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::MVI reg, d8 | 2 | 7 M = 10 | - - - - -
    private void mviOpcode(Register reg){
        if(reg == Register.M)
            cycles += 3;

        setRegisterByteValue(reg, mmu.readByteData(getRegisterShortValue(Register.PC) + 1));

        cycles += 7;
        setRegisterShortRelativeTo(Register.PC, (short) 2);
    }

    //NOTE::RLC/RRC | 1 | 4 | - - - - C
    private void rcOpcode(boolean rotRight){
        boolean bitOfAccum;

        if(rotRight) {
            bitOfAccum = getBitOfByte(getRegisterByteValue(Register.A), 0);
            setRegisterByteValue(Register.A, rotByteRight(getRegisterByteValue(Register.A)));
        } else {
            bitOfAccum = getBitOfByte(getRegisterByteValue(Register.A), 7);
            setRegisterByteValue(Register.A, rotByteLeft(getRegisterByteValue(Register.A)));
        }

        if(bitOfAccum)
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE);
        else
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE);

        cycles += 4;
        setRegisterShortValue(Register.PC, (short) 1);
    }

    //NOTE::RAL/RAR | 1 | 4 | - - - - C
    private void raOpcode(boolean rotRight){
        boolean bitOfAccum;

        if(rotRight) {
            setRegisterByteValue(Register.A, rotByteRight(getRegisterByteValue(Register.A)));
            bitOfAccum = getBitOfByte(getRegisterByteValue(Register.A), 0);
        } else {
            setRegisterByteValue(Register.A, rotByteLeft(getRegisterByteValue(Register.A)));
            bitOfAccum = getBitOfByte(getRegisterByteValue(Register.A), 7);
        }

        swapAccumWithCarry(rotRight, bitOfAccum);

        cycles += 4;
        setRegisterShortValue(Register.PC, (short) 1);
    }

    //NOTE::DAA | 1 | 4  | S Z A P C
    private void daaOpcode(){
        if((getRegisterByteValue(Register.A) & 0xF) > 9 || getFlag(Flags.AUXILIARY_CARRY_FLAG)) {
            checkSetAuxiliaryCarryFlag(Operation.ADD, getRegisterByteValue(Register.A), (byte) 6);
            setRegisterByteRelativeTo(Register.A, (byte) 6);
        } else if((getRegisterByteValue(Register.A) & 0xF0) > 9 || getFlag(Flags.CARRY_FLAG)) {
            byte temp = (byte) (0xF0 & getRegisterByteValue(Register.A));
            checkSetCarryFlag(Operation.ADD, temp, (byte) 6);
            temp += 6;
            setRegisterByteRelativeTo(Register.A, (byte) (getRegisterByteValue(Register.A) | temp));
        }

        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::STC | 1 | 4 | - - - - C
    private void stcOpcode(){
        setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE);
    }

    //NOTE::DAD reg | 1 |  10 | - - - - C
    private void dadOpcode(Register reg){
        short value1 = getRegisterShortValue(Register.HL);
        short value2 = getRegisterShortValue(reg);

        setRegisterShortRelativeTo(Register.HL, getRegisterShortValue(reg));

        checkSetCarryFlag(Operation.ADD, getHighByteOfShort(value1), getHighByteOfShort(value2));

        cycles += 10;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::LDAX reg | 1 | 7 | - - - - -
    private void ldaxOpcode(Register reg) {
        setRegisterByteValue(Register.A, mmu.readByteData(getRegisterShortValue(reg)));

        cycles += 7;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::LHLD a16 | 3 | 16 | - - - - -
    private void lhldOpcode(){
        setRegisterShortValue(Register.HL, mmu.readShortData(mmu.readShortData(getRegisterShortValue(Register.PC) + 1)));

        cycles += 16;
        setRegisterShortRelativeTo(Register.PC, (short)3);
    }

    //NOTE::LDA a16 | 3 | 13 | - - - - -
    private void ldaOpcode(){
        setRegisterByteValue(Register.A, mmu.readByteData(mmu.readShortData(getRegisterShortValue(Register.PC) + 1)));

        cycles += 13;
        setRegisterShortRelativeTo(Register.PC, (short) 3);
    }

    //NOTE::DCX reg | 1 | 5 | - - - - -
    private void dcxOpcode(Register reg){
        setRegisterShortRelativeTo(reg, (short) -1);

        cycles += 5;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::CMA | 1 | 4 | - - - - -
    private void cmaOpcode(){
        setRegisterByteRelativeTo(Register.A, (byte) ~getRegisterByteValue(Register.A));

        cycles += 4;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::CMC | 1 | 4 | - - - - C
    private void cmcOpcode(){
        if(getFlag(Flags.CARRY_FLAG))
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE);
        else
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE);

        cycles += 4;
        setRegisterShortValue(Register.PC, (short) 1);
    }

    //NOTE::MOV reg, reg | 1 | 5 M = 7 | - - - - -
    private void movOpcode(Register reg1, Register reg2){
        if(reg1 == Register.M || reg2 == Register.M)
            cycles += 2;

        setRegisterByteValue(reg1, getRegisterByteValue(reg2));

        cycles += 5;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::HLT | 1 | 7 | - - - - -
    private void hltOpcode(){
        cpuStoppped = true;

        cycles += 7;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::ADD/ADC reg | 1 | 4 M = 7 | S Z A P C
    private void addOpcode(Register reg, boolean addCarry){
        byte value1 = getRegisterByteValue(Register.A);
        byte value2 = getRegisterByteValue(reg);
        byte result = (byte) (value1 + value2);

        if(addCarry && getFlag(Flags.CARRY_FLAG))
            result++;

        setRegisterByteValue(Register.A, result);

        if(reg == Register.M)
            cycles += 3;

        checkSetSignFlag(result);
        checkSetZeroFlag(result);
        checkSetAuxiliaryCarryFlag(Operation.ADD, value1, value2);
        checkSetParityFlag(result);
        checkSetCarryFlag(Operation.ADD, value1, value2);

        cycles += 4;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::SUB/SBB reg | 1 | 4 M = 7 | S Z A P C
    private void subOpcode(Register reg, boolean addCarry){
        byte value1 = getRegisterByteValue(Register.A);
        byte value2 = getRegisterByteValue(reg);
        byte result = (byte) (value1 - value2);

        if(addCarry && getFlag(Flags.CARRY_FLAG))
            result++;

        setRegisterByteValue(Register.A, result);

        if(reg == Register.M)
            cycles += 3;

        checkSetSignFlag(result);
        checkSetZeroFlag(result);
        checkSetAuxiliaryCarryFlag(Operation.SUB, value1, value2);
        checkSetParityFlag(result);
        checkSetCarryFlag(Operation.SUB, value1, value2);

        cycles += 4;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::ANA reg | 1 | 4 M = 7 | S Z A P C
    private void anaOpcode(Register reg){
        byte result = (byte) (getRegisterByteValue(Register.A) & getRegisterByteValue(reg));

        if(reg == Register.M)
            cycles += 3;

        setRegisterByteValue(Register.A, result);

        checkSetSignFlag(result);
        checkSetZeroFlag(result);
        //NOTE::Read that this flag is supposed to reflect a logical or with bit 3
        if(getBitOfByte(getRegisterByteValue(Register.A), 3) || getBitOfByte(getRegisterByteValue(reg), 3))
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE, FlagChoice.NULL, FlagChoice.NULL);
        else
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE, FlagChoice.NULL, FlagChoice.NULL);
        checkSetParityFlag(result);
        setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE);

        cycles += 4;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::XRA reg | 1 | 4 M = 7 | S Z A P C
    private void xraOpcode(Register reg){
        byte result = (byte) (getRegisterByteValue(Register.A) ^ getRegisterByteValue(reg));

        if(reg == Register.M)
            cycles += 3;

        setRegisterByteValue(Register.A, result);

        checkSetSignFlag(result);
        checkSetZeroFlag(result);
        //NOTE::Read that this flag is supposed to reflect a logical or with bit 3
        if(getBitOfByte(getRegisterByteValue(Register.A), 3) || getBitOfByte(getRegisterByteValue(reg), 3))
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE, FlagChoice.NULL, FlagChoice.NULL);
        else
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE, FlagChoice.NULL, FlagChoice.NULL);
        checkSetParityFlag(result);
        setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE);

        cycles += 4;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::ORA reg | 1 | 4 M = 7 | S Z A P C
    private void oraOpcode(Register reg){
        byte result = (byte) (getRegisterByteValue(Register.A) | getRegisterByteValue(reg));

        if(reg == Register.M)
            cycles += 3;

        setRegisterByteValue(Register.A, result);

        checkSetSignFlag(result);
        checkSetZeroFlag(result);
        //NOTE::Read that this flag is supposed to reflect a logical or with bit 3
        if(getBitOfByte(getRegisterByteValue(Register.A), 3) || getBitOfByte(getRegisterByteValue(reg), 3))
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE, FlagChoice.NULL, FlagChoice.NULL);
        else
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE, FlagChoice.NULL, FlagChoice.NULL);
        checkSetParityFlag(result);
        setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE);

        cycles += 4;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::CMP reg | 1 | 4 M = 7 | S Z A P C
    private void cmpOpcode(Register reg){
        byte value1 = getRegisterByteValue(Register.A);
        byte value2 = getRegisterByteValue(reg);
        byte result = (byte) (value1 - value2);

        if(reg == Register.M)
            cycles += 3;

        checkSetSignFlag(result);
        checkSetZeroFlag(result);
        checkSetAuxiliaryCarryFlag(Operation.SUB, value1, value2);
        checkSetParityFlag(result);
        checkSetCarryFlag(Operation.SUB, value1, value2);

        cycles += 4;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::RF/RT | 1 | 11/5 | - - - - -
    private void rtfOpcode(Flags flag, FlagChoice flagChoice){
        if ((!getFlag(flag) && flagChoice == FlagChoice.TRUE) || (getFlag(flag) && flagChoice == FlagChoice.FALSE)){
            cycles += 5;
            setRegisterShortRelativeTo(Register.PC, (short) 1);
            return;
        }

        setRegisterShortRelativeTo(Register.SP, (short) 2);

        cycles += 11;
        setRegisterShortValue(Register.PC, mmu.readShortData(getRegisterShortValue(Register.SP)));
    }

    //NOTE::POP reg | 1 | 10 | - - - - - PSW =  S Z A P C
    private void popOpcode(Register reg){
        setRegisterShortValue(reg, mmu.readShortData(getRegisterShortValue(Register.SP)));
        setRegisterShortRelativeTo(Register.SP, (short) 2);

        cycles += 10;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::JT/JF a16 | 3 | 10 | - - - - -
    private void jtfOpcode(Flags flag, FlagChoice flagChoice){
        if((getFlag(flag) && flagChoice == FlagChoice.TRUE) || (!getFlag(flag) && flagChoice == FlagChoice.FALSE)){
            setRegisterShortValue(Register.PC, mmu.readShortData(getRegisterShortValue(Register.PC) + 1));
            cycles += 7;
        }
        else
            setRegisterShortRelativeTo(Register.PC, (short) 3);

        cycles += 3;
    }

    //NOTE::JMP a16 | 3 | 10 | - - - - -
    private void jmpOpcode(){
        cycles += 10;
        setRegisterShortValue(Register.PC, mmu.readShortData(getRegisterShortValue(Register.PC) + 1));
    }

    //FIXME::Temp code for output
    //NOTE::IN d8 | 2 | 10 | - - - - -
    private void outOpcode(){
        cycles += 10;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::XCHG/XTHL | 1 | 5/18 | - - - - -
    private void xchOpcode(Register reg){
        short hlReg = getRegisterShortValue(Register.HL);

        if(reg == Register.SP) { cycles += 13; }

        setRegisterShortValue(Register.HL, getRegisterShortValue(reg));
        setRegisterShortValue(reg, hlReg);

        cycles += 5;
        setRegisterShortRelativeTo(Register.PC, (short)1);
    }

    //NOTE::DI/EI | 1 | 4 | - - - - -
    private void intOpcode(boolean enable){
        if(enable)
            intEnabled = true;
        else
            intEnabled = false;

        cycles += 4;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //CT/CF a16 | 3 | 17/11 | - - - - -
    private void ctfOpcode(Flags flag, FlagChoice flagChoice) {
        //NOTE::Checking if we should not jump
        if ((!getFlag(flag) && flagChoice == FlagChoice.TRUE) || (getFlag(flag) && flagChoice == FlagChoice.FALSE)){
            cycles += 11;
            setRegisterShortValue(Register.PC, (short) 11);
            return;
        }

        //NOTE::Setting the stack pointer
        mmu.setShortData(getRegisterShortValue(Register.SP), (short) (getRegisterShortValue(Register.PC) + 3));
        setRegisterShortRelativeTo(Register.SP, (short) -2);

        cycles += 17;
        setRegisterShortValue(Register.PC, mmu.readShortData(getRegisterShortValue(Register.PC) + 1));
    }

    //NOTE::PUSH reg | 1 | 11 | - - - -
    private void pushOpcode(Register reg){
        mmu.setShortData(getRegisterShortValue(Register.SP), getRegisterShortValue(reg));
        setRegisterShortRelativeTo(Register.SP, (short) -2);

        cycles += 11;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }


    //NOTE::OPI d8 | 2 | 7 | S Z A P C
    private void opiOpcode(Operation operation, boolean addCarry){
        byte value1 = getRegisterByteValue(Register.A);
        byte value2 = mmu.readByteData(getRegisterShortValue(Register.PC) + 1);
        byte result = 0;

        cycles += 7;

        if(addCarry && getFlag(Flags.CARRY_FLAG))
            result++;

        switch(operation){
            case ADD -> result += (byte) (value1 + value2);
            case SUB -> result += (byte) (value1 - value2);
            case AND -> result += (byte) (value1 & value2);
            case OR -> result += (byte) (value1 | value2);
            case XOR -> result += (byte) (value1 ^ value2);
            case NULL -> {
                result += (byte) (value1 - value2);

                checkSetSignFlag(result);
                checkSetZeroFlag(result);
                checkSetAuxiliaryCarryFlag(Operation.SUB, value1, value2);
                checkSetParityFlag(result);
                checkSetCarryFlag(Operation.SUB, value1, value2);

                setRegisterShortRelativeTo(Register.PC, (short) 2);
                return;
            }
        }

        checkSetSignFlag(result);
        checkSetZeroFlag(result);
        //NOTE::Read that this flag is supposed to reflect a logical or with bit 3
        if(getBitOfByte(getRegisterByteValue(Register.A), 3) || getBitOfByte(value2, 3))
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.TRUE, FlagChoice.NULL, FlagChoice.NULL);
        else
            setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE, FlagChoice.NULL, FlagChoice.NULL);
        setFlag(FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.NULL, FlagChoice.FALSE);
        checkSetParityFlag(result);

        setRegisterShortRelativeTo(Register.PC, (short) 2);
    }

    //NOTE::RET | 1 | 10 | - - - - -
    private void retOpcode(){
        setRegisterShortRelativeTo(Register.SP, (short) 2);

        cycles += 10;
        setRegisterShortValue(Register.PC, mmu.readShortData(getRegisterShortValue(Register.SP)));
    }

    //NOTE::PCHL/SPHL | 1 | 5 | - - - - -
    private void ldpcOpcode(Register reg){
        setRegisterShortValue(Register.PC, getRegisterShortValue(reg));

        cycles += 5;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //FIXME::Temp code for input
    //NOTE::IN d8 | 2 | 10 | - - - - -
    private void inOpcode(){
        cycles += 10;
        setRegisterShortRelativeTo(Register.PC, (short) 1);
    }

    //NOTE::CALL a16 | 3 | 17 | - - - - -
    private void callOpcode(){
        //NOTE::Setting the stack pointer
        mmu.setShortData(getRegisterShortValue(Register.SP), (short) (getRegisterShortValue(Register.PC) + 3));
        setRegisterShortRelativeTo(Register.SP, (short) -2);

        cycles += 17;
        setRegisterShortValue(Register.PC, mmu.readShortData(getRegisterShortValue(Register.PC) + 1));
    }

    public String registersToString(){
        return super.registersToString();
    }

    public String flagsToString(){
        return super.flagsToString();
    }

    public String opcodeToString(int opcode){
        return getOpcodeString((short) opcode);
    }
}
