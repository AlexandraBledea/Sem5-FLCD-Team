import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.stream.StreamSupport;

public class Main {
    private static void printToFile(String filePath, Object object) {
        try (PrintStream printStream = new PrintStream(filePath)) {
            printStream.println(object);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void printMenu(){
        System.out.println("\n\n0. Exit");
        System.out.println("1. Print non-terminals");
        System.out.println("2. Print terminals");
        System.out.println("3. Print starting symbol");
        System.out.println("4. Print all productions");
        System.out.println("5. Print all productions for a non terminal");
        System.out.println("6. Is the grammar a context free grammar (CFG) ?");
        System.out.println("7. Run LR ");
        System.out.println("8. Run tests");

        System.out.println("9. Run another LR");
    }

    public static void runGrammar() throws Exception {
        Grammar grammar = new Grammar("Input_Output/G1.txt");
        boolean notStopped = true;
        while(notStopped) {
            printMenu();
            Scanner keyboard = new Scanner(System.in);
            System.out.println("Enter your option");
            int option = keyboard.nextInt();
            switch (option) {
                case 0:
                    notStopped = false;
                    break;
                case 1:
                    System.out.println("\n\nNon-terminals -> " + grammar.getNonTerminals());
                    break;
                case 2:
                    System.out.println("\n\nTerminals -> " + grammar.getTerminals());
                    break;
                case 3:
                    System.out.println("\n\nStarting symbol -> " + grammar.getStartingSymbol());
                    break;
                case 4:
                    System.out.println("\n\nAll productions: ");
                    grammar.getProductions().forEach((lhs, rhs)-> System.out.println(lhs + " -> " + rhs));
                    break;
                case 5:
                    Scanner sc= new Scanner(System.in); //System.in is a standard input stream.
                    System.out.print("Enter a non-terminal: ");
                    String nonTerminal= sc.nextLine(); //reads string.
                    System.out.println("\n\n Productions for the non-terminal: " + nonTerminal);
                    List<String> key = new ArrayList<>();
                    key.add(nonTerminal);
                    try {
                        grammar.getProductions().get(key).forEach((rhs) -> System.out.println(key + " -> " + rhs));
                    } catch (NullPointerException e) {
                        System.out.println("This is not a defined non-terminal");
                    }
                    break;
                case 6:
                    System.out.println("\n\nIs it a context free grammar (CFG) ? " + grammar.isCFG());
                    break;
                case 7:
                    Grammar grammar1 = new Grammar("Input_Output/G2.txt");
                    LR lrAlg = new LR(grammar1);

                    CanonicalCollection canonicalCollection = lrAlg.canonicalCollection();

                    System.out.println("States");
                    writeToFile("Input_Output/ParseOutput.txt", "States");

                    for(int i = 0; i < canonicalCollection.getStates().size(); i++){
                        System.out.println(i + " " + canonicalCollection.getStates().get(i));
                    }

                    System.out.println("\nState transitions");
                    writeToFile("Input_Output/ParseOutput.txt", "\nState transitions");

                    for(Map.Entry<Pair<Integer, String>, Integer> entry: canonicalCollection.getAdjacencyList().entrySet()){
                        System.out.println(entry.getKey() + " -> " + entry.getValue());
                        writeToFile("Input_Output/ParseOutput.txt", entry.getKey() + " -> " + entry.getValue());
                    }

                    System.out.println();

                    ParsingTable parsingTable = lrAlg.getParsingTable(canonicalCollection);
                    if(parsingTable.entries.size() == 0){
                        System.out.println("We have conflicts in the parsing table so we can't go further with the algorithm");
                        writeToFile("Input_Output/ParseOutput.txt", "We have conflicts in the parsing table so we can't go further with the algorithm");
                    }
                    else {
                        System.out.println(parsingTable.toString());
                        writeToFile("Input_Output/ParseOutput.txt", parsingTable.toString());
                    }

                    Stack<String> word;

                    word = readFirstElemFromFile("Input_Output/WordInput.txt");
                    System.out.println("Printing words " + word);
                    lrAlg.parse(word, parsingTable);

                    break;
                case 8:
                    Tests test = new Tests();
                    test.runAllClosureTest();
                    test.runAllGoToTests();
                    test.runAllCanonicalTests();
                    break;

            }
        }

        launchApp();
    }


    public static void launchApp() throws Exception {
        System.out.println("0. Exit");
        System.out.println("1. Grammar");
        System.out.println("Your option: ");

        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();

        switch (option) {
            case 1:
                runGrammar();
                break;

            case 0:
                break;

            default:
                System.out.println("Invalid command!");
                break;

        }
    }

    public static Stack<String> readFromFile(String filename) {
        BufferedReader reader;
        Stack<String> wordStack = new Stack<String>();
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            while(line != null) {
                wordStack.add(line);
                line = reader.readLine();
            }
            return wordStack;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stack<String> readFirstElemFromFile(String filename) {
        BufferedReader reader;
        Stack<String> wordStack = new Stack<String>();
        ArrayList<String> normal = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            while(line != null) {
                String[] split = line.split("\\s+");
                normal.add(split[0]);
                line = reader.readLine();
            }
            for(int i = normal.size() -1; i>=0 ; i--) {
                wordStack.add(normal.get(i));
            }
            return wordStack;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeToFile(String file, String line) throws IOException {
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(line);
        bw.newLine();
        bw.close();
    }

    public static void main(String[] args) throws Exception {
        launchApp();
    }
}
