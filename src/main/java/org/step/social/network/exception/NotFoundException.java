package org.step.social.network.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

//@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    private final LocalDateTime time;

    public NotFoundException(String message) {
        super(message);
        this.time = LocalDateTime.now();
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.time = LocalDateTime.now();
    }

    public LocalDateTime getTime() {
        return time;
    }
}
