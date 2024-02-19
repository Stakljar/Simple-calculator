package com.example.calculator;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class CalculationsExecuter {

	public static ArrayList<Character> operators = new ArrayList<Character>(Arrays.asList('+', '-', '*', '/', '%'));
	private static DecimalFormat decimalFormat = new DecimalFormat("#.####");
	
	public static void setRoundingMode(RoundingMode roundingMode) {
		
		decimalFormat.setRoundingMode(roundingMode);
	}
	
	public static ArrayList<String> orderBrackets(ArrayList<String> equation) {
		
		int index = 0;
		ArrayList<String> temp = new ArrayList<String>();
		
		if(!equation.contains("("))
				return performOperations(equation, '+');
		
		for(int i = 0; i < equation.size(); i++) {
			if(equation.get(i) == "(")
				index = i;
			else if(equation.get(i) == ")") {
				for (int j = index + 1; j < i; j++) {
					temp.add(equation.get(j));
				}
				equation.set(index, performOperations(temp, '+').get(0));
				equation.subList(index + 1, i + 1).clear();
				break;
			}
		}
		
		equation = orderBrackets(equation);
		return equation;
	}
	
	private static ArrayList<String> performOperations(ArrayList<String> equation, Character operator) {
		
		try {
			equation = performOperations(equation, operators.get(operators.indexOf(operator) + 1));
		}
		catch(IndexOutOfBoundsException e) {}
		
		for(int i = 0; i < equation.size(); i++) {
			if(equation.get(i).toCharArray()[0] == operator && i != 0) {
				switch(operator) {
					case '+': {
						equation.set(i - 1, decimalFormat.format(Double.parseDouble(equation.get(i - 1))
								+ Double.parseDouble(equation.get(i + 1))));
						equation.remove(i + 1);
						equation.remove(i);
						equation.set(i - 1, equation.get(i - 1).replace(',', '.'));
						i--;
						break;
					}
					case '-': {
						equation.set(i - 1, decimalFormat.format(Double.parseDouble(equation.get(i - 1))
								- Double.parseDouble(equation.get(i + 1))));
						equation.remove(i + 1);
						equation.remove(i);
						equation.set(i - 1, equation.get(i - 1).replace(',', '.'));
						i--;
						break;
					}
					case '*': {
						equation.set(i - 1, decimalFormat.format(Double.parseDouble(equation.get(i - 1))
								* Double.parseDouble(equation.get(i + 1))));
						equation.remove(i + 1);
						equation.remove(i);
						equation.set(i - 1, equation.get(i - 1).replace(',', '.'));
						i--;
						break;
					}
					case '/': {
						equation.set(i - 1, decimalFormat.format(Double.parseDouble(equation.get(i - 1))
								/ Double.parseDouble(equation.get(i + 1))));
						equation.remove(i + 1);
						equation.remove(i);
						equation.set(i - 1, equation.get(i - 1).replace(',', '.'));
						i--;
						break;
					}
					case '%': {
						equation.set(i - 1, decimalFormat.format(Double.parseDouble(equation.get(i - 1)) / 100));
						equation.remove(i);
						equation.set(i - 1, equation.get(i - 1).replace(',', '.'));
						i--;
						break;
					}
				}
			}
		}
			
		return equation;
	}
}
