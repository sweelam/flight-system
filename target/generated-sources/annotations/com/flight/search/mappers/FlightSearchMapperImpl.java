package com.flight.search.mappers;

import com.flight.search.dto.FlightDto;
import com.flight.search.entity.Flight;
import java.math.BigDecimal;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-08T21:32:56+0400",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class FlightSearchMapperImpl implements FlightSearchMapper {

    @Override
    public FlightDto convertToFlightDto(Flight flight) {
        if ( flight == null ) {
            return null;
        }

        Integer id = null;
        String flightNumber = null;
        String departureAirport = null;
        String arrivalAirport = null;
        Instant departureTime = null;
        Instant arrivalTime = null;
        BigDecimal price = null;

        id = flight.getId();
        flightNumber = flight.getFlightNumber();
        departureAirport = flight.getDepartureAirport();
        arrivalAirport = flight.getArrivalAirport();
        departureTime = flight.getDepartureTime();
        arrivalTime = flight.getArrivalTime();
        price = flight.getPrice();

        FlightDto flightDto = new FlightDto( id, flightNumber, departureAirport, arrivalAirport, departureTime, arrivalTime, price );

        return flightDto;
    }

    @Override
    public Flight convertToFlightEntity(FlightDto flightDto) {
        if ( flightDto == null ) {
            return null;
        }

        Flight.FlightBuilder flight = Flight.builder();

        flight.arrivalAirport( flightDto.arrivalAirport() );
        flight.arrivalTime( flightDto.arrivalTime() );
        flight.departureAirport( flightDto.departureAirport() );
        flight.departureTime( flightDto.departureTime() );
        flight.flightNumber( flightDto.flightNumber() );
        flight.id( flightDto.id() );
        flight.price( flightDto.price() );

        return flight.build();
    }
}
