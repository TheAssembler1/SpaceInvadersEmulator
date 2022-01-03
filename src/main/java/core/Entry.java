//NOTE::Check the working of the emulator https://altairclone.com/downloads/manuals/8080%20Programmers%20Manual.pdf
//NOTE::CHECK opcodes with these instructions http://www.emulator101.com/reference/8080-by-opcode.html
//NOTE::CHECK opcodes with this spreadsheet https://pastraiser.com/cpu/i8080/i8080_opcodes.html

package core;

import core.cpu.Intel8080;
import core.gpu.Gpu;
import debug.Debugger;
import core.memory.Mmu;

//FIXME::Current bit that's causing the error
/*
ClearScreen:
; Clear the screen
; Thanks to Mark Tankard for pointing out what this really does
1A5C: 21 00 24        LD      HL,$2400            ; Screen coordinate
1A5F: 36 00           LD      (HL),$00            ; Clear it
1A61: 23              INC     HL                  ; Next byte
1A62: 7C              LD      A,H                 ; Have we done ...
1A63: FE 40           CP      $40                 ; ... all the screen? //FIXME::Check that this compare method is working
1A65: C2 5F 1A        JP      NZ,$1A5F            ; No ... keep going
1A68: C9              RET                         ; Out
 */


public class Entry {

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        /*
         * 0000-1FFF 8K ROM
         * 2000-23FF 1K RAM
         * 2400-3FFF 7K Video RAM
         * 4000- RAM mirror
         */
        Mmu mmu = new Mmu(0x4000 + 1);
        mmu.loadRom();

        Intel8080 cpu = new Intel8080(mmu);
        Gpu gpu = new Gpu(cpu);

        Debugger debugger = new Debugger(cpu, Debugger.RunMode.RUN_INSTRUCTIONS);

        while(true){
            short opcode = (short) Byte.toUnsignedInt(mmu.readOpcode(cpu.getPCReg()));
            //NOTE::Updating the debugger
            debugger.update(opcode);
            //NOTE::Converting signed byte to unsigned int
            cpu.executeOpcode(opcode);
            //NOTE::Rendering pixel window
            gpu.repaint();
        }
    }
}
