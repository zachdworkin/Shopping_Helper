import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;


public class RecipeTest {
    private Recipe recipe;
    private final Path RECIPE_RESOURCES_PATH = Path.of((new File(System.getProperty("user.dir"))).getAbsolutePath()).resolve("resources").resolve("recipes");

    @BeforeEach
    public void init() {
        recipe = new Recipe("test");
        recipe.addIngredient(new Ingredient("Pound(s)", 10, "pig"));
        recipe.addIngredient(new Ingredient("Ounce(s)", 10, "cow"));
        recipe.addIngredient(new Ingredient("Fillet(s)", 10, "fish"));
        recipe.addIngredient(new Ingredient("Breast(s)", 10, "chicken"));

        recipe.addInstruction("Cook pig in the ground on high for 10 hours");
        recipe.addInstruction("Cook cow on top of pig for 10 hours");
        recipe.addInstruction("Make fish into sushi");
        recipe.addInstruction("Deep fry chicken until golden brown");

        try {
            recipe.writeFile(RECIPE_RESOURCES_PATH.resolve("test.recipe").toFile());
        } catch (IOException ioe) {
            System.out.println("Could not create File\n");
        }
    }

    @AfterEach
    public void cleanup() {
        if(!RECIPE_RESOURCES_PATH.resolve("test.recipe").toAbsolutePath().toFile().delete())
            System.out.println("File could not be deleted");
    }

    @Test
    void writeFileCreatesFile() {
        assertTrue(RECIPE_RESOURCES_PATH.resolve("test.recipe").toFile().isFile());
    }

    @Test
    void readFileProperlyReadsFile() {
        Recipe newRecipe = null;
        try {
            newRecipe = Recipe.readFile(RECIPE_RESOURCES_PATH.resolve("test.recipe").toFile());
        } catch (IOException ioe) {
            System.out.println("File not found");
        }

        if (newRecipe != null) {
            assertEquals(newRecipe.getName(), recipe.getName());
            ArrayList<Ingredient> newIngredients = newRecipe.getIngredients();
            ArrayList<Ingredient> oldIngredients = recipe.getIngredients();
            for (int i = 0; i < oldIngredients.size(); i++) {
                assertEquals(oldIngredients.get(i).toString(), newIngredients.get(i).toString());
            }

            ArrayList<String> newInstructions = newRecipe.getInstructions();
            ArrayList<String> oldInstructions = recipe.getInstructions();
            for (int i = 0; i < oldInstructions.size(); i++) {
                assertEquals(oldInstructions.get(i), newInstructions.get(i));
            }
        } else {
            fail();
        }
    }

    @Test
    void myFirstCommitTest() {
        assertTrue(true);
    }
}
