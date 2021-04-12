import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

class ImageComponent extends JComponent {
    private Image image;
    private final Path resourcesDirectory;

    public ImageComponent() throws IOException {
        resourcesDirectory = Path.of((new File(System.getProperty("user.dir"))).getAbsolutePath()).resolve("resources");
        this.image = getImageFromFile("cook_book.png");
    }

    public Image getImage() { return this.image;}

    public void setImage(Image image) {
        this.image = image;
    }

    public Path getResourcesDirectory() {
        return resourcesDirectory;
    }

    public Image getImageFromFile(String image) throws IOException {
        return ImageIO.read(resourcesDirectory.resolve(image).toFile());
    }

    public void paintComponent(Graphics g) {
        if (image == null) return;

        g.drawImage(image, 0, 0, null);
    }
}