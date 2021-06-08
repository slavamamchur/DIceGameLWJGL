package com.cubegames.engine.auth;

import com.cubegames.engine.domain.auth.User;
import com.cubegames.engine.services.UserService;
import com.cubegames.engine.utils.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

@Service
public class AuthService {

  static final String HEADER_USER_NAME = "user-name";
  static final String HEADER_USER_PASS = "user-pass";
  static final String HEADER_USER_TOKEN = "user-token";

  private static final String TOKEN_SEPARATOR = "/";

  private static final long SECOND = 1000;
  private static final long MINUTE = 60 * SECOND;
  private static final long HOUR = 60 * MINUTE;
  private static final long TOKEN_LIFE_TIME = 200 * HOUR;  //TODO: set to 2 hours

  private static final Logger log = Logger.getLogger(AuthService.class.getName());

  @Autowired
  UserService userService;



  public String getTenantId(HttpServletRequest request) {
    String token = getHeaderOrParameter(request, HEADER_USER_TOKEN);
    if (Utils.stringIsEmpty(token)) {
      throw new SessionAuthenticationException("Token cannot be empty: " + HEADER_USER_TOKEN);
    }
    return getTenantId(token);
  }


  public String getTenantId(String token) {
    TokenInfo tokenInfo = decodeToken(token);
    checkForWrongToken(tokenInfo);
    return tokenInfo.getUserId();
  }


  public String generateToken(HttpServletRequest request) {
    String userName = getHeaderOrParameter(request, HEADER_USER_NAME);
    if (Utils.stringIsEmpty(userName)) {
      throw new UsernameNotFoundException("No value for " + HEADER_USER_NAME);
    }

    String userPass = getHeaderOrParameter(request, HEADER_USER_PASS);
    if (Utils.stringIsEmpty(userPass)) {
      throw new UsernameNotFoundException("No value for " + HEADER_USER_PASS);
    }

    User user = userService.findByName(userName, null);
    if (user == null) {
      throw new BadCredentialsException("User Name is wrong");
    }

    if (!userPass.equals(user.getPassword())) {
      throw new BadCredentialsException("User Password is wrong");
    }

    TokenInfo tokenInfo = new TokenInfo();
    tokenInfo.setUserId(user.getId());
    tokenInfo.setExpireDate(System.currentTimeMillis() + TOKEN_LIFE_TIME);
    return encodeToken(tokenInfo);
  }


  private void checkForWrongToken(TokenInfo tokenInfo) {
    if (tokenInfo == null) {
      throw new SessionAuthenticationException("Token cannot be empty");
    }

    if (Utils.stringIsEmpty(tokenInfo.getUserId())) {
      throw new BadCredentialsException("Token Id cannot be empty");
    }

    if (tokenInfo.getExpireDate() < System.currentTimeMillis()) {
      throw new NonceExpiredException("Token is expired");
    }

    String userId = tokenInfo.getUserId();
    User user = userService.get(userId, null);
    if (user == null) {
      throw new BadCredentialsException("Token Id is wrong");
    }
  }


  private static String getHeaderOrParameter(HttpServletRequest request, String pattern) {
    return getHeaderOrParameter(request, pattern, false);
  }


  private static String getHeaderOrParameter(HttpServletRequest request, String pattern, boolean decodeUrl) {
    String result = request.getHeader(pattern);
    if (Utils.stringIsEmpty(result)) {
      result = request.getParameter(pattern);
      if (!Utils.stringIsEmpty(result) && result.contains(" ")) {
        result = result.replace(" ", "+");
      }
    }
    if (result != null && decodeUrl) {
      try {
        result = URLDecoder.decode(result, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        log.severe("Unsupported encoding of header or parameter: " + e.getMessage());
      }
    }
    return result;
  }


  TokenInfo decodeToken(String encodedToken) {
    if (Utils.stringIsEmpty(encodedToken)) {
      throw new SessionAuthenticationException("Token cannot be empty");
    }

    String token = Cypher.decode(encodedToken);
    if (Utils.stringIsEmpty(token)) {
      throw new BadCredentialsException("Wrong token code");
    }

    String[] arr = token.split(TOKEN_SEPARATOR);
    if (arr.length < 2) {
      throw new BadCredentialsException("Wrong token");
    }

    String userId = arr[0];
    String expired = arr[1];

    long exp;
    try {
      exp = Long.parseLong(expired);
    } catch (NumberFormatException e) {
      throw new BadCredentialsException("Wrong token expired");
    }

    TokenInfo tokenInfo = new TokenInfo();
    tokenInfo.setUserId(userId);
    tokenInfo.setExpireDate(exp);
    return tokenInfo;
  }


  private String encodeToken(TokenInfo tokenInfo) {
    //checkForWrongToken(tokenInfo);
    String userId = tokenInfo.getUserId();
    String expired = "" + tokenInfo.getExpireDate();
    String token = Cypher.encode(userId + TOKEN_SEPARATOR + expired);
    if (Utils.stringIsEmpty(token)) {
      throw new BadCredentialsException("Wrong token code");
    }
    return token;
  }
}
