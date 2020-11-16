import java.io.*;
import java.net.Socket;
import java.util.Scanner;

@SuppressWarnings("Duplicates")
public class MultiUploader {
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
//        Socket socket = new Socket("127.0.0.1",6789);
        String fileName;
        FileInputStream fis;
        BufferedInputStream inputStream;
        File file;
        int count;

        //DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
//        OutputStream os = socket.getOutputStream();
//        PrintWriter os = new PrintWriter(socket.getOutputStream());
        byte[] buffer = new byte[4096];
//        os.write("UPLOAD\r\n".getBytes());

        while (true){
            Socket socket = new Socket("127.0.0.1",2222);
            OutputStream os = socket.getOutputStream();
            System.out.println("Enter file name: ");
            fileName = input.nextLine();
            System.out.println(fileName);

            file = new File(fileName);
            if(!file.isFile()){
                System.out.println("Invalid File Name");
                os.write("UPLOAD\r\n".getBytes());
                os.write("NOFILE\r\n".getBytes());
                os.write("0\r\n".getBytes());
                os.flush();
                os.close();
                socket.close();
            }else {
                new FileUploadThread(fileName, os,socket);
            }


//            if(!file.isFile()){
//                System.out.println("Invalid File Name");
//                os.write("UPLOAD\r\n".getBytes());
//                os.write("NOFILE\r\n".getBytes());
//                os.write("0\r\n".getBytes());
//                os.flush();
//                os.close();
//            } else{
//                fis = new FileInputStream(fileName);
//                inputStream = new BufferedInputStream(fis);
//
//                os.write("UPLOAD\r\n".getBytes());
//                String name = file.getName() + "\r\n";
//                os.write(name.getBytes());
//                String fileSize = file.length() + "\r\n";
//                os.write(fileSize.getBytes());
//
//                while ((count = inputStream.read(buffer)) > 0) {
//                    os.write(buffer, 0, count);
//                    os.flush();
//                }
//
//                os.flush();
//                os.close();
//            }


        } //while

    }
}
