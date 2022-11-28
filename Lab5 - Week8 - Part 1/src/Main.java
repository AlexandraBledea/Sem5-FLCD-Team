import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.stream.StreamSupport;

public class Main {
    private static void printToFile(String filePath, Object object) {
        try (PrintStream printStream = new PrintStream(filePath)) {
            printStream.println(object);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void runGrammar(){
        Grammar grammar = new Grammar("Input_Output/G2.txt");
        System.out.println("Non terminals - " + grammar.getNonTerminals());
        System.out.println("Terminals - " + grammar.getTerminals());
        System.out.println("Starting symbol - " + grammar.getStartingSymbol());
        System.out.println("Productions - ");
        grammar.getProductions().forEach((lhs, rhs) -> System.out.println(lhs + " -> " + rhs ));
        System.out.println("Is it a context free grammar (CFG) ? " +  grammar.isCFG());
    }


    public static void main(String[] args) {
        System.out.println("1. Grammar");
        System.out.println("Your option: ");

        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();

        switch (option) {
            case 1:
                runGrammar();
                break;

            default:
                System.out.println("Invalid command!");
                break;

        }

    }
}
