package filepacker;

import java.awt.EventQueue;

/**
 * Main
 * ----
 * Single entry point of the Madhura File Packer - Unpacker project.
 * Launches the AWT graphical frontend (FilePackerFrame), which in turn
 * drives Packer and Unpacker.
 */
public class Main
{
    public static void main(String[] args)
    {
        EventQueue.invokeLater(() ->
        {
            FilePackerFrame frame = new FilePackerFrame();
            frame.setVisible(true);
        });
    }
}
