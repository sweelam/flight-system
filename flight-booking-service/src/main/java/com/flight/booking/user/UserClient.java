package com.flight.booking.user;

import com.flight.booking.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-client", configuration = UserClientConfig.class, url = "${app.user-service.url}")
public interface UserClient {


    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") Integer userId);

}
