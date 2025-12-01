package com.merufureku.aromatica.review_service.exceptions;

import com.merufureku.aromatica.review_service.enums.CustomStatusEnums;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{

    private final CustomStatusEnums customStatusEnums;

    public ServiceException(CustomStatusEnums customStatusEnums){
        super(customStatusEnums.getMessage());
        this.customStatusEnums = customStatusEnums;
    }

}
