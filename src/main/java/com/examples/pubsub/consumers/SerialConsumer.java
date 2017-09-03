package com.examples.pubsub.consumers;

import java.util.List;

import org.json.JSONObject;

import com.examples.pubsub.interfaces.Consumer;

public class SerialConsumer implements Consumer {
	private List<Consumer> consumers;
	
	public SerialConsumer(List<Consumer> consumers) {
		this.consumers = consumers;
	}

	public void consume(JSONObject message) throws Exception {
		for(Consumer consumer : consumers) {
			consumer.consume(message);
		}
	}
}
