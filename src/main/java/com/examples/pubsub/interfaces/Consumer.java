package com.examples.pubsub.interfaces;

import org.json.JSONObject;

public interface Consumer {
	void consume(JSONObject object) throws Exception;
}
