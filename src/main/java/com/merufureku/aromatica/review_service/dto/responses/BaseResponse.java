package com.merufureku.aromatica.review_service.dto.responses;

public record BaseResponse<T>(int status, String message, T data){}
