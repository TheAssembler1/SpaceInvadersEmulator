package core.gpu;

import core.cpu.Intel8080;
import util.Window;

import javax.swing.*;
import java.awt.*;

public class Gpu extends JPanel{
    JFrame window;
    Intel8080 cpu;

    static private final int screenXResolution = 256;
    static private final int screenYResolution = 224;
    static private final int halfScreenCycles = 33333 / 2;

    static private boolean halfScreenInterrupt = true;

    byte[] pixelBuffer = new byte[screenXResolution * screenYResolution];

    int availableCycles = 0;

    public enum GPU_STATE{
        NONE,
        MID_SCREEN_INTERRUPT,
        FULL_SCREEN_INTERRUPT
    }

    private GPU_STATE gpuState = GPU_STATE.NONE;

    public Gpu(Intel8080 cpu){
        window = new Window("Space Invaders");
        this.cpu = cpu;

        setPreferredSize(new Dimension(screenYResolution, screenXResolution));

        window.add(this);
        window.pack();
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        drawScreen(g);
    }

    private void drawScreen(Graphics g){
        gpuState = GPU_STATE.NONE;

        if(availableCycles > halfScreenCycles) {
            availableCycles -= halfScreenCycles;

            byte[] currentPixelBuffer = cpu.getPixelBuffer();

            int startingPixel;
            int endingPixel;

            if(halfScreenInterrupt){
                gpuState = GPU_STATE.MID_SCREEN_INTERRUPT;
                halfScreenInterrupt = false;

                startingPixel = 0;
                endingPixel = currentPixelBuffer.length / 2 - 1;
            }else{
                gpuState = GPU_STATE.FULL_SCREEN_INTERRUPT;
                halfScreenInterrupt = true;

                startingPixel = currentPixelBuffer.length / 2;
                endingPixel = currentPixelBuffer.length - 1;
            }

            if (endingPixel + 1 - startingPixel >= 0)
                System.arraycopy(currentPixelBuffer, startingPixel, pixelBuffer, startingPixel, endingPixel + 1 - startingPixel);
        }

        for(int i = 0; i < pixelBuffer.length - 1; i++){
            byte currentByte = pixelBuffer[i];

            for(int j = 0; j < 8; j++){
                if(cpu.getBitOfByte(currentByte, j))
                    g.setColor(Color.WHITE);
                else
                    g.setColor(Color.BLACK);

                int x = (i * 8 + j + 1) % screenXResolution;
                int y = (i * 8 + j + 1) / screenXResolution;

                g.drawRect(y, screenXResolution - x, 1, 1);
            }
        }
    }

    public GPU_STATE getGpuState(){
        return gpuState;
    }

    public int getCycles(){
        return availableCycles;
    }

    public void increaseAvailableCycles(int value){
        availableCycles += value;
    }

    public static short getScreenXResolution(){
        return screenXResolution;
    }

    public static short getScreenYResolution(){
        return screenYResolution;
    }
}