package io.github.xeltalliv.lab2.part2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerQueue<T> {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    
	private final T[] values;
    private final int capacity;
    private int producerCount;
    private int head = 0;
    private int tail = 0;
    private int count = 0;

    @SuppressWarnings("unchecked")
    public ProducerConsumerQueue(int capacity, int producerCount) {
        this.values = (T[]) new Object[capacity];
        this.capacity = capacity;
        this.producerCount = producerCount;
    }
    
    public void put(T item) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (count == capacity) {
                notFull.await();
            }
            
            values[tail] = item;
            tail = (tail + 1) % capacity;
            count++;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
    
    public void endProducer() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            producerCount--;
            if (producerCount == 0) {
                notEmpty.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (count == 0 && producerCount > 0) {
                notEmpty.await();
            }
            if (count == 0 && producerCount == 0) return null;
            
            T value = values[head];
            values[head] = null;
            head = (head + 1) % capacity;
            count--;
            notFull.signal();
            return value;
        } finally {
            lock.unlock();
        }
    }
    
    public int getSize() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
