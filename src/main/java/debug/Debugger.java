package debug;

import cpu.Intel8080;
import cpu.Intel8080Base;
import util.Window;

import javax.swing.*;

public class Debugger {
    Intel8080 cpu;
    Window window;

    //NOTE::JSwing widgets
    JPanel panel = new JPanel();
    JTextArea textField = new JTextArea("");
    JButton testButton = new JButton("Test");

    public Debugger(Intel8080 cpu){
        this.cpu = cpu;

        window = new Window("Debugger", 270, 270);

        //NOTE::Setting the layout of the JPanel
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //NOTE::Adding all the widgets to the panel
        panel.add(testButton);
        panel.add(textField);
        textField.setText(cpu.toString());

        //NOTE::Finalizing the window and making it visible
        window.add(panel);
        window.pack();
        window.setVisible(true);
    }

    public void update(){
        textField.setText(cpu.toString());
    }
}
