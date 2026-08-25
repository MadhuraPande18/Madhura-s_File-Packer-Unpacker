package filepacker;

import java.io.*;
import java.nio.file.Path;

/**
 * Unpacker
 * --------
 * Reads a package (.mvp) file created by Packer and restores the
 * original files - including nested folder structure - into a
 * chosen output directory.
 */
public class Unpacker
{
    /**
     * Unpacks the given package file into the given output directory.
     *
     * @param packPath   path of the package (.mvp) file
     * @param outputDir  directory where extracted files will be written
     * @param listener   optional progress listener (may be null)
     */
    public void unpack(String packPath, String outputDir, ProgressListener listener) throws IOException
    {
        File packFile = new File(packPath);
        if(!packFile.exists() || !packFile.isFile())
        {
            throw new FileNotFoundException("Package file not found : " + packPath);
        }

        File outDir = new File(outputDir);
        if(!outDir.exists())
        {
            outDir.mkdirs();
        }
        Path outDirCanonical = outDir.getCanonicalFile().toPath();

        try(DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(packFile))))
        {
            int totalFiles = dis.readInt();
            report(listener, "Files found in package : " + totalFiles);

            for(int index = 0; index < totalFiles; index++)
            {
                int pathLength = dis.readInt();
                byte[] pathBytes = new byte[pathLength];
                dis.readFully(pathBytes);
                String relativePath = new String(pathBytes, "UTF-8");

                long fileLength = dis.readLong();

                // Rebuild the path using this OS's separator, then resolve against outDir
                String osPath = relativePath.replace('/', File.separatorChar);
                File outFile = new File(outDir, osPath);

                // Safety check: make sure the resolved file is actually inside outDir
                // (guards against a maliciously crafted package using "../" to escape)
                Path resolvedCanonical = outFile.getCanonicalFile().toPath();
                if(!resolvedCanonical.startsWith(outDirCanonical))
                {
                    throw new IOException("Unsafe path in package, refusing to extract : " + relativePath);
                }

                File parentDir = outFile.getParentFile();
                if(parentDir != null && !parentDir.exists())
                {
                    parentDir.mkdirs();
                }

                try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile)))
                {
                    byte[] buffer = new byte[8192];
                    long remaining = fileLength;

                    while(remaining > 0)
                    {
                        int toRead = (int) Math.min(buffer.length, remaining);
                        int bytesRead = dis.read(buffer, 0, toRead);

                        if(bytesRead == -1)
                        {
                            throw new EOFException("Unexpected end of package file while reading : " + relativePath);
                        }

                        bos.write(buffer, 0, bytesRead);
                        remaining -= bytesRead;
                    }
                }

                report(listener, "Unpacked : " + relativePath + " (" + fileLength + " bytes)");
            }
        }

        report(listener, "Unpacking completed successfully -> " + outDir.getAbsolutePath());
    }

    /** Backward-compatible overload without a progress listener. */
    public void unpack(String packPath, String outputDir) throws IOException
    {
        unpack(packPath, outputDir, null);
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
