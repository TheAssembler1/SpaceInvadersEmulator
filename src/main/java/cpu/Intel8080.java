package cpu;

import memory.Mmu;

public class Intel8080 {
    private static class Registers{
        //NOTE::Accumulator | Flags
        //NOTE::Flag bits set as S:Z:0:A:0:P:1:C
        /*
        *7:S - Sign flag
        *6:Z - Zero
        *5:0 - Note used, always zero
        *4:A - Also called AC, auxiliary carry flag
        *3:0 - Not used, always 0
        *2:P - Parity flag
        *1:1 - Not used, always one
        *0:C - Carry flag
        */
        short af;
        //NOTE::General purpose
        short bc;
        //NOTE::General purpose
        short de;
        //NOTE::General purpose
        short hl;

        //NOTE::Stack pointer
        short sp;
        //NOTE::Program counter
        short pc;
    }

    //NOTE::M means memory pointed to by (hl)
    private enum RegisterSelection{
        AF, BC, DE, HL, SP, PC, M
    }

    private enum FlagsState{
        TRUE, FALSE, NULL
    }

    private enum Flags{
        SIGN_FLAG(7), ZERO_FLAG(6), AUXILIARY_CARRY_FLAG(4), PARITY_FLAG(2), CARRY_FLAG(0);

        int Pos;
        Flags(int Pos) {
            this.Pos = Pos;
        }
        int getPos(){
            return Pos;
        }
    }

    private Registers registers = new Registers();
    private Mmu mmu;
    private long cycles = 0;

    public Intel8080(Mmu mmu){
        //FIXME::Fix the initial state of the registers here
        registers.af = 0;
        registers.bc = 0;
        registers.de = 0;
        registers.hl = 0;
        registers.sp = 0;
        registers.pc = 0;

        setInitialFlagsState();

        this.mmu = mmu;
    }

    private void executeOpcode(byte opcode){
        //NOTE::Opcode bytes | cycles | flags
        switch (opcode) {
            //NOTE::NOP 1 | 4 | - - - - -
            case 0x00, 0x10, 0x20, 0x30, 0x08, 0x18, 0x28, 0x38 -> nopOpcode();
            //NOTE::LXI reg d16 3 | 10 | - - - - -
            case 0x01 -> lxiOpcode(RegisterSelection.BC);
            case 0x11 -> lxiOpcode(RegisterSelection.DE);
            case 0x21 -> lxiOpcode(RegisterSelection.HL);
            case 0x31 -> lxiOpcode(RegisterSelection.SP);
            //NOTE::STAX reg | 1 | 7 | - - - - -
            case 0x02 -> staxOpcode(RegisterSelection.BC);
            case 0x12 -> staxOpcode(RegisterSelection.DE);
            //NOTE::SHLD d16 3 | 16 | - - - - -
            case 0x22 -> shldOpcode();
            //NOTE::STA d16 3 | 13 | - - - - -
            case 0x32 -> staOpcode();
            //NOTE::INX reg | 1 | 5 | - - - - -
            case 0x03 -> inxOpcode(RegisterSelection.BC);
            case 0x13 -> inxOpcode(RegisterSelection.DE);
            case 0x23 -> inxOpcode(RegisterSelection.HL);
            case 0x33 -> inxOpcode(RegisterSelection.SP);
            default -> {
                System.out.println("ERROR::Opcode does not exist!!!");
                System.exit(-1);
            }
        }
    }

    //NOTE::NOP 1 | 4 | - - - - -
    private void nopOpcode(){
        registers.pc++;
        cycles += 4;
    }

    //NOTE::LXI reg d16 3 | 10 | - - - - -
    private void lxiOpcode(RegisterSelection reg){
        short readShort = mmu.readData((short)(registers.pc + 1));

        switch (reg) {
            case BC -> registers.bc = readShort;
            case DE -> registers.de = readShort;
            case HL -> registers.hl = readShort;
            case SP -> registers.sp = readShort;
        }

        registers.pc += 3;
        cycles += 10;
    }

