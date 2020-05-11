import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 *  FileHandling.java
 *
 *  FileHandling opens and saves files in the following file formats:
 *  *.BMP, *.PNG, *.GIF and *.JPEG
 *
 *  @author Jan Øyvind Kruse
 *  @version 1.0
 */

public class FileHandling {

    Paint parentPaint;
    JFileChooser fc;
    File file, currentDirectory;
    String suffix, name;
    boolean overwriteFile;

    public BufferedImage openFile() {

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files (*.BMP, *.PNG, *.GIF, *.JPG, *.JPEG)",
                "bmp", "png", "gif", "jpg", "jpeg");

        fc = new JFileChooser();
        fc.setCurrentDirectory( currentDirectory );  // Sets directory to the last one used in KB Paint this session, if any.

        fc.setFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);

        int returnVal = fc.showOpenDialog(parentPaint);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            file = fc.getSelectedFile();

            try {

                BufferedImage img = ImageIO.read(file);
                parentPaint.setCanvasName( file.getName() );

                currentDirectory = fc.getCurrentDirectory();

                return img;

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }


    void checkFile() {

        file = fc.getSelectedFile();

        if (fc.getFileFilter() instanceof FileNameExtensionFilter) {

            String[] exts = ((FileNameExtensionFilter)fc.getFileFilter()).getExtensions();
            String nameLower = file.getName().toLowerCase();

            suffix = exts[0];

            for (String ext : exts) { // check if the file already has a valid extension

                if(!nameLower.endsWith('.' + ext.toLowerCase())) {
                    // if it does not, append the first extension from the selected filter
                    file = new File(file.toString() + '.' + exts[0]);
                }
            }
        }
    }


    public String saveFile(BufferedImage img, String n) {

        suffix = null;
        name = n;

        fc = new JFileChooser();
        fc.setCurrentDirectory( currentDirectory );

        String[][] extensions = {
                { "PNG (*.PNG)", "png" },
                { "BMP (*.BMP)", "bmp" },
                { "CompuServe GIF (*.GIF)", "gif" },
                { "JPEG (*.JPG)", "jpg" }
        };

        FileNameExtensionFilter[] f = new FileNameExtensionFilter[4];

        for(int i = 0; i < 4; i++) {

            f[i] = new FileNameExtensionFilter( extensions[i][0], extensions[i][1] );
            fc.addChoosableFileFilter( f[i] );
        }

        fc.setAcceptAllFileFilterUsed(false);
        fc.setSelectedFile(new File(name));

        if( fc.showSaveDialog( parentPaint ) == JFileChooser.APPROVE_OPTION ) {

            boolean doExport = true;
            overwriteFile = false;

            checkFile();

            while( doExport && file.exists() && !overwriteFile ) {

                overwriteFile =
                        (JOptionPane.showConfirmDialog(parentPaint,
                                "Replace file?",
                                "Export Settings",
                                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);

                if(!overwriteFile) {

                    if( fc.showSaveDialog( parentPaint ) == JFileChooser.APPROVE_OPTION ) {
                        checkFile();
                    }
                    else {
                        doExport = false;
                    }
                }
            }

            if(doExport) {

                try {
                    ImageIO.write(img, suffix, file );
                    currentDirectory = fc.getCurrentDirectory();

                    return file.getName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;

    }


    public void addPaint(Paint p) {
        parentPaint = p;  // Tell FileHandling about Paint.java
    }
}
