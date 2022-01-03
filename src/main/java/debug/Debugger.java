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
    JTextField opcodeStringField = new JTextField("");
    JTextField opcodeNumField = new JTextField("");

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
        verticalBox.add(opcodeStringField);
        verticalBox.add(opcodeNumField);

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

        //NOTE::Setting the border of the text fields
        registersTextArea.setBorder(BorderFactory.createCompoundBorder(etchedBorder, insetsBorder));
        flagsTextArea.setBorder(BorderFactory.createCompoundBorder(etchedBorder, insetsBorder));
        registersTextField.setBorder(BorderFactory.createCompoundBorder(etchedBorder, insetsBorder));
        flagsTextField.setBorder(BorderFactory.createCompoundBorder(etchedBorder,insetsBorder));
        opcodeStringField.setBorder(BorderFactory.createCompoundBorder(etchedBorder,insetsBorder));
        opcodeNumField.setBorder(BorderFactory.createCompoundBorder(etchedBorder,insetsBorder));

        //NOTE::Setting the font of the text fields
        registersTextArea.setFont(fontTextArea);
        flagsTextArea.setFont(fontTextArea);
        registersTextField.setFont(fontTextField);
        flagsTextField.setFont(fontTextField);
        opcodeStringField.setFont(fontTextField);
        opcodeNumField.setFont(fontTextField);

        //NOTE::Making the text fields non-editable
        registersTextArea.setEditable(false);
        flagsTextArea.setEditable(false);
        registersTextField.setEditable(false);
        flagsTextField.setEditable(false);
        opcodeStringField.setEditable(false);
        opcodeNumField.setEditable(false);

        //NOTE::Disabling highlighting on text fields
        registersTextArea.setHighlighter(null);
        flagsTextArea.setHighlighter(null);
        registersTextField.setHighlighter(null);
        flagsTextField.setHighlighter(null);
        opcodeStringField.setHighlighter(null);
        opcodeNumField.setHighlighter(null);

        //NOTE::Have to set background explicitly when disabling editable on text fields
        registersTextArea.setBackground(Color.WHITE);
        flagsTextArea.setBackground(Color.WHITE);
        registersTextField.setBackground(Color.WHITE);
        flagsTextField.setBackground(Color.WHITE);
        opcodeStringField.setBackground(Color.WHITE);
        opcodeNumField.setBackground(Color.WHITE);

        //NOTE::Setting initial state of text in text areas
        registersTextArea.setText(cpu.registersToString());
        flagsTextArea.setText(cpu.flagsToString());
        opcodeStringField.setText(cpu.opcodeToString((short) 0));
        opcodeNumField.setText(String.format("Hex: %x | Dec: %d", 0, 0));
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

    public void update(short opcode){
        //NOTE::Checking if we are stepping through instructions
        if(stepThroughInstructions){
            while(!stepThroughNextInstruction)
                Thread.onSpinWait();
            stepThroughNextInstruction = false;
        }

        registersTextArea.setText(cpu.registersToString());
        flagsTextArea.setText(cpu.flagsToString());
        opcodeStringField.setText(cpu.opcodeToString(opcode));

        opcodeNumField.setText(String.format("Hex: %x | Dec: %d", opcode, opcode));
    }
}
