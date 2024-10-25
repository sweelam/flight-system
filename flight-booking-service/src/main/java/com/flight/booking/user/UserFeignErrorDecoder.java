package com.flight.booking.user;

import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserFeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.warn("retry {}", methodKey);

        Exception th = errorDecoder.decode(methodKey, response);

        if (response.status() == 404) {
            return null;
        }

        if (response.status() == 400) {
            return null;
        }

        if (response.status() == 503) {
            return new RetryableException(
                    response.status(),
                    "message connection issue",
                    response.request().httpMethod(),
                    th.getCause(),
                    (Long) null,
                    response.request()
            );
        }

        return null;
    }
}
