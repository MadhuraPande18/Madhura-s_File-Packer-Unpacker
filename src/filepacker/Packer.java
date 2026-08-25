//////////////////////////////////
// Final Packing Code
//////////////////////////////////


import java.io.*;
import java.util.*;

public class FilePacker
{
    public static void main(String A[]) throws Exception
    {     
        int iRet = 0;
        int Size = 0;
        int i = 0, j = 0;

        Scanner sobj = new Scanner(System.in);
        String FolderName = null;
        String PackFileName = null;
        String header = "";

        FileOutputStream foobj = null;
        FileInputStream fiobj = null;

        byte Buffer[] = new byte[1024];
        byte bHeader[] = null;

        System.out.println("Enter Folder name : ");
        FolderName = sobj.nextLine();

        System.out.println("Enter the name of packed file : ");
        PackFileName = sobj.nextLine();
        
        File fobjfolder = new File(FolderName);

        if((fobjfolder.exists()) && (fobjfolder.isDirectory()))
        {
            System.out.println("Folder exists");

            File fobjpack = new File(PackFileName);
            fobjpack.createNewFile();   // Pack file gets created

            foobj = new FileOutputStream(fobjpack);

            File fArr[] = fobjfolder.listFiles();
         
            System.out.println("Number of files in folder : "+fArr.length);

            for(i = 0; i < fArr.length; i++)
            {
                fiobj = new FileInputStream(fArr[i]);

                header = header + fArr[i].getName();
                header = header + " ";
                header = header + fArr[i].length();

                Size = 100 - header.length();

                for(j = 1; j <= Size; j++)
                {
                    header = header + " ";
                }

                bHeader = header.getBytes();
                                
                // Write file name and size 
                
                foobj.write(bHeader);

                // Loop to read from fiobj & write to foobj
                
                while((iRet = fiobj.read(Buffer)) != -1)
                {
                    foobj.write(Buffer,0,iRet);
                }

                fiobj.close();
                header = "";
            }

            foobj.close();
            sobj.close();
        }
        else
        {
            System.out.println("There is no such folder");
        }

    }
}
