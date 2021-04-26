package chapter04.pool;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
@Getter
@Setter
@AllArgsConstructor
public class SimpleHttpServer {
    public static void main(String[] args) throws IOException {
        setBasePath("/tmp");
        setPort(8081);
        start();
    }
    static ThreadPool<HttpRequestHandler> threadPool = new DefaultThreadPool<>(1);
    static String basePath;
    static ServerSocket serverSocket;
    static int port = 8080;
    public static void setPort(int _port){
        port = _port;
    }
    public static void setBasePath(String _path){
        if(_path != null && new File(_path).exists() && new File(_path).isDirectory())
            basePath = _path;
    }
    public static void start() throws IOException{
        serverSocket = new ServerSocket(port);
        Socket socket = null;
        while((socket = serverSocket.accept()) != null){
            System.out.printf("get connection from %s\n", socket.getInetAddress().toString());
            threadPool.execute(new HttpRequestHandler(socket));
        }
        serverSocket.close();
    }
    static void close(Closeable ... closeables){
        if(closeables != null){
            for(Closeable closeable:closeables){
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Setter
    @AllArgsConstructor
    static class HttpRequestHandler implements Runnable{
        Socket socket;

        @Override
        public void run() {
            BufferedReader reader = null;
            PrintStream out = null;
            InputStream in = null;
            try{
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String header = reader.readLine();
                System.out.printf("read header: %s\n", header);
                String[] items = header.trim().split(" ");
                if(items.length <= 1){
                    // 400 bad request
                    throw new Exception("Bad request");
                }
                String filePath = basePath + items[1];
                File resFile = new File(filePath);
                if(!resFile.exists() || !resFile.canRead()){
                    // 404 Not Found
                }
                in = new FileInputStream(filePath);
                out = new PrintStream(socket.getOutputStream());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int readCount = 0;
                byte[] fileContent = new byte[1024];
                while((readCount = in.read(fileContent)) > 0){
                    bos.write(fileContent, 0, readCount);
                }
                byte[] content = bos.toByteArray();
                String contentType = "text/html; charset=UTF-8";
                if(filePath.endsWith("jpg") || filePath.endsWith("ico")){
                    out.println("Content-Type: image/jpeg\r\n");
                }
                out.println("HTTP/1.1 200 OK\r\n" +
                        "Server: Molly\r\n"+
                        "Content-Length: " + content.length + "\r\n" +
                        "Content-Type: " + contentType +"\r\n"
                );


                socket.getOutputStream().write(content, 0, content.length);
                out.flush();
            }catch(FileNotFoundException ex){
                System.out.println("404 Not Found\n");
                out.println("HTTP/1.1 404 Not Found\r\n");
                out.flush();
            }
            catch (Exception ex){
                System.out.println("400 bad request\n");
                out.println("HTTP/1.1 400 Bad request\r\n");
                out.flush();
            }finally {
                close(reader, out, in);
            }

        }
    }
}
