package com.rai.online.aidemo.exceptions;

import com.rai.online.aidemo.apis.model.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.util.Objects.isNull;

@Slf4j
@ControllerAdvice
public class SpringAIDemoExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseBody
    @ExceptionHandler(java.lang.IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException exception) {

        Error error = new Error();
        error.setCode(SpringAIDemoErrorCode.E2002.getCode());
        error.setMessage(exception.getMessage());

        exception.printStackTrace();
        logError(exception);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Error> exception(Exception exception) {
        Error error = new Error();
        error.setCode(SpringAIDemoErrorCode.E2004.getCode());
        error.setMessage(isNull(exception.getMessage()) ? "Not Available" : exception.getMessage());
        logError(exception);
        exception.printStackTrace();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SpringAIDemoException.class)
    public ResponseEntity<Error> handleDDDTechnicalException(SpringAIDemoException ex) {
        Error error = new Error();
        error.setCode(ex.getErrorCode().getCode());
        error.setMessage(ex.getMessage());
        logError(ex);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    private void logError(Throwable t) {
        if (log.isDebugEnabled()) {
            log.error(t.getClass().getSimpleName() + " : {}", t.getMessage(), t);
        } else {
            log.error(t.getClass().getSimpleName() + " : {}", t.getMessage());
        }
    }
}
