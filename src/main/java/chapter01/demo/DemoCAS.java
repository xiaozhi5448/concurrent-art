package chapter01.demo;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DemoCAS {
    private int i = 0;
    private AtomicInteger atomicI = new AtomicInteger(0);
    private void count(){
        i++;
    }
    private void safeCount(){
        for(;;){
            int i = atomicI.get();
            boolean flag = atomicI.compareAndSet(i, i+1);
            if(flag){
                break;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DemoCAS cas = new DemoCAS();
        ArrayList<Thread> threads = new ArrayList<>(600);
        for(int i = 0; i < 600; i++){
            threads.add(new Thread(()->{
                for(int pivot = 0; pivot < 10000; pivot++){
                    cas.count();
                    cas.safeCount();
                }
            }));
        }
        for(Thread t:threads){
            t.start();
        }
        for(Thread t:threads){
            t.join();
        }
        System.out.println(cas.i);
        System.out.println(cas.atomicI.get());
    }
}
