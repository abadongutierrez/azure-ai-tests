package com.jabaddon.azureai.imageanalysis;

import com.azure.ai.vision.imageanalysis.ImageAnalysisClient;
import com.azure.ai.vision.imageanalysis.ImageAnalysisClientBuilder;
import com.azure.ai.vision.imageanalysis.models.*;
import com.azure.core.credential.KeyCredential;
import com.azure.core.util.BinaryData;
import com.jabaddon.azureai.EnvVarCollector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageAnalysisExample {
    public static void main(String[] args) throws URISyntaxException {
        List<String> vars = EnvVarCollector.collectEnv("VISION_ENDPOINT", "VISION_KEY");
        String endpoint = vars.get(0);
        String key = vars.get(1);

        if (endpoint == null || key == null) {
            System.out.println("Missing environment variable 'VISION_ENDPOINT' or 'VISION_KEY'.");
            System.out.println("Set them before running this sample.");
            System.exit(1);
        }

        // Create a synchronous client using API key authentication
        ImageAnalysisClient client = new ImageAnalysisClientBuilder()
                .endpoint(endpoint)
                .credential(new KeyCredential(key))
                .buildClient();

        URL imageUrl = ImageAnalysisExample.class.getResource("RAG.png");
        ImageAnalysisResult result = client.analyze(
                BinaryData.fromFile(Path.of(imageUrl.toURI())), // imageUrl: the URL of the image to analyze
                Arrays.asList(VisualFeatures.READ), // visualFeatures
                null); // options: There are no options for READ visual feature

        // Print analysis results to the console
        System.out.println("Image analysis results:");
        System.out.println(" Read:");
        java.util.List<TextRectangle> rectangleList = new ArrayList<>();
        for (DetectedTextLine line : result.getRead().getBlocks().get(0).getLines()) {
            System.out.println("   Line: '" + line.getText()
                    + "', Bounding polygon " + line.getBoundingPolygon());
            for (DetectedTextWord word : line.getWords()) {
                List<ImagePoint> boundingPolygon = word.getBoundingPolygon();
                rectangleList.add(new TextRectangle(word.getText(),
                        new Rectangle(
                                boundingPolygon.get(0).getX(),
                                boundingPolygon.get(0).getY(),
                                boundingPolygon.get(1).getX() - boundingPolygon.get(0).getX(),
                                boundingPolygon.get(2).getY() - boundingPolygon.get(0).getY()
                        )
                ));
                System.out.println("     Word: '" + word.getText()
                        + "', Bounding polygon " + boundingPolygon
                        + ", Confidence " + String.format("%.4f", word.getConfidence()));
            }
        }

        JFrame frame = new JFrame("Image Analysis Example");
        ImageDrawer panel = new ImageDrawer(imageUrl.getPath(), rectangleList);

        // Wrap the panel in a JScrollPane for scrolling ability
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        frame.add(scrollPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

class ImageDrawer extends JPanel {
    private final java.util.List<TextRectangle> rectangles;
    private BufferedImage image;

    public ImageDrawer(String imagePath,java.util.List<TextRectangle> rectangles) {
        this.rectangles = rectangles;
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the loaded image
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }

        // Set color for the rectangles
        g.setColor(Color.RED);

        // Draw rectangles
        for (TextRectangle textRectangle : rectangles) {
            Rectangle rectangle = textRectangle.rectangle();
            g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            g.drawString(textRectangle.text(), rectangle.x, rectangle.y);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image != null ? image.getWidth() : 800,
                image != null ? image.getHeight() : 600);
    }
}

record TextRectangle(String text, Rectangle rectangle) {
}