package com.examples.pubsub.interfaces;

import org.json.JSONObject;


public interface MessageChannel {
	void publish(JSONObject message);
	void subscribe(Criteria criteria, Consumer consumer);
}
