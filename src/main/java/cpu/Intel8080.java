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
    }

    public void executeOpcode(short opcode){
        opcodes[opcode].execute();
    }

    //NOTE::NOP | 1 | 4 | - - - - -
    private void nopOpcode(){
        registers.pc++;
        cycles += 4;
    }

    @Override
    public String toString(){
        return super.toString();
    }
}
