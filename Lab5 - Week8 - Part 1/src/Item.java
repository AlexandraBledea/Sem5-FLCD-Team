import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;

import java.util.List;

public final class Item {
    private final String lhs;

    private final List<String> rhs;
    private final int dotPosition;

    public String toString() {
        StringBuilder srhs = new StringBuilder();
        for (int i = 0; i < dotPosition; i++)
            srhs.append(rhs.get(i));

        srhs.append(".");

        for (int i = dotPosition; i < rhs.size(); i++)
            srhs.append(rhs.get(i));
        return this.lhs + " -> " + srhs;
    }

    public final String getLhs() {
        return this.lhs;
    }

    public final List getRhs() {
        return this.rhs;
    }

    public final int getDotPosition() {
        return this.dotPosition;
    }

    public Item(String lhs, List<String> rhs, int dotPosition) {
        Intrinsics.checkNotNullParameter(lhs, "lhs");
        Intrinsics.checkNotNullParameter(rhs, "rhs");
        this.lhs = lhs;
        this.rhs = rhs;
        this.dotPosition = dotPosition;
    }
}