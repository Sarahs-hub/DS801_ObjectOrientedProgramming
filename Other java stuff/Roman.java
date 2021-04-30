import java.util.Scanner;

/*
*Write system with several options which converts roman letters and numbers
*/
public class Roman {
	
	/* initilize scanner*/
	private static Scanner reader; 
	
	
	
	/*
	* constant of roman letters
	*/
	
	private static final int I = 1, 
		V = 5,
		X = 10, 
		L = 50, 
		C = 100, 
		D = 500, 
		M = 1000;
	
	
	
	/*
	* initialization
	*/
	private static void init(){
		reader = new Scanner(System.in);
	}
	
	public static void main(String[]args){
	init(); 
	int choice;
		do{
			printMenu();
			System.out.println("your choice:"); 
			choice = reader.nextInt();
			
			switch(choice){
				case 1:
					System.out.println("Type Roman number:");
					// Gets var from user through reader/scanner
					reader.nextLine();
					String romanNumber = reader.nextLine().trim(); 
					//point to variable romanNumber above and saves string as int
					int result = romanToNumber(romanNumber);  
					System.out.println("Your Roman number is: " + result);
					reader.nextLine(); 
					break; 
				case 2: 
					System.out.println("Type your number: ");
					reader.nextLine();
					int normNumber = reader.nextInt();
					String result2 = numberToRoman(normNumber); 
					System.out.println("Your number in Roman is: " + result2);
					reader.nextLine();
					break; 
				case 3: 
					System.out.println("Type first number? ");
					reader.nextLine();
					System.out.println("Type second number? ");
					reader.nextLine();
					String addRoman1 = reader.nextLine().trim(); 
					String addRoman2 = reader.nextLine().trim(); 
					String result3 = addTwoRomanNumbers(addRoman1, addRoman2);  
					System.out.println("Your Roman number is: " + result3);
					reader.nextLine(); 
					break; 
				case 4:
					System.out.println("Type first number? ");
					reader.nextLine();
					System.out.println("Type second number? ");
					reader.nextLine();
					String diffRoman1 = reader.nextLine().trim(); 
					String diffRoman2 = reader.nextLine().trim(); 
					String result4 = differString(diffRoman1, diffRoman2);  
					System.out.println("Your Roman number is: " + result4);
					reader.nextLine(); 
					break; 
				case 5: 
					System.out.println("Type your roman number here: ");
					reader.nextLine();
					String checkRoman = reader.nextLine();
					boolean result5 = isRomanNumber(checkRoman); 
					System.out.println("Your number is Roman: " + result5);
					reader.nextLine();
					break;
				case 0: 
					System.out.println("Goodbye!");
					break; 
				default:
					System.out.println("invalid Option"); 
					break; 
			}
		}while(choice!=0);
	}
	
	private static void printMenu(){
		System.out.println("Please choose an option, valid choices: ");
		System.out.println("1: Convert Roman number to number");
		System.out.println("2: Convert number to Roman number");
		System.out.println("3: Add two Roman numbers");
		System.out.println("4: Find the difference between two Roman numbers");
		System.out.println("5: Check if input is a valid roman number");
		System.out.println("0: Exit menu");
	}

	/*
	* Covert roman letter to numbers 
	*/	
	private static int romanToNumber(String num){
		int sum = 0; 
		int i = 0; 
		
		while (i < num.length()){
			char c = num.charAt(i);
			switch(c){
				case 'I':
				sum = sum + I;
				break; 
				case 'V':
				sum = sum + V;
				break;
				case 'X': 
				sum = sum + X; 
				break; 
				case 'L':
				sum = sum + L;
				break; 
				case 'C': 
				sum = sum + C;
				break; 
				case 'D':
				sum = sum + D; 
				break;
				case 'M': 
				sum = sum + M; 
				break; 
				default:System.out.println("Found invaild Charater: " + c); 
				break; 
			}
		i = i + 1; 
		
		}
	 		
		return sum; 
	}	


	/*
	* string method which convert regular numbers into roman letters
	*/
	private static String numberToRoman(int n){
		String romanNumber = ""; 
		while (n > 0){
			if (n >= M){
				romanNumber = romanNumber + "M";
				n = n - M; 
			}
			else if ( n >= D){
				romanNumber = romanNumber + "D"; 
				n = n - D; 
			}
			else if ( n >= C){
				romanNumber = romanNumber + "C";
				n = n - C; 
			}
			else if ( n >= L){
				romanNumber = romanNumber + "L";
				n = n - L; 
			}
			else if ( n >= X){
				romanNumber = romanNumber + "X"; 
				n = n - X; 
			}
			else if ( n >= V){
				romanNumber = romanNumber + "V"; 
				n = n - V;
			}
			else if ( n >= I){
				romanNumber = romanNumber + "I"; 
				n = n - I; 
			}
		}
		return romanNumber;
	}


	/*
	*string method which adds two roman numbers together 
	*/
	private static String addTwoRomanNumbers(String num1, String num2){
		
		int one = romanToNumber(num1);
		int two = romanToNumber(num2);
		int result = one + two;
		return numberToRoman(result);
	}



	/*
	* method takes two arguments where 1st should be larger than last 
	*/
	private static String differString(String num1, String num2){
		int one = romanToNumber(num1);
		int two = romanToNumber(num2);
		int result = one - two; 
		return numberToRoman(result);
	}

	/*
	*method determine if string gives a valid roman number 
	*/
	private static boolean isRomanNumber(String s){
		int i = 0; 
		int v = 0; 
		int x = 0;
		int l = 0; 
		int c = 0; 
		int d = 0; 
		int m = 0; 
		
		int k = 0; 
		
		int lastValue = 1000;
		
		
		while ( k < s.length()){
			char symbol = s.charAt(k);
			switch(symbol){
				case 'I':
					if (lastValue < 1) return false;
					else lastValue = 1;
					i = i + 1;
					break; 
					
				case 'V':
					if (lastValue < 5) return false;
					else lastValue = 5;
					v = v + 1;
					break;
					
				case 'X': 
					if (lastValue < 10) return false;
					else lastValue = 10;
					x = x + 1;  
					break; 
					
				case 'L':
					if (lastValue < 50) return false;
					else lastValue = 50;
					l = l + 1; 
					break; 
					
				case 'C': 
					if (lastValue < 100) return false;
					else lastValue = 100;
					c = c + 1; 
					break; 
					
				case 'D':
					if (lastValue < 500) return false;
					else lastValue = 500;
					d = d + 1; 
					break;
					
				case 'M': 
					if (lastValue < 1000) return false;
					else lastValue = 1000;
					m = m + 1; 
					break; 
					
				default:System.out.println("Found invaild Charater: " + symbol); 
					return false; 
			}
			
			k = k + 1; 
		}
		
		if ( i > 4) return false;
		if ( v > 1) return false; 
		if ( x > 4) return false; 
		if ( l > 1) return false; 
		if ( c > 4) return false; 
		if ( d > 1) return false; 
		
		return true;
	}	

}	
