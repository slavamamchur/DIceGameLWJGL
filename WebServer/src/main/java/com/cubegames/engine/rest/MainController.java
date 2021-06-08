package com.cubegames.engine.rest;

import com.cubegames.engine.auth.AuthService;
import com.cubegames.engine.domain.rest.requests.RegisterRequest;
import com.cubegames.engine.domain.rest.responses.IdResponse;
import com.cubegames.engine.domain.rest.responses.PingResponse;
import com.cubegames.engine.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
//@RequestMapping(value = "/")
public class MainController {

  @Autowired
  private AuthService authService;

  @Autowired
  private UserService userService;

  @ResponseBody
  @RequestMapping(value = RestConst.URL_PING, method = RequestMethod.GET)
  public PingResponse ping() {
    return new PingResponse();
  }


  @ResponseBody
  @RequestMapping(value = RestConst.URL_LOGIN, method = RequestMethod.GET)
  public IdResponse login(HttpServletRequest request) {
    String token = authService.generateToken(request);
    return new IdResponse(token);
  }


  @ResponseBody
  @RequestMapping(value = RestConst.URL_REGISTER, method = RequestMethod.POST)
  @ResponseStatus(value = HttpStatus.OK)
  public void register(@RequestBody RegisterRequest request) {
    userService.registerNewUser(request);
  }

}
