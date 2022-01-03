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
        window.setResizable(false);
        window.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        //drawScreen(g);
    }

    private void drawScreen(Graphics g){
        return;
        //byte[] pixelBuffer = cpu.getPixelBuffer();
    }

    public static short getScreenXResolution(){
        return screenXResolution;
    }

    public static short getScreenYResolution(){
        return screenYResolution;
    }
}