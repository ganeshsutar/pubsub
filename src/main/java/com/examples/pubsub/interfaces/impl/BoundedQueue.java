package com.examples.pubsub.interfaces.impl;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * BoundedQueue with configurable capacity
 * 
 * @author jack
 *
 * @param <E>
 */
public class BoundedQueue<E> extends ArrayBlockingQueue<E> implements Queue<E> {
	private int capacity;
	
	public BoundedQueue(int capacity) {
		super(capacity);
		this.capacity = capacity;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
}
