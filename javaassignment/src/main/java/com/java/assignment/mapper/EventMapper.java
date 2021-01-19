package com.java.assignment.mapper;

import org.springframework.stereotype.Component;

import com.java.assignment.entity.Event;
import com.java.assignment.shared.dto.EventDto;

@Component
public class EventMapper {
	
	

	/**
	 * Takes eventDTO start and finish objects and calculates (non)alert event based on elapsed timestamp
	 * 
	 * @param startRequest
	 * @param finishRequest
	 * @return
	 */
	public Event EventDTOCalc(EventDto startRequest, EventDto finishRequest) {
		// TODO Auto-generated method stub
		Long duration =  finishRequest.getTimestamp() - startRequest.getTimestamp();
        boolean isAlert = duration > 4;
        return new Event(startRequest.getId(), duration, startRequest.getType(), startRequest.getHost(), isAlert);
	}

}
