import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;

import java.util.*;

public final class LR {
    private final Grammar grammar;

    public LR(Grammar grammar) {
        this.grammar = grammar;
    }

    private final String getDotPrecededNonTerminal(Item item) {
        String term = (String) CollectionsKt.getOrNull(item.getRhs(), item.getDotPosition());
        return !CollectionsKt.contains(this.grammar.getNonTerminals(), term) ? null : term;
    }

    private final State closure(Item item) {
        Set oldClosure = null;
        Set currentClosure = SetsKt.mutableSetOf(new Item[]{item});

        do {
            oldClosure = CollectionsKt.toMutableSet(currentClosure);
            Set newClosure = CollectionsKt.toMutableSet(currentClosure);


        } while (oldClosure != currentClosure);
        return new State();
    }

    private final State goTo(State state, String element) {
        Set result = new LinkedHashSet();
        Iterator it = state.getItems().iterator();

        while (it.hasNext()) {
            Item item = (Item) it.next();
            String nonTerminal = (String) CollectionsKt.getOrNull(item.getRhs(), item.getDotPosition());
            if (Intrinsics.areEqual(nonTerminal, element)) {
                Item nextItem = new Item(item.getLhs(), item.getRhs(), item.getDotPosition() + 1);
                result.addAll((Collection) this.closure(nextItem).getItems());
            }
        }

        return new State();
    }

}