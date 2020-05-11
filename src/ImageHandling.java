import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import static java.awt.MultipleGradientPaint.CycleMethod.NO_CYCLE;

/**
 *  ImageHandling.java
 *
 *  ImageHandling performs various tasks for a given canvas, such as creating backup images, applying filters and
 *  rotations, the flood fill algorithm, text methods, etc.
 *
 *  @author Jan Øyvind Kruse
 *  @version 1.0
 */

public class ImageHandling {

    Paint p;

    public ImageHandling(Paint View) {
        p = View;  // Tell ImageHandling about Paint.java
    }


    void makeBackupImage(BufferedImage i, Canvas c) {

        // Makes a backup of the image on the canvas, so undo will work.

        c.edited = true;

        c.zImage = new BufferedImage( i.getWidth(), i.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = c.zImage.getGraphics();

        g.drawImage(i,0,0, null);
        g.dispose();
    }


    void restoreBackupImage(Canvas c) {

        //  The Undo Function, restores a previously stored backup image

        c.temp = new BufferedImage( c.getSize().width, c.getSize().height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = c.temp.getGraphics();

        g.drawImage(c.image,0,0, null);
        g.dispose();

        c.setSize( c.zImage.getWidth(), c.zImage.getHeight() );
        c.setPreferredSize( new Dimension( c.zImage.getWidth(), c.zImage.getHeight() ) ) ;
        resetImage(c);

        c.g2d.drawImage(c.zImage,0,0,null);
        c.revalidate();

        makeBackupImage(c.temp, c);
        // switch the images so undo works as a one-level undo/redo.
    }


    void makePreviewImage(Canvas c) {

        //  Makes a buffer image that will get redrawn when the user drags the mouse around,
        //  for example with the Line Tool. Slightly different from makeBackupImage, since
        //  there are two "backup" images involved.

        makeBackupImage(c.image, c);

        // Preview Step 1 ) Copy the existing image into a temporary backup buffer image, bImage ...

        c.bImage = new BufferedImage( c.getSize().width, c.getSize().height, BufferedImage.TYPE_4BYTE_ABGR );
        Graphics g = c.bImage.getGraphics();

        g.drawImage(c.image,0,0, null);
        g.dispose();
    }


    void executePreviewDraw(int cap, int join, int flag, Canvas c) {

        //  Preview Step 2) draw the temporary bImage on g2d, then the line on g2d
        //                      ... rinse and repeat ...

        c.g2d.drawImage(c.bImage,0,0,null);
        c.setUpStroke(p.getLineSize(), cap, join, p.getSelectedForeground());

        if(flag == 0) {

            c.x1 = c.oldX; c.y1 = c.oldY; c.x2 = c.currentX - c.oldX; c.y2 = c.currentY - c.oldY;

            if(c.oldX > c.currentX) {
                    c.x1 = c.currentX;
                    c.x2 = c.oldX - c.currentX;
            }
            if(c.oldY > c.currentY) {
                c.y1 = c.currentY;
                c.y2 = c.oldY - c.currentY;
            }

            c.minimum = c.x2; // Used for making rectangles square when Shift is held down, etc.

            if(c.y2 < c.x2)
                c.minimum = c.y2;

            if(c.shiftKeyDown) {
                c.x1 = c.oldX; c.y1 = c.oldY; c.x2 = c.minimum; c.y2 = c.minimum;

                if(c.oldX > c.currentX)
                    c.x1 = c.oldX - c.minimum;

                if(c.oldY > c.currentY)
                    c.y1 = c.oldY - c.minimum;
            }
        }
    }


    //  **  Flood Fill algorithm with help from Rosetta Code:
    //        https://rosettacode.org/wiki/Bitmap/Flood_fill#Java

    public void floodFill(BufferedImage bImage, Point node, Color targetColor, Color replacementColor, Canvas c) {

        int width = c.bImage.getWidth();
        int height = c.bImage.getHeight();
        int target = targetColor.getRGB();
        int replacement = replacementColor.getRGB();

        if (target != replacement) {
            Deque<Point> queue = new LinkedList<Point>();

            do {
                int x = node.x;
                int y = node.y;
                while (x > 0 && c.bImage.getRGB(x - 1, y) == target) {
                    x--;
                }
                boolean spanUp = false;
                boolean spanDown = false;

                while (x < width && c.bImage.getRGB(x, y) == target) {
                    c.bImage.setRGB(x, y, replacement);

                    if (!spanUp && y > 0 && c.bImage.getRGB(x, y - 1) == target) {
                        queue.add(new Point(x, y - 1));
                        spanUp = true;
                    } else if (spanUp && y > 0 && c.bImage.getRGB(x, y - 1) != target) {
                        spanUp = false;
                    }
                    if (!spanDown && y < height - 1 && c.bImage.getRGB(x, y + 1) == target) {
                        queue.add(new Point(x, y + 1));
                        spanDown = true;
                    } else if (spanDown && y < height - 1 && c.bImage.getRGB(x, y + 1) != target) {
                        spanDown = false;
                    }
                    x++;
                }
            } while ((node = queue.pollFirst()) != null);
        }
    }


    void readyCanvas(Canvas c) {

        //  Called before executing resize, rotate, flip etc.

        if(c.getPhase() != 0) {
            c.setPhase(0);
            executePreviewDraw(1,2,1, c);
        }

        makeBackupImage(c.image, c);
        c.selectionActive = 0;
    }


    void resizeCanvas(int newWidth, int newHeight, Canvas c) {

        readyCanvas(c);

        c.temp = new BufferedImage( newWidth, newHeight, BufferedImage.TYPE_4BYTE_ABGR );
        Graphics2D g = c.temp.createGraphics();

        g.drawImage(c.image,0,0, newWidth, newHeight,null);
        g.dispose();

        c.setSize( newWidth, newHeight );
        c.setPreferredSize( new Dimension( newWidth, newHeight ) ) ;
        resetImage(c);

        c.g2d.drawImage(c.temp, 0,0, null);
        c.revalidate();
    }


    void rotateCanvas(int i, Canvas c) {

        readyCanvas(c);

        int oldWidth = c.getSize().width, oldHeight = c.getSize().height, newWidth = oldWidth, newHeight = oldHeight;

        if(i != 180) {
            newWidth = oldHeight;
            newHeight = oldWidth;
        }

        double rotationRequired = Math.toRadians(i);

        c.temp = new BufferedImage( newWidth, newHeight, BufferedImage.TYPE_4BYTE_ABGR );
        Graphics2D g = c.temp.createGraphics();

        if(i == 90) {
            g.translate((newWidth - oldWidth)/2, (newWidth - oldWidth)/2);  // works for 90CW
        }

        if(i == 270) {
            g.translate((oldWidth - newWidth)/2, (oldWidth - newWidth)/2);  // works for 90CCW
        }

        g.rotate(rotationRequired, newWidth / 2, newHeight / 2);
        g.drawRenderedImage(c.image, null);

        c.setSize( newWidth, newHeight );
        c.setPreferredSize( new Dimension( newWidth, newHeight ) ) ;

        resetImage(c);

        c.g2d.drawImage(c.temp,0,0,null);

        c.revalidate();
    }


    void flipCanvas(int i, Canvas c) {

        readyCanvas(c);

        if(i == 0) {

            //  FLIP CANVAS HORIZONTAL
            c.g2d.drawImage(c.image, 0 + c.image.getWidth(), 0, -c.image.getWidth(), c.image.getHeight(), null);

        }
        else {

            //  FLIP CANVAS VERTICAL

            c.temp = new BufferedImage( c.getSize().width, c.getSize().height, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics g = c.temp.getGraphics();

            g.drawImage(c.image, 0, c.image.getHeight( c ), c.image.getWidth( c ), 0,
                    0, 0, c.image.getWidth( c ), c.image.getHeight( c ), null);
            g.dispose();
            c.g2d.drawImage(c.temp,0,0,null);
        }
        c.repaint();
    }


    void adjustCanvas(int a, Canvas c) {

        readyCanvas(c);

        c.temp = new BufferedImage( c.getSize().width, c.getSize().height, BufferedImage.TYPE_4BYTE_ABGR);

        for (int x = 0; x < c.image.getWidth(); x++) {
            for (int y = 0; y < c.image.getHeight(); y++) {
                int rgba = c.image.getRGB(x, y);
                Color col = new Color(rgba, true);

                if(a == 0) { //  BLACK & WHITE

                    int value = (col.getRed() + col.getGreen() + col.getBlue()) / 3;
                    col = new Color(value, value, value);

                }
                else { //  INVERT

                    col = new Color(255 - col.getRed(),
                            255 - col.getGreen(),
                            255 - col.getBlue());
                }

                c.temp.setRGB(x, y, col.getRGB());
            }
        }

        c.g2d.drawImage(c.temp,0,0,null);
        c.repaint();
    }


    //  **  The Blur and Sharpen filters were made with help from this article:
    //        http://www.javaworld.com/article/2076764/java-se/image-processing-with-java-2d.html?page=2

    void blurCanvas(Canvas c) {

        readyCanvas(c);

        float ninth = 1.0f / 9.0f;
        float[] blurKernel = {
                 ninth, ninth, ninth,
                 ninth, ninth, ninth,
                 ninth, ninth, ninth
               };

        BufferedImageOp blur = new ConvolveOp(new Kernel(3, 3, blurKernel));
        BufferedImage temp = blur.filter(c.image, null);

        c.g2d.drawImage(temp,0,0,null);
        c.repaint();
    }


    void sharpenCanvas(Canvas c) {

        readyCanvas(c);

        float[] sharpKernel = {
                0.0f, -1.0f, 0.0f,
                -1.0f, 5.0f, -1.0f,
                0.0f, -1.0f, 0.0f
        };

        BufferedImageOp sharpen = new ConvolveOp(
                new Kernel(3, 3, sharpKernel),
                ConvolveOp.EDGE_NO_OP, null);

        BufferedImage temp = sharpen.filter(c.image, null);

        c.g2d.drawImage(temp,0,0,null);
        c.repaint();
    }


    void cropCanvas(Canvas c) {

        //  Crops the canvas if there is a selection active.

        if(c.selectionActive == 1) {

            c.timer.stop();  //  Stop the ants
            c.selectionActive = 0;

            c.sImage = c.bImage.getSubimage(c.x1, c.y1, c.x2, c.y2);

            c.setSize( c.x2, c.y2 );
            c.setPreferredSize( new Dimension( c.x2, c.y2 ));
            resetImage(c);

            c.g2d.drawImage(c.sImage,0,0,null);

            c.revalidate();
        }
    }


    Color getPixelColor(int x, int y, Canvas c) {

        int cl = c.image.getRGB(x, y);

        int  red = (cl & 0x00ff0000) >> 16;
        int  green = (cl & 0x0000ff00) >> 8;
        int  blue = cl & 0x000000ff;

        return new Color(red, green, blue);
    }


    public void clear(Canvas c) {
        c.g2d.setPaint(Color.white);
        c.g2d.fillRect(0, 0, c.getSize().width, c.getSize().height);
        c.g2d.setPaint(Color.black);
        c.repaint();
    }


    void resetImage(Canvas c) {
        c.image = new BufferedImage( c.getSize().width, c.getSize().height, BufferedImage.TYPE_INT_ARGB );
        c.g2d = (Graphics2D)c.image.getGraphics();
        clear(c);
    }

    //  ==============================================================================================================

    void textBounds(int x, int y, Canvas c) {

        //  Used for centering the text on the mouse cursor when it is being dragged.

        int tw1, tw0 = 0, th1, th0 = 0;

        initFont(c);

        for (String line : c.text.split("\n")) {

            if(line.equals("")) line = " ";  // Make sure TextLayout will work.

            AffineTransform affinetransform = new AffineTransform();
            FontRenderContext frc = new FontRenderContext(affinetransform,true,true);

            tw1 = (int)(c.font.getStringBounds(line, frc).getWidth());
            th0 += (int)(c.font.getStringBounds(line, frc).getHeight());

            if(tw1 > tw0) tw0 = tw1;
        }

        c.xa = x - (tw0 / 2);
        c.ya = y - (th0 / 2);
    }


    void drawString(String text, int x, int y, Canvas c) {

        //  Draws a single or multi-line string on the canvas, with or without effects

        boolean stroke = p.oF.Effects[0].isSelected(), shadow = p.oF.Effects[1].isSelected();
        int lineSize = p.getLineSize();


        for (String line : text.split("\n")) {

            if(line.equals("")) line = " ";  // Make sure TextLayout will work.

            if(stroke || shadow ) {

                // ** Stroke / Shadow

                FontRenderContext frc = new FontRenderContext(null,false,false);
                TextLayout tl = new TextLayout(line, c.font, frc);

                AffineTransform textAt = new AffineTransform();
                textAt.translate( x, y += c.g2d.getFontMetrics().getHeight() );

                if(stroke) {

                    // ** Stroke

                    Shape outline = tl.getOutline( textAt );

                    c.g2d.setColor( p.getSelectedBackground(1) );
                    BasicStroke wideStroke = new BasicStroke(lineSize, 1, 2);

                    c.g2d.setStroke(wideStroke);
                    c.g2d.draw(outline);

                    c.g2d.setPaint( p.getSelectedForeground() );
                    c.g2d.fill( outline );
                }
                else {

                    //  ** Shadow

                    textAt.translate( lineSize, lineSize );
                    Shape outline = tl.getOutline( textAt );

                    c.g2d.setPaint( p.getSelectedBackground(1) );
                    c.g2d.fill( outline );

                    textAt.translate( -lineSize, -lineSize );
                    outline = tl.getOutline( textAt );

                    c.g2d.setPaint( p.getSelectedForeground() );
                    c.g2d.fill( outline );
                }
            }
            else {

                // ** No effect
                c.g2d.drawString(line, x, y += c.g2d.getFontMetrics().getHeight());
            }
        }
    }


    void initFont(Canvas c) {

        // Set up the font, for use in other functions.

        int style = 0;

        if( p.oF.Styles[0].isSelected() )
            style += 1;

        if( p.oF.Styles[1].isSelected() )
            style += 2;

        c.font = new Font( (String)p.oF.fontList.getSelectedItem(), style,
                Integer.parseInt( (String)p.oF.fontSize.getSelectedItem() ));

        if(p.oF.Styles[2].isSelected() ) {
            Map attributes = c.font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            c.g2d.setFont(c.font.deriveFont(attributes));
        }
        else {
            c.g2d.setFont(c.font);
        }
    }


    void textFunction(int i, Canvas c) {

        //  The entry function for drawing text on the canvas

        String s = c.text;

        executePreviewDraw(1, 2, 0, c);

        initFont(c);

        if(i == 2) {
            c.phase = 0;
            c.text = "";   // end the Text Function, since another tool has been selected
        }
        else s += "|";

        drawString(s, c.xa , c.ya, c);
        c.repaint();
    }

    //  ==============================================================================================================

    void initMyGradient(Canvas c) {

        //  Set up different gradients, or none, for filling shapes on the canvas.

        int xga = c.x1, xgb = c.x1 + c.x2;
        int yga = c.y1, ygb = c.y1 + c.y2;

        switch( p.fillSelected ) {

            case 1 :

                // HORIZONTAL

                GradientPaint myHorizontalGradient = new GradientPaint(
                        xga, yga, p.getSelectedForeground(),
                        xgb, yga, p.getSelectedBackground(1) );

                c.g2d.setPaint(myHorizontalGradient);

                break;

            case 2 :

                // VERTICAL

                GradientPaint myVerticalGradient = new GradientPaint(
                        xga, yga, p.getSelectedForeground(),
                        xga, ygb, p.getSelectedBackground(1) );

                c.g2d.setPaint(myVerticalGradient);

                break;

            case 3 :

                // RADIAL

                //  Set up the variables
                Point2D center = new Point2D.Float(c.x1 + c.x2/2, c.y1 + c.y2/2);
                Rectangle2D myRectangle = new Rectangle2D.Double(c.x1, c.y1, c.x2, c.y2);
                float radius = c.x2 / 2;
                float[] dist = {0.0f, 1.0f};
                Color[] colors = { p.getSelectedBackground(1), p.getSelectedForeground() };

                //  Keep it from giving errors when the rectangle is very small, or empty.
                if(myRectangle.isEmpty())
                    myRectangle = new Rectangle2D.Double(c.x1, c.y1, c.x1 + 1, c.y1 + 1);
                if( radius < 1 ) radius = 1;

                //  Create the gradient
                RadialGradientPaint myRadialGradient =
                        new RadialGradientPaint( myRectangle, dist, colors, NO_CYCLE );
                                //center, radius, dist, colors, gradientTransform

                c.g2d.setPaint(myRadialGradient);
                break;

            default : break;
        }
    }


    void initMyPolygon(Canvas c) {

        // Make a new polygon, to be drawn on the canvas

        c.moves.add(new Point( c.moves.get(0).x, c.moves.get(0).y ));

        c.poly = new Polygon();

        c.x1 = c.moves.get(0).x; c.x2 = c.x1;
        c.y1 = c.moves.get(0).y; c.y2 = c.y1;

        for(int i = 0; i < c.moves.size() - 1; i++) {
            c.poly.addPoint( c.moves.get(i).x, c.moves.get(i).y );

            if( c.moves.get(i).x < c.x1) c.x1 = c.moves.get(i).x;
            if( c.moves.get(i).x > c.x2) c.x2 = c.moves.get(i).x;

            if( c.moves.get(i).y < c.y1) c.y1 = c.moves.get(i).y;
            if( c.moves.get(i).y > c.y2) c.y2 = c.moves.get(i).y;
        }

        c.x2 = c.x2 - c.x1;
        c.y2 = c.y2 - c.y1;

        executePreviewDraw(2, 0, 1, c);
        // initMyGradient(c);
    }


    boolean polygonSnap(Canvas c) {

        //  Make it so you do not need to be 100% accurate when closing a polygon.

        int radius = 8;

        c.xa = c.moves.get(0).x; c.ya = c.moves.get(0).y;   // center
        c.xb = c.currentX; c.yb = c.currentY;               // current point

        if(((int)Math.pow((c.xb - c.xa), 2) + (int)Math.pow((c.yb - c.ya), 2)) < (int)Math.pow(radius, 2))
            return true;

        return false;
    }
}
