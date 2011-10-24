package utils.display;

import java.util.Scanner;

public class WPCLatexFormatter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		for (int i = 0; i < 64; i++) {
			if ((i + 1) % 8 == 0) {
				System.out.println(s.next() + " \\tabularnewline"); 
			} else {
				System.out.print(s.next() + " & ");
			}
		}	
	}
}
