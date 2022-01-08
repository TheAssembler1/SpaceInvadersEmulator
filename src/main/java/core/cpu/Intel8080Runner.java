package core.cpu;

import core.gpu.Gpu;
import core.memory.Mmu;
import debug.Debugger;

public class Intel8080Runner implements Runnable{
    private boolean intEnabled = false;

    short midScreenInterrupt = 0x8;
    short endScreenInterrupt = 0x10;

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
                debugger.update(cpu, gpu);
                int opcode = Byte.toUnsignedInt(mmu.readByteData(cpu.getPCReg()));

                if(Short.toUnsignedInt(cpu.getPCReg()) > 0x1400 && Short.toUnsignedInt(cpu.getPCReg()) < 0x1421)
                    System.exit(0);

                cpu.executeOpcode(opcode);

                gpu.increaseAvailableCycles(cpu.getCycles());
                gpu.repaint();

                if(intEnabled) {
                    if (gpu.getGpuState() == Gpu.GPU_STATE.MID_SCREEN_INTERRUPT)
                        cpu.executeOpcode(midScreenInterrupt);
                    else if (gpu.getGpuState() == Gpu.GPU_STATE.FULL_SCREEN_INTERRUPT)
                        cpu.executeOpcode(endScreenInterrupt);
                }

                intEnabled = cpu.isIntEnabled();
            }
        }
    }
}
