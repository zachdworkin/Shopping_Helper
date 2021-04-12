import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ImageComponentTest {
    private Recipe recipe;
    private final Path RESOURCES_PATH = Path.of((new File(System.getProperty("user.dir"))).getAbsolutePath()).resolve("resources");

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
            recipe.writeFile(RESOURCES_PATH.resolve("recipes").resolve("test.recipe").toFile());
        } catch (IOException ioe) {
            System.out.println("Could not create File\n");
        }
    }

    @AfterEach
//    public void cleanup() {
//        if(!RESOURCES_PATH.resolve("recipes").resolve("test.recipe").toAbsolutePath().toFile().delete())
//            System.out.println("Recipe File could not be deleted");
//
//        if (!RESOURCES_PATH.resolve("recipeCardImages").resolve("test.png").toAbsolutePath().toFile().delete())
//            System.out.println("PNG File could not be deleted");
//    }

    @Test
    void createImageFromTextCreatedImage() {
        ImageComponent ic;
        try {
            ic = new ImageComponent();
            ic.createImageFromText(RESOURCES_PATH.resolve("recipes").resolve("test.recipe"));
        } catch (IOException ioe) {
            System.out.println("Image Source File not Found.");
        }

        assertTrue(RESOURCES_PATH.resolve("recipeCardImages").resolve("test.png").toFile().isFile());
    }
}
