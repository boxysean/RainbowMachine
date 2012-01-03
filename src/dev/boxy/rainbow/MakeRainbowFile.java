package dev.boxy.rainbow;
import javax.imageio.*;
import java.io.*;
import java.awt.image.*;

public class MakeRainbowFile {

	public static final int PIXEL_BUFFER = 20;
	public static final int OUTPUT_HEIGHT = 78;
	public static final int OUTPUT_WIDTH = 300;

	public static void main(String[] args) throws Exception {
		File f = new File(args[0]);
		System.out.println(f.getAbsolutePath());
		BufferedImage img = ImageIO.read(f);
		int width = img.getWidth();
		int height = img.getHeight();

		int[][] imgArray = new int[height][width];
		byte[] output = new byte[OUTPUT_WIDTH * OUTPUT_HEIGHT * 3];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				imgArray[y][x] = img.getRGB(x, y);
			}
		}

		// assume outWidth and outHeight < width and height

		float widthRatio = (float) width / OUTPUT_WIDTH;
		float heightRatio = (float) height / OUTPUT_HEIGHT;

		int idx = 0;

		for (int x = 0; x < OUTPUT_WIDTH; x++) {
			int x0 = Math.round(widthRatio * x);
			int x1 = Math.round(widthRatio * (x+1));
			for (int y = 0; y < OUTPUT_HEIGHT; y++) {
				int y0 = Math.round(heightRatio * y);
				int y1 = Math.round(heightRatio * (y+1));
				long r = 0;
				long g = 0;
				long b = 0;
				int count = 0;
				for (int yy = y0; yy < y1; yy++) {
					for (int xx = x0; xx < x1; xx++) {
						int rr = red(imgArray[yy][xx]) / 2;
						int gg = green(imgArray[yy][xx]) / 2;
						int bb = blue(imgArray[yy][xx]) / 2;
//						System.out.printf("%d %d %d\n", rr, gg, bb);

						r += rr;
						g += gg;
						b += bb;
						count++;
					}
				}
				if (y < OUTPUT_HEIGHT-PIXEL_BUFFER) {
					output[idx++] = (byte) (r / count);
					output[idx++] = (byte) (g / count);
					output[idx++] = (byte) (b / count);
				} else { idx += 3; }

//				System.out.printf("%d %d %d\n", output[idx-3], output[idx-2], output[idx-1]);
//				System.out.printf("%d %d %d\n", r / count, g / count, b / count);
			}
		}

		OutputStream out = new BufferedOutputStream(new FileOutputStream(args[1]));
		out.write(output, 0, output.length);
		out.flush();
		out.close();
	}

	public static int red(int x) {
		return (x >> 16) & 0xFF;
	}

	public static int green(int x) {
		return (x >> 8) & 0xFF;
	}

	public static int blue(int x) {
		return (x >> 0) & 0xFF;
	}

}
