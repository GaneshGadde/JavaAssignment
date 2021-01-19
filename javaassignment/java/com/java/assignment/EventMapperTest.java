package com.java.assignment;

import com.java.assignment.entity.Event;
import com.java.assignment.mapper.EventMapper;
import com.java.assignment.shared.dto.EventDto;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventMapperTest {
    private static final String APPLICATION_LOG = "APPLICATION_LOG";
    private static final String ID = "scsmbstgra";
    private static final String HOST_1 = "HOST1";
    private EventMapper eventConverter = new EventMapper();

    @Test
    public void testEventDTOToEvent_NonAlertEvent() {
        EventDto start = new EventDto(ID, EventDto.State.STARTED, APPLICATION_LOG, HOST_1, 123L);
        EventDto finish = new EventDto(ID, EventDto.State.FINISHED, APPLICATION_LOG, HOST_1, 124L);

        Event event = eventConverter.EventDTOCalc(start, finish);
        assertFalse("Request should not be returned as alert", event.isAlert());
    }

    @Test
    public void testEventDTOToEvent_AlertEvent() {
    	EventDto start = new EventDto(ID, EventDto.State.STARTED, APPLICATION_LOG, HOST_1, 123L);
    	EventDto finish = new EventDto(ID, EventDto.State.FINISHED, APPLICATION_LOG, HOST_1, 128L);

        Event event = eventConverter.EventDTOCalc(start, finish);
        assertTrue("Request should be returned as alert", event.isAlert());
    }
}