import java.util.Map;

public class Row {

    public StateActionType action;

    public Map<String, Integer> goTo;

    public Integer reductionIndex;

    @Override
    public String toString(){
        if (action.equals(StateActionType.ACCEPT)) {
            return "ACCEPT";
        } else if(action.equals(StateActionType.SHIFT)){
            return "SHIFT " + goTo;
        } else if(action.equals(StateActionType.REDUCE)){
            return "REDUCE " + reductionIndex;
        }

        return "";
    }

}
