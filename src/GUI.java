import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;

public class GUI extends JFrame {
    private ArrayList<Recipe> recipes;
    private ArrayList<String> cart;

    private JPanel buttonPanel;
    private JPanel cartPanel;
    private RecipePane recipePanel;

    private final JButton inputRecipe = new JButton("Input Recipe");
    private final JButton next = new JButton("Next");
    private final JButton previous = new JButton("Previous");
    private final JButton viewRecipe = new JButton("View Recipe");
    private final JButton selected = new JButton("Select");
    private final JButton checkout = new JButton("Checkout");
    private final JButton printShoppingList = new JButton("Print Shopping List");
    private final JButton printInstructions = new JButton("Print Instructions");
    private final JButton exit = new JButton("Exit");

    private JDialog dialog;

    private AbstractAction nextFrameAction;
    private AbstractAction previousFrameAction;

    private JLabel imageLabel;
    private int currentImage;
    private ImageComponent imageComponent;
    private GridBagConstraints gbc;

    public GUI () {
        initAll();
        render();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() ->
        {
            GUI gui = new GUI();
            gui.setTitle("Shopping List Helper");
            WindowListener exitListener = new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            };
            gui.addWindowListener(exitListener);
            gui.setVisible(true);
        });
    }

    private void initAll() {
        initializeLayout();
        initImageComponent();
        initializeButtonPanel();
        initializeCart();
        initializeDialog();
        initializeKeystrokeActions();
        addActionListeners();
        initializeRecipes();
    }

    private void initializeLayout() {
        setLayout(new FlowLayout());
        this.gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 0, 0, 2);
        gbc.anchor = GridBagConstraints.NORTHWEST;
    }

    private void initImageComponent() {
        currentImage = 1;
        try {
            imageComponent = new ImageComponent();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null,"Default Image not found.");
        }

        imageComponent.setBorder(BorderFactory.createEtchedBorder());
        imageLabel = new JLabel();
        gbc.gridy = 0;
        gbc.gridx = 0;
        this.getContentPane().add(imageLabel, gbc);
    }

    private void initializeButtonPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        gbc.gridx = 0;
        buttonPanel.add(inputRecipe, gbc);
        gbc.gridy++;
        buttonPanel.add(viewRecipe, gbc);
        gbc.gridy++;
        buttonPanel.add(next);
        gbc.gridy++;
        buttonPanel.add(previous);
        gbc.gridy++;
        buttonPanel.add(selected);
        gbc.gridy++;
        buttonPanel.add(checkout);
        gbc.gridy++;
        buttonPanel.add(exit);
        gbc.gridx = 1;
        gbc.gridy = 0;
        this.getContentPane().add(buttonPanel, gbc);
    }

    private void initializeCart() {
        cartPanel = new JPanel();
        cartPanel.setPreferredSize(new Dimension(250, 250));
        gbc.gridx = 2;
        gbc.gridy = 0;
        this.getContentPane().add(cartPanel, gbc);

        cartPanel.add(new JLabel(createCartItem("My Cart:")));

    }

    private void initializeDialog() {
        dialog = new JDialog(this);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setSize(size.width, size.height);

        recipePanel = new RecipePane();
        dialog.add(recipePanel);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                if (recipePanel.isFinalized()) {
                    Recipe recipe = recipePanel.getRecipe();
                    Path directory = imageComponent.getResourcesDirectory();
                    try {
                        imageComponent.createImageFromText(directory.resolve("recipes").resolve(recipe.getName() + ".recipe"));
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(null, "Could not generate .png from .recipe");
                    }

                    try {
                        recipe.setTextImage(ImageIO.read(directory.resolve("recipeCardImages").resolve(recipe.getName() + ".png").toFile()));
                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(null, "Error generating recipe png card");
                    }

                    recipes.add(recipe);
                }
            }
        });
    }

    private void initializeKeystrokeActions() {
        InputMap imap = buttonPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        imap.put(KeyStroke.getKeyStroke("RIGHT"), "panel.next");
        imap.put(KeyStroke.getKeyStroke("LEFT"), "panel.prev");
        nextFrameAction = new StepAction(1);
        previousFrameAction = new StepAction(-1);

        ActionMap actionMap = buttonPanel.getActionMap();
        actionMap.put("panel.next", nextFrameAction);
        actionMap.put("panel.prev", previousFrameAction);
    }

    private void addActionListeners() {
        inputRecipe.addActionListener(new InputRecipeAction());
        next.addActionListener(nextFrameAction);
        previous.addActionListener(previousFrameAction);
        viewRecipe.addActionListener(new ViewRecipeAction());
        selected.addActionListener(new SelectRecipeAction());
        checkout.addActionListener(new CheckoutAction(this));
        exit.addActionListener(new ExitAction());
    }

    private void initializeRecipes() {
        Path recipeResourcesDirectory = imageComponent.getResourcesDirectory().resolve("recipes");
        recipes = new ArrayList<>();
        cart = new ArrayList<>();

        File[] recipeFiles = recipeResourcesDirectory.toFile().listFiles();
        if (recipeFiles == null) {
            JOptionPane.showMessageDialog(null, "No Recipes found");
            System.exit(1);
        }

        for (File currentFile : recipeFiles) {
            try {
                recipes.add(Recipe.readFile(currentFile));
                imageComponent.createImageFromText(currentFile.toPath());
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(null, "Recipe " + currentFile.toString() + "not found.");
            }

            for(Recipe recipe : recipes) {
                String png = recipe.getName() + ".png";
                String rcp = recipe.getName() + ".recipe";
                try {
                    recipe.setImage(ImageIO.read(imageComponent.getResourcesDirectory().resolve(png).toFile()));
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(null, "Recipe Image " + png + " Not Found.");
                }

                if (!imageComponent.getResourcesDirectory().resolve("recipeCardImages").resolve(png).getFileName().toFile().isFile()) {
                    try {
                        imageComponent.createImageFromText(imageComponent.getResourcesDirectory().resolve("recipes").resolve(rcp));
                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(null, "Could not generate recipe instructions image for " + rcp + ".");
                    }
                }
                try {
                    recipe.setTextImage(ImageIO.read(imageComponent.getResourcesDirectory().resolve("recipeCardImages").resolve(png).toFile()));
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(null, "Recipe Image Card Not Found.");
                }
            }
        }
    }

    private void render() {
        if (imageComponent.getImage() == null) {
            JOptionPane.showMessageDialog(null, "Recipe Image Not Found. Using default image");
            if (!viewRecipe.getText().equals("View Recipe Image"))
                try {
                    imageComponent.setImage(ImageIO.read(imageComponent.getResourcesDirectory().resolve("DefaultImageFile").resolve("default.png").toFile()));
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(null, "Default Image not found");
                }
            else
                imageComponent.setImage(recipes.get(0).getTextImage());
        }

        imageLabel.setIcon(new ImageIcon(imageComponent.getImage()));
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
        revalidate();
        repaint();
    }

    private void updateViewImageButton() {
        if (viewRecipe.getText().equals("View Recipe Image"))
            viewRecipe.setText("View Recipe");
        else
            viewRecipe.setText("View Recipe Image");
    }

    private String createCartItem(String item) {
        int MAX_CART_ITEM_CHARACTERS = 32;
        return item + " ".repeat(Math.max(0, MAX_CART_ITEM_CHARACTERS - item.length()));
    }

    private void updateCart() {
        for (Component comp : cartPanel.getComponents())
            cartPanel.remove(comp);

        cartPanel.add(new JLabel(createCartItem("My Cart:")));
        for (String item : cart)
            cartPanel.add(new JLabel(createCartItem(item)));

        render();
    }

    private void writeFile(File file, String words) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(file);
        out.write(words);
        out.close();
    }


    private ArrayList<String> lineParser(String line) {
        ArrayList<String> lines = new ArrayList<>();
        if (line.length() > 80) {
            int line_count = 1;
            int step_num = 1;
            String[] ln = line.split(" ");
            StringBuilder newLine = new StringBuilder();
            String previous_word = "";

            for (String word : ln) {
                if (previous_word.equals("Step")) step_num = Integer.parseInt(word.substring(0, word.length() - 1));
                if (word.equals("Step")) line_count = 1;

                if (newLine.length() + word.length() < 80) {
                    newLine.append(word).append(" ");
                } else {
                    lines.add(newLine.append("\n").toString());
                    newLine = new StringBuilder();
                    if (++line_count > 1) {
                        newLine.append("        ");
                        if (step_num >= 10) {
                            newLine.append(" ");
                        }

                        newLine.append(word).append(" ");
                    }
                }

                previous_word = word;
            }

            lines.add(newLine.append("\n").toString());
        } else {
            lines.add(line + "\n");
        }

        return lines;
    }

    private void generateShoppingList(File file) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        sb.append("GO TO THE STORE FOO!");
        writeFile(file, sb.toString());
    }

    private void exportInstructions(File file) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        for (Recipe rcp : recipes) {
            sb.append(rcp.getName()).append("\n");
            for (String line : rcp.getInstructions()) {
                for (String shorterLine : lineParser(line)) {
                    sb.append(shorterLine);
                }
            }

            sb.append("\n\n");
        }

        writeFile(file, sb.toString());
    }

    private class StepAction extends AbstractAction {
        private final int direction;

        public StepAction(int direction) {
            this.direction = direction;
        }

        public void actionPerformed(ActionEvent event) {
            if (direction == 1) {
                if (++currentImage >= recipes.size())
                    currentImage = 0;

            } else if (direction == -1) {
                if (--currentImage < 0)
                    currentImage = recipes.size() - 1;
            }

            Recipe currentRecipe = recipes.get(currentImage);
            if (!viewRecipe.getText().equals("View Recipe Image"))
                imageComponent.setImage(currentRecipe.getImage());
            else
                imageComponent.setImage(currentRecipe.getTextImage());

            if (currentRecipe.isSelected())
                selected.setText("Selected");
            else
                selected.setText("Select");

            render();
        }
    }

    private class ViewRecipeAction extends AbstractAction {
        public ViewRecipeAction() {

        }

        public void actionPerformed(ActionEvent event) {
            Recipe currentRecipe = recipes.get(currentImage);
            if (viewRecipe.getText().equals("View Recipe Image")) {
                imageComponent.setImage(currentRecipe.getImage());
            } else {
                try {
                    imageComponent.setImage(currentRecipe.getTextImage());
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(null, "Recipe File Not Found.");
                }
            }

            updateViewImageButton();
            render();
        }
    }

    private class InputRecipeAction extends AbstractAction {
        public InputRecipeAction() {
        }

        public void actionPerformed(ActionEvent event) {
            dialog.setVisible(true);
        }
    }

    private class SelectRecipeAction extends AbstractAction {
        public SelectRecipeAction() {

        }

        public void actionPerformed(ActionEvent event) {
            Recipe currentRecipe = recipes.get(currentImage);
            currentRecipe.setSelected(!currentRecipe.isSelected());
            if (currentRecipe.isSelected()){
                selected.setText("Selected");
                cart.add(currentRecipe.getName());
            }else {
                cart.remove(currentRecipe.getName());
                selected.setText("Select");
            }

            updateCart();
        }
    }

    private class CheckoutAction extends AbstractAction {
        private final JFrame parent;
        public CheckoutAction(JFrame parent) {
            this.parent = parent;
        }

        public void actionPerformed(ActionEvent event) {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "No items in cart, cannot checkout.");
                return;
            }

            ArrayList<Recipe> selectedRecipes = new ArrayList<>();
            for (Recipe recipe : recipes) {
                if (cart.contains(recipe.getName()))
                    selectedRecipes.add(recipe);
            }


            recipes = selectedRecipes;
            resetImage();
            initNewButtonPanel();

            render();
        }

        private void resetImage() {
            currentImage = 0;
            imageComponent.setImage(recipes.get(currentImage).getImage());
        }

        private void initNewButtonPanel() {
            removeButtons();
            addNewPrintButtons();
            addNewActionListeners();
        }

        private void removeButtons() {
            buttonPanel.remove(selected);
            buttonPanel.remove(inputRecipe);
            buttonPanel.remove(checkout);
            buttonPanel.remove(exit);
            parent.remove(cartPanel);
        }

        private void addNewPrintButtons() {
            buttonPanel.add(printShoppingList);
            buttonPanel.add(printInstructions);
            buttonPanel.add(exit);
        }

        private void addNewActionListeners() {
            printShoppingList.addActionListener(new PrintShoppingListAction());
            printInstructions.addActionListener(new PrintInstructionsAction());
            exit.addActionListener(new ExitAction());
        }
    }

    private class PrintShoppingListAction extends AbstractAction {
        public PrintShoppingListAction() {}

        public void actionPerformed(ActionEvent event) {
            FileDialog fd = new FileDialog(GUI.this, "Select where to save shopping list", FileDialog.SAVE);
            fd.setFile("shopping_list.txt");
            fd.setVisible(true);
            String name = fd.getDirectory() + fd.getFile();
            if (!name.endsWith(".txt")) name = name + ".txt";
            File file = new File(name);
            try {
                generateShoppingList(file);
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(null, "Could not Generate Shopping List.");
            }
        }
    }

    private class PrintInstructionsAction extends AbstractAction {
        public PrintInstructionsAction() {}

        public void actionPerformed(ActionEvent event) {
            FileDialog fd = new FileDialog(GUI.this, "Select where to save instructions", FileDialog.SAVE);
            fd.setFile("instructions.txt");
            fd.setVisible(true);
            String name = fd.getDirectory() + fd.getFile();
            if (!name.endsWith(".txt")) name = name + ".txt";
            File file = new File(name);
            try {
                exportInstructions(file);
            } catch (FileNotFoundException exception) {
                JOptionPane.showMessageDialog(null, "Could not Generate Shopping List.");
            }
        }
    }

    private static class ExitAction extends AbstractAction {
        public ExitAction() {}

        public void actionPerformed(ActionEvent event) {
            System.exit(0);
        }
    }
}
