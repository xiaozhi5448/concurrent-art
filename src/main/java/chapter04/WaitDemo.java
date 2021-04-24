package chapter04;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WaitDemo {
    static boolean flag = true;
    static Object object = new Object();

    public static void main(String[] args) {
        Thread waitThread = new Thread(()->{
            synchronized (WaitDemo.class){
                while(flag){
                    try{
                        System.out.println(Thread.currentThread() + " flag is true, wait @" +
                                new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        WaitDemo.class.wait();
                    }catch (InterruptedException e){

                    }
                }
            }
        }, "waitThread");

        Thread notifyThread = new Thread(()->{
            synchronized (WaitDemo.class){

                System.out.printf("%s hold lock. nofify @ %s\n", Thread.currentThread(),
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
                flag = false;
                try{
                    Thread.sleep(5000);
                }catch (InterruptedException e){

                }
                WaitDemo.class.notifyAll();

            }
            synchronized (WaitDemo.class){
                System.out.printf("%s hold lock again. sleep @%s\n", Thread.currentThread(), new SimpleDateFormat("" +
                        "HH:mm:ss").format(new Date()));
                try{
                    Thread.sleep(5000);
                }catch (InterruptedException e){

                }
            }
        }, "notifyThread");
        waitThread.start();
        notifyThread.start();
    }
}
