package dev.boxy.rainbow;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

public class MakeInverseRainbowFile {

	public static final int PIXEL_BUFFER = 0;
	public static final int OUTPUT_HEIGHT = 66;
	public static final int OUTPUT_WIDTH = 300;
	
	public static int[] dr = { 0, 1, 1, 1, 0, -1, -1, -1, -2, -2, -2, -1, 0, 1, 2, 2, 2, 1, 0, -1 };
	public static int[] dc = { -1, -1, 0, 1, 1, 1, 0, -1, -1, 0, 1, 2, 2, 2, 1, 0, -1, -2, -2, -2 };

	public static void main(String[] args) throws Exception {
		String fileName = args[0];
		String outputFileName = args[1];
		int outputHeight = RainbowUtils.getInt(args, 2, OUTPUT_HEIGHT);
		int outputWidth = RainbowUtils.getInt(args, 3, OUTPUT_WIDTH);

		BufferedImage img = ImageIO.read(new File(fileName));
		int width = img.getWidth();
		int height = img.getHeight();

		int[][] imgArray1 = new int[height][width];
		byte[] output = new byte[OUTPUT_WIDTH * OUTPUT_HEIGHT * 3];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				imgArray1[y][x] = img.getRGB(x, y);
			}
		}
		
		// do something in here to make a second real imgArray drawing from the first one
		
		int[][] imgArray2 = new int[height][width];
		boolean[][] v = new boolean[height][width];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double translate = width / 2.0;
				int c = (int) Math.round(width / Math.PI * Math.atan2(height-y-1, width-x-1 - translate));
				int r = (int) Math.round((height-y-1) / Math.sin(Math.PI * c / width));
				
				if (0 <= r && r < height && 0 <= c && c < width) {
					imgArray2[height-r-1][c] = imgArray1[y][x];
					v[height-r-1][c] = true;
				} else {
//					System.out.printf("x,y: %3d,%3d / r,c: %3d,%3d\n", x, y, r, c);
				}
			}
		}
		
		// do some averaging...
		
		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				if (!v[r][c]) {
					int count = 0;
					double red = 0;
					double green = 0;
					double blue = 0;
					
					for (int i = 0; i < dr.length; i++) {
						int rr = r + dr[i];
						int cc = c + dc[i];
						if (0 <= rr && rr < height && 0 <= cc && cc < width && v[rr][cc]) {
							red += red(imgArray2[rr][cc]);
							green += green(imgArray2[rr][cc]);
							blue += blue(imgArray2[rr][cc]);
							count++;
						}
					}
					
					if (count > 0) {
						imgArray2[r][c] = colour((int) (red / count), (int) (green / count), (int) (blue / count));
					}
				}
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
						int rr = red(imgArray2[yy][xx]) / 2;
						int gg = green(imgArray2[yy][xx]) / 2;
						int bb = blue(imgArray2[yy][xx]) / 2;
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

		DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFileName));
		out.writeShort(outputWidth);
		out.writeShort(outputHeight);
		out.write(output, 0, output.length);
		out.flush();
		out.close();
	}

	public static int colour(int r, int g, int b) {
		return (r << 16) | (g << 8) | b;
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
