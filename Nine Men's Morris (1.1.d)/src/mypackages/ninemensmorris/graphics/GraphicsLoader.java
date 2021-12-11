package mypackages.ninemensmorris.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GraphicsLoader {
	
	//converts path into BufferedImage
	
	public BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(GraphicsLoader.class.getResource(path));
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

}
