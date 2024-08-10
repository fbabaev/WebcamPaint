import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;
/**
 * Code provided for PS-1
 * Webcam-based drawing
 * Dartmouth CS 10, Winter 2024
 *
 * @author Muhammad Moiz, Firdavskhon Babaev , Dartmouth CS10, Winter 2024.
 */



public class CamPaint extends VideoGUI {
    private char displayMode = 'w'; // 'w': live webcam, 'r': recolored image, 'p': painting
    private RegionFinder finder; // handles the finding
    private Color targetColor; // color of regions of interest (set by mouse press)
    private Color paintColor = new Color(135, 206, 235); // the color to put into the painting from the "brush"
    private BufferedImage painting; // the resulting masterpiece

    public CamPaint() {
        finder = new RegionFinder();
        clearPainting();
    }

    protected void clearPainting() {
        painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }


    @Override
    public void handleImage() {
        if (image == null) return; // Exit if there's no image

        // Display the live webcam image if in webcam mode ('w')
        if (displayMode == 'w') {
            setImage1(image);
        }

        // Process and display the recolored image if in recolored mode ('r')
        else if (displayMode == 'r') {
            if (targetColor != null){
                finder.setImage(image);
                finder.findRegions(targetColor);
                setImage1(finder.getRecoloredImage());
            }
            else if(targetColor == null){
                displayMode = 'w';
            }
        }

        // Update and display the painting if in painting mode ('p')
        else if (displayMode == 'p' && targetColor != null) {
            finder.setImage(image);
            finder.findRegions(targetColor);
            ArrayList<Point> largestRegion = finder.largestRegion();
            if (largestRegion != null) {
                for (Point p : largestRegion) {
                    painting.setRGB(p.x, p.y, paintColor.getRGB());
                }
            }
            setImage1(painting);
        }
    }


    @Override
    public void handleMousePress(int x, int y) {
        if (image != null) {
            targetColor = new Color(image.getRGB(x, y));
        }
    }

    @Override
    public void handleKeyPress(char k) {
        if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
            displayMode = k;
        }
        else if (k == 'c') { // clear
            clearPainting();
        }
        else if (k == 'o') { // save the recolored image
            ImageIOLibrary.saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
        }
        else if (k == 's') { // save the painting
            ImageIOLibrary.saveImage(painting, "pictures/painting.png", "png");
        }
        else {
            System.out.println("unexpected key "+k);
        }
    }

    public static void main(String[] args) {
        new CamPaint();
    }
}
