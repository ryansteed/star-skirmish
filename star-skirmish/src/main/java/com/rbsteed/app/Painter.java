/**
 * @author Ryan Steed
 * @version 1.0
 * @since 2018-12-06
 */

package com.rbsteed.app;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Color;

/**
 * Paints a given pixel map based on a 2d array. (Ugh. This was a tedious implementation).
 */
abstract class Painter {
    private Graphics2D g2;
    private Point origin;
    private double pixelWidth;
    private double pixelHeight;
    private double xShift;
    private double yShift;
    private char[][] pmap;
    private int height;
    private int width;
    private Color colorOverride;
    
    public Painter(int width, int height) {
        this.height = height;
        this.width = width;
        pmap = getPixelMap();
    }

    /**
     * Paint this object based on the 2d color coded pixel map.
     * @param g The graphics given for this object (different at each render).
     * @param givenOrigin The proper origin of the object to be painted.
     * @param size The size of the object to be painted.
     */
    protected void paint(Graphics g, Point givenOrigin, Dimension size) {
        this.g2 = (Graphics2D) g;
        origin = givenOrigin;
        // System.out.println(size);
        pixelWidth = size.getWidth() / width;
        pixelHeight = size.getHeight() / height;
        xShift = (size.getWidth()  - pixelWidth * width) / 2;
        yShift = (size.getHeight() - pixelHeight * height) / 2;
        // System.out.println(xShift);
        // System.out.println(yShift);

        // then add pixels according to design
        for (int x=0; x<pmap.length; x++) {
            for (int y=0; y<pmap[x].length; y++) {
                if (!(pmap[x][y] == '\u0000' || pmap[x][y] == 'e')) {
                    // System.out.println("Painting pixel with color "+pmap[x][y]);
                    Color color = getColor(pmap[x][y]);
                    paintPixel(new Point(x, y), color);
                }
            }
        }
        // System.out.println(pixelWidth);
        // System.out.println(pixelSize);
        return;
    }

    /**
     * Paint a pixel by constructing a rectangle at the given location.
     * Location is determined by mapping a grid onto the available space.
     * @param loc The proper location within the grid.
     * @param color The color of the pixel.
     */
    private void paintPixel(Point loc, Color color) {
        g2.setColor(color);

        Rectangle2D.Double rect = new Rectangle2D.Double(origin.x+xShift+loc.x*pixelWidth, origin.y+yShift+loc.y*pixelHeight, pixelWidth, pixelHeight);
        g2.fill(rect);
        // g2.fillRect(origin.x + loc.x * pixelSize, origin.y + loc.y * pixelSize, pixelSize, pixelSize);
    }

    /**
     * Generate the pixel map from half of a given design by coping the first row and mirroring
     * the rest.
     * @return A 2d array containing characters representing pixel color. Empty pixels are 'e' or empty.
     */
    private char[][] getPixelMap() {
        char[][] pm = new char[width][height];
        char[][] design = getDesign();
        
        for (int i=0; i<design.length; i++) {
            pm[width / 2][i] = design[i][0];
        }
        for (int i=-1; i<2; i+=2) {
            // i is either +1 or -1
            for (int row=0; row<design.length; row++) {
                for (int col=1; col<design[row].length; col++) {
                    // System.out.println("col length " + design[row].length);
                    // System.out.printf("x: %d, y: %d\n", mirrorCol(i, col), row);
                    char test = design[row][col];
                    pm[mirrorCol(i,col)][row] = test;
                }
            }
        }
        return pm;
    }

    protected void setColor(Color color) {
        colorOverride = color;
    }

    /**
     * If there is no color override, get the proper color given a character color code
     * from the pixel map.
     * @param c The character color code.
     * @return The correct color.
     * @see Color
     */
    protected Color getColor(char c) {
        if (colorOverride == null) {
            Color color = Color.white;
            switch (c) {
                case 'b':
                    color = Color.darkGray;
                    break;
                case 'r':
                    color = Color.red;
                    break;
                case 'g':
                    color = Color.gray;
                    break;
                case 'l':
                    color = Color.blue;
                    break;
                case 'o':
                    color = Color.orange;
                    break;
            }
            return color;
        }
        return colorOverride;
    }

    /**
     * A method for getting the right design for this object. Extended by various painters.
     * @return A 2d array representing half the pixel map. The other half is produced by mirroring.
     */
    protected abstract char[][] getDesign();

    /**
     * Produce the mirrored column number.
     * @param i Which side is being mapped (i==1 || i==-1).
     * @param x The length of the 2d array.
     * @return The correct mirrored column number.
     */
    private int mirrorCol(int i, int x) {
        return i*x+width/2;
    }
}

/**
 * Paints the player's ship - an X-Wing!
 */
class ShipPainter extends Painter {
    
    public ShipPainter() {
        super(23, 25);
    }

