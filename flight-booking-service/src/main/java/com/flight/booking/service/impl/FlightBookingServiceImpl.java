package com.flight.booking.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.booking.dto.BookingDto;
import com.flight.booking.dto.FlightResponse;
import com.flight.booking.dto.UserResponse;
import com.flight.booking.entity.Outbox;
import com.flight.booking.exceptions.BookingApiException;
import com.flight.booking.mappers.FlightBookingMapper;
import com.flight.booking.repo.FlightBookingRepo;
import com.flight.booking.repo.OutboxRepo;
import com.flight.booking.service.FlightBookingService;
import com.flight.booking.user.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;
import jakarta.annotation.PreDestroy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static java.util.Objects.nonNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightBookingServiceImpl implements FlightBookingService {
    private final FlightBookingRepo flightBookingRepo;
    private final FlightBookingMapper flightBookingMapper;
    private final UserClient userClient;
    private final RestTemplate restTemplate;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final OutboxRepo outboxRepo;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${app.user-service.url}")
    private String userServiceUrl;

    @Value("${app.flight-service.url}")
    private String flightServiceUrl;

    @Override
    public List<BookingDto> getBookings() {
        return flightBookingRepo.findAll()
                .stream().map(flightBookingMapper::convertToBookingtDto)
                .toList();
    }

    @Override
    public BookingDto getBookingById(Integer bookingId) {
        return flightBookingRepo.findById(bookingId)
                .map(flightBookingMapper::convertToBookingtDto)
                .orElseThrow(() -> new BookingApiException("Booking not found"));
    }

    @Override
    public CompletableFuture<BookingDto> createBooking(BookingDto bookingDto) {
        var userDetails = isUserIdValid(bookingDto.userId());
        var flightDetails = isFlightIdValid(bookingDto.flightId());

        return userDetails.thenCombine(flightDetails,
                (userResponse, flightResponse) -> {
                        if (!(isValidUserResponse(userResponse) && isValidFlightResponse(flightResponse))) {
                            throw new BookingApiException("Invalid user or flight ID");
                        }

                        return bookAndNotify(bookingDto);
                })
                .exceptionally(ex -> {
                    if (ex instanceof BookingApiException) {
                        throw (BookingApiException) ex;
                    }
                    log.error("Error occurred while booking flight", ex);
                    throw new BookingApiException("Error occurred while saving flight details %s"
                            .formatted(ex.getMessage()));
                });
    }


    private BookingDto bookAndNotify(BookingDto bookingDto) {
        return transactionTemplate.execute(ax -> {
            var bookingEntity = flightBookingMapper.convertToBookingEntity(bookingDto);
            var savedBooking = flightBookingRepo.save(bookingEntity);
            var booking = flightBookingMapper.convertToBookingtDto(savedBooking);

            String payload = null;
            try {
                payload = objectMapper.writeValueAsString(
                        Map.of("bookingId", booking.bookingId(),
                                "userId", booking.userId(),
                                "flightId", booking.flightId(),
                                "bookingTime", LocalDateTime.now().format(DATE_TIME_FORMATTER))
                );

            outboxRepo.save(
                    Outbox.builder()
                            .type("BOOKING_INITIATED")
                            .payload(payload)
                            .status("PENDING")
                            .build()
            );
            } catch (JsonProcessingException e) {
                throw new BookingApiException("Error occurred while converting booking to json %s"
                        .formatted(e.getMessage()));
            }
             return booking;
        });
    }

    private static boolean isValidFlightResponse(ResponseEntity<FlightResponse> flightResponse) {
        return nonNull(flightResponse) && flightResponse.getStatusCode().is2xxSuccessful();
    }

    private static boolean isValidUserResponse(ResponseEntity<UserResponse> userResponse) {
        return nonNull(userResponse) && userResponse.getStatusCode().is2xxSuccessful();
    }

    private CompletableFuture<ResponseEntity<UserResponse>> isUserIdValid(Integer userId) {
        return CompletableFuture.supplyAsync(() ->
                ResponseEntity.ok(userClient.getUser(userId))
         , executorService);
    }

    private CompletableFuture<ResponseEntity<FlightResponse>> isFlightIdValid(Integer flightId) {
        return CompletableFuture.supplyAsync(() ->
                    restTemplate.exchange(flightServiceUrl + "/" + flightId, HttpMethod.GET,
                            null, FlightResponse.class)
                , executorService);
    }

    @Override
    public BookingDto updateBooking(BookingDto bookingDto) {
        return null;
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
