package util;

import javax.swing.JFrame;
import java.awt.*;

public class Window extends JFrame {
    public Window(String winTitle){
        super(winTitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
