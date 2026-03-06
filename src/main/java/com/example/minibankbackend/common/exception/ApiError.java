package com.example.minibankbackend.common.exception;


import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        String path,
        String errorCode,
        String message,
        List<String> details
) {}