    //NOTE::SHLD d16 3 | 16 | - - - - -
    private void shldOpcode(){
        mmu.setData(registers.hl, (short)(mmu.readData(registers.pc) + 1));

        registers.pc += 3;
        cycles += 16;
    }

    //NOTE::STA d16 3 | 13 | - - - - -
    private void staOpcode(){
        mmu.setData(registers.af, (short)(mmu.readData(registers.pc) + 1));

        registers.pc += 3;
        cycles += 13;
    }

    //NOTE::STAX reg | 1 | 7 | - - - - -
    private void staxOpcode(RegisterSelection reg){
        switch(reg){
            case BC -> mmu.setData(registers.af, registers.bc);
            case DE -> mmu.setData(registers.af, registers.de);
        }

        registers.pc++;
        cycles += 7;
    }

    //NOTE::MOV reg, reg, | 1 | reg = 5, M = 7 | - - - - -
    private void movOpcode(RegisterSelection regDest, RegisterSelection regSource){
        switch (regDest){

        }
    }

    //NOTE::INX reg | 1 | 5 | - - - - -
    private void inxOpcode(RegisterSelection reg){
        switch (reg){
            case BC -> registers.bc++;
            case DE -> registers.de++;
            case HL -> registers.hl++;
            case SP -> registers.pc++;
        }

        registers.pc++;
        cycles += 5;
    }

    private void setFlags(FlagsState signFlag, FlagsState zeroFlag, FlagsState auxiliaryCarryFlag, FlagsState parityFlag, FlagsState carryFlag){
        switch(signFlag){
            case TRUE -> registers.af |= 1 << Flags.SIGN_FLAG.getPos();
            case FALSE -> registers.af &= ~(1 << Flags.SIGN_FLAG.getPos());
        }

        switch (zeroFlag){
            case TRUE -> registers.af |= 1 << Flags.ZERO_FLAG.getPos();
            case FALSE -> registers.af &= ~(1 << Flags.ZERO_FLAG.getPos());
        }

        switch (auxiliaryCarryFlag){
            case TRUE -> registers.af |= 1 << Flags.AUXILIARY_CARRY_FLAG.getPos();
            case FALSE -> registers.af &= ~(1 << Flags.AUXILIARY_CARRY_FLAG.getPos());
        }

        switch (parityFlag){
            case TRUE -> registers.af |= 1 << Flags.PARITY_FLAG.getPos();
            case FALSE -> registers.af &= ~(1 << Flags.PARITY_FLAG.getPos());
        }

        switch (carryFlag){
            case TRUE -> registers.af |= Flags.CARRY_FLAG.getPos();
            case FALSE -> registers.af &= ~Flags.CARRY_FLAG.getPos();
        }
    }

    private boolean checkFlagState(Flags flag){
        return switch (flag) {
            case SIGN_FLAG -> (byte) (registers.af & (1 << Flags.SIGN_FLAG.getPos())) == 1;
            case ZERO_FLAG -> (byte) (registers.af & (1 << Flags.ZERO_FLAG.getPos())) == 1;
            case AUXILIARY_CARRY_FLAG -> (byte) (registers.af & (1 << Flags.AUXILIARY_CARRY_FLAG.getPos())) == 1;
            case PARITY_FLAG -> (byte) (registers.af & (1 << Flags.PARITY_FLAG.getPos())) == 1;
            case CARRY_FLAG -> (byte) (registers.af & (1 << Flags.CARRY_FLAG.getPos())) == 1;
        };
    }

    private void setInitialFlagsState(){
        registers.af = 0;

        //NOTE::This bit is always 1
        registers.af |= 1 << 1;
    }

    @Override
    public String toString(){
        String string = "Registers:\n";
        string = string.concat(String.format("AF: %x\n", registers.af));
        string = string.concat(String.format("BC: %x\n", registers.bc));
        string = string.concat(String.format("DE: %x\n", registers.de));
        string = string.concat(String.format("HL: %x\n", registers.hl));
        string = string.concat(String.format("SP: %x\n", registers.sp));
        string = string.concat(String.format("PC: %x\n", registers.pc));

        return string;
    }
}
