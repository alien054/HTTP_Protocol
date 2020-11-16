import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Date;

public class SendFile implements Runnable {
    File fileToSend;
    OutputStream os;
    Socket socket;
    Thread t;

    SendFile(File file,OutputStream outputStream,Socket socket){
        this.fileToSend = file;
        this.os = outputStream;
        this.socket = socket;
        t = new Thread(this);
        t.start();
        System.out.println(t.getName());
    }

    public void run(){
        try {
            String contentType = Files.probeContentType(this.fileToSend.toPath());
            byte[] buffer = new byte[16];
            int count;
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(this.fileToSend));

            String data;
            this.os.write("HTTP/1.1 200 OK\r\n".getBytes());
            this.os.write("Server: Java HTTP Server: 1.0\r\n".getBytes());
            data = "Date: " + new Date() + "\r\n";
            this.os.write(data.getBytes());
            this.os.write("Content-Disposition: attachment\r\n".getBytes());
            data = "Content-Type: " + contentType + "\r\n";
            this.os.write(data.getBytes());
            data = "Content-Length: " + this.fileToSend.length() + "\r\n";
            this.os.write(data.getBytes());
            this.os.write("\r\n".getBytes());

            while ((count = inputStream.read(buffer)) > 0) {
                this.os.write(buffer, 0, count);
                this.os.flush();
            }
            this.os.flush();

            this.os.close();

            //socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
