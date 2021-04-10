import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class RecipePane extends JPanel {
    private final Recipe recipe;

    private final JButton addRecipe;
    private final JButton addIngredient;
    private final JButton addInstruction;

    private final JLabel displayRecipeName;

    ArrayList<JLabel> ingredientLabels;
    ArrayList<JLabel> instructionLabels;

    public RecipePane() {
        setAlignmentX(SwingConstants.LEFT);
        ingredientLabels = new ArrayList<>();
        instructionLabels = new ArrayList<>();
        addRecipe = new JButton("Add Recipe Name");
        addIngredient = new JButton("Add Ingredient");
        addInstruction = new JButton("Add Instruction");
        recipe = new Recipe("");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        displayRecipeName = new JLabel("Recipe Name: " + recipe.getName());
        add(displayRecipeName, gbc);
        addRecipe.addActionListener(new InputRecipeName(addRecipe));
        gbc.gridx++;

        add(addRecipe, gbc);
        gbc.gridx++;

        add(addIngredient, gbc);
        gbc.gridx++;
        addIngredient.addActionListener(new InputIngredient(addIngredient));
        displayIngredients();

        add(addInstruction, gbc);
        addInstruction.addActionListener(new InputInstruction(addInstruction));
        displayInstructions();
    }

    private void displayInstructions() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 2; gbc.gridy = 1;
        add(new JLabel("Instructions: "), gbc);
        gbc.gridy++;
        for (JLabel label : instructionLabels) {
            String text = label.getText();
            add(new JLabel("\tStep " + (instructionLabels.indexOf(label) + 1) + ": " + text), gbc);
            gbc.gridy++;
        }
    }

    private void displayIngredients() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Ingredients :"), gbc);
        gbc.gridy++;
        for (JLabel label : ingredientLabels) {
            add(label, gbc);
            gbc.gridy++;
        }
    }

    private String checkStringInput(Component parent, JTextArea name) {
        String ingredientName = null;
        try {
            ingredientName = name.getText();
        } catch (NullPointerException npe) {
            JOptionPane.showMessageDialog(parent, "Invalid Input.");
        }

        return ingredientName;
    }

    private double checkNumberInput(Component parent, JTextField number) {
        double measureValue = -1;
        try {
            measureValue = Double.parseDouble(number.getText());
            if (measureValue < 0)
                throw new NumberFormatException();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(parent, "Invalid Input.");
        }
        return measureValue;
    }

    private class InputRecipeName extends AbstractAction {
        Component parent;

        public InputRecipeName(Component parent) {
            this.parent = parent;
        }

        public void actionPerformed(ActionEvent event) {
            recipe.setName(getRecipeNameFromUser());
            displayRecipeName.setText("Recipe Name: " + recipe.getName());
            addRecipe.setText("Add New Recipe Name");
            revalidate();
            repaint();
        }

        private String getRecipeNameFromUser() {
            JTextArea recipeName = new JTextArea();
            Object[] message = {
                    "Input Recipe Name",
                    recipeName ,
            };

            int result = JOptionPane.showConfirmDialog(parent, message,
                    "Input Recipe", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                return null;
            }

            return checkStringInput(parent, recipeName);
        }
    }

    private class InputIngredient extends AbstractAction {
        Component parent;
        private final Object[] measureTypes = new Object[]{"Pound(s)", "Ounce(s)", "Teaspoon(s)", "Tablespoon(s)",
                "Breast(s)", "Fillet(s)", "Chop(s)", "Bag(s)", "Cup(s)"};

        public InputIngredient(Component parent) {
            this.parent = parent;
        }

        public void actionPerformed(ActionEvent event) {
            for (JLabel label : ingredientLabels) {
                remove(label);
            }

            getIngredients();
            addIngredient.setText("Add New Ingredient");
            displayIngredients();
            revalidate();
            repaint();
        }

        private void getIngredients() {
            int result = getRecipeIngredientsFromUser();
            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION)
                return;

            result = JOptionPane.showConfirmDialog(addIngredient, "Do you want to add another ingredient?",
                    "Add ingredient", JOptionPane.YES_NO_OPTION);
            while (result == JOptionPane.YES_OPTION) {
                getRecipeIngredientsFromUser();
                result = JOptionPane.showConfirmDialog(addIngredient, "Do you want to add another ingredient?",
                        "Add ingredient", JOptionPane.YES_NO_OPTION);
            }
        }

        private int getRecipeIngredientsFromUser() {
            JTextArea name = new JTextArea();
            JTextField number = new JTextField();
            JComboBox<Object> measurementType = new JComboBox<>(measureTypes);
            Object[] message = {
                    "Ingredient name",
                    name,
                    "Amount of ingredient",
                    number,
                    measurementType,
            };

            int result = JOptionPane.showConfirmDialog(parent, message,
                    (recipe.getName() == null ? "" : (recipe.getName() + ": ")) + "Input Ingredient",
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                return JOptionPane.CANCEL_OPTION;
            }

            String ingredientName = checkStringInput(parent, name);
            if (ingredientName.equals(""))
                return JOptionPane.CANCEL_OPTION;

            double measureValue = checkNumberInput(parent, number);
            if (measureValue < 0)
                return JOptionPane.CANCEL_OPTION;

            Ingredient newIngredient = new Ingredient(measureTypes[measurementType.getSelectedIndex()].toString(),
                    measureValue, ingredientName);
            recipe.addIngredient(newIngredient);
            ingredientLabels.add(new JLabel("\t" + newIngredient.toString()));

            return JOptionPane.OK_OPTION;
        }
    }

    private class InputInstruction extends AbstractAction {
        Component parent;

        public InputInstruction(Component parent) {
            this.parent = parent;
        }

        public void actionPerformed(ActionEvent event) {
            for (JLabel label : instructionLabels) {
                remove(label);
            }

            getInstructions();
            addInstruction.setText("Add New Instruction");
            displayInstructions();
            revalidate();
            repaint();
        }

        private void getInstructions() {
            getInstructionFromUser();
            while (JOptionPane.showConfirmDialog(null, "Is there another step?",
                    "Add Recipe Instruction", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                getInstructionFromUser();
            }
        }

        private void getInstructionFromUser() {
            JTextField instruction = new JTextField();
            Object[] message = {
                    ("Step " + (recipe.getInstructions().size() + 1)),
                    instruction,
            };

            int result = JOptionPane.showConfirmDialog(null, message,
                    recipe.getName() + ": Input Instruction", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                return;
            }

            String words = null;
            try {
                words = instruction.getText();
            } catch (NullPointerException npe) {
                JOptionPane.showMessageDialog(null, "No instruction was input.");
            }

            instructionLabels.add(new JLabel(words));
            recipe.addInstruction(words);
        }
    }
}
