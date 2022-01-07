package debug;

import core.cpu.Intel8080;
import util.Window;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Debugger{
    Insets textFieldInsets = new Insets(1, 2, 1, 2);
    Font fontTextArea = new Font(Font.SERIF, Font.PLAIN, 14);
    Font fontTextField = new Font(Font.SERIF, Font.BOLD, 14);

    Intel8080 cpu;
    Window window;

    //NOTE::JSwing widgets
    JPanel panel = new JPanel();

    //NOTE::Boxes for panel layout
    Box horizontalBox = Box.createHorizontalBox();
    Box verticalBox = Box.createVerticalBox();

    //NOTE::Titles for the text areas
    JTextField registersTextField = new JTextField("Registers");
    JTextField flagsTextField = new JTextField("Flags");
    JTextField opcodeStringTextField = new JTextField("");
    JTextField opcodeNumField = new JTextField("");
    JTextField cyclesTextField = new JTextField("Cycles: ");

    //NOTE::Actual text areas
    JTextArea registersTextArea = new JTextArea("");
    JTextArea flagsTextArea = new JTextArea("");

    //NOTE::Buttons for the
    JRadioButton runInstructions = new JRadioButton("Run Instructions");
    JRadioButton stepInstructions = new JRadioButton("Step Instructions");
    //NOTE::Group of buttons controlling whether we run normally or are stepping over each instruction
    ButtonGroup runModeButtons = new ButtonGroup();
    JButton nextInstruction = new JButton("Next Instruction");

    //NOTE::Boolean to know whether to step instructions or what
    boolean stepThroughInstructions = false;
    //NOTE::Boolean to know where to step to next instruction
    volatile boolean stepThroughNextInstruction = false;

    //NOTE::Sets the defualt runMode
    public enum RunMode{
        RUN_INSTRUCTIONS, STEP_INSTRUCTIONS
    }

    public Debugger(Intel8080 cpu, RunMode runMode){
        this.cpu = cpu;
        window = new Window("Debugger");

        //NOTE::Setting the properties of the widgets
        setWindowProperties();
        setPanelProperties();
        setTextProperties();
        setButtonProperties();

        //NOTE::Setting the initial runMode
        setRunMode(runMode);

        //NOTE::Setting the button callback methods
        setButtonCallbacks();

        //NOTE::Adding all of the widgets
        addWidgets();

        //NOTE::Finalizing the window and making it visible
        window.pack();
        window.setVisible(true);
    }

    //NOTE::Setting initial run mode button states
    private void setRunMode(RunMode runMode){
        if(runMode == RunMode.RUN_INSTRUCTIONS){
            runModeButtons.setSelected(runInstructions.getModel(), true);
            stepThroughInstructions = false;
        }else{
            runModeButtons.setSelected(stepInstructions.getModel(), true);
            stepThroughInstructions = true;
            stepThroughNextInstruction = true;
        }
    }

    private void addWidgets(){
        horizontalBox.add(runInstructions);
        horizontalBox.add(stepInstructions);
        verticalBox.add(Box.createVerticalStrut(5));
        verticalBox.add(nextInstruction);
        verticalBox.add(Box.createVerticalStrut(5));
        verticalBox.add(registersTextField);
        verticalBox.add(Box.createVerticalStrut(2));
        verticalBox.add(registersTextArea);
        verticalBox.add(Box.createVerticalStrut(5));
        verticalBox.add(flagsTextField);
        verticalBox.add(Box.createVerticalStrut(2));
        verticalBox.add(flagsTextArea);
        verticalBox.add(opcodeStringTextField);
        verticalBox.add(opcodeNumField);
        verticalBox.add(cyclesTextField);

        //NOTE::Adding boxes to the panel
        panel.add(horizontalBox);
        panel.add(verticalBox);

        window.add(panel);
    }

    private void setWindowProperties(){
        window.setResizable(false);
    }

    private void setPanelProperties(){
        //NOTE::Setting the layout of the JPanel
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    private void setButtonProperties(){
        //NOTE::Group of buttons controlling whether we run normally or are stepping over each instruction
        runModeButtons.add(runInstructions);
        runModeButtons.add(stepInstructions);

        //NOTE::Setting properties of the buttons
        runInstructions.setFocusPainted(false);
        stepInstructions.setFocusPainted(false);
        nextInstruction.setFocusPainted(false);

        nextInstruction.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void setTextProperties(){
        //NOTE::Setting properties of the textArea and textFields
        Border etchedBorder = BorderFactory.createEtchedBorder();
        Border insetsBorder = BorderFactory.createEmptyBorder(
                textFieldInsets.top,
                textFieldInsets.left,
                textFieldInsets.bottom,
                textFieldInsets.right
        );

        setTextAreaDefaultProperties(registersTextArea, etchedBorder, insetsBorder, cpu.registersToString());
        setTextAreaDefaultProperties(flagsTextArea, etchedBorder, insetsBorder, cpu.flagsToString());

        setTextFieldDefaultProperties(registersTextField, etchedBorder, insetsBorder, "Registers:");
        setTextFieldDefaultProperties(flagsTextField, etchedBorder, insetsBorder, "Flags:");
        setTextFieldDefaultProperties(opcodeStringTextField, etchedBorder, insetsBorder, "");
        setTextFieldDefaultProperties(opcodeNumField, etchedBorder, insetsBorder, "");
        setTextFieldDefaultProperties(cyclesTextField, etchedBorder, insetsBorder, "");
    }

    private void setTextFieldDefaultProperties(JTextField textField, Border etchedBorder, Border insetsBorder, String defaultText){
        textField.setBorder(BorderFactory.createCompoundBorder(etchedBorder, insetsBorder));
        textField.setFont(fontTextField);
        textField.setEditable(false);
        textField.setHighlighter(null);
        textField.setBackground(Color.WHITE);
        textField.setText(defaultText);
    }

    private void setTextAreaDefaultProperties(JTextArea textField, Border etchedBorder, Border insetsBorder, String defaultText){
        textField.setBorder(BorderFactory.createCompoundBorder(etchedBorder, insetsBorder));
        textField.setFont(fontTextArea);
        textField.setEditable(false);
        textField.setHighlighter(null);
        textField.setBackground(Color.WHITE);
        textField.setText(defaultText);
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

            if(stepThroughInstructions)
                stepThroughNextInstruction = true;
        });
    }

    public void update(int opcode){
        //NOTE::Checking if we are stepping through instructions
        if(stepThroughInstructions){
            while(!stepThroughNextInstruction)
                Thread.onSpinWait();
            stepThroughNextInstruction = false;
        }

        registersTextArea.setText(cpu.registersToString());
        flagsTextArea.setText(cpu.flagsToString());
        opcodeStringTextField.setText(cpu.opcodeToString(opcode));
        cyclesTextField.setText("Cycles: " + cpu.getCycles());

        opcodeNumField.setText(String.format("Hex: %x | Dec: %d", opcode, opcode));
    }
}
