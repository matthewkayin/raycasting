package com.matthewkayin.ray;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

public class Texture{

    private int[] pixels;
    private int width;
    private int height;
    private BufferedImage sauce;

    public Texture(String path){

        BufferedImage source = null;
        //BufferedImage sauce;

        try{

            source = ImageIO.read(new File(path));

        }catch(IOException e){

            e.printStackTrace();
            System.exit(0);
        }

        width = source.getWidth();
        height = source.getHeight();
        sauce = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        sauce.createGraphics().drawImage(source, 0, 0, width, height, null);
        pixels = ((DataBufferInt)sauce.getRaster().getDataBuffer()).getData();
    }

    public int[] getPixels(){

        return pixels;
    }

    public int[] getARGB(int x, int y){

        int index = (x + (y * width));
        int argb[] = new int[4];
        argb[0] = (pixels[index] >> 24) & 0xFF;
        argb[1] = (pixels[index] >> 16) & 0xFF;
        argb[2] = (pixels[index] >> 8) & 0xFF;
        argb[3] = (pixels[index]) & 0xFF;

        return argb;
    }

    public int[] getSlice(int xoffset, int yoffset, int wdt, int ht){

        int rowsToUse[] = new int[wdt];
        for(int i = 0; i < wdt; i++){

            rowsToUse[i] = (xoffset + i);
        }
        int slice[] = new int[wdt * ht];
        double halfyoff = yoffset / 2.0;

        //for(int x = 0; x < 4; x++){
        for(int x = 0; x < wdt; x++){

            for(int y = 0; y < ht; y++){

                int texX = (int)Math.round((x / (double)wdt) * (width - 1));
                texX = (texX + rowsToUse[x]) % width;
                int texY = (int)Math.round(( (y + halfyoff) / (double)(ht + yoffset) ) * (height - 1));

                if(texX + (texY * width) < 0){

                    System.out.println(texX + ", " + texY);
                }
                slice[x + (y * wdt)] = pixels[texX + (texY * width)];
            }
        }

        return slice;
    }

    public int getFloorPixel(int x, int y){

        return pixels[x + (y * width)];
    }

    public int getWidth(){

        return width;
    }

    public int getHeight(){

        return height;
    }
}
