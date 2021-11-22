package cpu;

import memory.Mmu;

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
        opcodes[0x04] = () -> inrOpcode(Register.BC);
        opcodes[0x14] = () -> inrOpcode(Register.DE);
        opcodes[0x24] = () -> inrOpcode(Register.HL);
        opcodes[0x34] = () -> inrOpcode(Register.M);
    }

    public void executeOpcode(short opcode){
        opcodes[opcode].execute();
    }

    //NOTE::NOP | 1 | 4 | - - - - -
    private void nopOpcode(){
        registers.pc++;
        cycles += 4;
    }

    //NOTE::LXI reg, d16 | 3 | 10 | - - - - -
    private void lxiOpcode(Register reg){
        switch(reg){
            case BC -> setRegisterPairValue(Register.BC, mmu.readData((short) (registers.pc + 1)));
            case DE -> setRegisterPairValue(Register.DE, mmu.readData((short) (registers.pc + 1)));
            case HL -> setRegisterPairValue(Register.HL, mmu.readData((short) (registers.pc + 1)));
            case SP -> setRegisterPairValue(Register.SP, mmu.readData((short) (registers.pc + 1)));
        }

        registers.pc += 2;
        cycles += 10;
    }

    //NOTE::STAX reg | 1 | 7 | - - - - -
    private void staxOpcode(Register reg) {
        switch (reg) {
            case BC -> mmu.setData(registers.a, getRegisterPairValue(Register.BC));
            case DE -> mmu.setData(registers.a, getRegisterPairValue(Register.DE));
        }

        registers.pc++;
        cycles += 7;
    }

    //NOTE::SHLD a16 | 3 | 16 | - - - -
    private void shldOpcode(){
        mmu.setData(getRegisterPairValue(Register.HL), mmu.readData((short) (registers.pc + 1)));

        registers.pc += 3;
        cycles += 16;
    }

    //NOTE::STA a16 | 3 | 13 | - - - -
    private void staOpcode(){
        mmu.setData(registers.a, mmu.readData((short) (registers.pc + 1)));

        registers.pc += 3;
        cycles += 16;
    }

    //NOTE::INX reg | 1 | 5 | - - - - -
    private void inxOpcode(Register reg){
        switch(reg){
            case BC -> setRegisterPairValue(Register.BC, (short) (getRegisterPairValue(Register.BC) + 1));
            case DE -> setRegisterPairValue(Register.DE, (short) (getRegisterPairValue(Register.DE) + 1));
            case HL -> setRegisterPairValue(Register.HL, (short) (getRegisterPairValue(Register.HL) + 1));
            case SP -> registers.sp++;
        }

        registers.pc++;
        cycles += 5;
    }

    //NOTE::INR reg | 1 | 5 | S Z A P -
    private void inrOpcode(Register reg){
        short result = 0;
        short prevValue = 0;

        switch(reg){
            case BC -> {
                prevValue = getRegisterPairValue(Register.BC);
                result = (short) (prevValue + 1);
                setRegisterPairValue(Register.BC, result);
            }
            case DE -> {
                prevValue = getRegisterPairValue(Register.DE);
                result = (short) (prevValue + 1);
                setRegisterPairValue(Register.DE, result);
            }
            case HL -> {
                prevValue = getRegisterPairValue(Register.HL);
                result = (short) (prevValue + 1);
                setRegisterPairValue(Register.HL, result);
            }
            case M -> {
                prevValue = mmu.readData(getRegisterPairValue(Register.HL));
                result = (short) (prevValue + 1);
                mmu.setData(result, getRegisterPairValue(Register.HL));
            }
            case C -> {
                prevValue = registers.c;
                result = (short) (prevValue + 1);
                registers.c = (byte) result;
            }
        }

        checkSetSignFlag(ValueSizes.SHORT, result);
        checkSetZeroFlag(result);
        //FIXME::Add the size of the operand to the auxilary carry flag check
        checkSetAuxiliaryCarryFlag(Operations.ADD, prevValue, result);
        checkSetParityFlag(result);
    }


    @Override
    public String toString(){
        return super.toString();
    }
}
