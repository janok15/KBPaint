import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;

/**
 *  PaintController.java
 *
 *  This file serves as a controller for Paint.java, ToolFrame.java, OptionsFrame.java
 *  and CanvasFrame.java
 *
 *  @author Jan Øyvind Kruse
 *  @version 1.0
 */

public class PaintController implements ActionListener, MouseListener, ComponentListener {

    Paint p;
    ImageHandling iH;
    ClipboardHandling cH;
    FileHandling fH;
    ToolFrame tF;
    OptionsFrame oF;
    CanvasFrame cF;

    PaintController(Paint View, ImageHandling myImageHandling, ClipboardHandling myClipboardHandling,
                    FileHandling myFileHandling) {

        //  Initialize the PaintController, and let it know about the other components.

        p = View;
        iH = myImageHandling;
        cH = myClipboardHandling;
        fH = myFileHandling;
    }

    void initFrames(ToolFrame myToolFrame, OptionsFrame myOptionsFrame,
                    CanvasFrame myCanvasFrame) {
        tF = myToolFrame;
        oF = myOptionsFrame;
        cF = myCanvasFrame;
    }

    @Override public void componentMoved(ComponentEvent componentEvent) {}
    @Override public void componentShown(ComponentEvent componentEvent) {}
    @Override public void componentHidden(ComponentEvent componentEvent) {}
    @Override public void mouseClicked(MouseEvent mouseEvent) {}
    @Override public void mouseReleased(MouseEvent mouseEvent) {}
    @Override public void mouseEntered(MouseEvent mouseEvent) {}
    @Override public void mouseExited(MouseEvent mouseEvent) {}

    @Override
    public void mousePressed(MouseEvent e) {

        Color temp = null, c;

        //  OPTION FRAME - CUSTOM BRUSHES  (MousePressed)
        for(int i = 0; i < 2; i++) {

            int j = 1;

            if (e.getSource().equals(oF.CustomBrushes[i])) {

                //  Pick one of the two brushes from the short-list, or open the brush editor if double-clicked.

                if (i == 1) j = 0;

                oF.CustomBrushes[i].setSelected(true);
                p.brushSelected = i;

                oF.CustomBrushes[j].setSelected(false);
                oF.CustomBrushes[i].setIcon(oF.CustomIcon(i, 1));
                oF.CustomBrushes[j].setIcon(oF.CustomIcon(j, 0));

                p.selectTool(3, 0);  // Select the brush tool

                if (e.getClickCount() == 2) {
                    p.openBrushEditor(i);
                }
            }
        }

        //  TOOL FRAME - PALETTE COLORS  (MousePressed)
        for(int row = 0; row < 16; row++) {

            for(int col = 0; col < 4; col++) {

                //  Pick a new color when single-clicked, or open a JColorChooser when it is double-clicked.

                if(e.getSource().equals(tF.Colors[row][col])) {

                    c = tF.Colors[row][col].getBackground();

                    if (e.getClickCount() == 2) {
                        temp = JColorChooser.showDialog(p.desktop, "Pick a color", c);
                        tF.Colors[row][col].setBackground( c );

                        if(temp != null) {
                            c = temp;
                            tF.Colors[row][col].setBackground( temp );

                            tF.Colors[row][col].setToolTipText(
                                    "R: " + c.getRed() +
                                            " G:" + c.getGreen() +
                                            " B:" + c.getBlue() );
                        }
                    }

                    if(SwingUtilities.isRightMouseButton(e)) { tF.background = c; }
                    else { tF.foreground = c; }

                    tF.updateColorPanels();
                    p.reselectCanvas(0);
                }
            }
        }
    }

