package core.cpu;

import core.gpu.Gpu;
import core.memory.Mmu;
import debug.Debugger;

public class Intel8080Runner implements Runnable{
    private boolean intEnabled = true;
    private boolean cpuEnabled = true;

    Intel8080 cpu;
    Mmu mmu;
    Gpu gpu;
    Debugger debugger;

    public Intel8080Runner(Intel8080 cpu, Mmu mmu, Gpu gpu, Debugger debugger){
        this.cpu = cpu;
        this.mmu = mmu;
        this.gpu = gpu;
        this.debugger = debugger;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while(true){
            for(int i = 0; i <= cpu.cyclesPerSecond; i += cpu.getCycles()) {
                int opcode = Byte.toUnsignedInt(mmu.readByteData(cpu.getPCReg()));

                debugger.update(cpu, gpu);
                cpu.executeOpcode(opcode);

                gpu.increaseAvailableCycles(cpu.getCycles());
                gpu.repaint();
            }
        }
    }
}
