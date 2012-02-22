package dev.boxy.rainbow;

import java.io.*;

import processing.core.*;

public class PreviewRainbowFile extends PApplet {

	public static String[] args;
	
	public static final int DEFAULT_ROWS = 66;
	public static final int DEFAULT_COLS = 300;
	
	public static final int PREVIEW_HEIGHT = 400;
	
	public int video[][];
	public int rows;
	public int cols;
	
	public void setup() {
		try {
			size(640, 480);
			loadDat(args[0]);
//			noLoop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setupRainbow() {
		int colours[] = new int[] {
				color(221, 0, 0),
				color(254, 98, 48),
				color(254, 246, 0),
				color(0, 188, 0),
				color(0, 155, 254),
				color(0, 0, 131),
				color(48, 0, 155)
		};
		
		for (int r = 0; r < rows; r++) {
			int colourIdx = (int) (colours.length * (float) r / rows);
			
			for (int c = 0; c < cols; c++) {
				int colour = color(colours[colourIdx]);
				float red = red(colour) * 2;
				float green = green(colour) * 2;
				float blue = blue(colour) * 2;
				System.out.println("MULTIPLIED");
				video[r][c] = color(red, green, blue);
			}
		}
	}
	
	public void loadDat(String fileName) throws Exception {
		DataInputStream in = new DataInputStream(new FileInputStream(fileName));
		
		cols = in.readShort();
		rows = in.readShort();
		
		video = new int[rows][cols];
		
		int r = 0;
		int c = 0;
		
		try {
			while (true) {
				int red = in.readByte() * 2; // to account for the division by 2 in storage
				int green = in.readByte() * 2;
				int blue = in.readByte() * 2;
				
				video[r][c] = color(red, green, blue);
				
				r++;
				
				if (r == rows) {
					r = 0;
					c++;
				}
			}
		} catch (EOFException e) {
			
		}
	}
	
	public void showLinear() {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				stroke(video[r][c]);
				line(c, r, c+1, r);
			}
		}
	}
	
	public void showCurve() {
		pushMatrix();
		resetMatrix();
		
		translate(width / 2, 3 * height / 4);
		rotate(PI / 2);
		
		for (int c = 0; c < cols; c++) {
			rotate(PI / cols);
			for (int r = 0; r < rows; r++) {
				stroke(video[rows - r - 1][c]);
				strokeWeight(2f);
				line(0, r + PREVIEW_HEIGHT, 1, r + PREVIEW_HEIGHT);
			}
		}
		
		popMatrix();
	}
	
	public void showCurve2() {
		pushMatrix();
		resetMatrix();
		translate(width/2, 3*height/4);
		
		for (int r = rows-1; r >= 0; r--) {
			float radius = (float) r / rows * PREVIEW_HEIGHT;
			
			for (int c = 0; c < cols; c++) {
				float x = (2 * (rows-r)) * (float) -Math.cos(Math.PI * ((double) c / (cols)));
				float y = (2 * (rows-r)) * (float) -Math.sin(Math.PI * ((double) c / (cols)));
//				System.out.printf("(%.1f, %.1f)\n", x, y);
				
				float rad1 = PI * c / cols + PI;
				float rad2 = PI * (c+1) / cols + PI;
				
				noStroke();
				fill(video[rows-r-1][c]);
				
				arc(0, 0, radius, radius, rad1, rad2);
				
//				drawBox(x, y, video[r][c]);
			}
//			delay(50);
		}
		
		popMatrix();
	}
	
	public void drawBox(float x, float y, int color) {
		fill(color);
		noStroke();
		rect(x-1.0f, y-1.0f, 2f, 2f);
		flush();
	}
	
	public void draw() {
		background(128, 128, 128);
		showLinear();
//		showCurve();
		showCurve2();
	}

	public static void main(String args[]) {
		PreviewRainbowFile.args = args;
		PApplet.main(new String[] { "dev.boxy.rainbow.PreviewRainbowFile" });
	}
}
