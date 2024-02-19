package com.example.calculator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.RoundingMode;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class JCalculator extends JFrame {

	private JLabel resultDisplay = new JLabel();
	private boolean flag = false;
	private ArrayList<String> output = new ArrayList<>();
	private EquationManager equationManager = new EquationManager();
	private JPanel main = new JPanel(new GridLayout(6, 1, 0, 20));
	
	public JCalculator() {
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				JCalculator.super.setSize(500, 540);
				JCalculator.super.setVisible(true);
				JCalculator.super.setLocationRelativeTo(null);
				JCalculator.super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JCalculator.super.setTitle("Jednostavni Kalkulator");
				JCalculator.super.setMinimumSize(new Dimension(500, 500));
				CalculationsExecuter.setRoundingMode(RoundingMode.HALF_UP);
				attachComponents();
			}
		});
	}

	private void attachComponents() {
		
		resultDisplay.setHorizontalAlignment(SwingConstants.RIGHT);
		resultDisplay.setFont(new Font(null, Font.PLAIN, 50));
		
		main.setBorder(new EmptyBorder(20, 20, 20, 20));
		add(main);
		
		JPanel highest = new JPanel(new BorderLayout());
		highest.setBackground(Color.WHITE);
		highest.setBorder(new EmptyBorder(0, 10, 0, 10));
		highest.add(resultDisplay, BorderLayout.EAST);
		main.add(highest);
		
		JPanel higher = new JPanel(new GridLayout(1, 4, 20, 0));
		higher.add(addResetButton());
		higher.add(addOperationsButton("("));
		higher.add(addOperationsButton(")"));
		higher.add(addOperationsButton("/"));
		main.add(higher);
		
		JPanel aboveMiddle = new JPanel(new GridLayout(1, 4, 20, 0));
		aboveMiddle.add(addNumberButton("7"));
		aboveMiddle.add(addNumberButton("8"));
		aboveMiddle.add(addNumberButton("9"));
		aboveMiddle.add(addOperationsButton("*"));
		main.add(aboveMiddle);
		
		JPanel belowMiddle = new JPanel(new GridLayout(1, 4, 20, 0));
		belowMiddle.add(addNumberButton("4"));
		belowMiddle.add(addNumberButton("5"));
		belowMiddle.add(addNumberButton("6"));
		belowMiddle.add(addOperationsButton("+"));
		main.add(belowMiddle);
		
		JPanel lower = new JPanel(new GridLayout(1, 4, 20, 0));
		lower.add(addNumberButton("1"));
		lower.add(addNumberButton("2"));
		lower.add(addNumberButton("3"));
		lower.add(addOperationsButton("-"));
		main.add(lower);
		
		JPanel lowest = new JPanel(new GridLayout(1, 4, 20, 0));
		lowest.add(addNumberButton("0"));
		lowest.add(addNumberButton("."));
		lowest.add(addOperationsButton("%"));
		lowest.add(addEqualButton());
		main.add(lowest);
		
		SwingUtilities.updateComponentTreeUI(this);
	}

	private void configureButtons(JButton button, Color color) {
		
		button.setBackground(color);
		button.setFont(new Font(null, Font.PLAIN, 40));
	}
	
	private JButton addResetButton() {
		
		JButton reset = new JButton("C");
		configureButtons(reset, new Color(210, 190, 210));
		reset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				output.clear();
				resultDisplay.setText("");
			}
		});
		
		return reset;
	}
	
	private JButton addEqualButton() {
		
		JButton equal = new JButton("=");
		configureButtons(equal, new Color(210, 210, 210));
		equal.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				flag = true;
				new SwingWorker<Object, Object>() {

					@Override
					protected Object doInBackground() throws Exception {
						
						equationManager.calculate(output, resultDisplay);
						return null;
					}
				}.execute();
			}
		});
		
		return equal;
	}
	
	private JButton addOperationsButton(String symbol) {
		
		JButton operation = new JButton(symbol);
		configureButtons(operation, new Color(200, 200, 220));
		operation.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if((symbol == "(" || symbol == ")") && flag == true)
					output.clear();
				output.add(symbol);
				flag = false;
				resultDisplay.setText(String.join("", output));
			}
		});
			
		return operation;
	}

	private JButton addNumberButton(String symbol) {
		
		JButton number = new JButton(symbol);
		configureButtons(number, new Color(210, 220, 220));
		number.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(flag) {
					output.clear();
					flag = false;
				}
				
				try {
					if(!EquationManager.checkSymbols(output, output.size() - 1, new String[]{"+", "-", "*", "/", "%", "(", ")"})){
						StringBuilder previous = new StringBuilder(output.get(output.size()-1));
						output.remove(output.size()-1);
						output.add(previous.toString() + symbol);
					}
					else
						output.add(symbol);
				}
				catch(IndexOutOfBoundsException e1) {
					output.add(symbol);
				}
			
				resultDisplay.setText(String.join("", output));
			}
		});
		
		return number;
	}
}
