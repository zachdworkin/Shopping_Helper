import javax.swing.*;
import java.util.ArrayList;

public class CheckoutPane extends JPanel {
    private final ArrayList<Recipe> recipes;
    public CheckoutPane(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        initAll();
    }

    private void initAll() {
        initIngredients();
    }

    private void initIngredients() {
        for (Recipe recipes : recipes) {
            for (Ingredient ing : recipes.getIngredients()) {
                add(new JLabel(ing.toString()));
            }
        }
    }
}
