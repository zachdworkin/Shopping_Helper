import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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

    public void createImageFromText(Path imagePath) throws IOException {
        int width = 1000;
        int height = 1000;

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, 48);
        g2d.setFont(font);
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);
        File file = imagePath.toFile();

        BufferedReader br;
        int nextLinePosition=100;
        int fontSize = 48;
        br = new BufferedReader(new FileReader(file));

        String line;
        while ((line = br.readLine()) != null) {
            g2d.drawString(line, 0, nextLinePosition);
            nextLinePosition = nextLinePosition + fontSize;
        }
        br.close();

        g2d.dispose();
        String fileName = imagePath.toFile().getName();
        String newFileName = fileName.substring(0, fileName.length() - 7) + ".png";
        ImageIO.write(img, "png", resourcesDirectory.resolve("recipeCardImages").resolve(newFileName).toFile());
    }

    public void paintComponent(Graphics g) {
        if (image == null) return;

        g.drawImage(image, 0, 0, null);
    }
}