import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 *  BrushEditor.java
 *
 *  This is a simple editor that lets the user pick a brush from a set of defaults, or create his or her own brush.
 *  The defaults are mostly inspired by the brushes from MacPaint, and a couple of brushes from Deluxe Paint.
 *
 *  @author Jan Øyvind Kruse
 *  @version 1.0
 */

public class BrushEditor extends JFrame implements ActionListener {

    static JFrame editor;
    OptionsFrame oF;
    Paint p;
    int b;

    public JButton[][] Grid = new JButton[9][9];
    public JButton buttonOK = new JButton("OK");
    public JButton buttonCancel = new JButton("Cancel");
    public JButton buttonClear = new JButton("Clear");

    public JButton[][] DefaultBrushes = new JButton[4][8];

    int CustomBE[][][] = {

            //  Various MacPaint and Deluxe Paint inspired brushes

            { // 0  Square 4
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1}
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

            { // 2  / 4
                    {0, 0, 0, 0, 0, 0, 0, 1, 1},
                    {0, 0, 0, 0, 0, 0, 1, 1, 1},
                    {0, 0, 0, 0, 0, 1, 1, 1, 0},
                    {0, 0, 0, 0, 1, 1, 1, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 1, 1, 1, 0, 0, 0, 0},
                    {0, 1, 1, 1, 0, 0, 0, 0, 0},
                    {1, 1, 1, 0, 0, 0, 0, 0, 0},
                    {1, 1, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 3  \ 4
                    {1, 1, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 1, 0, 0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 0, 0, 0, 0, 0},
                    {0, 0, 1, 1, 1, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 1, 0, 0},
                    {0, 0, 0, 0, 0, 1, 1, 1, 0},
                    {0, 0, 0, 0, 0, 0, 1, 1, 1},
                    {0, 0, 0, 0, 0, 0, 0, 1, 1}
            },

            { // 4  | 4
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0}
            },

            { // 5  - 4
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 6  Star
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 1, 1, 0, 0, 0, 1, 1, 0},
                    {1, 0, 0, 0, 0, 0, 0, 0, 1}
            },

