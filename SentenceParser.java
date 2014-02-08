//package edu.cmu.sphinx.demo.Calculator1310;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/*
 * 		TODO
 * 		Optimizie Grammar.
 * 		work for forty, etc.
 * 		
 */

public class SentenceParser {
	
	private static String[] NUMBERS = {"zero", "one", "two", "three", "four", "five", "six", "seven",
		"eight", "nine"};
	private static String[] OPERATORS = {"plus", "minus", "divide", "times", "power"};
	private static String[] TENTHS = {"twenty", "thirty", "forty", "fifty", "sixty", "seventy",
			"eighty", "ninety"};
	
	
	double lastResult = 0;
	
	HashMap<String, Double> variableList;
	

	
	public SentenceParser()
	{
		variableList = new HashMap<String, Double>();
	}
	
	public double parse(String in)
	{
		in = normalizeString(in);
		if(in.indexOf("log") == 0)
			return logarithmicParse(in.split(" "));
		for(int i = 0; i < OPERATORS.length; i++)
		{
			if(in.contains(OPERATORS[i]))
				return arithmeticParse(in.split(" "));
		}
		return memoryParse(in.split(" "));
	}
	
	private double logarithmicParse(String[] in)
	{
		double x = 0.0;
		double result = 0.0;
		int power = 1;
		int stride = 0;
		if(in[1].equalsIgnoreCase("power"))
		{
			power = isNumber(in[2]) ? getNumber(in[2]) : (variableList.containsKey(in[2]) ? Integer.parseInt(variableList.get(in[3]) + "") : 0);
			stride = 1;
		}
		String operandString = ""; 
		for(int i = 1 + stride; i < in.length; i++)
			operandString += in[i] + " ";
				
		operandString = operandString.trim();
		operandString += " plus zero";
		x = parse(operandString); 			
		result = Math.pow(Math.log10(x), power);
		lastResult = result;
		return lastResult;	
	}
	
	private double arithmeticParse(String[] in)
	{
		String[] sentence = normalizeArray(in);
		ArrayList<String> instruction = new ArrayList<String>();
		for(int i = 0; i < sentence.length; i++)
			instruction.add(sentence[i]);
			
		int index = 0;
		// the above array will have the format {number, operator, number, operator, number}
		for(int i = OPERATORS.length - 1; i >= 0; i--)
		{
			if((index = indexOf(instruction, OPERATORS[i])) != -1)
			{
				String result = applyOperation(instruction.get(index - 1), instruction.get(index + 1), instruction.get(index));
				instruction.set(index - 1, result);
				instruction.remove(index);
				instruction.remove(index);
				i++;
			}
		}
		lastResult = Double.parseDouble(instruction.get(0));
		return lastResult;
	}
	
	private static String normalizeString(String in)
	{
		boolean normalSyntax = true;
		if(in.contains("thousand") || in.contains("hundred"))
			normalSyntax = false;
			for(int i = 0; i < TENTHS.length; i++)
				if(in.contains(TENTHS[i]))
					normalSyntax = false;
		if(normalSyntax)
			return in;
		String[] splittedIn = in.split(" ");
		String[] numbers = {"zero", "zero", "zero", "zero"};
		String number = "";
		String ret = "";

		for(int i = 0; i < splittedIn.length; i++)
		{
			if(isNumber(splittedIn[i]))
			{
				number = splittedIn[i];
			}
			
			if(splittedIn[i].equalsIgnoreCase("thousand"))
			{
				numbers[3] = number;
				number = "zero";
				continue;
			}
			
			if(splittedIn[i].equalsIgnoreCase("hundred"))
			{
				numbers[2] = number;
				number = "zero";
				continue;	
			}
			if(isOperator(splittedIn[i]) || splittedIn[i].equalsIgnoreCase("log"))
			{
				for(int j = numbers.length - 1; j > -1; j--)
				{
					ret += numbers[j] + " ";
					numbers[j] = "zero";
				}
				ret += splittedIn[i] + " ";
				continue;
			}
			if(isVariable(splittedIn[i]))
			{
				ret += splittedIn[i] + " ";
				continue;
			}
			for(int j = 0; j < TENTHS.length; j++)
			{
				if(splittedIn[i].equalsIgnoreCase(TENTHS[j]))
				{
					numbers[1] = NUMBERS[j + 2];
					continue;
				}
			}
			numbers[0] = number;
		}
		if(!isVariable(splittedIn[splittedIn.length - 1]))
			for(int i = numbers.length - 1; i > -1; i--)		
				ret += numbers[i] + " ";
		
		return ret.trim();
	}
	
	private static boolean isVariable(String x)
	{
		boolean ret = true;
		ret = !(x.equalsIgnoreCase("hundred") || x.equalsIgnoreCase("thousand"));
		ret = ret && !isOperator(x);
		ret = ret && !isNumber(x);
		ret = ret && !x.equalsIgnoreCase("log");
		for(int j = 0; j < TENTHS.length; j++)
			ret = ret && !x.equalsIgnoreCase(TENTHS[j]);
		return ret;
	}
	
