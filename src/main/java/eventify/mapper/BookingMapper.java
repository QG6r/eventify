package eventify.mapper;

import eventify.dto.BookingResponse;
import eventify.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface BookingMapper {

    @Mapping(target = "customerEmail", source = "user.email")
    @Mapping(target = "event", source = "event")
    BookingResponse toResponse(Booking booking);
}