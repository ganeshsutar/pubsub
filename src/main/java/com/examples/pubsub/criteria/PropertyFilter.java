package com.examples.pubsub.criteria;

import org.json.JSONObject;

import com.examples.pubsub.interfaces.Criteria;

public class PropertyFilter implements Criteria {
	private String name;
	private Object value;
	
	public PropertyFilter(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public boolean eval(JSONObject message) {
		if(!message.has(name)) return false;
		Object messageValue = message.get(name);
		return value.equals(messageValue);
	}
}
