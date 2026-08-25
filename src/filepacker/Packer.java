package filepacker;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Packer
 * ------
 * Combines multiple files (optionally an entire folder tree) into a
 * single package (.mvp) file, preserving relative paths so folder
 * structure can be restored exactly on unpack.
 *
 * Package file layout:
 *   [int]    number of entries
 *   For each entry:
 *       [int]    length of relative path (in bytes, UTF-8)
 *       [bytes]  relative path (uses '/' as separator, portable)
 *       [long]   length of file content (in bytes)
 *       [bytes]  file content
 */
public class Packer
{
    /** A single file to be packed, along with the relative path it should be stored under. */
    private static class PackEntry
    {
        final String relativePath;
        final File file;

        PackEntry(String relativePath, File file)
        {
            this.relativePath = relativePath;
            this.file = file;
        }
    }

    /**
     * Packs an explicit list of files.
     *
     * @param inputFilePaths  paths of files to pack
     * @param baseDir         directory used to compute relative paths (may be null/empty
     *                        to fall back to flat file names, i.e. no folder structure)
     * @param outputPackPath  path of the resulting package file
     * @param listener        optional progress listener (may be null)
     */
    public void pack(List<String> inputFilePaths, String baseDir, String outputPackPath,
                      ProgressListener listener) throws IOException
    {
        List<PackEntry> entries = new ArrayList<>();
        Path basePath = (baseDir != null && !baseDir.isBlank()) ? Paths.get(baseDir).toAbsolutePath().normalize() : null;

        for(String path : inputFilePaths)
        {
            File file = new File(path);
            if(!file.exists() || !file.isFile())
            {
                throw new FileNotFoundException("Input file not found : " + path);
            }

            String relativePath = computeRelativePath(basePath, file);
            entries.add(new PackEntry(relativePath, file));
        }

        packEntries(entries, outputPackPath, listener);
    }

    /** Backward-compatible overload: flat packing, no folder structure, no progress listener. */
    public void pack(List<String> inputFilePaths, String outputPackPath) throws IOException
    {
        pack(inputFilePaths, null, outputPackPath, null);
    }

    /**
     * Recursively packs every file inside the given folder, preserving the
     * folder structure relative to that folder.
     *
     * @param folderPath      folder to pack
     * @param outputPackPath  path of the resulting package file
     * @param listener        optional progress listener (may be null)
     */
    public void packFolder(String folderPath, String outputPackPath, ProgressListener listener) throws IOException
    {
        File folder = new File(folderPath);
        if(!folder.exists() || !folder.isDirectory())
        {
            throw new FileNotFoundException("Folder not found : " + folderPath);
        }

        Path basePath = folder.toPath().toAbsolutePath().normalize();
        List<PackEntry> entries = new ArrayList<>();

        try(var stream = Files.walk(basePath))
        {
            stream.filter(Files::isRegularFile)
                  .forEach(p -> entries.add(new PackEntry(computeRelativePath(basePath, p.toFile()), p.toFile())));
        }

        if(entries.isEmpty())
        {
            throw new IOException("No files found under folder : " + folderPath);
        }

        packEntries(entries, outputPackPath, listener);
    }

    private String computeRelativePath(Path basePath, File file)
    {
        if(basePath == null)
        {
            return file.getName();
        }

        Path filePath = file.toPath().toAbsolutePath().normalize();

        if(!filePath.startsWith(basePath))
        {
            // File lives outside the base directory - fall back to flat name
            return file.getName();
        }

        Path relative = basePath.relativize(filePath);
        // Normalize to forward slashes so the package is portable across OSes
        return relative.toString().replace(File.separatorChar, '/');
    }

    private void packEntries(List<PackEntry> entries, String outputPackPath, ProgressListener listener) throws IOException
    {
        try(DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputPackPath))))
        {
            dos.writeInt(entries.size());

            for(PackEntry entry : entries)
            {
                byte[] pathBytes = entry.relativePath.getBytes("UTF-8");
                dos.writeInt(pathBytes.length);
                dos.write(pathBytes);

                long fileLength = entry.file.length();
                dos.writeLong(fileLength);

                try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(entry.file)))
                {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalWritten = 0;

                    while(totalWritten < fileLength && (bytesRead = bis.read(buffer)) != -1)
                    {
                        dos.write(buffer, 0, bytesRead);
                        totalWritten += bytesRead;
                    }
                }

                report(listener, "Packed : " + entry.relativePath + " (" + fileLength + " bytes)");
            }

            dos.flush();
        }

        report(listener, "Packing completed successfully -> " + outputPackPath);
    }

    private void report(ProgressListener listener, String message)
    {
        if(listener != null)
        {
            listener.onMessage(message);
        }
        else
        {
            System.out.println(message);
        }
    }
}
