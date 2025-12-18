package com.merufureku.aromatica.review_service.helper;

import com.merufureku.aromatica.review_service.dao.entity.Token;
import com.merufureku.aromatica.review_service.dao.repository.TokenRepository;
import com.merufureku.aromatica.review_service.exceptions.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.merufureku.aromatica.review_service.constants.CollectionConstants.ACCESS_TOKEN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenHelperTest {

    @InjectMocks
    private TokenHelper tokenHelper;

    @Mock
    private TokenRepository tokenRepository;

    private static final Integer USER_ID = 1;
    private static final String TOKEN = "sampleAccessToken";
    private static final String INVALID_TOKEN = "invalidToken";
    private static final String JTI = "sampleJti";

    @Test
    public void testValidateAccessToken_ValidToken() {

        Token token = Token.builder()
                .userId(USER_ID)
                .token(TOKEN)
                .jti(JTI)
                .type(ACCESS_TOKEN)
                .expirationDt(LocalDateTime.now().plusMinutes(6))
                .build();

        when(tokenRepository.findByUserIdAndJtiAndType(anyInt(), anyString(), anyString()))
                .thenReturn(Optional.of(token));

        assertDoesNotThrow(() -> tokenHelper.validateAccessToken(USER_ID, JTI, TOKEN));
    }

    @Test
    void validateAccessToken_shouldThrow_whenTokenNotFound() {
        when(tokenRepository.findByUserIdAndJtiAndType(anyInt(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () ->
                tokenHelper.validateAccessToken(USER_ID, JTI, TOKEN)
        );
    }

    @Test
    void validateAccessToken_shouldThrow_whenTokenMismatch() {

        Token mismatchToken = Token.builder()
                .userId(USER_ID)
                .token(INVALID_TOKEN)
                .jti(JTI)
                .type(ACCESS_TOKEN)
                .expirationDt(LocalDateTime.now())
                .build();

        when(tokenRepository.findByUserIdAndJtiAndType(anyInt(), anyString(), anyString()))
                .thenReturn(Optional.of(mismatchToken));

        assertThrows(ServiceException.class, () ->
                tokenHelper.validateAccessToken(USER_ID, JTI, TOKEN)
        );
    }

    @Test
    void validateAccessToken_shouldThrow_whenTokenExpired() {

        Token expiredToken = Token.builder()
                .userId(USER_ID)
                .token(TOKEN)
                .jti(JTI)
                .expirationDt(LocalDateTime.now().minusMinutes(10))
                .type(ACCESS_TOKEN)
                .build();

        when(tokenRepository.findByUserIdAndJtiAndType(anyInt(), anyString(), anyString()))
                .thenReturn(Optional.of(expiredToken));

        assertThrows(ServiceException.class, () ->
                tokenHelper.validateAccessToken(USER_ID, JTI, TOKEN)
        );
    }

}
