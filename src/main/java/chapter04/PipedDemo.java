package chapter04;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

public class PipedDemo {
    static PipedWriter out = new PipedWriter();
    static PipedReader in = new PipedReader();
    public static void main(String[] args) throws IOException {
        out.connect(in);
        Thread printThread = new Thread(()->{
            int receive = 0;
            try{
                while((receive = in.read()) != -1){
                    System.out.print((char)receive);
                }
            } catch (IOException e) {

                e.printStackTrace();
            }finally {
//                in.close();
            }
        }, "printThread");
        printThread.start();
        int receive = 0;
        try{
            while((receive = System.in.read()) != -1){
                out.write(receive);
                if(receive == 'q'){
                    break;
                }
            }
        }finally {
            out.close();
        }
        printThread.interrupt();
    }
}
