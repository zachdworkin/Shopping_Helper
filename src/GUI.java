import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;



public class GUI extends JFrame {

    private final JPanel buttonPanel;
    private final JButton inputRecipe = new JButton("Input Recipe");
    private JLabel displayedImage;
    private ImageComponent imageComponent;

    public GUI () {
        this.buttonPanel = new JPanel();
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
        initializeButtonPanel();
        initializeKeystrokeActions();
        initImageComponent();
        addActionListeners();
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
        add(displayedImage);
    }

    private void initializeKeystrokeActions() {
        InputMap imap = buttonPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        imap.put(KeyStroke.getKeyStroke("RIGHT"), "panel.next");
        imap.put(KeyStroke.getKeyStroke("LEFT"), "panel.prev");
        AbstractAction nextFrameAction = new StepAction(1);
        AbstractAction previousFrameAction = new StepAction(-1);

        ActionMap actionMap = buttonPanel.getActionMap();
        actionMap.put("panel.next", nextFrameAction);
        actionMap.put("panel.prev", previousFrameAction);
    }

    private void initializeButtonPanel() {
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(inputRecipe);
        add(buttonPanel);
    }

    private void initializeLayout() {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        SimpleAttributeSet as = new SimpleAttributeSet();
        StyleConstants.setAlignment(as, StyleConstants.ALIGN_LEFT);
    }

    private void render() {
        displayedImage.setIcon(new ImageIcon(imageComponent.getImage()));
        pack();
        revalidate();
        repaint();
    }

    private class StepAction extends AbstractAction {
        private int number;

        public StepAction(int direction) {
            this.number = direction;
        }

        public void actionPerformed(ActionEvent event) {
            try {
                imageComponent.setImage("cook_book1.png");
            } catch (IOException | IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, "Image not found");
            }

            render();
        }
    }

    private class InputRecipeAction extends AbstractAction{
        public InputRecipeAction() {}

        public void actionPerformed(ActionEvent event) {
        }
    }
}