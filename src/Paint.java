import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.ArrayList;

/**
 *  Paint.java
 *
 *  This contains the main method, and the View.
 *
 *  When initialized, it will create a desktop environment for the program with a ToolFrame (the left-hand menu for
 *  picking tools), an OptionsFrame (the top-right menu for picking various options), and one open CanvasFrame that is
 *  adjusted according to screen size.
 *
 *  It uses PaintController.java as a controller.
 *
 *  @author Jan Øyvind Kruse
 *  @version 1.0
 */

public class Paint extends JFrame {

    //  GLOBAL VARIABLES

    static String app_name = "KB Paint";

    int bx, by;  //  Used for canvas glue
    int toolSelectedRow = 3, toolSelectedColumn = 1;
    int lineSelected = 1, canvasCount = 0, canvasCountChronological = 0;
    int lastCanvas = -1, fillSelected = 0, brushSelected = 0;
    static int width, height;
    static boolean valid, closeApp, brushEditorOpen = false;

    String tempString;
    static FileHandling fH;
    static PaintController pC;

    URL url;
    ImageIcon imgicon;

    //  VARIOUS GUI COMPONENTS

    JDesktopPane desktop;

    static Paint myFrame;
    static CanvasFrame cF;
    public OptionsFrame oF;
    public ToolFrame tF;
    public ImageHandling iH;
    public BrushEditor bE;
    public ClipboardHandling cH;

    //  MENU ITEMS

    static JMenuItem newFile = new JMenuItem("New…");
    static JMenuItem openFile = new JMenuItem("Open…");
    static JMenuItem saveFile = new JMenuItem("Save…");
    static JMenuItem editUndo = new JMenuItem("Undo");
    static JMenuItem editCut = new JMenuItem("Cut");
    static JMenuItem editCopy = new JMenuItem("Copy");
    static JMenuItem editPaste = new JMenuItem("Paste");
    static JMenuItem editBrushEditor = new JMenuItem("Brush Editor");
    static JMenuItem imageBlackWhite = new JMenuItem("Black & White");
    static JMenuItem imageInvert = new JMenuItem("Invert");
    static JMenuItem imageBlur = new JMenuItem("Blur");
    static JMenuItem imageSharpen = new JMenuItem("Sharpen");
    static JMenuItem imageSize = new JMenuItem("Image Size…");
    static JMenuItem imageCrop = new JMenuItem("Crop");
    static JMenuItem imageRotate180 = new JMenuItem("180°");
    static JMenuItem imageRotate90CW = new JMenuItem("90° CW");
    static JMenuItem imageRotate90CCW = new JMenuItem("90° CCW");
    static JMenuItem imageFlipHorizontal = new JMenuItem("Flip Canvas Horizontal");
    static JMenuItem imageFlipVertical = new JMenuItem("Flip Canvas Vertical");
    static JMenuItem helpAbout = new JMenuItem("About " + app_name + "…");

    static JMenu windowMenu = new JMenu("Window");

    //  ARRAYLISTS FOR HANDLING CANVASES

    ArrayList<JMenuItem> windowsOpen = new ArrayList<JMenuItem>();


    public Paint() {

        //  Initialize and load the various components that make up the application so they can be used later.

        super(app_name);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        width = (int)screenSize.getWidth();
        height = (int)screenSize.getHeight();

        desktop = new JDesktopPane();
        desktop.setBackground(Color.lightGray);

        int cWidth = width - 358;
        int cHeight = height - 106;

        bx = cWidth / 10; by = cHeight / 10;

        iH = new ImageHandling( this );
        cH = new ClipboardHandling();

        fH = new FileHandling();
        fH.addPaint( this );  // tell FileHandling about Paint

        pC = new PaintController( this , iH, cH, fH);

        tF = new ToolFrame();
        tF.createToolMenu(10, 10, this, pC);

        oF = new OptionsFrame();
        oF.createOptionsMenu( width - 260, 10, this, pC);

        cF = new CanvasFrame();
        cF.createFrame(88, 10, cWidth, cHeight, "", this, iH, pC);  // The first canvas window when opening program

        pC.initFrames( tF, oF, cF );  // Tell PaintController about the various frames.

        setContentPane(desktop);
        desktop.addComponentListener( pC );
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }


