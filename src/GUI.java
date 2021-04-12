import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class GUI extends JFrame {

    private ArrayList<Recipe> recipes;

    private JPanel buttonPanel;
    private RecipePane recipePanel;
    private CheckoutPane checkoutPanel;

    private final JButton inputRecipe = new JButton("Input Recipe");
    private final JButton next = new JButton("Next");
    private final JButton previous = new JButton("Previous");
    private final JButton viewRecipe = new JButton("View Recipe");
    private AbstractAction nextFrameAction;
    private AbstractAction previousFrameAction;

    private JLabel imageLabel;
    private int currentImage;
    private ImageComponent imageComponent;
    private GridBagConstraints gbc;


    private enum panelType {
        viewing,
        inputting,
        checkout,
    }

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
        initializePanels();
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

    private void initializePanels() {
        initializeButtonPanel();
        recipePanel = new RecipePane();
        checkoutPanel = new CheckoutPane();
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
                JOptionPane.showMessageDialog(null, "Recipe " + currentFile.toString() +
                                              "not found.");
            }

            for(Recipe recipe : recipes) {
                String name = recipe.getName() + ".png";
                try {
                    recipe.setImage(ImageIO.read(imageComponent.getResourcesDirectory().resolve(name).toFile()));
                    recipe.setTextImage(ImageIO.read(imageComponent.getResourcesDirectory().resolve("recipeCardImages").resolve(name).toFile()));
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(null, "Recipe Image Not Found.");
                }
            }
        }


    }

    private void swapPanels(panelType panel) {
        Container container = this.getContentPane();
        container.removeAll();
        switch (panel) {
            case viewing -> {
                container.add(imageLabel);
                container.add(buttonPanel);
            }
            case inputting -> {
                container.add(imageLabel);
                container.add(recipePanel);
            }
            case checkout -> container.add(checkoutPanel);
            default -> container.add(imageLabel);
        }

        render();
    }

    private void render() {
        imageLabel.setIcon(new ImageIcon(imageComponent.getImage()));
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
        revalidate();
        repaint();
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

                imageComponent.setImage(recipes.get(currentImage).getImage());
            }

            if (direction == -1) {
                if (--currentImage < 0)
                    currentImage = recipes.size() - 1;

                imageComponent.setImage((recipes.get(currentImage).getImage()));
            }

            render();
        }
    }

    private class ViewRecipeAction extends AbstractAction {
        public ViewRecipeAction() {

        }

        public void actionPerformed(ActionEvent event) {
            if (viewRecipe.getText().equals("View Recipe Image")) {
                viewRecipe.setText("View Recipe");
                imageComponent.setImage((recipes.get(currentImage).getImage()));
            } else {
                try {
                    imageComponent.setImage(recipes.get(currentImage).getTextImage());
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(null, "Recipe File Not Found.");
                }

                viewRecipe.setText("View Recipe Image");
            }

            render();
        }
    }

    private class InputRecipeAction extends AbstractAction {
        public InputRecipeAction() {
        }

        public void actionPerformed(ActionEvent event) {swapPanels(panelType.inputting);}
    }
}