import java.lang.reflect.Array;
import java.util.*;

public class LR {

    private final Grammar grammar;

    private final Grammar workingGrammar;
    private List<Pair<String, List<String>>> orderedProductions;

    public LR(Grammar grammar) throws Exception {
        this.grammar = grammar;

        if (this.grammar.getIsEnriched()) {
            this.workingGrammar = this.grammar;
        } else {
            this.workingGrammar = this.grammar.getEnrichedGrammar();
        }

        orderedProductions = this.grammar.getOrderedProductions();
    }

    /**
     * With this method we get the non-terminal which is preceded by dot
     *
     * @param item - the item in which we look for the non-terminal
     * @return - the non-terminal if it is found or null otherwise
     */
    public String getNonTerminalPrecededByDot(Item item) {
        try {
            String term = item.getRightHandSide().get(item.getPositionForDot());
            if (!grammar.getNonTerminals().contains(term)) {
                return null;
            }

            return term;
        } catch (Exception e) {
            return null;
        }

    }


    /**
     * With this method we compute the closure for an item (an item being of the form [A->alpha.beta])
     *
     * @param item - the analysis element
     * @return - the closure for item given as input
     */
    public State closure(Item item) {

        Set<Item> oldClosure;
        Set<Item> currentClosure = Set.of(item);

        do {
            oldClosure = currentClosure;
            Set<Item> newClosure = new LinkedHashSet<>(currentClosure);
            for (Item i : currentClosure) {
                String nonTerminal = getNonTerminalPrecededByDot(i);
                if (nonTerminal != null) {
                    for (List<String> prod : grammar.getProductionsForNonTerminal(nonTerminal)) {
                        Item currentItem = new Item(nonTerminal, prod, 0);
                        newClosure.add(currentItem);
                    }
                }
            }
            currentClosure = newClosure;
        } while (!oldClosure.equals(currentClosure));

        return new State(currentClosure);
    }

    /**
     * With this method, in state S, we search LR(0) item that has dot in front of symbol X.
     * Move the dot after symbol X and call closure for this new item.
     *
     * @param state - the state S from which we want to move
     * @param elem  - the symbol after we look
     * @return - returns a State containing  a list of states
     * composed of the states for each computer closure
     */
    public State goTo(State state, String elem) {
        Set<Item> result = new LinkedHashSet<>();

        for (Item i : state.getItems()) {
            try {
                String nonTerminal = i.getRightHandSide().get(i.getPositionForDot());
                if (Objects.equals(nonTerminal, elem)) {
                    Item nextItem = new Item(i.getLeftHandSide(), i.getRightHandSide(), i.getPositionForDot() + 1);
                    State newState = closure(nextItem);
                    result.addAll(newState.getItems());
                }
            } catch (Exception ignored) {
            }
        }

        return new State(result);
    }

    /**
     * With this method we compute the canonical collection for the grammar.
     *
     * @return - the formed canonical collection
     */
    public CanonicalCollection canonicalCollection() {
        CanonicalCollection canonicalCollection = new CanonicalCollection();

        canonicalCollection.addState(
                closure(
                        new Item(
                                workingGrammar.getStartingSymbol(),
                                workingGrammar.getProductionsForNonTerminal(workingGrammar.getStartingSymbol()).get(0),
                                0
                        )
                )
        );

        int index = 0;
        while (index < canonicalCollection.getStates().size()) {
            for (String symbol : canonicalCollection.getStates().get(index).getSymbolsSucceedingTheDot()) {
                State newState = goTo(canonicalCollection.getStates().get(index), symbol);
                if (newState.getItems().size() != 0) {
                    int indexState = canonicalCollection.getStates().indexOf(newState);
                    if (indexState == -1) {
                        canonicalCollection.addState(newState);
                        indexState = canonicalCollection.getStates().size() - 1;
                    }
//                    System.out.println("(" + index + ", " + symbol + ") -> " + indexState);
                    canonicalCollection.connectStates(index, symbol, indexState);
                }
            }
            ++index;
        }
        return canonicalCollection;

    }

