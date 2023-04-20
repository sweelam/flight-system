package com.flight.service;

import com.flight.dto.Flight;
import com.flight.exceptions.FlightApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {
    private final CustomerService customerService;
    private final EmailService emailService;


    public FlightService(CustomerService customerService, EmailService emailService) {
        this.customerService = customerService;
        this.emailService = emailService;
    }

    public void bookNewFlight(final Flight flightAdded, List<Flight> allFlights ) {
        if (!customerService.customerFound(flightAdded.customerEmail())) {
            throw new FlightApiException("No customer found with provided email", HttpStatus.NOT_FOUND);
        }

        allFlights.stream()
                .filter(fl ->
                        fl.flightName().equalsIgnoreCase(flightAdded.flightName()) ||
                                fl.id().equals(flightAdded.id())
                )
                .findAny()
                .ifPresent(fl -> {
                    throw new FlightApiException("Flight already booked!", HttpStatus.BAD_REQUEST);
                });

        allFlights.add(flightAdded);

        // push email event
        emailService.sendEmail(flightAdded.customerEmail(), flightAdded.flightName());
    }
}
