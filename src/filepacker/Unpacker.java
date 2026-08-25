package filepacker;

//////////////////////////////////
// Final File UnPacker Code
//////////////////////////////////

import java.io.*;
import java.util.*;

public class Unpacker
{
    public String unpack(String PackFileName) throws Exception
    {
        File fpackobj = new File(PackFileName);

        if(fpackobj.exists())
        {
            StringBuilder result = new StringBuilder();

            FileInputStream fiobj = new FileInputStream(fpackobj);

            byte Header[] = new byte[100];
            String strHeader = null;
            String Tokens[] = null;
            File NewFile = null;
            byte Buffer[] = null;
            int iRet = 0;

            while((iRet = fiobj.read(Header, 0, 100)) != -1)
            {
                // CHANGED: decode as UTF-8 explicitly, matching how Packer
                // now encodes the header — must match on both sides
                strHeader = new String(Header, "UTF-8");
                strHeader = strHeader.trim();

                // CHANGED: split on "|" instead of " ", so filenames
                // with spaces stay intact as a single token
                Tokens = strHeader.split("\\|");

                String fileName = Tokens[0];
                int fileSize = Integer.parseInt(Tokens[1].trim());

                NewFile = new File(fileName);
                NewFile.createNewFile();

                FileOutputStream foobj = new FileOutputStream(NewFile);

                Buffer = new byte[fileSize];

                // read data
                fiobj.read(Buffer, 0, fileSize);

                // Write the data
                foobj.write(Buffer, 0, fileSize);

                foobj.close();

                result.append("Unpacked : ").append(fileName)
                      .append(" (").append(fileSize).append(" bytes)\n");
            } // End of while

            fiobj.close();
            return result.toString();
        }
        else
        {
            return "There is no such pack file";
        }
    }

    public static void main(String A[]) throws Exception
    {
        Scanner sobj = new Scanner(System.in);

        System.out.println("Enter the name of packed file : ");
        String PackFileName = sobj.nextLine();

        Unpacker u = new Unpacker();
        System.out.println(u.unpack(PackFileName));

        sobj.close();
    }
}