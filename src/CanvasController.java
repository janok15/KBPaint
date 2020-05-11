import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.ArrayList;

/**
 *  CanvasController.java
 *
 *  This serves as a controller for a given canvas.
 *
 *  @author Jan Øyvind Kruse, with minor contributions by Edmund Bosco
 *  @version 1.0
 */

public class CanvasController implements ActionListener, MouseListener, MouseMotionListener, KeyListener {

    Paint p;
    Canvas c;
    ImageHandling iH;

    public CanvasController(Canvas myCanvas, Paint View, ImageHandling myImageHandling) {

        p = View;
        iH = myImageHandling;
        c = myCanvas;

    }

    @Override public void mouseClicked(MouseEvent mouseEvent) {}
    @Override public void keyTyped(KeyEvent e) {}

/*

    Tool  0 - Hand Tool                 Tool  1 - Rect. Marquee Tool
    Tool 10 - Eyedropper Tool           Tool 11 - Text Tool
    Tool 20 - Fill Tool                 Tool 21 - Airbrush Tool
    Tool 30 - Brush Tool                Tool 31 - Pencil Tool
    Tool 40 - Line Tool                 Tool 41 - Eraser Tool
    Tool 50 - Box Tool (outline)        Tool 51 - Box Tool (filled)
    Tool 60 - Rounded Box (outline)     Tool 61 - Rounded Box Tool (filled)
    Tool 70 - Oval Tool (outline)       Tool 71 - Oval Tool (filled)
    Tool 80 - Quad Curve Tool           Tool 81 - Free-form shape Tool (filled)
    Tool 90 - Polygon Tool (outline)    Tool 91 - Polygon Tool (filled)

*/

