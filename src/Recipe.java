import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Recipe {
    private String name;
    private final ArrayList<Ingredient> ingredients;
    private final ArrayList<String> instructions;
    private Image image;
    private Image textImage;
    private boolean selected;

    public Recipe(String name) {
        this.name = name;
        ingredients = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.image = null;
        this.textImage = null;
        this.selected = false;
    }

    public ArrayList<String> getInstructions() {
        return instructions;
    }

    public void addInstruction(String instruction) {
        instructions.add(instruction);
    }

    public ArrayList<Ingredient> getIngredients() { return ingredients; }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {this.name = name;}

    public Image getTextImage() {return textImage;}

    public void setTextImage(Image textImage) {this.textImage = textImage; }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public boolean isSelected() {return selected;}

    public void setSelected(boolean value) {this.selected = value;}

    public static Recipe readFile(File recipeFile) throws IOException {
        Scanner scanner = new Scanner(recipeFile);
        Recipe recipe = new Recipe(scanner.nextLine());
        scanner.nextLine(); scanner.nextLine();
        String currentLine = scanner.nextLine();
        while (!currentLine.equals("")) {
            String[] ingredientComponents = currentLine.split(" ");
            recipe.addIngredient(new Ingredient(ingredientComponents[1].replace(":", ""),
                    Double.parseDouble(ingredientComponents[0]),
                    ingredientComponents[2]));
            currentLine = scanner.nextLine();
        }

        scanner.nextLine();
        while (scanner.hasNext()) {
            recipe.addInstruction(scanner.nextLine());
        }

        scanner.close();
        return recipe;
    }

    public void writeFile(File recipeFile) throws IOException {
        if (recipeFile.createNewFile()) {
            FileWriter writer = new FileWriter(recipeFile);
            writer.write(name + "\n");
            writer.write("\nIngredients\n");
            for (Ingredient ingredient : ingredients) {
                writer.write(ingredient.toString() + "\n");
            }

            writer.write("\nInstructions\n");
            int i = 1;
            for (String instruction : instructions) {
                writer.write("Step " + i + ": " + instruction + "\n");
                i++;
            }

            writer.close();
        } else {
            throw new IOException(); //file already exists
        }
    }
}
