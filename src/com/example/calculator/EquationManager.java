package com.example.calculator;

import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class EquationManager {

	public static boolean checkSymbols(ArrayList<String> input, int index, String[] symbols) {

		for (int i = 0; i < symbols.length; i++) {
			if(input.get(index) == symbols[i])
				return true;
		}

		return false;
	}

	private boolean checkForDoubleDots(String input) {

		boolean firstCheck = false;
		for (int i = 0; i < input.length(); i++)
			if(input.charAt(i) == '.' ) {
				if(firstCheck) {
					return true;
				}
				firstCheck = true;
			}

		return false;
	}

	private boolean checkForOnlyDotElement(ArrayList<String> equation) {

		for(int i = 0; i < equation.size(); i++) {
			if(equation.get(i) == ".") {
				return true;
			}
		}

		return false;
	}

	private void performPostIncorrentInputOperations(ArrayList<String> equation, JLabel equationDisplay) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				equationDisplay.setText("Incorrect input.");
				equation.clear();
			}
		});

	}

	private void performPostUndefinedNumberOperations(ArrayList<String> equation, JLabel equationDisplay) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				equationDisplay.setText("Undefined number.");
				equation.clear();
			}
		});
	}
	
	private String performOperationsForLongNumber(String input) {

		String result = input;
		if(result.length() > 15) {
			if(result.contains(".")) {
				result =  result.substring(0, result.indexOf('.'));
				if(result.length() > 15) {
						result = performOperationsForLongNumber(result);
					}	
			}		
			else {
				result = result.substring(0, 8) + "E" + Integer.toString(result.length() - 8);
			}
		}
		return result;
	}

	private void performIfFirstIsOnly(ArrayList<String> equation, JLabel equationDisplay) {

		if(equation.get(0) == ".") {
			equation.set(0, "0");
			equationDisplay.setText(String.join("", equation));
		}
		else if(equation.get(0).startsWith(".")) {
			equation.set(0, "0" + equation.get(0));
			equationDisplay.setText(String.join("", equation));
		}
		else if(equation.get(0).endsWith(".")) {
			equation.set(0, equation.get(0) + "0");
			equationDisplay.setText(String.join("", equation));
		}
	}
	
	private boolean checkIncorrectInput(ArrayList<String> equation, int index) {

		if((checkSymbols(equation, index - 1, new String[]{"+", "-", "*", "/"})
				&& checkSymbols(equation, index, new String[]{"+", "-", "*", "/", "%"})) 
				|| (equation.get(index - 1) == "%" && !checkSymbols(equation, index, new String[]{"+", "-", "*", "/", ")"}))) {
			return true;
		}
		else if((!checkSymbols(equation, index - 1, new String[]{"+", "-", "*", "/", "("}) && equation.get(index) == "(")
				|| (equation.get(index - 1) == ")" && !checkSymbols(equation, index, new String[]{"+", "-", "*", "/", ")"})))  {
			return true;
		}
		else if(((checkSymbols(equation, index, new String[]{"*", "/", ")"})) && equation.get(index - 1) == "(")
				|| (checkSymbols(equation, index - 1, new String[]{"+", "-", "*", "/"})) && equation.get(index) == ")")  {
			return true;
		} 
		return false;
	}

	private void mergeNumberAndPrefix(ArrayList<String> equation, int index) {
		
		if(index == 0) {
			if(equation.get(index) == "+") {
				equation.set(index, equation.get(1));
				equation.remove(index + 1);
			}
			else if(equation.get(index) == "-") {
				equation.set(index, "-" + equation.get(index + 1));
				equation.remove(index + 1);
			}
		}
		else {
			if(equation.get(index - 1) == "(" && equation.get(index) == "+") {
				equation.set(index, equation.get(index + 1));
				equation.remove(index + 1);
			}
			if(equation.get(index - 1) == "(" && equation.get(index) == "-") {
				equation.set(index, "-" + equation.get(index + 1));
				equation.remove(index + 1);
			}	
		}
	}

	private boolean checkBrackets(ArrayList<String> equation) {

		int bracketChecker = 0;
		for(int i = 0; i < equation.size(); i++) {
			if(equation.get(i) == "(") {
				bracketChecker++;
			}
			else if(equation.get(i) == ")")
				bracketChecker--;

			if(bracketChecker < 0) {
				return false;
			}
		}

		if(bracketChecker != 0) {
			return false;
		}

		return true;
	}

	public void calculate(ArrayList<String> equation, JLabel equationDisplay) {

		if(equation.size() == 1) {
			performIfFirstIsOnly(equation, equationDisplay);
			equation.set(0, performOperationsForLongNumber(equation.get(0)));
			equationDisplay.setText(String.join("", equation));
		}

		if(!equation.isEmpty()) {
			if(checkSymbols(equation, equation.size() - 1, new String[]{"+", "-", "*", "/"}) 
					|| checkSymbols(equation, 0, new String[]{"%", "*", "/"})) {
				performPostIncorrentInputOperations(equation, equationDisplay);
				return;
			}
			else if(checkForOnlyDotElement(equation)) {
				performPostIncorrentInputOperations(equation, equationDisplay);
				return;
			}
			for(int i = 0; i < equation.size(); i++) {
				if(i > 0) {
					if(checkIncorrectInput(equation, i)) {
						performPostIncorrentInputOperations(equation, equationDisplay);
						return;
					}
					mergeNumberAndPrefix(equation, i);
				}

				if(checkForDoubleDots(equation.get(i))) {
					performPostIncorrentInputOperations(equation, equationDisplay);
					return;
				}
			}

			if(!checkBrackets(equation)) {
				performPostIncorrentInputOperations(equation, equationDisplay);
				return;
			}

			try {
				mergeNumberAndPrefix(equation, 0);
				StringBuilder result = new StringBuilder(CalculationsExecuter.orderBrackets(equation).get(0));
				result.replace(0, result.length(), performOperationsForLongNumber(result.toString()));
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						equationDisplay.setText(result.toString());
						equation.clear();
						equation.add(result.toString());
					}
				});
			}
			catch(NumberFormatException e) {
				performPostUndefinedNumberOperations(equation, equationDisplay);
			}
		}
	}
}