	private String applyOperation(String firstOperand, String secondOperand, String operation)
	{
		double result = 0;
		char switchChar = operation.charAt(2);

		double x = Double.parseDouble(firstOperand);
		double y = Double.parseDouble(secondOperand);

		switch(switchChar)
		{
			case 'u' : result = x + y; break;
			case 'n' : result = x - y; break;
			case 'm' : result = x * (y * 1.0); break;
			case 'v' : result = x / (y * 1.0); break;
			case 'w' : result = Math.pow(x, y); break;
		}
		lastResult = result;
		return result + "";
	}
	
	private static int indexOf(ArrayList<String> in, String search)
	{
		for(int i = 0; i < in.size(); i++)
		{
			if(in.get(i).equalsIgnoreCase(search))
				return i;
		}
		return -1;
	}
	
	private String[] normalizeArray(String[] in)
	{
		ArrayList<String> arr = new ArrayList<String>();
		ArrayList<Integer> tempOperand = new ArrayList<Integer>();
		for(int i = 0; i < in.length; i++)
		{
			if(isNumber(in[i]))
			{
				tempOperand.add(getNumber(in[i]));
			}
			else
			{
				if(isOperator(in[i]))
				{
					if(!tempOperand.isEmpty())
						arr.add(toInt(tempOperand) + "");
					tempOperand.clear();
					arr.add(in[i]);
				}
				else 
					if(in[i].equalsIgnoreCase("log"))
					{
						String[] logOperation = Arrays.copyOfRange(in, i, in.length);
						arr.add(logarithmicParse(logOperation) + "");
						break;
					}
					else
						if(variableList.containsKey(in[i]))
							arr.add(variableList.get(in[i]) + "");
						else
							arr.add("0");
			}	
		}
		if(!tempOperand.isEmpty())
			arr.add(toInt(tempOperand) + "");

		String ret[] = new String[arr.size()];
		for(int i = 0; i < arr.size(); i++)
			ret[i] = arr.get(i) + "";
		return ret;
	}
	
	private static int toInt(ArrayList<Integer> tempOperand)
	{
		int x = 0;
		int unit = 1;
		for(int i = tempOperand.size() - 1; i >= 0; i--)
		{
			x += tempOperand.get(i) * unit;
			unit *= 10;
		}
		return x;
	}
	
	private double memoryParse(String[] in)
	{
		if(in[0].equalsIgnoreCase("define"))
			variableList.put(in[1], 0.0);
			
		if(in[0].equalsIgnoreCase("store"))
		{
			if(in[1].equalsIgnoreCase("last"))
			{
				variableList.put("temp", lastResult);
				return lastResult;
			}
			else
			{
				String numString = "";
				for(int i = 2; i < in.length; i++)
				{
					numString += in[i];
					numString += " ";
				}
				numString = numString.trim();
				int num = convertNumber(numString);
				variableList.put(in[1], num * 1.0);
				return num;
			}
		}
		if(in[0].equalsIgnoreCase("retrieve"))
		{
			if(in[1].equalsIgnoreCase("last"))
				return lastResult;
			else
				return variableList.get(in[1]);
		}
		return 0.0;
	}
	private static int convertNumber(String in)
	{
		ArrayList<Integer> firstOperand = new ArrayList<Integer>();
		String[] sentence = in.split(" ");
		for(int i = 0; i < sentence.length; i++)
		{
			if(isNumber(sentence[i]))
			{
				firstOperand.add(getNumber(sentence[i]));
			}
		}
		return toInt(firstOperand);
	}
	
	
	private static int getNumber(String in)
	{
		for(int i = 0; i < NUMBERS.length; i++)
		{
			if(in.equalsIgnoreCase(NUMBERS[i]))
				return i;
		}
		return 0;
	}
	
	private static boolean isOperator(String in)
	{
		for(int i = 0; i < OPERATORS.length; i++)
		{
			if(OPERATORS[i].equalsIgnoreCase(in))
				return true;
		}
		return false;
	}
	
	private static boolean isNumber (String in)
	{
		for(int i = 0; i < NUMBERS.length; i++)
			if(in.equalsIgnoreCase(NUMBERS[i]))
				return true;
		return false;
	}
	
	public static void main(String[] args)
	{	SentenceParser sp = new SentenceParser();
		System.out.println(sp.parse("two two two divide two three"));
		System.out.println(sp.lastResult);
		System.out.println(sp.parse("store x two"));
		System.out.println(sp.parse("store y two"));
		System.out.println(sp.parse("x divide two"));
		System.out.println(sp.parse("two times two plus two"));
		System.out.println(sp.variableList.get("x"));
		System.out.println(sp.parse("two two two plus zero"));
		System.out.println(sp.parse("log two three power x"));
		System.out.println(normalizeString("two thousand five hundred forty one"));
		System.out.println(normalizeString("eight divide ninety two"));
		System.out.println(sp.parse("two plus log two"));
		System.out.println(sp.parse("two plus two plus two plus two"));
		System.out.println(normalizeString("two hundred plus two"));
		System.out.println(sp.parse("log y power x plus two"));
		System.out.println((sp.parse("one thousand two hundred forty plus two")));
		System.out.println(sp.parse("two plus two hundred plus log fifty divide x"));
		System.out.println(normalizeString("log two"));
		System.out.println(isVariable("twenty"));
		System.out.println(sp.parse("two one one one one plus two plus two plus two divide two times two plus log power four two power two"));
		System.out.println(normalizeString("two one one one one one one plus one hundred"));
	}
}
