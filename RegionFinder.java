import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Code provided for PS-1
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Dartmouth CS 10, Winter 2024
 *
 * @author Muhammad Moiz, Firdavskhon Babaev , Dartmouth CS10, Winter 2024.
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	public BufferedImage getRecoloredImageBrush() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
    public void findRegions(Color targetColor) {
    regions = new ArrayList<>();
    BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

    for (int x = 0; x < image.getWidth(); x++) {
        for (int y = 0; y < image.getHeight(); y++) {
            if (visited.getRGB(x, y) != 0 || !colorMatch(new Color(image.getRGB(x, y)), targetColor)) {
                continue;
            }

            ArrayList<Point> region = new ArrayList<>();
            ArrayList<Point> toVisit = new ArrayList<>();
            toVisit.add(new Point(x, y));

            while (!toVisit.isEmpty()) {
                Point p = toVisit.remove(toVisit.size() - 1);

                if (p.x < 0 || p.x >= image.getWidth() || p.y < 0 || p.y >= image.getHeight()) {
					continue;
				}
                if (visited.getRGB(p.x, p.y) != 0) {
					continue;
				}
                if (!colorMatch(new Color(image.getRGB(p.x, p.y)), targetColor)) {
					continue;
				}

                visited.setRGB(p.x, p.y, 1);
                region.add(p);

                // Add neighboring points
                toVisit.add(new Point(p.x + 1, p.y));
                toVisit.add(new Point(p.x - 1, p.y));
                toVisit.add(new Point(p.x, p.y + 1));
                toVisit.add(new Point(p.x, p.y - 1));
				toVisit.add(new Point(p.x + 1, p.y+1));
				toVisit.add(new Point(p.x - 1, p.y-1));
				toVisit.add(new Point(p.x-1, p.y + 1));
				toVisit.add(new Point(p.x-1, p.y - 1));
            }

            if (region.size() >= minRegion) {
                regions.add(region);
            }
        }
    }
    recolorImage();
}


	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	protected static boolean colorMatch(Color c1, Color c2) {
		int redDiff = Math.abs(c1.getRed() - c2.getRed());
		int greenDiff = Math.abs(c1.getGreen() - c2.getGreen());
		int blueDiff = Math.abs(c1.getBlue() - c2.getBlue());
		return (redDiff < maxColorDiff && greenDiff < maxColorDiff && blueDiff < maxColorDiff);
	}



	/**
         * Returns the largest region detected (if any region has been detected)
         */
	public ArrayList<Point> largestRegion() {
		if (regions == null || regions.isEmpty()) {
			return null;
		}
		ArrayList<Point> largest = regions.get(0);
		for (ArrayList<Point> region : regions) {
			if (region.size() > largest.size()) {
				largest = region;
			}
		}
		return largest;
	}

	/**
	 * Sets recoloredImage to be a copy of image,
	 * but with each region a uniform random color,
	 * so we can see where they are
	 */

	public void recolorImage() {
		recoloredImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int rgb = image.getRGB(x, y);
				recoloredImage.setRGB(x, y, rgb);
			}
		}

		for (ArrayList<Point> region : regions) {
			int color = new Color((float)Math.random(), (float)Math.random(), (float)Math.random()).getRGB();
			for (Point p : region) {
				recoloredImage.setRGB(p.x, p.y, color);
			}
		}
	}
}
