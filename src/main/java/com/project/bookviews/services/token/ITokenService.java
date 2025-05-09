package com.project.bookviews.services.token;

import com.project.bookviews.models.Token;
import com.project.bookviews.models.User;
import org.springframework.stereotype.Service;

@Service

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobileDevice);
    Token refreshToken(String refreshToken, User user) throws Exception;
}
