import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;



public class GUI extends JFrame {

    private final JPanel buttonPanel;
    private JButton inputRecipe = new JButton("Input Recipe");
    private ImageComponent displayedImage;

    public GUI () {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        SimpleAttributeSet as = new SimpleAttributeSet();
        StyleConstants.setAlignment(as, StyleConstants.ALIGN_CENTER);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        buttonPanel.add(inputRecipe);

        InputMap imap = buttonPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        imap.put(KeyStroke.getKeyStroke("RIGHT"), "panel.next");
        imap.put(KeyStroke.getKeyStroke("LEFT"), "panel.prev");
        AbstractAction nextFrameAction = new StepAction(1);
        AbstractAction previousFrameAction = new StepAction(-1);
        //map those names of inputs to actions
        ActionMap actionMap = buttonPanel.getActionMap();
        actionMap.put("panel.next", nextFrameAction);
        actionMap.put("panel.prev", previousFrameAction);

        inputRecipe.addActionListener(new InputRecipeAction());

        displayedImage = new ImageComponent(new ImageIcon(getClass().getResource("cook_book.png")).getImage());
        displayedImage.setBorder(BorderFactory.createEtchedBorder());

        render();
    }

    private void render() {
        pack();
        validate();
        repaint();
    }

    private class StepAction extends AbstractAction {
        private int number;

        public StepAction(int direction) {
            this.number = direction;
        }

        public void actionPerformed(ActionEvent event) {
        }
    }

    private class InputRecipeAction extends AbstractAction{
        public void actionPerformed(ActionEvent event) {
        }
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
}