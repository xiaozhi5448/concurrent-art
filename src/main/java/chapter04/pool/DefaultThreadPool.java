package chapter04.pool;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job>{
    class Worker implements Runnable{
        private volatile boolean running = true;

        public void shutdown(){
            running = false;
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
            Job job = null;
            while(running){
                synchronized (jobs){
                    while(jobs.isEmpty()){
                        try{
                            jobs.wait();
                        }catch (InterruptedException ex){
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    job = jobs.removeFirst();
                }
                if(job != null){
                    try{
                        job.run();
                    }catch (Exception ex){
                        // ignore
                    }
                }
            }


        }
    }
    private static final int MAX_WORKER_NUMBERS = 10;
    private static final int DEFAULT_WORKER_NUMBERS = 5;
    private static final int MIN_WORKER_NUMBERS = 1;
    private final LinkedList<Job> jobs = new LinkedList<>();
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<>());
    private AtomicLong threadNum = new AtomicLong();
    private int workerCount = DEFAULT_WORKER_NUMBERS;

    private void initializeWorkers(int num){
        for(int i = 0; i < num; i++){
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.getAndIncrement());
            thread.start();
        }
    }

    public DefaultThreadPool(){
        initializeWorkers(MIN_WORKER_NUMBERS);
        workerCount = MIN_WORKER_NUMBERS;
    }

    public DefaultThreadPool(int num){
        num = Math.min(num, MAX_WORKER_NUMBERS);
        num = Math.max(num, MIN_WORKER_NUMBERS);
        initializeWorkers(num);
        workerCount = num;
    }
    @Override
    public void execute(Job job) {
        if(job != null){
            synchronized (jobs){
                jobs.addLast(job);
                jobs.notify();
            }
        }
    }

    @Override
    public void shutdown() {
        for(Worker worker:workers){
            worker.shutdown();
        }
    }

    @Override
    public void addWorkers(int num) {
        synchronized (jobs){
            if(num + this.workerCount > MAX_WORKER_NUMBERS){
                num = MAX_WORKER_NUMBERS - this.workerCount;
            }
            initializeWorkers(num);
            this.workerCount += num;
        }
    }

    @Override
    public void removeWorkers(int num) {
        synchronized (jobs){
            if(num > this.workerCount){
                throw new IllegalArgumentException("beyond workNum!");
            }

        }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }
}