            { // 7
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

            { // 8 Beginning of Row 2
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 9  Circle 3
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 10  / 3
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 1, 1, 0},
                    {0, 0, 0, 0, 0, 1, 1, 1, 0},
                    {0, 0, 0, 0, 1, 1, 1, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 1, 1, 1, 0, 0, 0, 0},
                    {0, 1, 1, 1, 0, 0, 0, 0, 0},
                    {0, 1, 1, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 11  \ 3
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 1, 0, 0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 0, 0, 0, 0, 0},
                    {0, 0, 1, 1, 1, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 1, 0, 0},
                    {0, 0, 0, 0, 0, 1, 1, 1, 0},
                    {0, 0, 0, 0, 0, 0, 1, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 12  | 3
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 13  - 3
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 14   / 3 Dotted
                    {0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {0, 0, 0, 0, 0, 0, 1, 0, 1},
                    {0, 0, 0, 0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 1, 0, 0, 0, 0, 0, 0},
                    {1, 0, 1, 0, 0, 0, 0, 0, 0},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 15
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

            { // 16   Beginning of row 3
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 17  Circle 2
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
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

            { // 19  \ 2
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 1, 1, 0, 0, 0, 0, 0},
                    {0, 0, 1, 1, 1, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 1, 0, 0},
                    {0, 0, 0, 0, 0, 1, 1, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 20  | 2
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 21  - 2
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 22   / 2 Dotted
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 1, 0, 0, 0, 0, 0, 0},
                    {0, 0, 1, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 23
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0}
            },

            { // 24  Beginning of row 4
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 25  Circle 1
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 26  / 1
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 0, 1, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 27  \ 1
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 28  | 1
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 29  - 1
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 30   / 1 Dotted
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            },

            { // 31
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 0, 1, 1, 0, 1, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            }
    };


    ImageIcon CustomIcon(int i) {

        Color c;

        ImageIcon imgicon = new ImageIcon();

        BufferedImage temp = new BufferedImage( 32, 32, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g = (Graphics2D)temp.getGraphics();

        g.setPaint(new Color(246,246,246));
        c = Color.black;

        g.fillRect(0, 0, 32, 32);
        int rgb = c.getRGB();

        for(int j = 0; j < 9; j++) { // rows

            for(int k = 0; k < 9; k++) { // columns

                if( CustomBE[i][j][k] == 1 ) {
                    temp.setRGB((k + 3) * 2 + 1 , (j + 4) * 2 - 1, rgb);
                    temp.setRGB((k + 3) * 2 + 2, (j + 4) * 2 - 1, rgb);
                    temp.setRGB((k + 3) * 2 + 1, (j + 4) * 2, rgb);
                    temp.setRGB((k + 3) * 2 + 2, (j + 4) * 2, rgb);
                }
            }
        }

        imgicon.setImage( temp );
        return imgicon;

    }


    public BrushEditor(Paint controller, OptionsFrame o, int brush) {

        oF = o;  // Tell the BrushEditor about the OptionsFrame and Paint.java.
        p = controller;
        b = brush;

        editor = new JFrame();
        editor.setTitle("Brush Editor");
        editor.setSize(600, 300);

        editor.setResizable(false);
        editor.setVisible(true);

        URL url = getClass().getResource("icon/icon.png");
        ImageIcon imgicon = new ImageIcon(url);
        editor.setIconImage(imgicon.getImage());
        editor.setFocusable(true);
        editor.setLocationRelativeTo(null);  // Centers the window

        editor.setLayout(null);

        for(int j = 0; j < 9; j++) { // rows

            for(int k = 0; k < 9; k++) {  // columns

                Grid[j][k] = new JButton();

                if( oF.CustomB[brush][j][k] == 0 )
                    Grid[j][k].setBackground(Color.white);
                else
                    Grid[j][k].setBackground(Color.black);

                Grid[j][k].setBounds( 10 + k * 28, 10 + j * 28, 24, 24 );
                editor.add( Grid[j][k] );
                Grid[j][k].addActionListener( this );

            }
        }

        for(int j = 0; j < 4; j++) {  // rows

            for(int k = 0; k < 8; k++) {  // columns

                DefaultBrushes[j][k] = new JButton();

                imgicon = CustomIcon(j*8 + k);

                DefaultBrushes[j][k].setIcon(imgicon);

                DefaultBrushes[j][k].setBounds( 274 + k * 36, 22 + j * 36, 32, 32 );
                editor.add( DefaultBrushes[j][k] );
                DefaultBrushes[j][k].addActionListener( this );
            }
        }

        editor.add(buttonOK);
        editor.add(buttonCancel);
        editor.add(buttonClear);

        buttonOK.setBounds(294, 219, 75, 26);
        buttonCancel.setBounds(379, 219, 75, 26);
        buttonClear.setBounds(464, 219, 75, 26);

        buttonOK.addActionListener( this );
        buttonCancel.addActionListener( this );
        buttonClear.addActionListener( this );

        editor.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                p.brushEditorOpen = false;

            }
        });

    }

    public void BringToFront() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(editor != null) {
                    editor.toFront();
                    editor.repaint();
                }
            }
        });
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        for(int j = 0; j < 4; j++) {  // rows
            for(int k = 0; k < 8; k++) {  // columns

                if(e.getSource().equals( DefaultBrushes[j][k])) {

                    for(int l = 0; l < 9; l++) { // rows
                        for (int m = 0; m < 9; m++) {  // columns

                            if( CustomBE[j*8 + k][l][m] == 0)
                                Grid[l][m].setBackground(Color.white);
                            else
                                Grid[l][m].setBackground(Color.black);

                        }
                    }
                }
            }
        }

        for(int j = 0; j < 9; j++) { // rows
            for (int k = 0; k < 9; k++) {  // columns

                //  The user edited the brush on the left side.

                if(e.getSource().equals( Grid[j][k] )) {

                    if( Grid[j][k].getBackground() == Color.white )
                        Grid[j][k].setBackground(Color.black);
                    else
                        Grid[j][k].setBackground(Color.white);
                }
            }
        }

        if( e.getSource().equals( buttonClear )) {

            //  Clear the current brush
            for(int j = 0; j < 9; j++)  // rows
                for (int k = 0; k < 9; k++)   // columns
                    Grid[j][k].setBackground(Color.white);

        }

        if( e.getSource().equals( buttonCancel )) {

            p.brushEditorOpen = false;
            editor.dispose();

        }

        if( e.getSource().equals( buttonOK )) {

            for(int j = 0; j < 9; j++) { // rows
                for (int k = 0; k < 9; k++) {  // columns

                    if( Grid[j][k].getBackground() == Color.white )
                        oF.CustomB[b][j][k] = 0;
                    else
                        oF.CustomB[b][j][k] = 1;
                }
            }

            oF.CustomBrushes[b].setIcon( oF.CustomIcon(b, 1));

            p.brushEditorOpen = false;
            editor.dispose();

        }
    }
}
