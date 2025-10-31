package com.flight.booking.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.booking.dto.BookingDto;
import com.flight.booking.dto.FlightResponse;
import com.flight.booking.dto.UserResponse;
import com.flight.booking.infra.IntegrationTestSupport;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.math.BigDecimal;
import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableWireMock({
        @ConfigureWireMock(
                name = "user-service",
                port = 8086
        ),
        @ConfigureWireMock(
                name = "flight-service",
                port = 8089
        )
})
@TestPropertySource(properties = {
        "app.user-service.url=http://localhost:8086",
        "app.flight-service.url=http://localhost:8089"
})
class FLightBookingControllerTest extends IntegrationTestSupport {
    private final String FLIGHT_BOOKING_URL = "/bookings";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectWireMock("user-service")
    private WireMockServer userServiceWireMockServer;

    @InjectWireMock("flight-service")
    private WireMockServer flightServiceWireMockServer;

    @Test
    void getBookings_ShouldReturnOk() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(FLIGHT_BOOKING_URL).accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        assertNotNull(contentAsString);
        assertFalse(contentAsString.isEmpty());
    }

    @Test
    void getBookingsById_ShouldReturnOk() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(FLIGHT_BOOKING_URL)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("bookingId", "1")
                ).andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        assertNotNull(contentAsString);
        assertFalse(contentAsString.isEmpty());
    }

    @Test
    void bookFlight_ShouldReturnOk() throws Exception {
        var userId = 2;
        var flightId = 1;

        var userResponse =
                new UserResponse(2, "abas.gmail.com", "abas");

        userServiceWireMockServer.stubFor(WireMock.get("/" + userId)
                .willReturn(ok(objectMapper.writeValueAsString(userResponse))
                        .withHeader("Content-Type", "application/json")));

        var flightResponse = new FlightResponse(1,
                "AKE123",
                "DXB",
                "Cairo",
                Instant.now(),
                Instant.now().plusSeconds(60*60*8),
                BigDecimal.valueOf(2514));

        flightServiceWireMockServer.stubFor(WireMock.get("/" + flightId)
                .willReturn(ok(objectMapper.writeValueAsString(flightResponse))
                .withHeader("Content-Type", "application/json")));

        var request = new BookingDto(null,
                userId, flightId, null, null);

        MvcResult mvcResult = mockMvc.perform(
                post(FLIGHT_BOOKING_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn();


        mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isCreated());
    }

    @Test
    void bookFlight_ShouldReturnBadRequest() throws Exception {
        var request = new BookingDto(null, null, 1, null, null);
        mockMvc.perform(
                post(FLIGHT_BOOKING_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void bookFlightWithWrongBookingId_ShouldReturnBadRequest() throws Exception {
        var request = new BookingDto(null, -2, 1, null, null);

        MvcResult mvcResult = mockMvc.perform(
                post(FLIGHT_BOOKING_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn();

        mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isBadRequest());
    }
}
