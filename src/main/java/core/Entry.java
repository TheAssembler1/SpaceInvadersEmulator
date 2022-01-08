//NOTE::Check the working of the emulator https://altairclone.com/downloads/manuals/8080%20Programmers%20Manual.pdf
//NOTE::CHECK opcodes with these instructions http://www.emulator101.com/reference/8080-by-opcode.html
//NOTE::CHECK opcodes with this spreadsheet https://pastraiser.com/cpu/i8080/i8080_opcodes.html

package core;

import core.cpu.Intel8080;
import core.cpu.Intel8080Runner;
import core.gpu.Gpu;
import debug.Debugger;
import core.memory.Mmu;

//FIXME::Notable things to check
//We need to disable and enable interrupts when going into them

public class Entry {
    public static void main(String[] args) throws InterruptedException {
        /*
         * 0000-1FFF 8K ROM
         * 2000-23FF 1K RAM
         * 2400-3FFF 7K Video RAM
         * 4000- RAM mirror
         */
        Mmu mmu = new Mmu(0x4000);
        mmu.loadRom();

        Debugger debugger = new Debugger(Debugger.RunMode.STEP_INSTRUCTIONS);
        Intel8080 cpu = new Intel8080(mmu, debugger);

        Gpu gpu = new Gpu(cpu);

        Intel8080Runner cpuRunner = new Intel8080Runner(cpu, mmu, gpu, debugger);
        cpuRunner.run();
    }
}
