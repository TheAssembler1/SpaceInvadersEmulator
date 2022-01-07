package core.gpu;

import core.cpu.Intel8080;
import util.Window;

import javax.swing.*;
import java.awt.*;

public class Gpu extends JPanel{
    JFrame window;
    Intel8080 cpu;

    static private short screenXResolution = 256;
    static private short screenYResolution = 224;

    static private int halfScreenCycles = 68;
    static private int currentPixel = 0;

    static private boolean halfScreenInterrupt = true;

    int availableCycles = 0;

    short midScreenInterrupt = 0x8;
    short endScreenInterrupt = 0x10;

    public Gpu(Intel8080 cpu){
        window = new Window("Space Invaders");
        this.cpu = cpu;

        setPreferredSize(new Dimension(screenXResolution, screenYResolution));

        window.add(this);
        window.pack();
        //window.setResizable(false);
        window.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        drawScreen(g);
    }

    private void drawScreen(Graphics g){
        while(availableCycles > 68) {
            System.out.println("INFO::Rendering half of line");

            availableCycles -= 68;

            byte[] pixelBuffer = cpu.getPixelBuffer();

            for(int i = 0; i < (screenXResolution / 2) - 1; i++){
                int index = (currentPixel + i)  / 8 ;
                byte currentByte = pixelBuffer[index];

                for(int j = 0; j <= 7; j++) {
                    if (cpu.getBitOfByte(currentByte, j))
                        g.setColor(Color.WHITE);
                    else
                        g.setColor(Color.BLACK);

                    g.drawRect(currentPixel % screenXResolution, currentPixel / screenXResolution, 0, 0);
                }
            }

            currentPixel += screenXResolution / 2;

            if(currentPixel > (screenXResolution * screenYResolution) - 1)
                currentPixel = 0;

            if(halfScreenInterrupt) {
                halfScreenInterrupt = false;
                cpu.rstOpcode(midScreenInterrupt);
            } else {
                halfScreenInterrupt = true;
                cpu.rstOpcode(endScreenInterrupt);
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