package debug;

import core.cpu.Intel8080;
import util.Window;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Debugger{
    Intel8080 cpu;
    Window window;

    //NOTE::JSwing widgets
    JPanel panel = new JPanel();

    //NOTE::Boxes for panel layout
    Box horizontalBox = Box.createHorizontalBox();
    Box verticalBox = Box.createVerticalBox();

    JTextField registersTextField = new JTextField("Registers");
    JTextField flagsTextField = new JTextField("Flags");

    JTextArea registersTextArea = new JTextArea("");
    JTextArea flagsTextArea = new JTextArea("");

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

        window = new Window("Debugger");

        setWindowProperties();
        setPanelProperties();
        setTextProperties();
        setButtonProperties();
        setButtonCallbacks();
        addWidgets();

        //NOTE::Finalizing the window and making it visible
        window.pack();
        window.setVisible(true);
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
        runMode.add(runInstructions);
        runMode.add(stepInstructions);

        //NOTE::Setting initial run mode button states
        runMode.setSelected(stepInstructions.getModel(), true);

        //NOTE::Setting properties of the buttons
        runInstructions.setFocusPainted(false);
        stepInstructions.setFocusPainted(false);
        nextInstruction.setFocusPainted(false);

        nextInstruction.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void setTextProperties(){
        //NOTE::Setting properties of the textArea and textFields
        Font fontArea = new Font(Font.SERIF, Font.PLAIN, 14);
        Font fontField = new Font(Font.SERIF, Font.BOLD, 14);
        Border border = BorderFactory.createEtchedBorder();

        //NOTE::Setting the border of the text fields
        registersTextArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(1, 2, 1, 2)));
        flagsTextArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(1, 2, 1, 2)));
        registersTextField.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(1, 2, 1, 2)));
        flagsTextField.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(1, 2, 1, 2)));

        //NOTE::Setting the font of the text fields
        registersTextArea.setFont(fontArea);
        flagsTextArea.setFont(fontArea);
        registersTextField.setFont(fontField);
        flagsTextField.setFont(fontField);

        //NOTE::Making the text fields non-editable
        registersTextArea.setEditable(false);
        flagsTextArea.setEditable(false);
        registersTextField.setEditable(false);
        flagsTextField.setEditable(false);

        //NOTE::Disabling highlighting on text fields
        registersTextArea.setHighlighter(null);
        flagsTextArea.setHighlighter(null);
        registersTextField.setHighlighter(null);
        flagsTextField.setHighlighter(null);

        //NOTE::Have to set background explicitly when disabling editable on text fields
        registersTextField.setBackground(Color.WHITE);
        flagsTextField.setBackground(Color.WHITE);

        //NOTE::Setting initial state of text in text areas
        registersTextArea.setText(cpu.registersToString());
        flagsTextArea.setText(cpu.flagsToString());
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
            while(!stepThroughNextInstruction)
                Thread.onSpinWait();
            stepThroughNextInstruction = false;
        }

        registersTextArea.setText(cpu.registersToString());
        flagsTextArea.setText(cpu.flagsToString());
    }
}
