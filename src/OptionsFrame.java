import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 *  OptionsFrame.java
 *
 *  This is the top-right menu for picking various tools.
 *
 *  @author Jan Øyvind Kruse
 *  @version 1.0
 */

public class OptionsFrame extends JInternalFrame {

    MyOptionsMenu optionsFrame;

    JSlider lineWidthSlider;
    JSlider opacitySlider;
    JCheckBox antiAlias;

    int CustomB[][][] = {
            {
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 1, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },
            {
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 1, 0, 0, 0}
            },

            { // 18  / 2
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 1, 1, 0, 0},
                    {0, 0, 0, 0, 1, 1, 1, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 1, 1, 1, 0, 0, 0, 0},
                    {0, 0, 1, 1, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 1  Circle 4
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0}
            },

    };

    public JButton[] Brushes = new JButton[2];
    public JToggleButton[] CustomBrushes = new JToggleButton[2];
    public JToggleButton[] Styles = new JToggleButton[3];
    public JToggleButton[] Effects = new JToggleButton[2];
    public JButton[] ShapeFill = new JButton[4];
    public JLabel fillLabel = new JLabel();

    JComboBox<String> fontList;
    JComboBox<String> fontSize;

    Paint p;
    PaintController pC;

    // ===============================================================================================================
    // CREATE OPTIONS MENU

    void copyBrush(int from, int to) {

        //  Used for the Pencil Button in the Options Menu

        for(int j = 0; j < 9; j++) { // rows

            for(int k = 0; k < 9; k++) { // columns

                if( CustomB[ from ][j][k] == 1 )
                    CustomB[ to ][j][k] = 1;
                else
                    CustomB[ to ][j][k] = 0;
            }
        }
    }


    ImageIcon CustomIcon(int i, int inverse) {

        Color c;

        ImageIcon imgicon = new ImageIcon();

        BufferedImage temp = new BufferedImage( 16, 16, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g = (Graphics2D)temp.getGraphics();

        if(inverse == 0) {
            g.setPaint(new Color(246,246,246));
            c = Color.black;
        }
        else {
            g.setPaint(new Color(9,9,9));
            c = Color.white;
        }

        g.fillRect(0, 0, 16, 16);
        int rgb = c.getRGB();

        for(int j = 0; j < 9; j++) { // rows

            for(int k = 0; k < 9; k++) { // columns

                if( CustomB[i][j][k] == 1 )
                    temp.setRGB(k + 3 , j + 4, rgb);

            }
        }

        imgicon.setImage( temp );
        return imgicon;
    }


    public void createOptionsMenu(int x, int y, Paint View, PaintController Controller) {

        p = View;
        pC = Controller;

        URL url;

        optionsFrame = new MyOptionsMenu(x, y);
        optionsFrame.setVisible(true);
        optionsFrame.setLayout(null);

        // ===========================================================================================================

        JPanel lineOptions = new JPanel();
        lineOptions.setVisible(true);
        lineOptions.setLayout(null);

        antiAlias = new JCheckBox("Anti-aliasing");
        antiAlias.setBounds(10, 20, 100, 20);
        antiAlias.setSelected(true);
        antiAlias.addChangeListener( optionsFrame );

        lineOptions.add(antiAlias);

        String[] brushToolTip = { "Pencil", "Pen" };

        for(int i = 0; i < 2; i++) {

            Brushes[i] = new JButton();
            url = Paint.class.getResource("icon/iconoF" + i + ".png");
            ImageIcon imgicon = new ImageIcon(url);

            Brushes[i].setIcon(imgicon);
            Brushes[i].setBounds(155 + (28*i), 17, 26, 23 );

            Brushes[i].setToolTipText( brushToolTip[i] );
            lineOptions.add( Brushes[i] );

            Brushes[i].addActionListener( pC );
        }

        String[] customBrushToolTip = { "Brush A (Double-Click to edit)", "Brush B (Double-Click to edit)" };

        for(int i = 0; i < 2; i++) {

            String s = "";

            CustomBrushes[i] = new JToggleButton();

            if(i == 0) {
                s = "i";
                CustomBrushes[i].setSelected(true);
            }

            ImageIcon imgicon = CustomIcon(i, 0) ;

            CustomBrushes[i].setIcon(imgicon);
            CustomBrushes[i].setBounds(119 + (17*i), 17, 15, 15 );

            CustomBrushes[i].setToolTipText( customBrushToolTip[i] );
            lineOptions.add( CustomBrushes[i] );

            CustomBrushes[i].addActionListener( pC );
            CustomBrushes[i].addMouseListener( pC );
        }


        JLabel sliderLabel = new JLabel("Line Width", JLabel.LEFT);
        sliderLabel.setBounds(10, 45, 100, 20);
        lineOptions.add(sliderLabel);

        lineWidthSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, 1);  // Min / Max / Default
        lineWidthSlider.addChangeListener( optionsFrame );
        lineWidthSlider.setBounds(85, 40, 125, 50);

        lineWidthSlider.setMajorTickSpacing(10);
        lineWidthSlider.setMinorTickSpacing(5);
        lineWidthSlider.setPaintTicks(true);
        lineWidthSlider.setPaintLabels(true);

        Font font = new Font("Serif", Font.ITALIC, 6);
        lineWidthSlider.setFont(font);
        lineOptions.add(lineWidthSlider);

        JLabel opacityLabel = new JLabel("Opacity", JLabel.LEFT);

        opacityLabel.setBounds(10, 85, 100, 20);
        lineOptions.add(opacityLabel);

        opacitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);  // Min / Max / Default
        opacitySlider.addChangeListener( optionsFrame );

        opacitySlider.setBounds(85, 85, 125, 50);
        opacitySlider.setMajorTickSpacing(20);
        opacitySlider.setMinorTickSpacing(10);
        opacitySlider.setPaintTicks(true);
        opacitySlider.setPaintLabels(true);
        opacitySlider.setFont(font);

        lineOptions.add(opacitySlider);

        TitledBorder lineOptionsBorder;
        lineOptionsBorder = BorderFactory.createTitledBorder("Line Options");

        lineOptions.setBorder(lineOptionsBorder);
        lineOptions.setBounds(10, 10, 220, 140);

        optionsFrame.add(lineOptions);

        //  ==========================================================================================================

        JPanel textOptions = new JPanel();
        textOptions.setVisible(true);
        textOptions.setLayout(null);

        JLabel fontLabel = new JLabel("Font", JLabel.LEFT);
        fontLabel.setBounds(10, 20, 100, 20);
        textOptions.add(fontLabel);

        String fonts[] =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        fontList = new JComboBox<>(fonts);

        fontList.setBounds(10, 40, 140, 25);
        fontList.addActionListener( pC );

        textOptions.add(fontList);

        // **

        JLabel sizeLabel = new JLabel("Size", JLabel.LEFT);
        sizeLabel.setBounds(159, 20, 100, 20);
        textOptions.add(sizeLabel);

        String fontSizeList[] = {
                "8", "9", "10", "11", "12", "14", "16", "18",
                "20", "22", "24", "26", "28", "36", "48", "72" };

        fontSize = new JComboBox<>(fontSizeList);
        fontSize.setSelectedItem("48");

        fontSize.setBounds(159, 40, 50, 25);
        fontSize.setEditable(true);
        fontSize.addActionListener( pC );

        fontSize.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {

            // Make it impossible to input anything else than digits into fontSize

            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (fontSize.getEditor().getItem().toString().length() < 4) {
                    if (!(Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                        getToolkit().beep();
                        e.consume();
                    }
                } else {
                    e.consume();
                }
            }
        });

        textOptions.add(fontSize);

        // **

        String[] stylesToolTip = { "Bold", "Italic", "Underline" };

        for(int i = 0; i < 3; i++) {

            Styles[i] = new JToggleButton();
            url = Paint.class.getResource("icon/iconA" + i + ".png");
            ImageIcon imgicon = new ImageIcon(url);

            Styles[i].setIcon(imgicon);
            Styles[i].setBounds(127 + (28*i), 73, 26, 23 );

            Styles[i].setToolTipText( stylesToolTip[i] );
            textOptions.add( Styles[i] );

            Styles[i].addActionListener( pC );
        }

        // **

        String[] effectsToolTip = { "Stroke", "Drop Shadow" };

        for(int i = 0; i < 2; i++) {

            Effects[i] = new JToggleButton();
            url = Paint.class.getResource("icon/iconB" + i + ".png");
            ImageIcon imgicon = new ImageIcon(url);

            Effects[i].setIcon(imgicon);
            Effects[i].setBounds(11 + (28*i), 73, 26, 23 );

            Effects[i].setToolTipText( effectsToolTip[i] );
            textOptions.add( Effects[i] );

            Effects[i].addActionListener( pC );
        }

        // **

        TitledBorder textOptionsBorder;
        textOptionsBorder = BorderFactory.createTitledBorder("Text Options");

        textOptions.setBorder(textOptionsBorder);
        textOptions.setBounds(10, 158, 220, 107);

        optionsFrame.add(textOptions);

        //  ==========================================================================================================
        //  fillOptions

        JPanel fillOptions = new JPanel();
        fillOptions.setVisible(true);
        fillOptions.setLayout(null);

        String[] shapeFillToolTip = { "Solid Color", "Horizontal Gradient", "Vertical Gradient", "Radial Gradient" };

        for(int i = 0; i < 4; i++) {

            ShapeFill[i] = new JButton();

            url = Paint.class.getResource("icon/iconG" + i + ".png");
            ImageIcon imgicon = new ImageIcon(url);
            ShapeFill[i].setIcon(imgicon);

            ShapeFill[i].setBounds(15 + (28*i), 21, 26, 23 );
            ShapeFill[i].setToolTipText( shapeFillToolTip[i] );
            fillOptions.add( ShapeFill[i] );

            ShapeFill[i].addActionListener( pC );
        }

        fillLabel = new JLabel("Solid", JLabel.LEFT);
        fillLabel.setBounds(132, 22, 100, 20);
        fillOptions.add(fillLabel);

        TitledBorder fillOptionsBorder;
        fillOptionsBorder = BorderFactory.createTitledBorder("Shape Fill Options");

        fillOptions.setBorder(fillOptionsBorder);
        fillOptions.setBounds(10, 273, 220, 59);

        optionsFrame.add(fillOptions);

        //  ==========================================================================================================

        setRootPaneCheckingEnabled(false);
        javax.swing.plaf.InternalFrameUI ifu= optionsFrame.getUI();
        ((javax.swing.plaf.basic.BasicInternalFrameUI)ifu).setNorthPane(null);

        p.desktop.add(optionsFrame, new Integer( 9 ));
        try {
            optionsFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }

    }


    public class MyOptionsMenu extends JInternalFrame implements ChangeListener {

        public MyOptionsMenu(int offsetX, int offsetY) {
            super("Options",
                    false, //resizable
                    false, //closable
                    false, //maximizable
                    false);//iconifiable

            setSize(250, 358);

            setLocation(offsetX, offsetY);
        }

        @Override
        public void stateChanged(ChangeEvent changeEvent) {

            if( changeEvent.getSource().equals( lineWidthSlider )) {

                int a = lineWidthSlider.getValue();

                if(a == 0) {
                    lineWidthSlider.setValue(1);
                    a++;
                }

                p.tF.updateLineWidth( a );
            }
        }
    }
}
