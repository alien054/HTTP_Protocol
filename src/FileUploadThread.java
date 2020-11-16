import java.io.*;
import java.net.Socket;

@SuppressWarnings("Duplicates")
public class FileUploadThread implements Runnable{
    String fileName;
    FileInputStream fis;
    BufferedInputStream inputStream;
    OutputStream os;
    Socket socket;
    File file;
    int count;
    byte[] buffer;
    Thread t;

    FileUploadThread(String fileName,OutputStream outputStream,Socket socket) throws FileNotFoundException {
        this.buffer = new byte[4096];
        this.os = outputStream;
        this.socket = socket;
        this.fileName = fileName;
        file = new File(fileName);
        fis = new FileInputStream(fileName);
        inputStream = new BufferedInputStream(fis);


        t = new Thread(this);
        System.out.println(t.getName());
        t.start();
    }

    public void run(){
        try{
                os.write("UPLOAD\r\n".getBytes());
                String name = file.getName() + "\r\n";
                os.write(name.getBytes());
                String fileSize = file.length() + "\r\n";
                os.write(fileSize.getBytes());

                while ((count = inputStream.read(buffer)) > 0) {
                    os.write(buffer, 0, count);
                    os.flush();
                }

                System.out.println(fileName + " Uploaded Successfully");
                os.flush();
                os.close();
                socket.close();

        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
