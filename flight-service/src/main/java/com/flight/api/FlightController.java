package com.flight.api;

import com.flight.dto.Flight;
import com.flight.exceptions.CustomerApiException;
import com.flight.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/flights")
public class FlightController {
    private final List<Flight> allFlights = new ArrayList(
            List.of(
                new Flight(UUID.fromString("558c85a2-b542-11ed-afa1-0242ac120002"), "cairo-827",
                        Instant.now().plus(15, ChronoUnit.DAYS), "md.ahmed@gmail.com"),

                new Flight(UUID.fromString("94631732-b542-11ed-afa1-0242ac120002"), "tokyo-661",
                        Instant.now().plus(20, ChronoUnit.DAYS), "alex.mon@gmail.com"),

                new Flight(UUID.fromString("bc0776c0-b542-11ed-afa1-0242ac120002"), "dubai-515",
                        Instant.now().plus(25, ChronoUnit.DAYS), "hesham.masoud@gmail.com")
            )
    );

    private final EmailService emailService;

    public FlightController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("")
    public ResponseEntity<List<Flight>> getAllFlights() {
        return ResponseEntity.ok(allFlights);
    }

    @PostMapping("")
    public ResponseEntity<Flight> bookNewFlight(@RequestBody Flight flight) {
        UUID uuid = UUID.randomUUID();
        Flight flightAdded = new Flight(uuid, flight.flightName(), null == flight.time() ? Instant.now() : flight.time(), flight.customerEmail());

        if (!emailService.customerFound(flightAdded.customerEmail())) {
            throw new CustomerApiException("No customer found with provided email", HttpStatus.NOT_FOUND);
        }

        allFlights.add(flightAdded);

        // push email event
        emailService.sendEmail(flight.customerEmail(), flight.flightName());

        return new ResponseEntity(flightAdded, HttpStatus.CREATED);
    }
}
