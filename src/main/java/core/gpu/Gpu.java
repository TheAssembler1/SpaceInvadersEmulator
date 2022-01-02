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

    public Gpu(Intel8080 cpu){
        window = new Window("Space Invaders");
        this.cpu = cpu;

        setPreferredSize(new Dimension(screenXResolution, screenYResolution));

        window.add(this);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        drawScreen(g);
    }

    private void drawScreen(Graphics g){
        byte[] pixelBuffer = cpu.getPixelBuffer();

        short currentX = 0;
        short currentY = 0;

        for (short currentByte : pixelBuffer) {
            for (byte j = 0; j < 7; j++) {
                if (cpu.checkBitOfByte(currentByte, j))
                    g.setColor(Color.WHITE);
                else
                    g.setColor(Color.BLACK);

                //NOTE::Finally drawing the pixel
                g.drawRect(currentX, currentY, 0, 0);

                currentX++;
                if (currentX >= screenXResolution - 1) {
                    currentY++;
                    currentX = 0;
                }
            }
        }
    }

    public static short getScreenXResolution(){
        return screenXResolution;
    }

    public static short getScreenYResolution(){
        return screenYResolution;
    }
}