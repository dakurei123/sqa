package com.example.sqa.exception;


import java.io.Serializable;

public class ApiInputException extends BaseException implements Serializable {
    private static final long serialVersionUID = -4657293239269065718L;

    public ApiInputException(String errorMessage) {
        super(errorMessage, null, null);
    }

    public ApiInputException(String errorMessage, Object value) {
        super(errorMessage, value, null);
    }

    public ApiInputException(String errorMessage, Object value, Throwable cause) {
        super(errorMessage, value, cause);
    }
}
