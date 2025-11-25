package com.air.quality.index.aqi.Exception;

public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message) { super(message); }
    public ExternalApiException(String message, Throwable cause) { super(message, cause); }
}
