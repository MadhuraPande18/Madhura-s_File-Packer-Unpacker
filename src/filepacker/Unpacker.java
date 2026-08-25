//////////////////////////////////
// Final File UnPacker Code
//////////////////////////////////


import java.io.*;
import java.util.*;

class Unpacker
{
    public static void main(String A[]) throws Exception
    {
        Scanner sobj = new Scanner(System.in);
        String PackFileName = null;
        File fpackobj = null;
        FileInputStream fiobj = null;
        FileOutputStream foobj = null;
        byte Header[] = new byte[100];
        String strHeader = null;
        String Tokens[] = null;
        File NewFile = null;
        byte Buffer[] = null;
        int iRet = 0;

        System.out.println("Enter the name of packed file : ");
        PackFileName = sobj.nextLine();

        fpackobj = new File(PackFileName);

        if(fpackobj.exists())
        {
            fiobj = new FileInputStream(fpackobj);

            // Read header
            while((iRet = fiobj.read(Header, 0, 100)) != -1)
            {
                strHeader = new String(Header);

                System.out.println("Header is : "+strHeader);

                strHeader = strHeader.trim();
                strHeader = strHeader.replaceAll("\\s+", " ");

                Tokens = strHeader.split(" ");

                System.out.println("File name : "+Tokens[0]);
                System.out.println("File size : "+Tokens[1]);

                NewFile = new File(Tokens[0]);
                NewFile.createNewFile();

                foobj = new FileOutputStream(NewFile);

                Buffer = new byte[Integer.parseInt(Tokens[1])];

                // read data
                fiobj.read(Buffer, 0, Integer.parseInt(Tokens[1]));

                // Write the data
                foobj.write(Buffer, 0, Integer.parseInt(Tokens[1]));
            }// End of while
        }
        else
        {
            System.out.println("There is no such pack file");
        }
    }
}
