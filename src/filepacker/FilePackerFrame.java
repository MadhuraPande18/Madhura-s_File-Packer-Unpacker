package filepacker;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FilePackerFrame extends JFrame
{
    private JTextField folderField = new JTextField(25);
    private JTextField packFileField = new JTextField(25);
    private JTextField unpackFileField = new JTextField(25);
    private JTextArea logArea = new JTextArea(10, 40);

    private Packer packer = new Packer();
    private Unpacker unpacker = new Unpacker();

    public FilePackerFrame()
    {
        setTitle("File Packer - Unpacker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(3, 3, 5, 5));

        // Row 1: folder to pack
        topPanel.add(new JLabel("Folder to pack:"));
        topPanel.add(folderField);
        JButton browseFolder = new JButton("Browse");
        browseFolder.addActionListener(this::chooseFolder);
        topPanel.add(browseFolder);

        // Row 2: pack file name
        topPanel.add(new JLabel("Pack file name:"));
        topPanel.add(packFileField);
        JButton packBtn = new JButton("Pack");
        packBtn.addActionListener(this::doPack);
        topPanel.add(packBtn);

        // Row 3: unpack file
        topPanel.add(new JLabel("Pack file to unpack:"));
        topPanel.add(unpackFileField);
        JButton unpackBtn = new JButton("Unpack");
        unpackBtn.addActionListener(this::doUnpack);
        topPanel.add(unpackBtn);

        add(topPanel, BorderLayout.NORTH);

        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void chooseFolder(ActionEvent e)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            folderField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void doPack(ActionEvent e)
    {
        try
        {
            String folder = folderField.getText();
            String packFile = packFileField.getText();
            String output = packer.packFolder(folder, packFile);
            log(output);
        }
        catch (Exception ex)
        {
            log("Error while packing: " + ex.getMessage());
        }
    }

    private void doUnpack(ActionEvent e)
    {
        try
        {
            String packFile = unpackFileField.getText();
            String output = unpacker.unpack(packFile);
            log(output);
        }
        catch (Exception ex)
        {
            log("Error while unpacking: " + ex.getMessage());
        }
    }

    private void log(String message)
    {
        logArea.append(message + "\n");
    }
}