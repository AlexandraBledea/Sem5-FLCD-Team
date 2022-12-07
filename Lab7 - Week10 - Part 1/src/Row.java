import java.util.Map;

public class Row {

    Row(StateActionType action, Map<String, Integer> goTo, Integer reductionIndex){
        this.action = action;
        this.goTo = goTo;
        this.reductionIndex = reductionIndex;
    }

    public StateActionType action;

    public Map<String, Integer> goTo;

    public Integer reductionIndex;

    public Map<String, Integer> getGoTo(){
        return this.goTo;
    }

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
