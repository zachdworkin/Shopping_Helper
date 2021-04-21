import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.concurrent.Flow;

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

    private String fillLabelSize(String item, int maxCartItemCharacters) {
        return item + " ".repeat(Math.max(0, maxCartItemCharacters - item.length()));
    }

    private ArrayList<String> getAllIngredients() {
        ArrayList<String> ingredients = new ArrayList<>();
        for (Recipe recipe : recipes)
            for (Ingredient ingredient : recipe.getIngredients())
                ingredients.add(ingredient.toString());

        return ingredients;
    }


    private int[] calculateIngredientWidth(ArrayList<String> allIngredients) {
        int[] dimensions = new int[3];
        Font font = new Font("Ariel", Font.PLAIN, 12);
        dimensions[1] = (int) font.getStringBounds(allIngredients.get(0), new FontRenderContext(new AffineTransform(),
                true,
                true)).getHeight();
        for (String str : allIngredients) {
            if (str.length() > dimensions[2]) {
                dimensions[2] = str.length();
                dimensions[0] = (int) font.getStringBounds(str, new FontRenderContext(new AffineTransform(),
                        true,
                        true)).getWidth();
            }
        }

        return dimensions;
    }

    private void initIngredients() {
        ArrayList<String> allIngredients = getAllIngredients();
        int[] dimensions = calculateIngredientWidth(allIngredients);
        ingredientsPanel.setPreferredSize(new Dimension(dimensions[0], dimensions[1] * allIngredients.size() * 2));
        for (Recipe recipe : recipes) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredientsPanel.add(new JLabel(fillLabelSize(ingredient.toString(), dimensions[2]), SwingConstants.LEFT));
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
