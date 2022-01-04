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
        //window.setResizable(false);
        window.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        drawScreen(g);
    }

    private void drawScreen(Graphics g){
        byte[] pixelBuffer = cpu.getPixelBuffer();

        int currentRow = 0;

        for(int i = 0; i < pixelBuffer.length; i++){
            byte currentByte = pixelBuffer[i];

            for(int j = 0; j < 7; j++){
                if(cpu.checkBitOfByte(currentByte, (byte)j))
                    g.setColor(Color.WHITE);
                else
                    g.setColor(Color.BLACK);

                g.drawRect(((i * 8) + j) % screenXResolution, ((i * 8) + j) / screenXResolution, 0, 0);
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