import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@SuppressWarnings("Duplicates")
public class FileRecieveThread implements Runnable{
    String fileName;
    Socket socket;
    int fileSize;
    byte[] buffer;
    InputStream inputStream;
    BufferedReader in;
    Thread t;

    FileRecieveThread(InputStream is,Socket socket){
        t = new Thread(this);
        this.inputStream = is;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(is));
        System.out.println(t.getName());
        t.start();
    }

    public void run(){
        //while (true) {
            try {
                fileName = in.readLine();
                fileSize = Integer.parseInt(in.readLine());
                if(fileName.equalsIgnoreCase("NOFILE")){
                    System.out.println("Invalid File Name");
                } else{
                    SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    DataInputStream dis = new DataInputStream(inputStream);
                    buffer = new byte[4096];

                    FileOutputStream fos = new FileOutputStream("./root/"+sdf.format(timestamp)+fileName);
                    int read = 0;
                    int totalRead = 0;
                    int remaining = fileSize;

                    while ((read = dis.read(buffer,0,Math.min(buffer.length,remaining)))>0){
                        totalRead += read;
                        remaining -= read;
                        fos.write(buffer,0,read);
                    }

                    fos.close();
                    dis.close();
                    System.out.println(fileName + " Received From Client");
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        //}//while

    }
}
