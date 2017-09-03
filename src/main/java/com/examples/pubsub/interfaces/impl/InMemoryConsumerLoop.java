package com.examples.pubsub.interfaces.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.json.JSONObject;

import com.examples.pubsub.interfaces.Consumer;
import com.examples.pubsub.interfaces.Criteria;

public class InMemoryConsumerLoop implements Runnable, Closeable {
	private final BlockingQueue<JSONObject> queue;
	private final Map<Criteria, Consumer> consumers;
	private volatile boolean keepRunning = true;
	
	public InMemoryConsumerLoop(BlockingQueue<JSONObject> queue, Map<Criteria, Consumer> consumers) {
		this.queue = queue;
		this.consumers = consumers;
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
	
	public void close() throws IOException {
		keepRunning = false;
	}
}
