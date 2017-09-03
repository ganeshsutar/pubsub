package com.examples.pubsub.criteria;

import java.util.List;

import org.json.JSONObject;

import com.examples.pubsub.interfaces.Criteria;

public class AndCriteria implements Criteria {
	private List<Criteria> conds;
	
	public AndCriteria(List<Criteria> conds) {
		this.conds = conds;
	}

	public boolean eval(JSONObject message) {
		for(Criteria criteria: conds) {
			if( !criteria.eval(message) ) {
				return false;
			}
		}
		return true;
	}

}
