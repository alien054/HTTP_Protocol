import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HTTPServer {
    static final int PORT = 2222;

    public static void main(String[] args) throws IOException {
        ServerSocket httpserver = new ServerSocket(PORT);
        System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
        Socket client;
        ExecutorService pool = Executors.newFixedThreadPool(8);


        while (true){
            client = httpserver.accept();

//            pool.execute(new ClientThread(client));
            new ClientThread(client);
        }


    }
}
