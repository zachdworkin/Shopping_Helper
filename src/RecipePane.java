import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.JOptionPane.*;

public class RecipePane extends JPanel {
    private final Path RESOURCES_PATH = Path.of((new File(System.getProperty("user.dir"))).getAbsolutePath()).resolve("resources");

    private Path userFile;

    private final Recipe recipe;
    private boolean finalized;

    private final JButton addRecipe;
    private final JButton addIngredient;
    private final JButton addInstruction;

    private final JLabel displayRecipeName;
    private JLabel image;

    ArrayList<JLabel> ingredientLabels;
    ArrayList<JLabel> instructionLabels;

    public RecipePane() {
        setAlignmentX(SwingConstants.LEFT);
        ingredientLabels = new ArrayList<>();
        instructionLabels = new ArrayList<>();
        addRecipe = new JButton("Add Recipe Name");
        addIngredient = new JButton("Add Ingredient");
        addInstruction = new JButton("Add Instruction");
        JButton addImage = new JButton("Add Image");
        JButton finish = new JButton("Finish");
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

        gbc.gridx++;
        add(addImage, gbc);
        addImage.addActionListener(new OpenFile(null));

        gbc.gridx = 1;
        gbc.gridy = 3;
        add(finish, gbc);
        finish.addActionListener(new FinalizeRecipe(finish));

        addDropTarget(this);
        image = new JLabel();
        add(image);
        image.setVisible(false);
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
            showMessageDialog(parent, "Invalid Input.");
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
            showMessageDialog(parent, "Invalid Input.");
        }
        return measureValue;
    }

    private void addDropTarget(Component parent) {
        setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    createUserFile(droppedFiles.get(0).toPath(), getRecipeName());
                } catch (Exception ex) {
                    showMessageDialog(parent, "File Lost");
                    return;
                }

                image.setIcon(new ImageIcon(recipe.getImage()));
                image.setVisible(true);
            }
        });
    }

    public boolean isFinalized() {return finalized; }

    public Recipe getRecipe() {return recipe; }

    public void finalizeRecipe() {
        recipe.setName(getRecipeName());
        if (!RESOURCES_PATH.resolve("recipes").resolve(recipe.getName() + ".recipe").toFile().isFile())
            createRecipeFile();

        if (recipe.getImage() == null)
            saveUserPNGFile();

        finalized = true;
    }

    private void saveUserPNGFile() {
        try {
            Files.copy(RESOURCES_PATH.resolve("DefaultImageFile").resolve("default.png"), RESOURCES_PATH.resolve(recipe.getName() + ".png"), StandardCopyOption.REPLACE_EXISTING);
            recipe.setImage(ImageIO.read(RESOURCES_PATH.resolve(recipe.getName() + ".png").toFile()));
        } catch (IOException e) {
            showMessageDialog(null, "Could not generate a default recipe file");
        }
    }

    private void createRecipeFile() {
        try {
            recipe.writeFile(RESOURCES_PATH.resolve("recipes").resolve(recipe.getName() + ".recipe").toFile());
        } catch (IOException ioe) {
            showMessageDialog(null, "File already Exists");
        }
    }

    private class FinalizeRecipe extends AbstractAction {
        final Component parent;
        public FinalizeRecipe(Component parent) {
            this.parent = parent;
        }

        public void actionPerformed(ActionEvent event) {
            finalizeRecipe();
            Window window = SwingUtilities.windowForComponent((JButton)event.getSource());
            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
        }
    }

    private class OpenFile implements ActionListener {
        private final Dialog parent;

        public OpenFile(Dialog parent) {this.parent = parent; }

        public void actionPerformed(ActionEvent e) throws NumberFormatException {
            FileDialog fd = new FileDialog(parent, "Choose a File", FileDialog.LOAD);
            fd.setVisible(true);
            File[] files = fd.getFiles();

            //if user hits Cancel
            if (files.length == 0) {
                return;
            }

            try {
                createUserFile(files[0].toPath(), getRecipeName());
            } catch (IOException ioException) {
                showMessageDialog(parent, "File Lost");
            }

            image.setIcon(new ImageIcon(recipe.getImage()));
            image.setVisible(true);
        }
    }

    private void createUserFile(Path filePath, String name) throws IOException{
        Files.copy(filePath, RESOURCES_PATH.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        userFile = RESOURCES_PATH.resolve(name);
        recipe.setImage(ImageIO.read(userFile.toFile()));
    }

    private String getRecipeName() {
        String name = recipe.getName();
        if (!name.equals(""))
            return name;
        else
            return "DEFAULT_NAME";
    }

    private class InputRecipeName extends AbstractAction {
        final Component parent;

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
                    recipeName,
            };

            int result = showConfirmDialog(parent, message,
                    "Input Recipe", OK_CANCEL_OPTION);

            if (result == CANCEL_OPTION || result == CLOSED_OPTION) {
                return null;
            }

            return checkStringInput(parent, recipeName);
        }
    }

    private class InputIngredient extends AbstractAction {
        final Component parent;
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
            if (result == CANCEL_OPTION || result == CLOSED_OPTION)
                return;

            result = showConfirmDialog(addIngredient, "Do you want to add another ingredient?",
                    "Add ingredient", YES_NO_OPTION);
            while (result == YES_OPTION) {
                getRecipeIngredientsFromUser();
                result = showConfirmDialog(addIngredient, "Do you want to add another ingredient?",
                        "Add ingredient", YES_NO_OPTION);
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

            int result = showConfirmDialog(parent, message,
                    (recipe.getName() == null ? "" : (recipe.getName() + ": ")) + "Input Ingredient",
                    OK_CANCEL_OPTION);

            if (result == CANCEL_OPTION || result == CLOSED_OPTION) {
                return CANCEL_OPTION;
            }

            String ingredientName = checkStringInput(parent, name);
            if (ingredientName.equals(""))
                return CANCEL_OPTION;

            double measureValue = checkNumberInput(parent, number);
            if (measureValue < 0)
                return CANCEL_OPTION;

            Ingredient newIngredient = new Ingredient(measureTypes[measurementType.getSelectedIndex()].toString(),
                    measureValue, ingredientName);
            recipe.addIngredient(newIngredient);
            ingredientLabels.add(new JLabel("\t" + newIngredient.toString()));

            return OK_OPTION;
        }
    }

    private class InputInstruction extends AbstractAction {
        final Component parent;

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
            while (showConfirmDialog(null, "Is there another step?",
                    "Add Recipe Instruction", YES_NO_OPTION) == YES_OPTION) {
                getInstructionFromUser();
            }
        }

        private void getInstructionFromUser() {
            JTextField instruction = new JTextField();
            Object[] message = {
                    ("Step " + (recipe.getInstructions().size() + 1)),
                    instruction,
            };

            int result = showConfirmDialog(null, message,
                    recipe.getName() + ": Input Instruction", OK_CANCEL_OPTION);

            if (result == CANCEL_OPTION || result == CLOSED_OPTION) {
                return;
            }

            String words = null;
            try {
                words = instruction.getText();
            } catch (NullPointerException npe) {
                showMessageDialog(null, "No instruction was input.");
            }

            instructionLabels.add(new JLabel(words));
            recipe.addInstruction(words);
        }
    }
}
