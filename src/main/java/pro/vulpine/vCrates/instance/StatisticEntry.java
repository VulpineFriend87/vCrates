package pro.vulpine.vCrates.instance;

public class StatisticEntry {

    private final String type;
    private final String identifier;
    private int value;

    public StatisticEntry(String type, String identifier, int value) {
        this.type = type;
        this.identifier = identifier;
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getValue() {
        return value;
    }
}
