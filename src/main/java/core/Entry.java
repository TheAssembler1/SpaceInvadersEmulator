package core;

import cpu.Intel8080;
import memory.Mmu;

public class Entry {
    public static void main(String[] args) {
        Mmu memory = new Mmu(0x1FFF + 1);
        memory.loadRom();

        Intel8080 cpu = new Intel8080(memory);

        System.out.println(memory);
        System.out.println(cpu);
    }
}
