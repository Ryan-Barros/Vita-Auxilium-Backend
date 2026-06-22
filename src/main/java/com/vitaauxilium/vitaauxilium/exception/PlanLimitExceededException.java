package com.vitaauxilium.vitaauxilium.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
public class PlanLimitExceededException extends RuntimeException{
    public PlanLimitExceededException(String message) {
        super(message);
    }
}
