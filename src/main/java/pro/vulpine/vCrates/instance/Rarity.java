package pro.vulpine.vCrates.instance;

public class Rarity {

    private final String identifier;
    private final String name;
    private final int weight;

    public Rarity(String identifier, String name, int weight) {
        this.identifier = identifier;
        this.name = name;
        this.weight = weight;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }
}
