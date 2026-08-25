package filepacker;

import java.awt.EventQueue;

public class main
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