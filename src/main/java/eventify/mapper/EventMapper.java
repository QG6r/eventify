package eventify.mapper;

import eventify.dto.EventResponse;
import eventify.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "availableTickets", ignore = true)
    EventResponse toResponse(Event event);
}