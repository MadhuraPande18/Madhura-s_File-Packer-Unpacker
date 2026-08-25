package filepacker;

//////////////////////////////////
// Final Packing Code
//////////////////////////////////

import java.io.*;
import java.util.*;

public class Packer
{
    public String packFolder(String FolderName, String PackFileName) throws Exception
    {
        int iRet = 0;
        int i = 0;

        FileOutputStream foobj = null;
        FileInputStream fiobj = null;

        byte Buffer[] = new byte[1024];

        File fobjfolder = new File(FolderName);

        if((fobjfolder.exists()) && (fobjfolder.isDirectory()))
        {
            StringBuilder result = new StringBuilder();
            result.append("Folder exists\n");

            File fobjpack = new File(PackFileName);
            fobjpack.createNewFile();

            foobj = new FileOutputStream(fobjpack);
            File fArr[] = fobjfolder.listFiles();

            result.append("Number of files in folder : ").append(fArr.length).append("\n");

            for(i = 0; i < fArr.length; i++)
            {
                if (fArr[i].isDirectory())
                {
                    result.append("Skipped (subfolder) : ").append(fArr[i].getName()).append("\n");
                    continue;
                }

                // CHANGED: measure name + size in BYTES (UTF-8), not characters,
                // since non-ASCII filenames (emoji, Devanagari, etc.) take more
                // than 1 byte per character. Using "|" as delimiter since it's
                // an illegal character in Windows filenames, so it can never
                // collide with a real filename.
                byte[] nameBytes = fArr[i].getName().getBytes("UTF-8");
                byte[] sizeBytes = ("|" + fArr[i].length()).getBytes("UTF-8");

                int usedBytes = nameBytes.length + sizeBytes.length;
                int padding = 100 - usedBytes;

                if (padding < 0)
                {
                    result.append("Skipped (name too long) : ").append(fArr[i].getName()).append("\n");
                    continue;
                }

                fiobj = new FileInputStream(fArr[i]);

                ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
                headerStream.write(nameBytes);
                headerStream.write(sizeBytes);
                for (int p = 0; p < padding; p++)
                {
                    headerStream.write(' ');   // pad with actual space bytes
                }

                byte[] bHeader = headerStream.toByteArray();

                // Write file name and size
                foobj.write(bHeader);

                // Loop to read from fiobj & write to foobj
                while((iRet = fiobj.read(Buffer)) != -1)
                {
                    foobj.write(Buffer,0,iRet);
                }

                fiobj.close();
                result.append("Packed : ").append(fArr[i].getName()).append("\n");
            }

            foobj.close();
            return result.toString();
        }
        else
        {
            return "There is no such folder";
        }
    }

    public static void main(String A[]) throws Exception
    {
        Scanner sobj = new Scanner(System.in);

        System.out.println("Enter Folder name : ");
        String FolderName = sobj.nextLine();

        System.out.println("Enter the name of packed file : ");
        String PackFileName = sobj.nextLine();

        Packer p = new Packer();
        System.out.println(p.packFolder(FolderName, PackFileName));

        sobj.close();
    }
}