package com.examples.pubsub.interfaces;

import org.json.JSONObject;


public interface MessageChannel {
	void publish(JSONObject message) throws Exception;
	void subscribe(Criteria criteria, Consumer consumer);
	boolean isFull();
	void close();
}
