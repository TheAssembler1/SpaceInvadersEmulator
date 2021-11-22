package core;

import cpu.Intel8080;
import cpu.Intel8080Base;
import memory.Mmu;

public class Entry {
    public static void main(String[] args) {
        Mmu mmu = new Mmu(0x1FFF + 1);
        mmu.loadRom();

        Intel8080 cpu = new Intel8080(mmu);

        System.out.println(mmu);
        System.out.println(cpu);
    }
}
