import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class IngredientTest {
    private Ingredient ingredient;

    @BeforeEach
    public void init() {
        ingredient = new Ingredient("Ounce(s)", 1, "Sugar");
    }

    @Test
    public void toStringProducesProperString() {
        assertEquals(ingredient.toString(), "1 Ounce(s): Sugar");
    }
}