    private static void createAndShowGUI() {

        myFrame = new Paint();
        JMenuBar menuRow = new JMenuBar();  // Add the Menu
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu imageMenu = new JMenu("Image");
        JMenu imageMenuAdjust = new JMenu("Adjustments");
        JMenu imageMenuRotate = new JMenu("Rotate Canvas");
        JMenu helpMenu = new JMenu("Help");

        menuRow.add(fileMenu);
        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        menuRow.add(editMenu);
        editMenu.add(editUndo);
        editMenu.addSeparator();
        editMenu.add(editCut);
        editMenu.add(editCopy);
        editMenu.add(editPaste);
        editMenu.addSeparator();
        editMenu.add(editBrushEditor);
        menuRow.add(imageMenu);
        imageMenu.add(imageMenuAdjust);
        imageMenu.addSeparator();
        imageMenu.add(imageCrop);
        imageMenu.add(imageSize);
        imageMenu.add(imageMenuRotate);
        imageMenuAdjust.add(imageBlackWhite);
        imageMenuAdjust.add(imageInvert);
        imageMenuAdjust.addSeparator();
        imageMenuAdjust.add(imageBlur);
        imageMenuAdjust.add(imageSharpen);
        imageMenuRotate.add(imageRotate180);
        imageMenuRotate.add(imageRotate90CW);
        imageMenuRotate.add(imageRotate90CCW);
        imageMenuRotate.addSeparator();
        imageMenuRotate.add(imageFlipHorizontal);
        imageMenuRotate.add(imageFlipVertical);
        menuRow.add(windowMenu);
        menuRow.add(helpMenu);
        helpMenu.add(helpAbout);

        myFrame.setJMenuBar(menuRow);

        KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
        KeyStroke ctrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
        KeyStroke ctrlZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
        KeyStroke ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
        KeyStroke ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
        KeyStroke ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
        KeyStroke ctrlB = KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
        KeyStroke ctrlI = KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
        KeyStroke altCtrlI = KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK | InputEvent.ALT_MASK );

        newFile.setAccelerator(ctrlN);
        openFile.setAccelerator(ctrlO);
        saveFile.setAccelerator(ctrlS);
        editUndo.setAccelerator(ctrlZ);
        editCut.setAccelerator(ctrlX);
        editCopy.setAccelerator(ctrlC);
        editPaste.setAccelerator(ctrlV);
        imageBlackWhite.setAccelerator(ctrlB);
        imageInvert.setAccelerator(ctrlI);
        imageSize.setAccelerator( altCtrlI );

        newFile.addActionListener( pC );
        openFile.addActionListener( pC );
        saveFile.addActionListener( pC );
        editUndo.addActionListener( pC );
        editCut.addActionListener( pC );
        editCopy.addActionListener( pC );
        editPaste.addActionListener( pC );
        editBrushEditor.addActionListener( pC );
        imageBlackWhite.addActionListener( pC );
        imageInvert.addActionListener( pC );
        imageBlur.addActionListener( pC );
        imageSharpen.addActionListener( pC );
        imageCrop.addActionListener( pC );
        imageSize.addActionListener( pC );
        imageRotate180.addActionListener( pC );
        imageRotate90CW.addActionListener( pC );
        imageRotate90CCW.addActionListener( pC );
        imageFlipHorizontal.addActionListener( pC );
        imageFlipVertical.addActionListener( pC );
        helpAbout.addActionListener( pC );

        // Add a custom icon for the program
        URL url = Paint.class.getResource("icon/icon.png");
        ImageIcon imgicon = new ImageIcon(url);
        myFrame.setIconImage(imgicon.getImage());
        myFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        myFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                // Close all the open canvas windows one by one.
                int j = windowMenu.getItemCount();
                closeApp = true;

                for(int i = 0; i < j; i++) {

                    cF.frames.get(0).toFront();
                    cF.frames.get(0).doDefaultCloseAction();

                    if(!closeApp)
                        break;
                }

