package dev.boxy.rainbow;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

public class MakeRainbowFile {

	public static final int PIXEL_BUFFER = 0;
	public static final int OUTPUT_HEIGHT = 66;
	public static final int OUTPUT_WIDTH = 240;

	public static void main(String[] args) throws Exception {
		String fileName = args[0];
		String outputFileName = args[1];
		int outputHeight = RainbowUtils.getInt(args, 2, OUTPUT_HEIGHT);
		int outputWidth = RainbowUtils.getInt(args, 3, OUTPUT_WIDTH);
		
		System.out.println("Input: " + fileName);
		System.out.println("Output: " + outputFileName);
		System.out.println("Rows: " + outputHeight);
		System.out.println("Columns: " + outputWidth);
		
		File f = new File(fileName);
		BufferedImage img = ImageIO.read(f);
		int width = img.getWidth();
		int height = img.getHeight();
		
		System.out.println("Image width: " + width);
		System.out.println("Image height: " + height);

		int[][] imgArray = new int[height][width];
		byte[] output = new byte[outputWidth * outputHeight * 3];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				imgArray[y][x] = img.getRGB(x, y);
			}
		}

		// assume outWidth and outHeight < width and height

		float widthRatio = (float) width / outputWidth;
		float heightRatio = (float) height / outputHeight;

		int idx = 0;

		for (int x = 0; x < outputWidth; x++) {
			int x0 = Math.round(widthRatio * x);
			int x1 = Math.round(widthRatio * (x+1));
			for (int y = 0; y < outputHeight; y++) {
				int y0 = Math.round(heightRatio * y);
				int y1 = Math.round(heightRatio * (y+1));
				long r = 0;
				long g = 0;
				long b = 0;
				int count = 0;
				for (int yy = y0; yy <= y1 && yy < height; yy++) {
					for (int xx = x0; xx <= x1 && xx < width; xx++) {
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
				if (y < outputHeight-PIXEL_BUFFER) {
					output[idx++] = (byte) (r / count);
					output[idx++] = (byte) (g / count);
					output[idx++] = (byte) (b / count);
				} else { idx += 3; }

//				System.out.printf("%d %d %d\n", output[idx-3], output[idx-2], output[idx-1]);
//				System.out.printf("%d %d %d\n", r / count, g / count, b / count);
			}
		}

		DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFileName));
		out.writeShort(outputWidth);
		out.writeShort(outputHeight);
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
