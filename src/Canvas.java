import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *  Canvas.java
 *
 *  This is a component that exists within a given CanvasFrame, and contains the actual  image  that is used for
 *  drawing.
 *
 *  It uses an instance of CanvasController.java as a controller.
 *
 *  @author Jan Øyvind Kruse
 *  @version 1.0
 */

public class Canvas extends JComponent {

    int DELAY = 10;

    Paint p;
    ImageHandling iH;
    CanvasController cC;

    BufferedImage image, bImage, zImage = null, aImage = null, temp;
    BufferedImage sImage = null;

    // aImage is used when files are loaded
    // zImage is used for the Undo action
    // sImage is used for moving selections

    Font font;
    Graphics2D g2d;
    String lastAction, text = "";

    int currentX, currentY, oldX, oldY;  // Variables that hold our mouse coordinates
    int x1, x2, y1, y2, minimum, myID;
    int phase, xa, xb, ya, yb;  // For Quad Curve Tool / Rectangular Marquee
    int selectionActive;
    boolean shiftKeyDown = false, edited = false, handToolUsed = false, curveCooldown = false;

    float counter;
    float interval = 5.0f;  // Used for Rectangular Marquee Tool
    float dash[] = { interval };

    Timer timer, curveCooldownTimer;
    ArrayList<Point> moves;
    Polygon poly;

    //  ==============================================================================================================

    public Canvas(int sx, int sy, BufferedImage b, Paint View, ImageHandling myImageHandling, int id) {

        p = View;
        iH = myImageHandling;
        myID = id;

        setPreferredSize(new Dimension(sx, sy));
        setBackground(Color.white);
        setFocusable(true);

        if(b != null) { aImage = b; }

        cC = new CanvasController( this, p, iH );

        this.addMouseListener( cC );
        this.addMouseMotionListener( cC );
        this.addKeyListener( cC );

    }


    void grabCursor() {

        float dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        double scaler = dpi / 96.0;

        int pp = (int)(16 * scaler);  // Scale X, Y coordinates

        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                new ImageIcon("src/pointer/pointer0b.png").getImage(),
                new Point(pp, pp),"custom cursor"));
    }


    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if(image == null) {
            iH.resetImage(Canvas.this);
        }

        setSize( image.getWidth(), image.getHeight());
        setPreferredSize( new Dimension(  image.getWidth(), image.getHeight() ) ) ;


        if(p.getAntiAlias()) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        }
        else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        }

        if(aImage != null) {

            g2d.drawImage(aImage,0,0,null);
            aImage = null;

        }

        g.drawImage(image, 0, 0, null);

        p.lastCanvas();  // Let parent know this was the last Canvas used

    }


    public BufferedImage getImage() {

        // Used by Paint.java to get the image so it can be saved in FileHandling.java

        BufferedImage cImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = cImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        return cImage;

    }

    //  ==============================================================================================================

    void setUpStroke(int l, int cap, int join, Color c) {

        g2d.setStroke(new BasicStroke(l, cap, join));
        g2d.setPaint(c);

    }

    int getPhase() { return phase; }
    void setPhase(int i) { phase = i; }

    int getSelectionActive() { return selectionActive; }

    void setSelectionActive(int i) {

        if(selectionActive == 1)
            timer.stop();

        selectionActive = i;

    }

    //  ==============================================================================================================

    void deleteSelection(int delete) {

        timer.stop();  // stop the ants
        iH.restoreBackupImage(Canvas.this );
        iH.makePreviewImage(Canvas.this );

        if(delete == 1) {

            g2d.setPaint( p.getSelectedBackground(0));
            g2d.fill(new Rectangle2D.Double(x1, y1, x2, y2));
            repaint();

        }

        selectionActive = 0;
    }


    void brushTool(int xbrush1, int ybrush1, int xbrush2, int ybrush2) {

        setUpStroke(p.getLineSize(), 1, 2, p.getSelectedForeground() );

        int l = p.getLineSize();
        int br = p.brushSelected;

        for(int j = 0; j < 9; j++) {  // rows (y)

            for (int k = 0; k < 9; k++) {  // columns (x)

                if( p.oF.CustomB[br][j][k] == 1) {
                    g2d.drawLine(xbrush1 + (k-4) * l, ybrush1 + (j-4) * l, xbrush2 + (k-4) * l, ybrush2 + (j-4) * l);

                }
            }
        }
        repaint();
    }


    void antCrawl() {

        if(counter > 10) counter = 0;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        BasicStroke dashedA =
                new BasicStroke(1.0f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        interval, dash, 0.0f + counter);

        BasicStroke dashedB =
                new BasicStroke(1.0f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        interval, dash, interval + counter);

        g2d.setPaint(Color.black);
        g2d.setStroke(dashedA);
        g2d.drawRect(x1, y1, x2, y2);

        g2d.setPaint(Color.white);
        g2d.setStroke(dashedB);
        g2d.drawRect(x1, y1, x2, y2);

        repaint();

        counter++;
    }


    void airBrush() {

        setUpStroke(p.getLineSize(), 1, 2, p.getSelectedForeground() );

        int d, diameter = p.getLineSize() * 100;

        for(int i = 0; i < 125; i++) {

            x1 = currentX - (diameter / 2) + (int)(Math.random() * diameter);
            y1 = currentY - (diameter / 2) + (int)(Math.random() * diameter);

            x2 = (int)Math.pow((currentX - x1), 2);
            y2 = (int)Math.pow((currentY - y1), 2);

            d = (int)Math.sqrt( x2 + y2 );

            if( d < (diameter / 2) ) {
                g2d.drawLine(x1, y1, x1, y1);
            }
        }
        repaint();
    }
}
