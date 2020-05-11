import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.net.URL;

/**
 *  ToolFrame.java
 *
 *  This is the left-hand menu for picking tools.
 *
 *  @author Jan Øyvind Kruse
 *  @version 1.0
 */


public class ToolFrame extends JInternalFrame {

    MyToolMenu toolFrame;

    Paint p;
    PaintController pC;
    Color foreground, background;

    //  BUTTONS

    public JButton[][] Menu = new JButton[10][2];
    public JButton[] Lines = new JButton[5];
    public JButton[] Palette = new JButton[2];

    public JPanel fg = new JPanel();
    public JPanel bg_n = new JPanel();
    public JPanel bg_w = new JPanel();
    public JPanel bg_e = new JPanel();
    public JPanel bg_s = new JPanel();
    public JButton[][] Colors = new JButton[16][4];


    // ===============================================================================================================
    // CREATE TOOL MENU

    public void createToolMenu(int x, int y, Paint View, PaintController Controller) {

        p = View;
        pC = Controller;

        toolFrame = new MyToolMenu(x, y);
        toolFrame.setVisible(true);
        toolFrame.setLayout(null);

        URL url;

        String[][] ToolTip = {
                {"Move Tool", "Rectangular Marquee Tool"},
                {"Eyedropper Tool", "Text Tool"},
                {"Paint Bucket Tool", "Airbrush Tool"},
                {"Brush Tool", "Pencil Tool"},
                {"Line Tool", "Eraser Tool"},
                {"Rectangle Tool", "Filled Rectangle Tool"},
                {"Rounded Rectangle Tool", "Filled Rounded Rectangle Tool"},
                {"Ellipse Tool", "Filled Ellipse Tool"},
                {"Quadratic Curve Tool", "Filled Shape Tool"},
                {"Polygon Tool", "Filled Polygon Tool"} };

        //  ADD THE TOOL BUTTONS
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 2; j++) {

                Menu[i][j] = new JButton();

                url = Paint.class.getResource("icon/icon"+i+j+".png");

                if(i == 3 && j == 1)
                    url = Paint.class.getResource("icon/icon"+i+j+"i.png");

                ImageIcon imgicon = new ImageIcon(url);

                Menu[i][j].setIcon(imgicon);
                Menu[i][j].setBounds(2 + j*28, 2 + i*25, 26, 23);

                Menu[i][j].setToolTipText( ToolTip[i][j] );

                toolFrame.add( Menu[i][j] );
                Menu[i][j].addActionListener( pC );
            }
        }

        //  ADD LINE WIDTH BUTTONS
        for(int i = 1; i < 5; i++) {

            Lines[i] = new JButton();

            url = Paint.class.getResource("icon/line0"+i+".png");

            if(i == 1)
                url = Paint.class.getResource("icon/line0"+i+"i.png");

            ImageIcon imgicon = new ImageIcon(url);
            Lines[i].setIcon(imgicon);

            Lines[i].setBounds(2, 228 + i*24 , 54, 22);
            Lines[i].setToolTipText( "Line Width: " + i + " px" );

            toolFrame.add( Lines[i] );
            Lines[i].addActionListener( pC );
        }

        String[] paletteToolTip = { "Default Palette", "Switch Foreground and Background Colors" };


        //  ADD PALETTE BUTTONS
        for(int i = 0; i < 2; i++) {

            Palette[i] = new JButton();

            url = Paint.class.getResource("icon/iconp" + i + ".png");

            ImageIcon imgicon = new ImageIcon(url);
            Palette[i].setIcon(imgicon);
            Palette[i].setBounds(2 + i*28, 348, 26, 23);
            Palette[i].setToolTipText( paletteToolTip[i] );

            toolFrame.add( Palette[i] );
            Palette[i].addActionListener( pC );
        }

        fg.setOpaque(true);
        fg.setBounds(17, 377, 24, 8);

        toolFrame.add(fg, new Integer(10));

        bg_n.setBounds(2, 373, 54, 4);
        bg_w.setBounds(2, 377, 15, 8);
        bg_e.setBounds(41, 377, 15, 8);
        bg_s.setBounds(2, 385, 54, 4);

        toolFrame.add(bg_n);
        toolFrame.add(bg_w);
        toolFrame.add(bg_e);
        toolFrame.add(bg_s);

        Border emptyBorder = BorderFactory.createEmptyBorder();

        //  ADD BUTTONS FOR 64 COLORS
        for(int j = 0; j < 4; j++) { // columns
            for(int i = 0; i < 16; i++) { // rows

                Colors[i][j] = new JButton();

                if(j == 0)
                    Colors[i][j].setBounds(0, 391 + i*14, 15, 14);
                else if(j == 3)
                    Colors[i][j].setBounds(1 + j*14, 391 + i*14, 15, 14);
                else
                    Colors[i][j].setBounds(1 + j*14, 391 + i*14, 14, 14);


                toolFrame.add( Colors[i][j] );
                Colors[i][j].addActionListener( pC );
                Colors[i][j].addMouseListener( pC );

                Colors[i][j].setBorder(emptyBorder);
            }
        }

        resetPalette();

        setRootPaneCheckingEnabled(false);
        javax.swing.plaf.InternalFrameUI ifu= toolFrame.getUI();
        ((javax.swing.plaf.basic.BasicInternalFrameUI)ifu).setNorthPane(null);

        p.desktop.add(toolFrame, new Integer( 10 ));
        try {
            toolFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }


    private class MyToolMenu extends JInternalFrame {

        public MyToolMenu(int offsetX, int offsetY) {
            super("Tools",
                    false, //resizable
                    false, //closable
                    false, //maximizable
                    false);//iconifiable

            setSize(68, 625);
            setLocation(offsetX, offsetY);
        }
    }


    //  ==============================================================================================================

    void resetPalette() {

        int RGB[][] = new int[64][3];
        int counter = 0, rt, gt, bt;

        for(int red = 0; red < 256; red += 85) {

            for(int green = 0; green < 256; green += 85) {

                for(int blue = 0; blue < 256; blue += 85) {

                    RGB[counter][0] = red;
                    RGB[counter][1] = green;
                    RGB[counter][2] = blue;

                    counter++;
                }
            }
        }

        foreground = Color.black;
        background = Color.white;

        updateColorPanels();

        counter = 0;
        Color c;

        for(int j = 0; j < 4; j++) {
            for(int i = 0; i < 16; i++) {

                c = new Color(RGB[counter][0], RGB[counter][1],RGB[counter][2]);

                Colors[i][j].setBackground(c);
                Colors[i][j].setOpaque(true);

                Colors[i][j].setToolTipText(
                        "R: " + RGB[counter][0] +
                        " G:" + RGB[counter][1] +
                        " B:" + RGB[counter][2]);

                counter++;
            }
        }
    }


    void updateColorPanels() {

        bg_n.setBackground(background);
        bg_w.setBackground(background);
        bg_e.setBackground(background);
        bg_s.setBackground(background);
        fg.setBackground(foreground);
    }


    void setNewForeground(Color c) {
        foreground = c;
        updateColorPanels();
    }


    void updateLineWidth(int l) {

        URL url;
        ImageIcon imgicon;

        int a;

        if(p.lineSelected > 0 && p.lineSelected < 5) { a = p.lineSelected; }
        else { a = 4; }

        url = Paint.class.getResource("icon/line0" + a + ".png");
        imgicon = new ImageIcon(url);
        Lines[a].setIcon(imgicon);

        p.lineSelected = l;

        if(l > 4) l = 4;

        url = Paint.class.getResource("icon/line0" + l + "i.png");
        imgicon = new ImageIcon(url);

        Lines[l].setIcon(imgicon);
        p.oF.lineWidthSlider.setValue(p.lineSelected);

        p.reselectCanvas(0);
    }
}
