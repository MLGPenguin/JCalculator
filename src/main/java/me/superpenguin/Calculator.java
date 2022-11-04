package me.superpenguin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.text.html.HTML;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Calculator {


    private final String[] operatorOrder = new String[] {"+", "-", "*", "/"};
    private final ScriptEngine script = new ScriptEngineManager().getEngineByName("JavaScript");
    private final int SCREEN_WIDTH = 500;
    private final int SCREEN_HEIGHT = 700;
    private final Font BUTTON_FONT = new Font(null, -1, 30);

    private JLabel screen;
    private String exp = "";


    public void run() {
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

        GridLayout grid = new GridLayout(4, 4);
        JPanel keypad = new JPanel();
        keypad.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT-200));
        keypad.setLayout(grid);

        getKeypad().forEach(e -> keypad.add(e));
        keypad.add(getInputButton("0"));
        keypad.add(getInputButton("."));
        JButton equals = new JButton("=");
        equals.addActionListener(event -> evaluate());
        equals.setFont(BUTTON_FONT);
        keypad.add(equals);
        keypad.add(getInputButton(operatorOrder[3]));

        screen = new JLabel("0 ");
        screen.setOpaque(true);
        screen.setBackground(Color.gray);
        screen.setPreferredSize(new Dimension(SCREEN_WIDTH, 150));
        screen.setVerticalAlignment(JLabel.CENTER);
        screen.setHorizontalAlignment(JLabel.RIGHT);
        screen.setFont(new Font(null, -1, 32));

        frame.getContentPane().add(BorderLayout.NORTH, screen);
        frame.getContentPane().add(BorderLayout.SOUTH, keypad);
        frame.getContentPane().setBackground(Color.gray);
        frame.setVisible(true);
    }

    public void addToExpression(String expression){
        this.exp = this.exp + expression;
        screen.setText(this.exp + " ");
    }

    /**
     * @return The buttons 1-9, and the first three operator buttons in the correct order. (not including 0)
     */
    private List<JButton> getKeypad(){
        List<JButton> buttons = new ArrayList<>();
        int op = 0;
        for (int i = 3 ; i > 0 ; i--){
            for (int j = 3*(i-1)+1 ; j <= 3*i; j++) buttons.add(getInputButton(String.valueOf(j)));
            buttons.add(getInputButton(operatorOrder[op++]));
        }
        return buttons;
    }

    private void evaluate(){
        sanitise();
        exp = String.valueOf(eval(exp));
        // Remove unnecessary decimal places.
        if (isInt(exp)) exp = String.valueOf(getInt(exp));
        screen.setText(exp);
    }

    private void sanitise(){
        // Replace double negative with a positive and then remove duplicate operators (++//**) so that the evaluation doesn't throw errors.
        exp = exp.replaceAll("--", "+").replaceAll("[+*/-]+(?=[+*/-])", "");
    }

    private boolean isInt(String s){ return Double.parseDouble(s) % 1 == 0; }
    private int getInt(String s) { return Double.valueOf(s).intValue(); }

    /**
     * Evaluates an expression.
     * @param exp The expression to evaluate
     * @return
     */
    private double eval(String exp){
        try {
            Object result = script.eval(exp);
            // this is required as it returns boxed types.
            if (result instanceof Double) return (double) result;
            else if (result instanceof Integer) return (int) result;
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private JButton getInputButton(String text){
        JButton button = new JButton(text);
        button.addActionListener(event -> addToExpression(text));
        button.setFont(BUTTON_FONT);
        return button;
    }



}