    protected char[][] getDesign() {
        // adapted from
        // https://www.google.com/search?biw=1920&bih=978&tbm=isch&sa=1&ei=zOgGXLW5BsGZ_QaP8qyADg&q=x+wing+pixel+art+low+res&oq=x+wing+pixel+art+low+res&gs_l=img.3...2151.2151..2325...0.0..0.52.52.1......1....1..gws-wiz-img.EXfb2p39vuw#imgrc=coDiSVR4yuwPXM:
        char[][] design = {
            { 'b' }, 
            { 'r', 'b' }, 
            { 'w', 'r', 'b' }, 
            { 'w', 'w', 'b' }, 
            { 'w', 'w', 'b' },
            { 'w', 'w', 'b' }, 
            { 'w', 'w', 'b' }, 
            { 'w', 'w', 'b' }, 
            { 'w', 'w', 'b' }, 
            { 'b', 'w', 'b' }, // 10
            { 'g', 'b', 'b', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'g'}, 
            { 'g', 'b', 'g', 'b', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'g'}, 
            { 'g', 'b', 'g', 'b', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'g' },
            { 'g', 'b', 'g', 'b', 'e', 'e', 'e', 'e', 'b', 'b', 'b', 'b' },
            { 'l', 'w', 'b', 'b', 'b', 'b', 'b', 'b', 'r', 'w', 'r', 'b' }, // 15
            { 'g', 'g', 'w', 'b', 'w', 'w', 'w', 'w', 'w', 'w', 'w', 'b' },
            { 'g', 'b', 'w', 'b', 'w', 'w', 'w', 'w', 'w', 'w', 'w', 'b' },
            { 'w', 'w', 'b', 'w', 'w', 'w', 'w', 'w', 'b', 'b', 'b', 'b' },
            { 'w', 'w', 'b', 'b', 'w', 'w', 'b', 'b', 'e', 'e', 'e', 'g' },
            { 'b', 'b', 'w', 'w', 'b', 'b', 'w', 'w', 'b', 'b', 'b', 'b' }, // 20
            { 'w', 'w', 'g', 'b', 'w', 'w', 'w', 'w', 'r', 'w', 'r', 'b' },
            { 'w', 'g', 'r', 'g', 'b', 'w', 'w', 'w', 'w', 'w', 'w', 'b' },
            { 'w', 'g', 'r', 'g', 'b', 'b', 'b', 'b', 'w', 'w', 'w', 'b' },
            { 'w', 'w', 'g', 'b', 'b', 'e', 'e', 'e', 'b', 'b', 'b', 'b' }, 
            { 'b', 'b' } 
        };
        return design;
    }
}

/**
 * Paints the alien's ship - a TIE fighter!
 */
class AlienPainter extends Painter {

    public AlienPainter() {
        super(17, 16);
    }

    protected char[][] getDesign() {
        //adapted from
        // https://www.google.com/search?biw=1280&bih=698&tbm=isch&sa=1&ei=z-gGXKnyIYae_Qacs5KIDg&q=tie+fighter+pixel+art+low+res&oq=tie+fighter+pixel+art+low+res&gs_l=img.3...8137365.8139387..8139825...2.0..0.55.642.13......1....1..gws-wiz-img.RNdTq533TS4#imgrc=hbrNhX3tnWWptM:
        char[][] design = {
            {'e','e','e','e','e','b','b'},
            {'e','e','e','e','e','g','b','b'},
            {'e','e','e','e','e','e','g','b','b'},
            {'e','e','e','e','e','e','e','b','b'},
            {'b','b','b','e','e','e','e','b','w'}, 
            {'b','w','w','b','e','e','e','b','w'}, // 5
            {'b','b','g','w','b','e','g','b','w'},
            {'w','w','b','w','b','g','g','b','b'},
            {'g','w','b','b','b','b','b','b','b'},
            {'w','w','b','w','b','g','g','b','b'},
            {'b','b','g','w','b','e','g','b','w'}, // 10
            {'g','w','w','g','e','e','e','b','w'},
            {'b','b','b','e','e','e','e','b','w'},
            {'e','e','e','e','e','e','g','b','b'},
            {'e','e','e','e','e','g','b','b'},
            { 'e', 'e', 'e', 'e', 'e', 'b', 'b'}
        };
        return design;
    }
}

/** 
 * Paints a background star.
 */
class StarPainter extends Painter {
    public StarPainter() {
        super(7, 7);
    }

    protected char[][] getDesign() {
        char[][] design = { 
            { 'b', 'e', 'e', 'e'},
            { 'g', 'e', 'e', 'e'},
            { 'w', 'g', 'e', 'e'},
            { 'w', 'w', 'b', 'g'},
            { 'w', 'g', 'e', 'e'},
            { 'g', 'e', 'e', 'e'},
            { 'b', 'e', 'e', 'e'},
        };
        return design;
    }
}

class MissilePainter extends Painter {
    public MissilePainter() {
        super(3,10);
    }
    
    protected char[][] getDesign() {
        char[][] design = { 
            { 'r', 'e'},
            { 'o', 'r'},
            { 'o', 'r'},
            { 'o', 'r'},
            { 'o', 'r'},
            { 'o', 'r'},
            { 'o', 'r'},
            { 'o', 'r'},
            { 'o', 'r'},
            { 'r', 'e'},
        };
        return design;
    }
}