    /**
     * With this method we create the parsing table, if possible and detect conflicts if there are any
     *
     * @return - the parsing table corresponding to the parsed grammar if we don't have conflicts
     * - otherwise, return a table with no rows in it
     */
    public Table getParsingTable(CanonicalCollection canonicalCollection) {
        Table table = new Table();

        boolean foundConflicts = false;

        //SHIFT CASE ACTION
        for (Map.Entry<Pair<Integer, String>, Integer> entry : canonicalCollection.getAdjacencyList().entrySet()) {

            Pair<Integer, String> key = entry.getKey();
            Integer value = entry.getValue();

            // If the state is not in the table, we create an entry for it, we set the action -> SHIFT
            State state = canonicalCollection.getStates().get(key.getFirst());

            if (state.getStateActionType() == StateActionType.SHIFT) {
                if (!table.tableRow.containsKey(key.getFirst())) {
                    table.tableRow.put(key.getFirst(), new Row(state.getStateActionType(), new HashMap<>(), null));
                }

                //We update the goTo columns with the corresponding states
                table.tableRow.get(key.getFirst()).getGoTo().put(key.getSecond(), value);
            }

            if (state.getStateActionType() == StateActionType.SHIFT_REDUCE_CONFLICT || state.getStateActionType() == StateActionType.REDUCE_REDUCE_CONFLICT) {
                foundConflicts = true;
                Integer stateIndex = key.getFirst();

                for (Map.Entry<Pair<Integer, String>, Integer> e2 : canonicalCollection.getAdjacencyList().entrySet()) {
                    Pair<Integer, String> k2 = e2.getKey();
                    Integer v2 = e2.getValue();

                    if (v2.equals(stateIndex)) {
                        System.out.println("STATE INDEX -> " + stateIndex);
                        System.out.println("SYMBOL -> " + k2.getSecond());
                        System.out.println("INITIAL STATE -> " + k2.getFirst());
                        System.out.println("( " + k2.getFirst() + ", " + k2.getSecond() + " )" + " -> " + stateIndex);
                        System.out.println("STATE -> " + state);
                    }

                }

            }

        }

        // We go through each state and check if its action is REDUCE or ACCEPT
        for (int i = 0; i < canonicalCollection.getStates().size(); i++) {

            State state = canonicalCollection.getStates().get(i);

            // REDUCE CASE ACTION

            if (state.getStateActionType() == StateActionType.REDUCE) {
                // If the action is reduce, we look for the production index of the reduction
                Integer integer = orderedProductions.indexOf(new Pair<>(((Item) state.getItems().toArray()[0]).getLeftHandSide(), (((Item) state.getItems().toArray()[0]).getRightHandSide())));
                table.tableRow.put(i, new Row(state.getStateActionType(), null, integer));
            }

            // ACCEPT CASE ACTION
            if (state.getStateActionType() == StateActionType.ACCEPT) {
                table.tableRow.put(i, new Row(state.getStateActionType(), null, null));
            }

        }

        // We order the table rows based on the state index (increasing order)
        table.tableRow = new TreeMap<>(table.tableRow);

        if (foundConflicts) {
            return new Table();
        }

        return table;
    }

    public List<ParsingTreeRow> parse(Stack<String> inputStack, Table parsingTable) throws Exception {
        Stack<Pair<String, Integer>> workingStack = new Stack<>();
        Stack<Integer> outputBand = new Stack<>();

        workingStack.add(new Pair<>("$", 0));

        List<ParsingTreeRow> parsingTree = new ArrayList<>();
        Stack<Pair<String, Integer>> treeStack = new Stack<>();
        int currentIndex = 0;

        while (!inputStack.isEmpty() || !workingStack.isEmpty()) {
            Row tableRow = parsingTable.tableRow.get(workingStack.peek().getSecond());
            switch (tableRow.action) {
                case SHIFT:
                    try
                    {
                        String token = inputStack.peek();
                        Map<String, Integer> goTo = tableRow.getGoTo();

                        if(!goTo.containsKey(token)){
                            throw new Exception("Invalid symbol " + token + " for goto of state " + workingStack.peek().getSecond());
                        }

                        Integer stateIndex = goTo.get(token);

                        workingStack.add(new Pair<>(token, stateIndex));
                        inputStack.pop();

                        treeStack.add(new Pair<>(token, currentIndex++));

                    } catch (Exception e) {
                        throw new Exception("Action is shift but nothing else is left in the remaining stack");
                    }
                    break;

                case ACCEPT:
                    Pair<String, Integer> lastElement = treeStack.pop();
                    parsingTree.add(new ParsingTreeRow(
                            lastElement.getSecond(), lastElement.getFirst(), -1, -1)
                        );
                    return parsingTree;

                case REDUCE:
                    Pair<String, List<String>> productionToReduceTo = orderedProductions.get(tableRow.reductionIndex);

                    Integer parentIndex = currentIndex++;
                    Integer lastIndex = -1;

                    for(int j = 0; j < productionToReduceTo.getSecond().size(); j++){
                        workingStack.pop();
                        Pair<String, Integer> lastElementReduce = treeStack.pop();

                        parsingTree.add(
                                new ParsingTreeRow(lastElementReduce.getSecond(), lastElementReduce.getFirst(), parentIndex, lastIndex)
                        );

                        lastIndex = lastElementReduce.getSecond();

                    }

                    treeStack.add(new Pair<>(productionToReduceTo.getFirst(), parentIndex));

                    Pair<String, Integer> previous = workingStack.peek();

                    workingStack.add(
                            new Pair<>(
                                    productionToReduceTo.getFirst(),
                                    parsingTable.tableRow.get(previous.getSecond()).getGoTo().get(productionToReduceTo.getFirst())

                            )
                    );

                    outputBand.add(0, tableRow.reductionIndex);

                    break;
            }
        }
        throw new Exception("Wrong place to be...");

    }


    public Grammar getGrammar() {
        return grammar;
    }

    public Grammar getWorkingGrammar() {
        return workingGrammar;
    }
}

