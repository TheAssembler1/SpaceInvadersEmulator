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

    //NOTE::Buttons for the
    JRadioButton runInstructions = new JRadioButton("Run Instructions");
    JRadioButton stepInstructions = new JRadioButton("Step Instructions");
    //NOTE::Group of buttons controlling whether we run normally or are stepping over each instruction
    ButtonGroup runMode = new ButtonGroup();
    JButton nextInstruction = new JButton("Next Instruction");

    //NOTE::Boolean to know whether to step instructions or what
    boolean stepThroughInstructions = true;
    //NOTE::Boolean to know where to step to next instruction
    volatile boolean stepThroughNextInstruction = false;

    public Debugger(Intel8080 cpu){
        this.cpu = cpu;

        window = new Window("Debugger", 270, 350);

        //NOTE::Setting the layout of the JPanel
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //NOTE::Group of buttons controlling whether we run normally or are stepping over each instruction
        runMode.add(runInstructions);
        runMode.add(stepInstructions);

        //NOTE::Setting initial run mode button states
        runMode.setSelected(stepInstructions.getModel(), true);

        //NOTE::Adding all the widgets to the panel
        panel.add(runInstructions);
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
        runInstructions.addActionListener((ActionEvent e) -> {
            System.out.println("INFO::Run Instructions");

            stepThroughInstructions = false;
            stepThroughNextInstruction = true;
        });

        stepInstructions.addActionListener((ActionEvent e) -> {
            System.out.println("INFO::Step Instructions");

            stepThroughInstructions = true;
            stepThroughNextInstruction = false;
        });

        nextInstruction.addActionListener((ActionEvent e) -> {
            System.out.println("INFO::Next Instruction");

            stepThroughNextInstruction = true;
        });
    }

    public void update(){
        //NOTE::Checking if we are stepping through instructions
        if(stepThroughInstructions){
            while(!stepThroughNextInstruction) {
                //NOTE::Pausing the current thread until a variable changes value
                Thread.onSpinWait();
            }
            stepThroughNextInstruction = false;
        }

        textField.setText(cpu.toString());
    }
}
