import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class GUI extends JFrame {

    private boolean currentPanel;
    private JPanel buttonPanel;
    private RecipePane recipePanel;
    private final JButton inputRecipe = new JButton("Input Recipe");
    private JLabel displayedImage;
    private ImageComponent imageComponent;

    public GUI () {
        currentPanel = true;
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
    }

    private void initializeLayout() {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        SimpleAttributeSet as = new SimpleAttributeSet();
        StyleConstants.setAlignment(as, StyleConstants.ALIGN_LEFT);
    }

    private void initializePanels() {
        initializeButtonPanel();
        recipePanel = new RecipePane();
    }

    private void initializeButtonPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(inputRecipe);
        this.getContentPane().add(buttonPanel);
    }

    private void addActionListeners() {
        inputRecipe.addActionListener(new InputRecipeAction());
    }

    private void initImageComponent() {
        try {
            imageComponent = new ImageComponent();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null,"Default Image not found.");
        }

        imageComponent.setBorder(BorderFactory.createEtchedBorder());
        displayedImage = new JLabel();
        this.getContentPane().add(displayedImage);
    }

    private void initializeKeystrokeActions() {
        InputMap imap = displayedImage.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        imap.put(KeyStroke.getKeyStroke("RIGHT"), "panel.next");
        imap.put(KeyStroke.getKeyStroke("LEFT"), "panel.prev");
        AbstractAction nextFrameAction = new StepAction(1);
        AbstractAction previousFrameAction = new StepAction(-1);

        ActionMap actionMap = displayedImage.getActionMap();
        actionMap.put("panel.next", nextFrameAction);
        actionMap.put("panel.prev", previousFrameAction);
    }

    private void swapPanels() {
        Container container = this.getContentPane();
        if (!currentPanel) {
            container.remove(buttonPanel);
            container.add(recipePanel);
        } else {
            container.remove(recipePanel);
            container.add(buttonPanel);
        }

        render();
        currentPanel = !currentPanel;
    }

    private void render() {
        displayedImage.setIcon(new ImageIcon(imageComponent.getImage()));
        pack();
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
                try {
                    imageComponent.setImage("cook_book1.png");
                } catch (IOException | IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(null, "Image not found");
                }
            }

            render();
        }
    }

    private class InputRecipeAction extends AbstractAction {
        public InputRecipeAction() {
        }

        public void actionPerformed(ActionEvent event) {
            swapPanels();
        }
    }
}