    @Override
    public void mousePressed(MouseEvent e) {

        if(e.getButton() == 1) {

            c.oldX = e.getX();  // All the tools will invoke these now, removes some duplicate unnecessary code
            c.oldY = e.getY();

            Color color;

            switch( p.getToolSelected() ) {

                case 0 :  // *** HAND TOOL - 0 - mousePressed

                    if(c.selectionActive > 0) {

                        // selectionActive = 1  - Coming from Rectangular Marquee Tool
                        // selectionActive = 2  - From Paste

                        c.grabCursor();
                        c.handToolUsed = true;

                        if(c.selectionActive == 1)
                            c.timer.stop();  // stop the ants from the Rect. Marquee

                        if( c.lastAction != "Undo" || c.selectionActive == 2 )
                            iH.restoreBackupImage(c);

                        iH.makePreviewImage(c);

                        //  Only make an sImage when coming from the Rectangular Marquee Tool, not for Paste
                        if(c.selectionActive == 1)
                            c.sImage = c.bImage.getSubimage(c.x1, c.y1, c.x2, c.y2);

                        if( (c.oldX >= c.x1 && c.oldX <= (c.x1 + c.x2)) && (c.oldY >= c.y1 && c.oldY <= (c.y1 + c.y2))) {
                            c.xa = c.oldX - c.x1;
                            c.ya = c.oldY - c.y1;
                        }
                    }

                    break;

                case 1 :  // ***  RECTANGULAR MARQUEE TOOL - 1 - mousePressed

                    c.x1 = c.oldX; c.x2 = 0; c.y1 = c.oldY; c.y2 = 0;
                    c.handToolUsed = false;

                    if(c.selectionActive == 1) {
                        //  Make a new selection, when one is already active
                        iH.restoreBackupImage(c);
                        c.timer.stop();
                    }

                    iH.makePreviewImage(c);
                    c.selectionActive = 1;

                    c.timer = new Timer( c.DELAY * 3, this );
                    c.timer.start();

                    break;

                case 10 :  // ***  EYEDROPPER TOOL - 10 - mousePressed

                    color = iH.getPixelColor(e.getX(), e.getY(), c);
                    p.tF.setNewForeground(color);
                    break;

                case 11 :  // ***  TEXT TOOL - 11 - mousePressed

                    if(c.phase == 1) {
                        c.grabCursor();
                    }

                    if(c.phase == 0) {
                        c.xa = c.oldX; c.ya = c.oldY; c.phase = 1;

                        c.text = "";

                        iH.makePreviewImage(c);
                        iH.textFunction(0, c);
                    }

                    break;

                case 20 :  // ***  FILL TOOL - 20 - mousePressed

                    iH.makePreviewImage(c);

                    color = iH.getPixelColor(e.getX(), e.getY(), c);
                    iH.floodFill(c.bImage, new Point(e.getX(), e.getY()), color, p.getSelectedForeground(), c );

                    c.g2d.drawImage(c.bImage,0,0,null);
                    c.repaint();

                    break;

                case 21 :  // ***  AIRBRUSH TOOL - 21 - mousePressed

                    iH.makeBackupImage(c.image, c);
                    c.airBrush();

                    //  Add a timer that will run until the mouse button gets released
                    c.timer = new Timer( c.DELAY, this);
                    c.timer.start();

                    break;

                case 30 :  // ***  BRUSH TOOL - 30 - mousePressed

                    iH.makeBackupImage(c.image, c);
                    c.brushTool(c.oldX, c.oldY, c.oldX, c.oldY);
                    break;

                case 31:  // ***  PENCIL TOOL - 31 - mousePressed

                    iH.makeBackupImage(c.image, c);
                    c.setUpStroke(p.getLineSize(), 1, 2, p.getSelectedForeground() );
                    c.g2d.drawLine(c.oldX, c.oldY, c.oldX, c.oldY);
                    c.repaint();
                    break;

                case 41:  // *** ERASER TOOL - MousePressed

                    iH.makeBackupImage(c.image, c);
                    c.setUpStroke(p.getLineSize() * 2 + 3, 1, 2, p.getSelectedBackground(1));
                    c.g2d.drawLine(c.oldX, c.oldY, c.oldX, c.oldY);
                    c.repaint();
                    break;

                case 40 : case 50 : case 51 : case 60 : case 61 : case 70 : case 71 :

                    // *** LINE / *** BOX (Outline/Filled) / *** ROUNDED BOX (Outline/Filled) - MousePressed
                    // *** OVAL (Outline/Filled)  - MousePressed

                    iH.makePreviewImage(c);
                    break;

                case 80 :  // ***  QUAD CURVE TOOL - 80 - mousePressed

                    if(c.phase == 2) {

                        c.phase = 0;

                        //  Add a short cooldown period, to avoid accidentially triggering
                        //  this again immediately after drawing a curve.

                        c.curveCooldownTimer = new Timer( 250, this );
                        c.curveCooldownTimer.start();
                        c.curveCooldown = true;
                    }

                    break;

                case 90 : case 91 :  // *** POLYGON TOOL (Outline/Filled) - mousePressed

                    if(c.phase == 1) {

                        if( iH.polygonSnap(c) && (c.moves.size() > 1) ) {

                            iH.initMyPolygon(c);

                            if( p.getToolSelected() == 90 ) c.g2d.drawPolygon(c.poly);
                            else {
                                iH.initMyGradient(c);
                                c.g2d.fillPolygon(c.poly);
                            }

                            c.repaint();
                            c.phase = -1;
                        }
                        else {
                            c.moves.add(new Point( c.currentX, c.currentY ));
                        }
                    }

                    if(c.phase == 0) {

                        iH.makePreviewImage(c);

                        c.moves = new ArrayList<Point>();
                        c.phase = 1;

                        c.moves.add(new Point( c.oldX, c.oldY ));

                    }

                    if(c.phase == -1)
                        c.phase = 0;

                    break;

                default : break;
            }
        }
    }


