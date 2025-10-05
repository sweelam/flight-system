package com.flight.booking.builders;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class EmailBuilders {
    private static final String APPLICATION_NAME = "flight-booking-service";
    private static final String MESSAGE_BODY = "Your flight booking is Pending";

    private EmailBuilders() {
    }

    private static final Gson gson = new Gson();

    public static String buildEmailEvent(final String payload) {
        Instant now = Instant.now();
        var data = JsonParser.parseString(payload);

        return gson.toJson(
                new EmailEvent(UUID.randomUUID().toString(), MESSAGE_BODY,
                        APPLICATION_NAME, now.toString(), data));
    }

    private record EmailEvent(
            String flightUuid,
            String messageBody,
            String producer,
            String eventTime,
            Object data
    ) {}
}
