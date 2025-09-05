package com.honeyrest.honeyrest_host.serviceOwner;

import com.amazonaws.services.kms.model.NotFoundException;
import com.honeyrest.honeyrest_host.dtoOwner.EventDTO;
import com.honeyrest.honeyrest_host.entity.Event;
import com.honeyrest.honeyrest_host.repositoryOwner.OEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OEventService {
    private final OEventRepository eventRepository;
    private final ModelMapper modelMapper;

    public void registerEvent(EventDTO dto) {
        Event event = modelMapper.map(dto, Event.class);
        eventRepository.save(event);
    }

    public void removeEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public void modifyEvent(EventDTO dto) {
        Event event = modelMapper.map(dto, Event.class);
    }

    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(e -> modelMapper.map(e, EventDTO.class))
                .toList();
    }

    public EventDTO getEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("이벤트가 존재하지 않습니다."));
        return modelMapper.map(event, EventDTO.class);
    }
}
