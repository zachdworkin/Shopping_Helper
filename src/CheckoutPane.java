import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CheckoutPane extends JPanel {
    private final ArrayList<Recipe> recipes;
    private final JPanel ingredientsPanel;

    public CheckoutPane(ArrayList<Recipe> recipes) {
        this.setLayout(new FlowLayout());
        this.ingredientsPanel = new JPanel();
        this.recipes = recipes;
        initAll();
    }

    private void initAll() {
        initIngredients();
    }

    private void initIngredients() {
        ingredientsPanel.setPreferredSize(new Dimension(250, 250));
        for (Recipe recipes : recipes) {
            for (Ingredient ingredient : recipes.getIngredients()) {
                ingredientsPanel.add(new JLabel(ingredient.toString()));
            }
        }

        add(ingredientsPanel);
        render();
    }

    private void render() {
        revalidate();
        repaint();
    }
}
