import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Table {

    public Map<Integer, Row> tableRow;

    Table(){
        tableRow = new LinkedHashMap<>();
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();

        for(Integer key: tableRow.keySet()){
            stringBuilder.append(key);
            stringBuilder.append(": ");
            stringBuilder.append(tableRow.get(key));
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
