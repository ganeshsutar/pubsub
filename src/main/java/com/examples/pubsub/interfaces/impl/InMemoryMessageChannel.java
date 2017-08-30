package com.examples.pubsub.interfaces.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.examples.pubsub.interfaces.Consumer;
import com.examples.pubsub.interfaces.Criteria;
import com.examples.pubsub.interfaces.MessageChannel;

public class InMemoryMessageChannel implements MessageChannel {
	private Queue<JSONObject> queue;
	private Map<Criteria, List<Consumer>> consumers;
	
	public InMemoryMessageChannel(int capacity) {
		queue = new BoundedQueue<JSONObject>(capacity);
		consumers = new ConcurrentHashMap<Criteria, List<Consumer>>();
	}

	public void publish(JSONObject message) {
		queue.add(message);
		fireConsumers(message);
	}

	private void fireConsumers(JSONObject message) {
		//TODO: Run this on new thread so that queue can hold some messages
		try {
			for(Map.Entry<Criteria, List<Consumer>> entry : consumers.entrySet()){
				if( entry.getKey().eval(message) ) {
					handleMessage(message, entry.getValue());
				}
			}
		} finally {
			queue.remove(message);
		}
	}
	
	//TODO: message handling should be done in another way
	private void handleMessage(JSONObject message, List<Consumer> consumers) {
		for(Consumer consumer: consumers) {
			consumer.consume(message);
		}
	}

	public synchronized void subscribe(Criteria criteria, Consumer consumer) {
		if(!consumers.containsKey(criteria)){
			consumers.put(criteria, Collections.synchronizedList(new ArrayList<Consumer>()));
		}
		consumers.get(criteria).add(consumer);
	}
}
