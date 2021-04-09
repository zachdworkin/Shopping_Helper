import javax.swing.*;
import java.awt.*;

class ImageComponent extends JComponent {
    private Image image;

    public ImageComponent(Image image) {
        this.image = image;
    }

    public void paintComponent(Graphics graphics) {
        if (image == null) return;

        graphics.drawImage(image, 0, 0, null);
    }
}