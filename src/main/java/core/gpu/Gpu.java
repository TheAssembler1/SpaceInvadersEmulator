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

    short midScreenInterrupt = 0x8;
    short endScreenInterrupt = 0x10;

    public Gpu(Intel8080 cpu){
        window = new Window("Space Invaders");
        this.cpu = cpu;

        setPreferredSize(new Dimension(screenXResolution, screenYResolution));

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
        if(availableCycles > halfScreenCycles) {
            availableCycles -= halfScreenCycles;

            byte[] currentPixelBuffer = cpu.getPixelBuffer();

            int startingPixel;
            int endingPixel;

            if(halfScreenInterrupt){
                System.out.println("INFO::Drawing half of the screen");
                halfScreenInterrupt = false;

                startingPixel = 0;
                endingPixel = currentPixelBuffer.length / 2 - 1;
                //cpu.executeOpcode(midScreenInterrupt);
            }else{
                System.out.println("INFO::Drawing end of the screen");
                halfScreenInterrupt = true;

                startingPixel = currentPixelBuffer.length / 2;
                endingPixel = currentPixelBuffer.length - 1;
                //cpu.executeOpcode(endScreenInterrupt);
            }

            if (endingPixel + 1 - startingPixel >= 0)
                System.arraycopy(currentPixelBuffer, startingPixel, pixelBuffer, startingPixel, endingPixel + 1 - startingPixel);
        }

        for(int i = 0; i < pixelBuffer.length - 1; i++){
            byte currentByte = pixelBuffer[i];

            for(int j = 0; j < 8; j++){
                if(cpu.getBitOfByte(currentByte, j))
                    g.setColor(Color.GREEN);
                else
                    g.setColor(Color.BLUE);

                int x = (i * 8 + j + 1) % screenXResolution;
                int y = (i * 8 + j + 1) / screenXResolution;

                g.drawRect(x, y, 1, 1);
            }
        }
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