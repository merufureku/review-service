package com.merufureku.aromatica.review_service.utilities;

import com.merufureku.aromatica.review_service.config.KeyConfig;
import com.merufureku.aromatica.review_service.exceptions.ServiceException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Base64;

import static com.merufureku.aromatica.review_service.constants.CollectionConstants.REVIEW_SERVICE;
import static com.merufureku.aromatica.review_service.enums.CustomStatusEnums.INVALID_TOKEN;

@Component
public class TokenUtility {

    private final KeyConfig keyConfig;

    public TokenUtility(KeyConfig keyConfig) {
        this.keyConfig = keyConfig;
    }

    public Claims parseToken(String token) {

        try{
            var secretKey = Keys.hmacShaKeyFor(Base64.getDecoder()
                    .decode(keyConfig.getJwtAccessSecretKey()));

            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (Exception e){
            throw new ServiceException(INVALID_TOKEN);
        }
    }

    public Claims validateInternalToken(String token) {

        var secretKey = Keys.hmacShaKeyFor(Base64.getDecoder()
                .decode(keyConfig.getJwtInternalReviewsSecretKey()));

        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (JwtException ex) {
            throw new ServiceException(INVALID_TOKEN);
        }
    }

    public boolean isValidService(Claims claims) {
        return "internal".equals(claims.get("type"))
                && REVIEW_SERVICE.equals(claims.get("service"));
    }
}
