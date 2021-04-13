import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class GUI extends JFrame {

    private ArrayList<Recipe> recipes;

    private JPanel buttonPanel;
    private RecipePane recipePanel;

    private final JButton inputRecipe = new JButton("Input Recipe");
    private final JButton next = new JButton("Next");
    private final JButton previous = new JButton("Previous");
    private final JButton viewRecipe = new JButton("View Recipe");

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
        initializeDialog();
        initializeKeystrokeActions();
        addActionListeners();
        initializeRecipes();
    }

    private void initializeLayout() {
        setLayout(new GridBagLayout());
        this.gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
    }

    private void initImageComponent() {
        currentImage = 0;
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
        gbc.gridy = 0;
        buttonPanel.add(inputRecipe, gbc);
        gbc.gridy++;
        buttonPanel.add(viewRecipe, gbc);
        gbc.gridy++;
        buttonPanel.add(next);
        gbc.gridy++;
        buttonPanel.add(previous);
        gbc.gridx = 1;
        gbc.gridy = 0;
        this.getContentPane().add(buttonPanel, gbc);
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

                initializeDialog();
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
    }

    private void initializeRecipes() {
        Path recipeResourcesDirectory = imageComponent.getResourcesDirectory().resolve("recipes");
        recipes = new ArrayList<>();

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
                String name = recipe.getName() + ".png";
                try {
                    recipe.setImage(ImageIO.read(imageComponent.getResourcesDirectory().resolve(name).toFile()));
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(null, "Recipe Image " + name + " Not Found.");
                }

                try {
                    recipe.setTextImage(ImageIO.read(imageComponent.getResourcesDirectory().resolve("recipeCardImages").resolve(name).toFile()));
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

            if (!viewRecipe.getText().equals("View Recipe Image"))
                imageComponent.setImage(recipes.get(currentImage).getImage());
            else
                imageComponent.setImage(recipes.get(currentImage).getTextImage());

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
}