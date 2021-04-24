package chapter04;

import java.util.concurrent.TimeUnit;

public class JoinDemo {
    static class SeqRunner implements Runnable{
        Thread previous;
        public SeqRunner(Thread _previous){
            previous = _previous;
        }
        /**
         * When an object implementing interface {@code Runnable} is used
         * to create a thread, starting the thread causes the object's
         * {@code run} method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method {@code run} is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            try{
                previous.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("%s terminated!\n", Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread previous = Thread.currentThread();
        for(int i = 0; i < 10; i++){
            Thread thread = new Thread(new SeqRunner(previous), "thread" + i);
            thread.start();
            previous = thread;
        }
        TimeUnit.SECONDS.sleep(5);
        System.out.printf("%s terminated!\n", Thread.currentThread().getName());
    }
}
