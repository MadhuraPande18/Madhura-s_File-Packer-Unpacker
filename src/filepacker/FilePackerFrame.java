package filepacker;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.JFileChooser;

/**
 * FilePackerFrame
 * ----------------
 * AWT-based graphical frontend for the Madhura File Packer - Unpacker.
 * Provides a Pack screen and an Unpack screen (toggled by two buttons),
 * and a scrolling log area that shows live progress from Packer / Unpacker.
 *
 * Folder selection uses javax.swing.JFileChooser in directories-only mode,
 * since plain AWT's FileDialog cannot open a genuine "select a folder" dialog
 * on most platforms. Everything else stays AWT.
 */
public class FilePackerFrame extends Frame
{
    private final Packer packer = new Packer();
    private final Unpacker unpacker = new Unpacker();

    private final CardLayout cardLayout = new CardLayout();
    private final Panel cardPanel = new Panel(cardLayout);

    private final Label headerLabel = new Label("Madhura File Packer - Unpacker", Label.CENTER);
    private final TextArea logArea = new TextArea("", 8, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);

    // Pack screen fields
    private final TextField folderField = new TextField();
    private final TextField packOutputField = new TextField("archive.mvp");
    private final Button packButton = new Button("Pack Now");

    // Unpack screen fields
    private final TextField packFileField = new TextField();
    private final TextField outputDirField = new TextField();
    private final Button unpackButton = new Button("Unpack Now");

    public FilePackerFrame()
    {
        super("Madhura File Packer - Unpacker");

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        setLayout(new BorderLayout(8, 8));

        // ---- North : header + mode switch buttons ----
        Panel northPanel = new Panel(new BorderLayout());
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        northPanel.add(headerLabel, BorderLayout.NORTH);

        Panel modePanel = new Panel(new FlowLayout());
        Button showPackButton = new Button("Pack Files");
        Button showUnpackButton = new Button("Unpack Package");
        showPackButton.addActionListener(e -> cardLayout.show(cardPanel, "PACK"));
        showUnpackButton.addActionListener(e -> cardLayout.show(cardPanel, "UNPACK"));
        modePanel.add(showPackButton);
        modePanel.add(showUnpackButton);
        northPanel.add(modePanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        // ---- Center : card panel with Pack / Unpack screens ----
        cardPanel.add(buildPackPanel(), "PACK");
        cardPanel.add(buildUnpackPanel(), "UNPACK");
        add(cardPanel, BorderLayout.CENTER);

        // ---- South : log area ----
        logArea.setEditable(false);
        Panel logPanel = new Panel(new BorderLayout());
        logPanel.add(new Label("Log :"), BorderLayout.NORTH);
        logPanel.add(logArea, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);

        setSize(650, 560);
        setLocationRelativeTo(null);
    }

    private Panel buildPackPanel()
    {
        Panel panel = new Panel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0 : folder to pack
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new Label("Folder to pack (recursive):"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(folderField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        Button browseFolderButton = new Button("Browse...");
        panel.add(browseFolderButton, gbc);

        // Row 1 : output package file
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new Label("Output package file:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(packOutputField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        Button browseOutputButton = new Button("Browse...");
        panel.add(browseOutputButton, gbc);

        // Row 2 : pack button
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(packButton, gbc);

        // ---- Actions ----
        browseFolderButton.addActionListener(e ->
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select folder to pack");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if(!folderField.getText().trim().isEmpty())
            {
                chooser.setCurrentDirectory(new File(folderField.getText().trim()));
            }

            int result = chooser.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                folderField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        browseOutputButton.addActionListener(e ->
        {
            FileDialog fd = new FileDialog(this, "Save package as", FileDialog.SAVE);
            fd.setFile("archive.mvp");
            fd.setVisible(true);
            if(fd.getFile() != null)
            {
                String dir = fd.getDirectory() != null ? fd.getDirectory() : "";
                packOutputField.setText(dir + fd.getFile());
            }
        });

        packButton.addActionListener(e -> doPack());

        return panel;
    }

    private Panel buildUnpackPanel()
    {
        Panel panel = new Panel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0 : package file to unpack
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new Label("Package file (.mvp):"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(packFileField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        Button browsePackButton = new Button("Browse...");
        panel.add(browsePackButton, gbc);

        // Row 1 : output directory
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new Label("Output directory:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(outputDirField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        Button browseOutDirButton = new Button("Browse...");
        panel.add(browseOutDirButton, gbc);

        // Row 2 : unpack button
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(unpackButton, gbc);

        Label hint = new Label(
            "Tip: use the folder dialog's \"Create New Folder\" button to unpack into a fresh folder.",
            Label.CENTER);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        panel.add(hint, gbc);

        // ---- Actions ----
        browsePackButton.addActionListener(e ->
        {
            FileDialog fd = new FileDialog(this, "Select package file", FileDialog.LOAD);
            fd.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".mvp"));
            fd.setVisible(true);
            if(fd.getFile() != null)
            {
                String dir = fd.getDirectory() != null ? fd.getDirectory() : "";
                packFileField.setText(dir + fd.getFile());
            }
        });

        browseOutDirButton.addActionListener(e ->
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select output folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if(!outputDirField.getText().trim().isEmpty())
            {
                chooser.setCurrentDirectory(new File(outputDirField.getText().trim()));
            }

            int result = chooser.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                outputDirField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        unpackButton.addActionListener(e -> doUnpack());

        return panel;
    }

    private void doPack()
    {
        String folder = folderField.getText().trim();
        String output = packOutputField.getText().trim();

        if(folder.isEmpty() || output.isEmpty())
        {
            log("Please choose a folder to pack and an output package file.");
            return;
        }

        setBusy(true);
        log("Packing '" + folder + "' -> '" + output + "' ...");

        new Thread(() ->
        {
            try
            {
                packer.packFolder(folder, output, this::log);
            }
            catch(Exception ex)
            {
                log("Error while packing : " + ex.getMessage());
            }
            finally
            {
                EventQueue.invokeLater(() -> setBusy(false));
            }
        }).start();
    }

    private void doUnpack()
    {
        String packPath = packFileField.getText().trim();
        String outputDir = outputDirField.getText().trim();

        if(packPath.isEmpty() || outputDir.isEmpty())
        {
            log("Please choose a package file and an output directory.");
            return;
        }

        setBusy(true);
        log("Unpacking '" + packPath + "' -> '" + outputDir + "' ...");

        new Thread(() ->
        {
            try
            {
                unpacker.unpack(packPath, outputDir, this::log);
            }
            catch(Exception ex)
            {
                log("Error while unpacking : " + ex.getMessage());
            }
            finally
            {
                EventQueue.invokeLater(() -> setBusy(false));
            }
        }).start();
    }

    private void setBusy(boolean busy)
    {
        packButton.setEnabled(!busy);
        unpackButton.setEnabled(!busy);
    }

    /** Thread-safe log append; can be called from background worker threads. */
    private void log(String message)
    {
        EventQueue.invokeLater(() -> logArea.append(message + "\n"));
    }
}
