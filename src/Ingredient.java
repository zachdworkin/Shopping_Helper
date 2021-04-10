public class Ingredient {
    private final String measurementType;
    private final double measurementAmount;
    private final String name;


    public Ingredient(String measurementType, double measurementAmount, String name) {
        this.measurementType = measurementType;
        this.measurementAmount = measurementAmount;
        this.name = name;
    }

    public String toString() {
        return "" + measurementAmount + " " + measurementType + ": " + name;
    }
}
