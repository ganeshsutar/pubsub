package com.examples.pubsub.interfaces.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.json.JSONObject;

import com.examples.pubsub.interfaces.Consumer;
import com.examples.pubsub.interfaces.Criteria;

public class InMemoryConsumerLoopRetry implements Runnable, Closeable {
	private final BlockingQueue<JSONObject> queue;
	private final Map<Criteria, Consumer> consumers;
	private final int retryCount;
	private volatile boolean keepRunning = true;
	
	public InMemoryConsumerLoopRetry(BlockingQueue<JSONObject> queue, Map<Criteria, Consumer> consumers, int retryCount) {
		this.queue = queue;
		this.consumers = consumers;
		this.retryCount = retryCount;
	}

	public void run() {
		try {
			while(keepRunning) {
				JSONObject message = queue.take();
				for(Map.Entry<Criteria, Consumer> entry : consumers.entrySet()) {
					if(entry.getKey().eval(message)) {
						handleRetry(message, entry.getValue());
					}
				}
			}
		} catch (InterruptedException e) {
			// Exit gracefully
			Thread.currentThread().interrupt();
		}
	}
	
	private void handleRetry(JSONObject json, Consumer consumer) {
		for(int i=0 ; i<retryCount ; ++i) {
			try {
				consumer.consume(json);
				return;
			} catch (Exception e) {
				/* Ignore try again */
			}
		}
	}
	
	public void close() throws IOException {
		keepRunning = false;
	}
}
