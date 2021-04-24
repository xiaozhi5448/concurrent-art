package chapter01.demo;

public class Demo01 {
    private static final long count = 100001;

    static void concurrency() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread thread = new Thread(()->{
            int a = 0;
            for(long i = 0; i < count; i++){
                a += i;
            }
        });
        thread.start();
        int b = 0;
        for(long i = 0;  i < count; i++){
            b--;
        }
        thread.join();
        long time = System.currentTimeMillis();
        System.out.printf("concurrency: %d ms;b=%d\n", time - start, b);
    }



    public static void main(String[] args) throws InterruptedException {
        concurrency();
    }
}
