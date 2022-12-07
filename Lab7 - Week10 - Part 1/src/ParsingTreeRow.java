public class ParsingTreeRow {

    private final Integer index;
    private final String info;
    private final Integer parent;
    private final Integer rightSibling;

    public ParsingTreeRow(Integer index, String info, Integer parent , Integer rightSibling) {
        this.index = index;
        this.info = info;
        this.parent = parent;
        this.rightSibling = rightSibling;
    }

    public Integer getIndex() {
        return index;
    }

    public String getInfo() {
        return info;
    }

    public Integer getParent() {
        return parent;
    }

    public Integer getRightSibling() {
        return rightSibling;
    }
}
