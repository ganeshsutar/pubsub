package com.examples.pubsub;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.examples.pubsub.interfaces.Consumer;
import com.examples.pubsub.interfaces.Criteria;
import com.examples.pubsub.interfaces.MessageChannel;
import com.examples.pubsub.interfaces.impl.InMemoryMessageChannel;

/**
 * Application Started/Driver Program
 */
public class App 
{
    public static void main( String[] args )
    {
    	MessageChannel channel = new InMemoryMessageChannel(32);
    	Consumer consumer = new Consumer() {
			public void consume(JSONObject object) {
				System.out.println(object);
			}
		};
		Criteria trueCriteria = new Criteria() {
			public boolean eval(JSONObject message) {
				return true;
			}
		};
		channel.subscribe(trueCriteria, consumer);
		
		for(int i=0 ; i<1000 ; ++i) {
			Map<String, String> values = new HashMap<String, String>();
			values.put("name-"+i, "value-"+i);
			JSONObject message = new JSONObject(values);
			channel.publish(message);
		}
    }
}