    // =====================================================================

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        // Move the options menu when the window is resized, so it stays in the top right corner
        p.width = p.desktop.getWidth();
        p.height = p.desktop.getHeight();
        oF.optionsFrame.setLocation(p.width - 260,10);
    }

    // =====================================================================

    @Override
    public void actionPerformed(ActionEvent e) {

        //  HELP - ABOUT
        if(e.getSource().equals(p.helpAbout)) {

            JOptionPane.showMessageDialog(p.desktop,
                    p.app_name + " was created\nfor a university project by:\n\nJan Øyvind Kruse,\nand Edmund Bosco.\n" +
                            "\n\n" +
                            "Thanks to the following for creating software\nthat served as inspiration:\n" +
                            "\n" +
                            "Bill Atkinson and Susan Kare (MacPaint)\n" +
                            "Dan Silva (Deluxe Paint)\n" +
                            "Thomas and John Knoll (Adobe Photoshop)\n\n", "About " + p.app_name + "…", JOptionPane.PLAIN_MESSAGE);
        }

        //  TOOL FRAME - VARIOUS TOOLS
        for(int row = 0; row < 10; row++) {
            for(int col = 0; col < 2; col++) {

                if(e.getSource().equals(tF.Menu[row][col])) {
                    p.selectTool(row, col);
                }
            }
        }

        //  TOOL FRAME - LINE WIDTH
        for(int l = 1; l < 5; l++) {
            if (e.getSource().equals(tF.Lines[l])) {
                tF.updateLineWidth(l);
            }
        }

        //  TOOL FRAME - PALETTE TOOLS
        for(int i = 0; i < 2; i++) {

            if (e.getSource().equals( tF.Palette[i])) {

                if(i == 0) {
                    tF.resetPalette();  //  Reset the palette colors
                }
                else {
                    Color c = tF.foreground;  //  Switch the foreground and background colors
                    tF.foreground = tF.background;
                    tF.background = c;
                    tF.updateColorPanels();
                }
                p.reselectCanvas(0);
            }
        }

        //  OPTION FRAME - PENS
        for(int b = 0; b < 2; b++) {

            //  Use the brush tool to give the appearance of drawing with a pencil or pen.

            if (e.getSource().equals(oF.Brushes[b])) {

                oF.CustomBrushes[0].setSelected(true);
                p.brushSelected = 0;

                oF.CustomBrushes[1].setSelected(false);
                oF.CustomBrushes[0].setIcon( oF.CustomIcon(b + 2, 1));
                oF.CustomBrushes[1].setIcon(oF.CustomIcon(1, 0));
                oF.copyBrush(b + 2,0);

                p.selectTool(3, 0);

                tF.updateLineWidth(1);
                oF.opacitySlider.setValue(10 - 5 * b );  //  Opacity:  10 for pencil, 5 for pen.
            }
        }

        //  OPTION FRAME - FONT
        if(e.getSource().equals(oF.fontList)) {
            p.reselectCanvas(0);
        }

        //  OPTION FRAME - FONT SIZE
        if(e.getSource().equals(oF.fontSize)) {
            p.reselectCanvas(0);
        }

        //  OPTION FRAME - TEXT EFFECTS (Stroke and Shadow)
        for(int i = 0; i < 2; i++) {

            int j = 1;

            if (e.getSource().equals(oF.Effects[i])) {

                if(i == 1) j = 0;

                oF.Effects[j].setSelected(false);

                String s = "B" + i, t = "B" + j;

                if( oF.Effects[i].isSelected() )
                    s += "i";

                p.loadIcon( s );
                oF.Effects[i].setIcon(p.imgicon);
                p.loadIcon( t );
                oF.Effects[j].setIcon(p.imgicon);

                p.reselectCanvas(0);
            }
        }

        //  OPTION FRAME - SHAPE FILL
        String[] labelText = { "Solid", "Horizontal", "Vertical", "Radial" };

        for(int i = 0; i < 4; i++) {

            if (e.getSource().equals(oF.ShapeFill[i])) {
                oF.fillLabel.setText( labelText[i] );
                p.fillSelected = i;
                p.reselectCanvas(0);
            }
        }

        //  OPTION FRAME - TEXT STYLES
        for(int i = 0; i < 3; i++) {

            if (e.getSource().equals(oF.Styles[i])) {

                String s = "A" + i;

                if( oF.Styles[i].isSelected() )
                    s += "i";

                p.loadIcon( s );
                oF.Styles[i].setIcon(p.imgicon);

                p.reselectCanvas(0);
            }
        }

        //  WINDOW MENU - PICK CANVAS
        for(int i = 0; i < p.windowMenu.getItemCount(); i++) {

            if(e.getSource().equals( p.windowsOpen.get(i) )) {

                cF.frames.get(i).toFront();
                try {
                    cF.frames.get(i).setSelected(true);
                } catch (PropertyVetoException e1) {
                    e1.printStackTrace();
                }
            }
        }

        //  FILE - NEW
        if(e.getSource().equals(p.newFile)) {

            // TODO This could be simplified somehow ...

            String xs = "1280", ys = "720";

            String[] resolution = new String[] {
                    "640 x 480", "800 x 600", "1024 x 768", "1152 x 864",
                    "1280 x 720", "1280 x 1024", "1600 x 1200", "1920 x 1080"
            };

            JComboBox<String> resolutionList = new JComboBox<>(resolution);

            resolutionList.setSelectedItem("1280 x 720");

            BufferedImage temp = cH.getImage();  // Try to get image data from the clipboard

            if(temp != null) {
                //  If there is image data, set the width and height in the textfields accordingly
                xs = "" + temp.getWidth(); ys = "" + temp.getHeight();

            }

            JTextField xField = new JTextField(xs, 5);
            JTextField yField = new JTextField(ys,5);
            JPanel myPanel = new JPanel();

            myPanel.add(resolutionList);
            myPanel.add(Box.createHorizontalStrut(15)); // a spacer
            myPanel.add(new JLabel("Width:"));
            myPanel.add(xField);
            myPanel.add(Box.createHorizontalStrut(15)); // a spacer
            myPanel.add(new JLabel("Height:"));
            myPanel.add(yField);

            resolutionList.addActionListener (new ActionListener () {
                public void actionPerformed(ActionEvent e) {

                    switch( (String)resolutionList.getSelectedItem() ) {
                        case "640 x 480" : xField.setText("" + 640); yField.setText("" + 480); break;
                        case "800 x 600" : xField.setText("" + 800); yField.setText("" + 600); break;
                        case "1024 x 768" : xField.setText("" + 1024); yField.setText("" + 768); break;
                        case "1152 x 864" : xField.setText("" + 1152); yField.setText("" + 864); break;
                        case "1280 x 720" : xField.setText("" + 1280); yField.setText("" + 720); break;
                        case "1280 x 1024" : xField.setText("" + 1280); yField.setText("" + 1024); break;
                        case "1600 x 1200" : xField.setText("" + 1600); yField.setText("" + 1200); break;
                        case "1920 x 1080" : xField.setText("" + 1920); yField.setText("" + 1080); break;
                    }
                }
            });

            int result = JOptionPane.showConfirmDialog(p.desktop, myPanel,
                    "New Canvas - Enter Width and Height", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {

                p.checkCoordinates(xField.getText(), yField.getText());

                if(p.valid) {

                    int newWidth = p.width + p.bx, newHeight = p.height + p.by;

                    cF.createFrame(95, 10, newWidth , newHeight, "", p, iH, this );  // A new canvas window
                }
                else {
                    JOptionPane.showMessageDialog(p.desktop, "Invalid dimensions.");
                }
            }
        }

        //  FILE - OPEN FILE
        if(e.getSource().equals(p.openFile)) {

            // Ask FileHandling to open a JFileChooser so the user can select a file to open
            cF.tempImage = fH.openFile();

            if(cF.tempImage != null) {

                // If there is a tempImage, open a new internal frame with a canvas and display it
                cF.createFrame(95,10, cF.tempImage.getWidth() + p.bx, cF.tempImage.getHeight() + p.by,
                        p.tempString, p, iH, this );
            }
        }

        // ===========================================================================================================

        //  All of the following relies on a canvas window being selected

        for(int i = 0; i < p.windowMenu.getItemCount(); i++) {

            if( cF.frames.get(i).isSelected() ) {

                Canvas c = cF.frameCanvas.get(i);

                //  FILE - SAVE FILE
                if(e.getSource().equals(p.saveFile)) {
                    p.saveFile(c, i);
                }

                //  EDIT - UNDO
                if(e.getSource().equals(p.editUndo)) {

                    if(c.edited) {
                        iH.restoreBackupImage( c );  // Only restore a backup image if the image has been edited.

                        if(c.selectionActive == 1) {
                            c.setSelectionActive(0);
                        }
                        c.lastAction = "Undo";
                    }
                }

                //  EDIT - CUT / COPY
                if(e.getSource().equals(p.editCut) || e.getSource().equals(p.editCopy)) {

                    if( c.selectionActive == 1 ) {

                        int delete = 0;

                        c.sImage = c.bImage.getSubimage(c.x1, c.y1, c.x2, c.y2);
                        cH.copyImage( c.sImage );

                        //  If CUT was selected, also delete the area from the canvas
                        if(e.getSource().equals(p.editCut))
                            delete = 1;

                        c.deleteSelection( delete );
                    }
                }

                //  EDIT - PASTE
                if(e.getSource().equals(p.editPaste)) {

                    c.sImage = cH.getImage();  // Try to get image data from the clipboard

                    if(c.sImage != null) {

                        // If there is already a selection active, restore backup and remove selection.

                        if(c.selectionActive == 1) {
                            iH.restoreBackupImage(c);
                            c.setSelectionActive(0);
                        }

                        // If successful, paste the image into the canvas as a temporary "layer"
                        // that can be moved around with the Hand Tool

                        iH.makeBackupImage(c.image, c);

                        c.selectionActive = 2;
                        c.x1 = 0; c.y1 = 0; c.x2 = c.sImage.getWidth(); c.y2 = c.sImage.getHeight();

                        p.selectTool(0,0);  // Select the Hand Tool

                        c.g2d.drawImage( c.sImage, 0,0, null);
                        c.repaint();
                    }
                }

                //  EDIT - BRUSH EDITOR
                if(e.getSource().equals(p.editBrushEditor)) {
                    p.openBrushEditor(0);
                }

                //  IMAGE - ADJUSTMENTS - BLACK & WHITE
                if(e.getSource().equals(p.imageBlackWhite)) {
                    if(c.selectionActive == 1) {
                        iH.restoreBackupImage(c);
                        c.setSelectionActive(0);
                    }
                    iH.adjustCanvas(0, c);
                }

                //  IMAGE - ADJUSTMENTS - INVERT
                if(e.getSource().equals(p.imageInvert)) {
                    if(c.selectionActive == 1) {
                        iH.restoreBackupImage(c);
                        c.setSelectionActive(0);
                    }
                    iH.adjustCanvas(1, c);
                }

                //  IMAGE - ADJUSTMENTS - BLUR
                if(e.getSource().equals(p.imageBlur)) {
                    if(c.selectionActive == 1) {
                        iH.restoreBackupImage(c);
                        c.setSelectionActive(0);
                    }
                    iH.blurCanvas(c);
                }

                //  IMAGE - ADJUSTMENTS - SHARPEN
                if(e.getSource().equals(p.imageSharpen)) {
                    if(c.selectionActive == 1) {
                        iH.restoreBackupImage(c);
                        c.setSelectionActive(0);
                    }
                    iH.sharpenCanvas(c);
                }

                //  IMAGE - CROP
                if(e.getSource().equals(p.imageCrop)) {
                    iH.cropCanvas(c);
                }

                //  IMAGE - IMAGE SIZE
                if(e.getSource().equals(p.imageSize)) {

                    int cX = c.getWidth();
                    int cY = c.getHeight();

                    JTextField xField = new JTextField("" + cX, 5);
                    JTextField yField = new JTextField("" + cY, 5);

                    JPanel myPanel = new JPanel();

                    myPanel.add(new JLabel("Width:"));
                    myPanel.add(xField);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(new JLabel("Height:"));
                    myPanel.add(yField);

                    int result = JOptionPane.showConfirmDialog(p.desktop, myPanel,
                            "Resize - Enter new width and height", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {

                        p.checkCoordinates(xField.getText(), yField.getText());

                        if (p.valid) {

                            if(c.selectionActive == 1) {  // Send to the canvas and resize its image
                                iH.restoreBackupImage(c);
                                c.setSelectionActive(0);
                            }

                            iH.resizeCanvas(p.width, p.height, c);
                        } else {
                            JOptionPane.showMessageDialog(p.desktop, "Invalid dimensions.");
                        }
                    }
                }

                //  IMAGE - ROTATE 180°
                if(e.getSource().equals(p.imageRotate180)) {
                    if(c.selectionActive == 1) {
                        iH.restoreBackupImage(c);
                        c.setSelectionActive(0);
                    }
                    iH.rotateCanvas(180, c);
                }

                //  IMAGE - ROTATE 90° CW
                if(e.getSource().equals(p.imageRotate90CW)) {
                    if(c.selectionActive == 1) {
                        iH.restoreBackupImage(c);
                        c.setSelectionActive(0);
                    }
                    iH.rotateCanvas(90, c);
                }

                //  IMAGE - ROTATE 90° CCW
                if(e.getSource().equals(p.imageRotate90CCW)) {
                    if(c.selectionActive == 1) {
                        iH.restoreBackupImage(c);
                        c.setSelectionActive(0);
                    }
                    iH.rotateCanvas(270, c);
                }

                //  IMAGE - FLIP HORIZONTAL
                if(e.getSource().equals(p.imageFlipHorizontal)) {
                    if(c.selectionActive == 1) {
                        iH.restoreBackupImage(c);
                        c.setSelectionActive(0);
                    }
                    iH.flipCanvas(0, c);
                }

                //  IMAGE - FLIP VERTICAL
                if(e.getSource().equals(p.imageFlipVertical)) {
                    if(c.selectionActive == 1) {
                        iH.restoreBackupImage(c);
                        c.setSelectionActive(0);
                    }
                    iH.flipCanvas(1, c);
                }

                // FRAME SELECTED ENDS
            }
        }
    }
}