    @Override
    public void mouseDragged(MouseEvent e) {

        if (SwingUtilities.isLeftMouseButton(e)) {

            // This fixes a bug when using other mouse buttons to draw
            // You should only be able to draw with the left mouse button

            c.currentX = e.getX();
            c.currentY = e.getY();

            switch(p.getToolSelected()) {

                case 0 :  // ***  HAND TOOL - 0 - mouseDragged

                    if(c.selectionActive > 0) {
                        iH.executePreviewDraw(2, 0, 1, c);

                        if(c.selectionActive == 1) {
                            //  For normal selection, following a Selection Marquee

                            c.g2d.setPaint( p.getSelectedBackground(0) );
                            c.g2d.fill(new Rectangle2D.Double(c.x1, c.y1, c.x2, c.y2));
                        }
                        c.g2d.drawImage(c.sImage, c.currentX - c.xa, c.currentY - c.ya, null);
                        c.repaint();
                    }
                    break;

                case 1 :  // ***  RECTANGULAR MARQUEE TOOL - 1 - mouseDragged

                    iH.executePreviewDraw(2, 0, 0, c);
                    c.antCrawl();
                    break;

                case 11 :  // ***  TEXT TOOL - 11 - mouseDragged

                    if(c.phase == 1) {
                        iH.textBounds(c.currentX, c.currentY, c);
                        iH.textFunction(0, c);
                    }
                    break;

                case 30 :  // ***  BRUSH TOOL - 30 - mouseDragged

                    c.brushTool(c.oldX, c.oldY, c.currentX, c.currentY);
                    c.oldX = c.currentX;
                    c.oldY = c.currentY;
                    break;

                case 31 :  // ***  PENCIL TOOL - 31 - mouseDragged

                    c.setUpStroke(p.getLineSize(), 1, 2, p.getSelectedForeground() );
                    c.g2d.drawLine(c.oldX, c.oldY, c.currentX, c.currentY);
                    c.repaint();
                    c.oldX = c.currentX;
                    c.oldY = c.currentY;
                    break;

                case 40 :  // ***  LINE TOOL - 40 - mouseDragged  ***

                    iH.executePreviewDraw(1, 2, 0, c);
                    c.g2d.drawLine(c.oldX, c.oldY, c.currentX, c.currentY);
                    c.repaint();
                    break;

                case 41 :  // ***  ERASER TOOL - 41 - mouseDragged  ***

                    c.setUpStroke(p.getLineSize() * 2 + 3, 1, 2, p.getSelectedBackground(1));
                    c.g2d.drawLine(c.oldX, c.oldY, c.currentX, c.currentY);
                    c.repaint();
                    c.oldX = c.currentX;
                    c.oldY = c.currentY;
                    break;

                case 50 :  // ***  BOX TOOL (Outline) - 50 - mouseDragged  ***

                    iH.executePreviewDraw(2, 0, 0, c);
                    c.g2d.drawRect(c.x1, c.y1, c.x2, c.y2);
                    c.repaint();
                    break;

                case 51 :  // ***  BOX TOOL (Filled) - 51 - mouseDragged  ***

                    iH.executePreviewDraw(2, 0, 0, c);
                    iH.initMyGradient(c);
                    c.g2d.fill(new Rectangle2D.Double(c.x1, c.y1, c.x2, c.y2));
                    c.repaint();
                    break;

                case 60 :  // ***  ROUNDED BOX (Outline) - 60 - mouseDragged  ***

                    iH.executePreviewDraw(2, 0, 0, c);
                    c.g2d.drawRoundRect(c.x1, c.y1, c.x2, c.y2,20, 20);
                    c.repaint();
                    break;

                case 61 :  // ***  ROUNDED BOX (Filled) - 61 - mouseDragged  ***

                    iH.executePreviewDraw(2, 0, 0 , c);
                    iH.initMyGradient(c);
                    c.g2d.fill(new RoundRectangle2D.Double(c.x1, c.y1, c.x2, c.y2, 20, 20));
                    c.repaint();
                    break;

                case 70 :  // ***  OVAL TOOL (Outline) - 70 - mouseDragged  ***

                    iH.executePreviewDraw(2, 0, 0 , c);
                    c.g2d.drawOval(c.x1, c.y1, c.x2, c.y2);
                    c.repaint();
                    break;

                case 71 :  // ***  OVAL TOOL (Filled) - 71 - mouseDragged  ***

                    iH.executePreviewDraw(2, 0, 0 , c);
                    iH.initMyGradient(c);
                    c.g2d.fillOval(c.x1, c.y1, c.x2, c.y2);
                    c.repaint();
                    break;

                case 80 :  // ***  QUAD CURVE TOOL - 80 - mouseDragged  ***

                    if(!c.curveCooldown) {

                        if(c.phase == 0) {
                            c.phase = 1;
                            iH.makePreviewImage(c);
                            c.xa = c.oldX; c.ya = c.oldY;
                        }

                        iH.executePreviewDraw(1, 2, 1, c);

                        if(c.phase == 1) {
                            c.g2d.drawLine(c.oldX, c.oldY, c.currentX, c.currentY);
                            c.xb = c.currentX; c.yb = c.currentY;
                            c.repaint();
                        }
                    }

                    break;

                case 81 :  // ***  FREE-FORM SHAPE TOOL - 81 - mouseDragged  ***

                    if(c.phase == 1) {
                        iH.executePreviewDraw(1, 2, 1, c);
                        c.setUpStroke(p.getLineSize(), 1, 2, p.getSelectedForeground() );
                        c.moves.add(new Point( c.currentX, c.currentY ));

                        for (int i = 0; i < c.moves.size() - 1; i++) {
                            c.g2d.drawLine( c.moves.get(i).x, c.moves.get(i).y, c.moves.get(i+1).x, c.moves.get(i+1).y);
                        }
                        c.repaint();
                    }

                    if(c.phase == 0) {
                        iH.makePreviewImage(c);
                        c.moves = new ArrayList<Point>();
                        c.moves.add(new Point( c.oldX, c.oldY ));
                        c.moves.add(new Point( c.currentX, c.currentY ));
                        c.phase = 1;
                    }

                    break;

                default : break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if(e.getButton() == 1) {

            switch( p.getToolSelected() ) {

                case 0 :  // ***  HAND TOOL - 0 - mouseReleased  ***

                    c.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                            new ImageIcon("src/pointer/pointer0.png").getImage(),
                            new Point(9, 9),"custom cursor"));
                    break;

                case 1 :  // ***  RECTANGULAR MARQUEE TOOL - 1 - mouseReleased  ***

                    //  Need to make sure the selection is not outside the canvas area
                    c.xa = c.x1; c.xb = c.x2; c.ya = c.y1; c.yb = c.y2;

                    if(c.x1 < 0) { c.xa = 0; c.xb = c.x2 + c.x1; }
                    if(c.y1 < 0) { c.ya = 0; c.yb = c.y2 + c.y1; }
                    if( (c.x1 + c.x2) > c.image.getWidth() ) { c.xa = c.x1; c.xb = c.image.getWidth() - c.x1 - 1; }
                    if( (c.y1 + c.y2) > c.image.getHeight() ) { c.ya = c.y1; c.yb = c.image.getHeight() - c.y1 - 1; }

                    c.x1 = c.xa; c.x2 = c.xb; c.y1 = c.ya; c.y2 = c.yb;
                    c.lastAction = "Select";
                    break;

                case 11 :  // ***  TEXT TOOL - 11 - mouseReleased

                    c.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                    break;

                case 21 :  // ***  AIR BRUSH TOOL - 21 - mouseReleased  ***

                    c.timer.stop();
                    break;

                case 80 :  // ***  QUAD CURVE TOOL - 80 - mouseReleased  ***

                    if( c.phase == 1 ) { c.phase = 2; }
                    break;

                case 81 :  // ***  FREE-FORM SHAPE TOOL - 81 - mouseReleased  ***

                    if( c.phase == 1 ) {

                        iH.initMyPolygon(c);
                        c.g2d.fillPolygon(c.poly);
                        c.repaint();

                        c.phase = 0;
                    }
                    break;

                default : break;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        c.currentX = e.getX();
        c.currentY = e.getY();

        if(p.getToolSelected() == 80) {  // ***  QUAD CURVE TOOL - 80 - mouseMoved  ***

            if( c.phase == 2 ) {
                int xc, yc, xd, yd;

                iH.executePreviewDraw(1, 2, 1, c);

                //  The control point needs to be twice as far away for the curve to intersect
                //  with the mouse pointer ... Some vector math follows

                xc = c.xa + (c.xb - c.xa)/2;
                yc = c.ya + (c.yb - c.ya)/2;
                xd = 2 * (c.currentX - xc);
                yd = 2 * (c.currentY - yc);

                QuadCurve2D q = new QuadCurve2D.Float();
                q.setCurve(c.xa, c.ya, xc + xd, yc + yd, c.xb, c.yb);
                c.g2d.draw(q);
                c.repaint();
            }
        }

        if(p.getToolSelected() > 81) {  // ***  POLYGON TOOL - 90 & 91 - mouseMoved  ***

            if (c.phase == 1) {
                iH.executePreviewDraw(1, 2, 1, c);
                c.setUpStroke(p.getLineSize(), 1, 2, p.getSelectedForeground());

                for (int i = 0; i < c.moves.size() - 1; i++) {
                    c.g2d.drawLine(c.moves.get(i).x, c.moves.get(i).y, c.moves.get(i + 1).x, c.moves.get(i + 1).y);
                }

                c.g2d.drawLine(c.moves.get(c.moves.size() - 1).x, c.moves.get(c.moves.size() - 1).y, c.currentX, c.currentY);
                c.repaint();
            }
        }
    }

    @Override
    public void mouseEntered( MouseEvent e ) {

        //  Change the mouse cursor when it enters the canvas area

        int ll = p.getLineSize();
        float dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        double scaler = dpi / 96.0;

        if(ll > 4) ll = 4;

        int pointers[][] = {
                { 0, 16, 16 },   // HAND TOOL
                { 10, 2, 28 },   // EYEDROPPER TOOL
                { 20, 26, 30 },  // FILL TOOL
                { 21, 2, 4 },    // AIRBRUSH TOOL
                { 30, 14, 14 },  // BRUSH TOOL
                { 41, 14, 14 }   // ERASER TOOL
        };

        for(int i = 0; i < pointers.length; i++) {
            //  Scale X and Y coordinates of pointer for different screen DPI.
            pointers[i][1] *= scaler;
            pointers[i][2] *= scaler;
        }

        c.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        if(p.getToolSelected() == 11)
            c.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));


        for (int i = 0; i < pointers.length; i++  ) {
            if(p.getToolSelected() == pointers[i][0]) {

                String pp = "" + pointers[i][0];
                if( pointers[i][0] > 21) pp += ll;

                c.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                        new ImageIcon("src/pointer/pointer" + pp + ".png").getImage(),
                        new Point(pointers[i][1],pointers[i][2]),"custom cursor"));

            }
        }
    }

    public void mouseExited( MouseEvent e ) {
        //  Change mouse cursor back to normal when it exits the canvas area.
        c.setCursor(Cursor.getDefaultCursor());
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if(actionEvent.getSource() == c.curveCooldownTimer ) {
            c.curveCooldownTimer.stop();
            c.curveCooldown = false;
        }

        if(p.getToolSelected() < 10) c.antCrawl();
        if(p.getToolSelected() == 21) c.airBrush();
    }


    @Override
    public void keyPressed(KeyEvent e) {

        if(p.getToolSelected() == 11 && c.phase == 1) {

            //  For the Text Tool to input text

            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {

                int len = c.text.length() - 1;
                if(len < 0) len = 0;

                c.text = c.text.substring(0, len );

            }
            else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            }
            else {
                c.text += e.getKeyChar();
            }

            iH.textFunction(0, c);
        }
        else {

            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                //  Keep track of whether the shift key is held down, for making rectangles square, etc.
                c.shiftKeyDown = true;
            }

            if (e.getKeyCode() == KeyEvent.VK_DELETE) {

                if(c.selectionActive == 1) {

                    //  Make it possible to erase a whole block when there is a selection active.
                    c.deleteSelection(1);

                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            c.shiftKeyDown = false;
        }
    }
}
