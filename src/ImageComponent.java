import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

class ImageComponent extends JComponent {
    private Image image;
    private final Path resourcesDirectory;

    public ImageComponent() throws IOException {
        resourcesDirectory = Path.of((new File(System.getProperty("user.dir"))).getAbsolutePath()).resolve("resources");
        this.image = getImageFromFile("cook_book.png");
    }

    public Image getImage() { return this.image;}

    public void setImage(Image image) {
        this.image = scale(image, 800, 800);
    }

    public Path getResourcesDirectory() {
        return resourcesDirectory;
    }

    public Image getImageFromFile(String image) throws IOException {
        return ImageIO.read(resourcesDirectory.resolve(image).toFile());
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
                        newLine.append("            ");
                        if (step_num >= 10) {
                            newLine.append("  ");
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

    public void createImageFromText(Path imagePath) throws IOException {
        int width = 800;
        int height = 800;
        int fontSize = 16;
        int nextLinePosition = fontSize * 2 + 10;

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Times New Roman", Font.PLAIN, fontSize);
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

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            ArrayList<String> lines = lineParser(line);
            for (String ln : lines) {
                g2d.drawString(ln, 0, nextLinePosition);
                nextLinePosition = nextLinePosition + fontSize;
            }
        }

        br.close();
        g2d.dispose();
        String fileName = imagePath.toFile().getName();
        String newFileName = fileName.substring(0, fileName.length() - "recipe".length()) + "png";
        ImageIO.write(img, "png", resourcesDirectory.resolve("recipeCardImages").resolve(newFileName).toFile());
    }

    public void paintComponent(Graphics g) {
        if (image == null) return;

        g.drawImage(image, 0, 0, null);
    }

    public static Image scale(Image image, int width, int height){
        double displayAngle = Math.atan2(height, width);
        if (displayAngle < 0) {
            displayAngle += (2 * Math.PI);
        }
        double imageAngle = Math.atan2(image.getHeight(null), image.getWidth(null));
        if (imageAngle < 0) {
            imageAngle += (2 * Math.PI);
        }
        Image scaleImage;

        if (displayAngle >= imageAngle) {
            //Giving -1 keeps the Image's original aspect ratio.
            scaleImage = image.getScaledInstance(width, -1, Image.SCALE_DEFAULT);
        } else {
            scaleImage = image.getScaledInstance(-1, height, Image.SCALE_DEFAULT);
        }
        return scaleImage;
    }
}