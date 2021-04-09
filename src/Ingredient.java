public class Ingredient {
    private String measurementType;
    private double measurementAmount;
    private String name;

    public Ingredient(String measurementType, double measurementAmount, String name) {
        this.measurementType = measurementType;
        this.measurementAmount = measurementAmount;
        this.name = name;
    }

    public double getMeasurementAmount() {
        return measurementAmount;
    }

    public String getName() {
        return name;
    }

    public String getMeasurementType() {
        return measurementType;
    }
}
