package com.booking.system.service;

import com.booking.system.dto.EventResponse;
import com.booking.system.entity.Event;
import com.booking.system.exception.ResourceNotFoundException;
import com.booking.system.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    @Cacheable(value = "events", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<EventResponse> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(this::mapToEventResponse);
    }

    public Event getEventEntity(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }
    
    public EventResponse getEventById(Long id) {
        return mapToEventResponse(getEventEntity(id));
    }

    @Transactional
    public EventResponse createEvent(com.booking.system.dto.CreateEventRequest request) {
        Event event = Event.builder()
                .name(request.getName())
                .location(request.getLocation())
                .date(request.getDate())
                .build();
        
        Event saved = eventRepository.save(event);
        return mapToEventResponse(saved);
    }

    private EventResponse mapToEventResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .date(event.getDate())
                .location(event.getLocation())
                .build();
    }
}
