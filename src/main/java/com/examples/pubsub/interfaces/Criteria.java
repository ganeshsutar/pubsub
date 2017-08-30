package com.examples.pubsub.interfaces;

import org.json.JSONObject;

public interface Criteria {
	boolean eval(JSONObject message);
}
