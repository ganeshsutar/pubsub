package com.examples.pubsub.interfaces.impl;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.examples.pubsub.interfaces.Consumer;
import com.examples.pubsub.interfaces.Criteria;
import com.examples.pubsub.interfaces.MessageChannel;

public class InMemoryMessageChannel implements MessageChannel, Runnable {
	private BlockingQueue<JSONObject> queue;
	private Map<Criteria, Consumer> consumers;
	private volatile boolean keepRunning = true;
	private Thread consumerThread = null;
	
	public InMemoryMessageChannel(int capacity) {
		this.queue = new VariableLinkedBlockingQueue<JSONObject>(capacity);
		this.consumers = new ConcurrentHashMap<Criteria, Consumer>();
		this.consumerThread = new Thread(this);
		this.consumerThread.start();
	}
	
	public InMemoryMessageChannel(BlockingQueue<JSONObject> queue) {
		this.queue = queue;
		this.consumers = new ConcurrentHashMap<Criteria, Consumer>();
		this.consumerThread = new Thread(this);
		this.consumerThread.start();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		keepRunning = false;
	}
	
	public BlockingQueue<JSONObject> getQueue() {
		return this.queue;
	}
	
	public void publish(JSONObject message) throws Exception {
		queue.put(message);
	}

	public void subscribe(Criteria criteria, Consumer consumer) {
		consumers.put(criteria, consumer);
	}
	
	public boolean isFull() {
		return queue.remainingCapacity() == 0;
	}

	public void run() {
		try {
			while(keepRunning) {
				JSONObject message = queue.take();
				for(Map.Entry<Criteria, Consumer> entry : consumers.entrySet()) {
					if(entry.getKey().eval(message)) {
						try {
							entry.getValue().consume(message);
						} catch (Exception e) {
							/* Ignore for now */
						}
					}
				}
			}
		} catch (InterruptedException e) {
			// Exit gracefully
			Thread.currentThread().interrupt();
		}
	}

	public void close() {
		keepRunning = false;
	}
}