                if(closeApp)
                    System.exit(0);
            }
        });

        myFrame.setVisible(true);
        myFrame.setSize(width, height);
        myFrame.setExtendedState(myFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        try {
            cF.frames.get(0).setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    // =====================================================================

    void lastCanvas() {

        //  When selecting from the Tool or Option frames, the current CanvasFrame will lose focus,
        //  this stores the last canvas that was selected, so it can be reselected again.

        for(int i = 0; i < windowMenu.getItemCount(); i++) {

            if( cF.frames.get(i).isSelected() ) {
                lastCanvas = i;
            }
        }
    }

    void reselectCanvas(int i) {

        // Used for restoring focus to CanvasFrame when user picks something from the Tool or Option Frame

        if(lastCanvas > -1) {

            cF.frames.get(lastCanvas).toFront();

            try {
                cF.frames.get(lastCanvas).setSelected(true);

                if( cF.frameCanvas.get(lastCanvas).getPhase() != 0) {

                    // reset phase (for the Quad Curve Tool)
                    if(getToolSelected() != 11) {

                        if( i != 2 || ( i == 2 && cF.frameCanvas.get(lastCanvas).text.equals("") ) ) {

                            //  Do not reset phase for the text tool, yet...
                            iH.restoreBackupImage(cF.frameCanvas.get(lastCanvas));
                            cF.frameCanvas.get(lastCanvas).setPhase(0);
                        }
                    }
                }

                if( cF.frameCanvas.get(lastCanvas).getSelectionActive() != 0) {

                    if( getToolSelected() > 0 ) {  // not the Hand Tool and not the Selection Tool

                        // When there is a selection active, and user picks a different tool
                        // than the hand tool, remove the selection

                        if(i == 0)
                            iH.restoreBackupImage(cF.frameCanvas.get(lastCanvas));

                        if( getToolSelected() > 1 || ( getToolSelected() == 1 && cF.frameCanvas.get(lastCanvas).handToolUsed ))
                            cF.frameCanvas.get(lastCanvas).setSelectionActive(0);
                    }
                }

                if( cF.frameCanvas.get(lastCanvas).text != "") {
                    iH.textFunction(i, cF.frameCanvas.get(lastCanvas));
                }
            } catch (PropertyVetoException e1) {
                e1.printStackTrace();
            }
        }
    }


    public void checkCoordinates(String cX, String cY) {

        //  Check to see if the dimensions given by the user for a new canvas are reasonable.

        width = 0;
        height = 0;
        valid = false;

        try {

            width = Integer.parseInt(cX);
            height = Integer.parseInt(cY);
            valid = true;

            if(width < 1 || width > 10000 || height < 1 || height > 10000) {
                valid = false;
            }
        }
        catch (NumberFormatException f) {
        }
    }


    // ====  GETTERS (for Canvas.java)  ====================================

    int getToolSelected() {
        String s = "" + toolSelectedRow + toolSelectedColumn;
        return Integer.parseInt(s);
    }

    int getLineSize() {
        return lineSelected;
    }
    boolean getAntiAlias() { return oF.antiAlias.isSelected(); }

    Color processColor(Color c, int flag) {

        int alpha = 255;

        if(flag == 1) {
            alpha = oF.opacitySlider.getValue() * 255 / 100;
        }

        Color d = new Color( c.getRed(), c.getGreen(), c.getBlue(), alpha );
        return d;
    }

    Color getSelectedForeground() { return processColor( tF.foreground, 1 ); }
    Color getSelectedBackground(int flag) { return processColor( tF.background, flag ); }

    // ====  SETTERS (used temporarily)  ====================================

    void setCanvasName(String s) { tempString = s; }

    // =====================================================================


    void loadIcon(String s) {

        url = Paint.class.getResource("icon/icon" + s + ".png");
        imgicon = new ImageIcon(url);

    }


    void selectTool(int row, int column) {

        //  Select a tool from the Tool Frame

        int restoreBackup = 0;

        loadIcon("" + toolSelectedRow + toolSelectedColumn);
        tF.Menu[toolSelectedRow][toolSelectedColumn].setIcon(imgicon);

        //  If the previous tool was the Hand Tool, then reset the selection inside the canvas.
        if(getToolSelected() == 0 ) {
            restoreBackup = 1;
        }

        //  If the previous tool was the text tool.
        if(getToolSelected() == 11)
            restoreBackup = 2;

        toolSelectedRow = row;
        toolSelectedColumn = column;

        //  If the new tool is the Airbrush Tool, reset the line width
        if(getToolSelected() == 21)
            tF.updateLineWidth( 1 );

        //  If the new tool is the Airbrush Tool or the Eraser Tool, set opacity to 100.
        if(getToolSelected() == 21 || getToolSelected() == 41)
            oF.opacitySlider.setValue(100);

        loadIcon("" + toolSelectedRow + toolSelectedColumn + "i");
        tF.Menu[toolSelectedRow][toolSelectedColumn].setIcon(imgicon);

        reselectCanvas(restoreBackup);
    }


    void saveFile(Canvas c, int i) {

        //  Save the image in a canvas to a file.

        cF.tempImage = c.getImage();
        String temp;

        try {
            temp = fH.saveFile( cF.tempImage, cF.frames.get(i).getTitle() );

            if(!temp.equals(null)) {  // Update internal frame title and JMenuItem with the new name

                cF.frames.get(i).setTitle( temp );
                cF.frameCanvas.get(i).edited = false;
                windowsOpen.get(i).setText( temp );

            }

        } catch (NullPointerException e1) {
        }

        cF.tempImage = null;
    }

    void openBrushEditor(int i) {

        //  Open the brush editor, or if it is already open, bring it to the front.

        if(!brushEditorOpen) {
            bE = new BrushEditor( this, oF, i);
            brushEditorOpen = true;
        }
        else {
            bE.BringToFront();
        }
    }
}
