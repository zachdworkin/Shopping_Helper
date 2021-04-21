import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class IngredientTest {
    private Ingredient ingredient;
    private Ingredient multiWordIngredient;

    @BeforeEach
    public void init() {
        ingredient = new Ingredient("Ounce(s)", 1, "Sugar");
        ingredient = new Ingredient("Cup(s)", 100, "Frank's Red Hot Spice");
    }

    @Test
    public void toStringProducesProperString() {
        assertEquals(ingredient.toString(), "1.0 Ounce(s): Sugar");
    }

    @Test
    public void toStringProducesProperStringWithMultipleWordIngredients() {
        assertEquals(ingredient.toString(), "100.0 Cup(s): Frank's Red Hot Spice");
    }
}
