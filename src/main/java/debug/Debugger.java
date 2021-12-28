package debug;

import cpu.Intel8080;
import cpu.Intel8080Base;
import util.Window;

import javax.swing.*;

public class Debugger {
    Intel8080 cpu;
    Window window;

    //NOTE::Creating text boxes
    JTextArea textField = new JTextArea("");

    public Debugger(Intel8080 cpu){
        this.cpu = cpu;

        window = new Window("Debugger", 270, 270);

        window.add(textField);

        window.pack();
        window.setVisible(true);
    }

    public void update(){
        textField.setText(cpu.toString());
        window.pack();
    }
}
