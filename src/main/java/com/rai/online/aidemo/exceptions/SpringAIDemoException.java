package com.rai.online.aidemo.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@EqualsAndHashCode(callSuper = true)
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SpringAIDemoException extends RuntimeException {

    private SpringAIDemoErrorCode errorCode;

    public SpringAIDemoException(SpringAIDemoErrorCode errorCode, String message) {
        super(message);

        this.errorCode = errorCode;
    }
}
