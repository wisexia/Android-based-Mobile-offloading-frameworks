package com.xiahui.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class KMeansCluster {
	
	private final int[] RGB_INIT = {0xFFFFFF, 0x000000, 0xEE9611, 0x808080, 0xFFFF00, 0xFFFFFF};
    private final int[] RGB_FILL = {0xFFFFFF, 0x000000, 0x0000FF, 0x808080};
	
    public BufferedImage getResultGraphics(BufferedImage image, int k, int m) {
    	if(image == null) {
    		return null;
    	}
    	
    	int imageWidth = image.getWidth();
    	int imageHeigth = image.getHeight();
    	
    	int[][] data = new int[imageWidth][imageHeigth];
    	for(int i=0; i<imageWidth; i++) {
    		for(int j=0; j<imageHeigth; j++) {
    			data[i][j] = image.getRGB(i, j);
    		}
    	}
    	int[][] result = ImageSegmentation(data, k, m);
    	for(int i=0; i<imageWidth; i++) {
    		for(int j=0; j<imageHeigth; j++) {
    			int category = result[i][j];
    			image.setRGB(i, j, RGB_FILL[category]);
    		}
    	}
    	
    	return image;
    }
    
	private int[][] ImageSegmentation(int[][] data, int k, int m) {
		
		int imageHeight = data[0].length;
        int imageWidth = data.length;
        int[][] classify = new int[imageWidth][imageHeight];

        ArrayList<Integer> dataCenter = new ArrayList<>();
        ArrayList<ArrayList<Integer>> cluster = new ArrayList<>();
		
        for (int i = 0; i < k; i++) {
            Integer pixel = new Integer(RGB_INIT[i]);
            dataCenter.add(pixel);
        }
		
        for (int i = 0; i < k; i++) {
            ArrayList<Integer> pixels = new ArrayList<>();
            pixels.add(dataCenter.get(i));
            cluster.add(pixels);
        }
		
        for (int kk = 0; kk < m; kk++) {

            for (int i = 0; i < k; i++) {
                if (cluster.get(i) != null) {
                    cluster.get(i).clear();
                }
            }

            for (int i = 0; i < imageWidth; i++) {
                for (int j = 0; j < imageHeight; j++) {
                    Integer pixel = new Integer(data[i][j]);

                    int category = 0;
                    double minDistance = Double.POSITIVE_INFINITY;
                    for (int ii = 0; ii < k; ii++) {
                        Integer pixelCenter = dataCenter.get(ii);
                        int xr = (pixel & 0xFF0000) >> 16;
                        int xg = (pixel & 0x00FF00) >> 8;
                        int xb = (pixel & 0x0000FF);

                        int yr = (pixelCenter & 0xFF0000) >> 16;
                        int yg = (pixelCenter & 0x00FF00) >> 8;
                        int yb = (pixelCenter & 0x0000FF);

                        double distance = Math.sqrt((xr - yr) * (xr - yr) + (xg - yg) * (xg - yg) + (xb - yb) * (xb - yb));
                        if (minDistance > distance) {
                            category = ii;
                            minDistance = distance;
                        }
                    }

                    classify[i][j] = category;

                    cluster.get(category).add(pixel);
                }
            }


            for (int i = 0; i < k; i++) {
                long rSum = 0;
                long gSum = 0;
                long bSum = 0;
                java.util.ArrayList<Integer> rowPixel = cluster.get(i);
                for (int j = 0; j < rowPixel.size(); j++) {
                    rSum += ((rowPixel.get(j) & 0xFF0000) >> 16);
                    gSum += ((rowPixel.get(j) & 0x00FF00) >> 8);
                    bSum += rowPixel.get(j) & 0x0000FF;
                }

                int r = (int) rSum / rowPixel.size();
                int g = (int) gSum / rowPixel.size();
                int b = (int) bSum / rowPixel.size();
                dataCenter.set(i, new Integer(((r << 16) & 0xFF0000) + ((g << 8) & 0x00FFF00) + (b & 0x0000FF)));
            }
        }

        return classify;
	}
}
