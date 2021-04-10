import java.util.ArrayList;

public class Recipe {
    private String name;
    private final ArrayList<Ingredient> ingredients;
    private final ArrayList<String> instructions;

    public Recipe(String name) {
        this.name = name;
        ingredients = new ArrayList<>();
        this.instructions = new ArrayList<>();
    }

    public ArrayList<String> getInstructions() {
        return instructions;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public void addInstruction(String instruction) {
        instructions.add(instruction);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
