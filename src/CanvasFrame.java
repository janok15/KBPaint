import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *  CanvasFrame.java
 *
 *  This is the internal frame that contains the canvas that is used for drawing.
 *
 *  @author Jan Øyvind Kruse
 *  @version 1.0
 */

public class CanvasFrame extends JInternalFrame {

    Paint p;
    PaintController pC;
    BufferedImage tempImage = null;

    static ArrayList<MyInternalFrame> frames = new ArrayList<MyInternalFrame>();
    ArrayList<Canvas> frameCanvas = new ArrayList<Canvas>();

    // ===============================================================================================================
    // CREATE FRAME (CANVAS AREA WINDOW - INTERNALFRAME)

    public void createFrame(int x, int y, int sx, int sy, String s, Paint View, ImageHandling iH, PaintController Controller) {

        p = View;
        pC = Controller;

        p.canvasCountChronological++;  // how many canvas internal frames this session, even if they have been closed
        p.canvasCount = p.windowMenu.getItemCount();  // how many canvas internal frames currently open

        if(s.equals(""))
            s = "Untitled-" + p.canvasCountChronological;

        MyInternalFrame frame = new MyInternalFrame(x, y, sx, sy, s, p.canvasCountChronological);

        JScrollPane skrollTekst;

        Box box = new Box(BoxLayout.Y_AXIS);

        Canvas l = new Canvas(sx - p.bx, sy - p.by, tempImage, p, iH, p.canvasCountChronological);
        // If there is a tempImage - send it to the canvas, ie. for loading an image
        //l.addPaint( p, iH, p.canvasCountChronological );  // Tell canvas panel about Paint and ImageHandling

        frameCanvas.add(l);

        tempImage = null;

        JPanel wrappingPanel = new JPanel(new FlowLayout());
        wrappingPanel.add(l);

        box.add( Box.createVerticalGlue() );
        box.add( wrappingPanel );
        box.add( Box.createVerticalGlue() );

        skrollTekst = new JScrollPane(box);

        frame.add( skrollTekst );
        frame.setVisible(true);

        skrollTekst.getVerticalScrollBar().setUnitIncrement(16);

        frames.add( frame );  // an Arraylist of internal frames

        p.windowsOpen.add( new JMenuItem( frames.get(p.canvasCount).getTitle() ));  // The Window menu items
        p.windowMenu.add( p.windowsOpen.get( p.canvasCount ) );
        p.windowsOpen.get( p.canvasCount ).addActionListener( pC );

        frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

        p.desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }

        frame.addInternalFrameListener(new InternalFrameAdapter() {

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {

                for(int i = 0; i < p.windowMenu.getItemCount(); i++) {

                    if( frame.myID == frameCanvas.get(i).myID ) {

                        if( frameCanvas.get(i).edited ) {

                            Object[] options = {"Yes", "No", "Cancel"};

                            int n = JOptionPane.showOptionDialog(p,
                                    "Save changes to " + frame.getTitle() + " before closing?",
                                    p.app_name,
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    options,
                                    options[0]);


                            if (n == JOptionPane.CANCEL_OPTION) {
                                p.closeApp = false;
                            }
                            else {

                                if (n == JOptionPane.YES_OPTION) {
                                    p.saveFile( frameCanvas.get(i), i );
                                }

                                removeAction(i);
                                frame.dispose();

                            }
                        }
                        else {

                            // Window has not been edited, ok to remove.
                            removeAction(i);
                            frame.dispose();
                        }
                    }
                }
            }
        });
    }


    void removeAction(int i) {

        p.windowMenu.remove(i);
        p.windowsOpen.remove( i  );  // remove from The Window menu items
        frames.remove(i);            // remove internal frame
        frameCanvas.remove(i);       // remove canvas
        p.lastCanvas = -1;           // Tell Paint that no CanvasFrame is currently selected
    }


    public class MyInternalFrame extends JInternalFrame {

        int myID;

        public MyInternalFrame(int offsetX, int offsetY, int sizeX, int sizeY, String s, int id) {

            super(s,
                    true, //resizable
                    true, //closable
                    true, //maximizable
                    true);//iconifiable

            setSize(sizeX, sizeY);
            setLocation(offsetX, offsetY);

            myID = id;
        }
    }
}
