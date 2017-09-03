package com.examples.pubsub.interfaces.impl;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.examples.pubsub.interfaces.Consumer;
import com.examples.pubsub.interfaces.Criteria;
import com.examples.pubsub.interfaces.MessageChannel;

public class InMemoryMessageChannel implements MessageChannel {
	private BlockingQueue<JSONObject> queue;
	private Map<Criteria, Consumer> consumers;
	private Thread consumerThread = null;
	private Closeable consumerRunnable = null;
	
	public InMemoryMessageChannel(int capacity) {
		this(capacity, -1);
	}
	
	public InMemoryMessageChannel(BlockingQueue<JSONObject> queue) {
		this(queue, -1);
	}
	
	public InMemoryMessageChannel(int capacity, int retryCount) {
		this.queue = new VariableLinkedBlockingQueue<JSONObject>(capacity);
		this.consumers = new ConcurrentHashMap<Criteria, Consumer>();
		
		if( retryCount > 0 ) {
			startRetryThread(retryCount);
		} else {
			startNonRetryThread();
		}
	}

	public InMemoryMessageChannel(BlockingQueue<JSONObject> queue, int retryCount) {
		this.queue = queue;
		this.consumers = new ConcurrentHashMap<Criteria, Consumer>();

		if( retryCount > 0 ) {
			startRetryThread(retryCount);
		} else {
			startNonRetryThread();
		}
	}

	private void startNonRetryThread() {
		InMemoryConsumerLoop loop = new InMemoryConsumerLoop(queue, consumers);
		this.consumerRunnable = loop;
		this.consumerThread = new Thread(loop);
		this.consumerThread.start();
	}
	
	private void startRetryThread(int retry) {
		InMemoryConsumerLoopRetry loop = new InMemoryConsumerLoopRetry(queue, consumers, retry);
		this.consumerRunnable = loop;
		this.consumerThread = new Thread(loop);
		this.consumerThread.start();
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
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

	public void close() {
		try {
			this.consumerRunnable.close();
		} catch (Exception ex) {
			/* Ignore */
		}
	}
}
