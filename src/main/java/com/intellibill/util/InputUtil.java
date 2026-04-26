package com.intellibill.util;

import java.util.Scanner;

public final class InputUtil {
    private static final Scanner SCANNER = new Scanner(System.in);

    private InputUtil() {
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid integer. Please try again.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    public static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty.");
        }
    }
}
