package util;

import javax.swing.JFrame;
import java.awt.*;

public class Window extends JFrame {
    public Window(String winTitle, int width, int height){
        super(winTitle);

        setPreferredSize(new Dimension(width, height));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
