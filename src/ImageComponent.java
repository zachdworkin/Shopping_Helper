import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

class ImageComponent extends JComponent {
    private Image image;
    private Path resourcesDirectory;
    private File imageFile;

    public ImageComponent() throws IOException {
        resourcesDirectory = Path.of((new File(System.getProperty("user.dir"))).getAbsolutePath()).resolve("resources");
        this.image = setImageFile("cook_book.png");
    }

    public Image getImage() { return this.image;}

    public void setImage(String image) throws IOException {
        this.image = setImageFile(image);
    }

    public Image setImageFile(String image) throws IOException {
        imageFile = resourcesDirectory.resolve(image).toFile();
        return ImageIO.read(imageFile);
    }

    public void paintComponent(Graphics g) {
        if (image == null) return;

        g.drawImage(image, 0, 0, null);
        Graphics2D g2 = (Graphics2D) g;
    }
}