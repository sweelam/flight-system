package com.flight.booking.mappers;


import com.flight.booking.dto.BookingDto;
import com.flight.booking.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperSpringConfig.class)
public interface FlightBookingMapper {
    @Mapping(source = "id", target = "bookingId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "flightId", target = "flightId")
    BookingDto convertToBookingDto(Booking booking);

    @Mapping(source = "bookingId", target = "id")
    @Mapping(source = "status", target = "status", defaultValue = "PENDING")
    @Mapping(target = "bookingTime", expression = "java(bookingDto.bookingTimeOrDefault())")
    Booking convertToBookingEntity(BookingDto bookingDto);
}
