import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Table {

    public Map<Integer, Row> tableRow;

    Table(){
        tableRow = new HashMap<>();
    }

    @Override
    public String toString(){
        final String [] result = new String[1];

        tableRow.forEach((rowIndex, row) -> {
            result[0] += new AtomicReference<String>(rowIndex + ": " + row + "\n");
        });

        return result[0];
    }
}
