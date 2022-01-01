package debug;

import cpu.Intel8080;
import util.Window;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Debugger{
    Intel8080 cpu;
    Window window;

    //NOTE::JSwing widgets
    JPanel panel = new JPanel();
    JTextArea textField = new JTextArea("");

    //NOTE::Buttons for the debugger
    JToggleButton stepInstructions = new JToggleButton("Step Instructions");
    JButton nextInstruction = new JButton("Next Instruction");

    //NOTE::Boolean to know wether to step instructions or what
    boolean stepThroughInstructions = false;

    public Debugger(Intel8080 cpu){
        this.cpu = cpu;

        window = new Window("Debugger", 270, 350);

        //NOTE::Setting the layout of the JPanel
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //NOTE::Adding all the widgets to the panel
        panel.add(stepInstructions);
        panel.add(nextInstruction);
        panel.add(textField);

        //NOTE::Setting button callbacks
        setButtonCallbacks();

        textField.setText(cpu.toString());

        //NOTE::Finalizing the window and making it visible
        window.add(panel);
        window.pack();
        window.setVisible(true);
    }

    //NOTE::Method is used to set button callbacks
    private void setButtonCallbacks(){
        stepInstructions.addActionListener((ActionEvent e) -> {
            System.out.println("Step Instructions");
        });

        nextInstruction.addActionListener((ActionEvent e) -> {
            System.out.println("Next Instruction");
        });
    }

    public void update(){
        textField.setText(cpu.toString());
    }
}
