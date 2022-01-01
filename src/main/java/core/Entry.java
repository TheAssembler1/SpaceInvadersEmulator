//NOTE::Check the working of the emulator https://altairclone.com/downloads/manuals/8080%20Programmers%20Manual.pdf
//NOTE::CHECK opcodes with these instructions http://www.emulator101.com/reference/8080-by-opcode.html
//NOTE::CHECK opcodes with this spreadsheet https://pastraiser.com/cpu/i8080/i8080_opcodes.html

package core;

import cpu.Intel8080;
import debug.Debugger;
import memory.Mmu;
import util.Window;

import javax.swing.*;

public class Entry {

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        Mmu mmu = new Mmu(0x1FFF + 1);
        mmu.loadRom();

        Intel8080 cpu = new Intel8080(mmu);

        Debugger debugger = new Debugger(cpu);

        while(true){
            //NOTE::Converting signed byte to unsigned int
            cpu.executeOpcode((short) Byte.toUnsignedInt(mmu.readOpcode(cpu.getPCReg())));
            debugger.update();
        }
    }
}
