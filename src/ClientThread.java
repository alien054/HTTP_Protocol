import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

@SuppressWarnings("Duplicates")
public class ClientThread implements Runnable {
    Thread t;
    Socket socket;
    InputStream  socketInputStream;
    OutputStream socketOuputStream;
    String path;
    String root;

    ClientThread(Socket socket) throws IOException {
        this.root="./root/";
        this.socket = socket;
        socketOuputStream = this.socket.getOutputStream();
        socketInputStream = this.socket.getInputStream();
        t = new Thread(this);
        System.out.println(t.getName());
        t.start();
    }


    public void run(){
//        System.out.println(socket.getPort() + " " + socket.getLocalPort());
//        System.out.println(t.getName());

        try{
            File file = new File("index.html");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

            File logFile = new File("log.txt");
            FileWriter logFileWriter = new FileWriter(logFile.getName(),true);

            StringBuilder sb = new StringBuilder();
//            String line;
//            while(( line = br.readLine()) != null ) {
//                sb.append( line );
//                sb.append( '\n' );
//            }
//
//            String content = sb.toString();


            BufferedReader in = new BufferedReader(new InputStreamReader(socketInputStream));
            //PrintWriter pr = new PrintWriter(this.socketOutputStream);
            OutputStream pr = socketOuputStream;

            //while (true){

                String input = in.readLine();
                System.out.println(input);

                // String content = "<html>Hello</html>";
                if(input == null) {
                    System.out.println("Can't Parse Request");
                }
                else if(input.length() > 0) {
                    if(input.startsWith("GET"))
                    {
                        String[] tokens = input.split(" ",3);
                        if(!tokens[1].equalsIgnoreCase("/favicon.ico")){
                            path = tokens[1];
                        }else{
                            path = "/";
                        }
                        //System.out.println("path: " + path);

                        // Log File
                        logFileWriter.write("REQUEST\n");
                        while(!input.isEmpty()){
                            if(input.contains("favicon")) continue;
                            logFileWriter.write(input + "\n");
                            input = in.readLine();
                        }

                        File folder = new File(root+path);

                        if(folder.isDirectory()) {
                            sb.append("<html>");
                            sb.append("<head><title>FTP Server</title></head>");
                            sb.append("<body><h1>FTP Server</h1>");
                            sb.append("<ul>");
                            sb.append("<li><a href=\"../\">" + "../" + "</a></li>");

                            File[] listOfFiles = folder.listFiles();

                            for (int i = 0; i < listOfFiles.length; i++) {
                                if (listOfFiles[i].isFile()) {
                                    //System.out.println("File " + listOfFiles[i].getName());
                                    //String filePath = "./root" + path + listOfFiles[i].getName();
                                    String filePath = path + listOfFiles[i].getName();

                                    //System.out.println(filePath);
                                    sb.append("<li><a href=\"" + filePath + "\" download>" + listOfFiles[i].getName() + "</a></li>");
                                    //sb.append("<li>"+listOfFiles[i].getName()+"</li>");
                                } else if (listOfFiles[i].isDirectory()) {
                                    //System.out.println("Directory " + listOfFiles[i].getName());
                                    String newPath = path + listOfFiles[i].getName() + "/";
                                    //System.out.println("newPath: " + newPath);
                                    sb.append("<li><a href=\"" + newPath + "\"><b>" + listOfFiles[i].getName() + "</b></a></li>");
                                }
                            }

                            sb.append("<ul>");

                            sb.append("</body></html>");

                            String content = sb.toString();
                            String data;

                            //LogFile
                            logFileWriter.write("RESPONSE\n");
                            logFileWriter.write("HTTP/1.1 200 OK\n");
                            logFileWriter.write("Server: Java HTTP Server: 1.0\n");
                            logFileWriter.write("Date: " + new Date() + "\n");
                            logFileWriter.write("Content-Type: text/html\n");
                            logFileWriter.write("Content-Length: " + content.length() + "\n");
                            logFileWriter.write("\n");
                            logFileWriter.close();


                            //Actual Response
                            pr.write("HTTP/1.1 200 OK\r\n".getBytes());
                            pr.write("Server: Java HTTP Server: 1.0\r\n".getBytes());
                            data = "Date: " + new Date() + "\r\n";
                            pr.write(data.getBytes());
                            pr.write("Content-Type: text/html\r\n".getBytes());
                            data = "Content-Length: " + content.length() + "\r\n";
                            pr.write(data.getBytes());
                            pr.write("\r\n".getBytes());
                            pr.write(content.getBytes());
                            pr.flush();

//                            socket.close();

                            System.out.println(input + " Request Served");

                        } else if(folder.isFile()) {
                            String contentType = Files.probeContentType(folder.toPath());
                            byte[] buffer = new byte[32];
                            int count;
                            //System.out.println("here");
                            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(folder));
                            String data;


                            //Log File
                            logFileWriter.write("RESPONSE\n");
                            logFileWriter.write("HTTP/1.1 200 OK\n");
                            logFileWriter.write("Server: Java HTTP Server: 1.0\n");
                            logFileWriter.write("Date: " + new Date() + "\n");
                            logFileWriter.write("Content-Disposition: attachment\n");
                            logFileWriter.write("Content-Type: "+ contentType+"\n");
                            logFileWriter.write("Content-Length: " + folder.length() + "\n");
                            logFileWriter.write("\n");
                            logFileWriter.close();

                            //Actual Response
                            pr.write("HTTP/1.1 200 OK\r\n".getBytes());
                            pr.write("Server: Java HTTP Server: 1.0\r\n".getBytes());
                            data = "Date: " + new Date() + "\r\n";
                            pr.write(data.getBytes());
                            pr.write("Content-Disposition: attachment\r\n".getBytes());
                            data = "Content-Type: "+ contentType+"\r\n";
                            pr.write(data.getBytes());
                            data = "Content-Length: " + folder.length() + "\r\n";
                            pr.write(data.getBytes());
                            pr.write("\r\n".getBytes());

                            while ((count = inputStream.read(buffer)) > 0){
                                if(!socket.isClosed()){
                                    pr.write(buffer,0,count);
                                    pr.flush();
                                }
                            }
                            pr.flush();

//                            socket.close();

                            System.out.println(path + " is Uploaded Successfully");

                            //new SendFile(folder,socketOuputStream,socket);
                        } else {
                            String content;
                            String data;
                            sb.append("<html>");
                            sb.append("<head><title>404</title></head>");
                            sb.append("<body><h1>404</h1><h2>Page Not Found</h2></body></html>");

                            content = sb.toString();

                            //LogFile
                            logFileWriter.write("RESPONSE\n");
                            logFileWriter.write("HTTP/1.1 404 NOT FOUND\n");
                            logFileWriter.write("Server: Java HTTP Server: 1.0\n");
                            logFileWriter.write("Date: " + new Date() + "\n");
                            logFileWriter.write("Content-Type: text/html\n");
                            logFileWriter.write("Content-Length: " + content.length() + "\n");
                            logFileWriter.write("\n");
                            logFileWriter.close();


                            //Actual Response
                            pr.write("HTTP/1.1 404 NOT FOUND\r\n".getBytes());
                            pr.write("Server: Java HTTP Server: 1.0\r\n".getBytes());
                            data = "Date: " + new Date() + "\r\n";
                            pr.write(data.getBytes());
                            pr.write("Content-Type: text/html\r\n".getBytes());
                            data = "Content-Length: " + content.length() + "\r\n";
                            pr.write(data.getBytes());
                            pr.write("\r\n".getBytes());
                            pr.write(content.getBytes());
                            pr.flush();

//                            socket.close();

                            System.out.println("404 NOT FOUND");
                        }


                    }

                    else if(input.contains("UPLOAD"))
                    {
                        String fileName;
                        int fileSize;
                        byte[] buffer = new byte[4096];

                        System.out.println("here");
                        new FileRecieveThread(socketInputStream,socket);


//                        fileName = in.readLine();
//                        System.out.println(fileName);
//                        fileSize = Integer.parseInt(in.readLine());
//                        System.out.println(fileSize);
//                        if(fileName.equalsIgnoreCase("NOFILE")){
//                            System.out.println("Invalid File Name");
//                        } else {
//                            SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
//                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                            DataInputStream dis = new DataInputStream(socketInputStream);
//
//                            FileOutputStream fos = new FileOutputStream("./root/" + sdf.format(timestamp) + fileName);
//                            int read = 0;
//                            int totalRead = 0;
//                            int remaining = fileSize;
//
//                            while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
//                                totalRead += read;
//                                remaining -= read;
//                                fos.write(buffer, 0, read);
//                            }
//
//                            fos.close();
//                            dis.close();
//                        }
                    }
                }

           // } whileloop
            //socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }


    }
}
