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
        opcodes[0x04] = () -> inrOpcode(Register.BC);
        opcodes[0x14] = () -> inrOpcode(Register.DE);
        opcodes[0x24] = () -> inrOpcode(Register.HL);
        opcodes[0x34] = () -> inrOpcode(Register.M);
        opcodes[0x0C] = () -> inrOpcode(Register.C);
        opcodes[0x1C] = () -> inrOpcode(Register.E);
        opcodes[0x2C] = () -> inrOpcode(Register.L);
        opcodes[0x3C] = () -> inrOpcode(Register.A);
        //NOTE::DCR reg | 1 | 5 | S Z A P -
        opcodes[0x05] = () -> dcrOpcode(Register.BC);
        opcodes[0x15] = () -> dcrOpcode(Register.DE);
        opcodes[0x25] = () -> dcrOpcode(Register.HL);
        opcodes[0x35] = () -> dcrOpcode(Register.M);
        opcodes[0x0D] = () -> dcrOpcode(Register.C);
        opcodes[0x1D] = () -> dcrOpcode(Register.E);
        opcodes[0x2D] = () -> dcrOpcode(Register.L);
        opcodes[0x3D] = () -> dcrOpcode(Register.A);

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
        setRegisterPairValue(reg, mmu.readData((short) (registers.pc + 1)));

        registers.pc += 2;
        cycles += 10;
    }

    //NOTE::STAX reg | 1 | 7 | - - - - -
    private void staxOpcode(Register reg) {
        mmu.setData(registers.a, getRegisterPairValue(reg));

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
        if(reg != Register.SP)
            registers.sp++;
        else
            setRegisterPairValue(reg, (short) (getRegisterPairValue(reg) + 1));

        registers.pc++;
        cycles += 5;
    }

    //NOTE::INR reg | 1 | 5 | S Z A P -
    private void inrOpcode(Register reg){
        registers.pc++;
        cycles += 5;
    }

    //NOTE::DCR reg | 1 | 5 | S Z A P -
    private void dcrOpcode(Register reg) {
    }

    @Override
    public String toString(){
        return super.toString();
    }
}
