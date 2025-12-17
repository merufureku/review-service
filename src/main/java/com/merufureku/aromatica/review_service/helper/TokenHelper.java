package com.merufureku.aromatica.review_service.helper;

import com.merufureku.aromatica.review_service.dao.repository.TokenRepository;
import com.merufureku.aromatica.review_service.exceptions.ServiceException;
import com.merufureku.aromatica.review_service.utilities.TokenUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import static com.merufureku.aromatica.review_service.constants.CollectionConstants.ACCESS_TOKEN;
import static com.merufureku.aromatica.review_service.enums.CustomStatusEnums.INVALID_TOKEN;
import static com.merufureku.aromatica.review_service.utilities.DateUtility.isAccessTokenExpired;

@Component
public class TokenHelper {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final TokenRepository tokenRepository;
    private final TokenUtility tokenUtility;

    public TokenHelper(TokenRepository tokenRepository, TokenUtility tokenUtility) {
        this.tokenRepository = tokenRepository;
        this.tokenUtility = tokenUtility;
    }

    public boolean validateAccessToken(Integer userId, String jti, String validatingToken){
        logger.info("Validating token for: {}", userId);

        var originalToken = tokenRepository.findByUserIdAndJtiAndType(userId, jti, ACCESS_TOKEN)
                .orElseThrow(() -> new ServiceException(INVALID_TOKEN));

        if (!originalToken.getToken().equals(validatingToken)){
            logger.info("Invalid token found!");
            throw new ServiceException(INVALID_TOKEN);
        }
        if (isAccessTokenExpired(originalToken.getExpirationDt())){
            logger.info("Token expired!");
            throw new ServiceException(INVALID_TOKEN);
        }

        return true;
    }

    public void validateInternalToken(String token){
        var claims = tokenUtility.validateInternalToken(token);

        if (!tokenUtility.isValidService(claims)){
            throw new ServiceException(INVALID_TOKEN);
        }
    }
}
