package com.cubegames.engine.services;

import com.cubegames.engine.dao.AbstractDao;
import com.cubegames.engine.dao.UserDao;
import com.cubegames.engine.domain.auth.User;
import com.cubegames.engine.domain.auth.UserRole;
import com.cubegames.engine.domain.rest.requests.RegisterRequest;
import com.cubegames.engine.exceptions.BasicException;
import com.cubegames.engine.exceptions.ValidationBasicException;
import com.cubegames.engine.utils.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractServiceDb<User> {

  @Autowired
  protected UserDao dao;


  @Override
  public Class<User> getDomain() {
    return User.class;
  }

  @Override
  public AbstractDao<User> getDao() {
    return dao;
  }


  public User findByName(String name, String tenantId) {
    Utils.checkNotNullOrEmpty(name, "Username cannot be empty");
    return dao.findByName(name, tenantId);
  }


  public void registerNewUser(RegisterRequest request) {
    if (request == null) {
      throw new ValidationBasicException("Register request cannot be empty");
    }

    Utils.checkNotNullOrEmpty(request.getUserName(), "Username cannot be empty or it is too short", 4);
    Utils.checkNotNullOrEmpty(request.getUserPass(), "Password cannot be empty or it is too short", 6);
    Utils.checkNotNullOrEmpty(request.getEmail(), "E-mail cannot be empty or it is too short", 5);
    Utils.checkNotNullOrEmpty(request.getLanguage(), "Language cannot be empty or it is too short", 2);

    User user = dao.findByName(request.getUserName(), null);
    if (user != null) {
      throw new ValidationBasicException("Username is busy already");
    }

    User newUser = new User();
    newUser.setName(request.getUserName());
    newUser.setPassword(request.getUserPass());
    newUser.setCreatedDate(System.currentTimeMillis());
    newUser.setEmail(request.getEmail());
    newUser.setLanguage(request.getLanguage());
    newUser.setUserRole(UserRole.USER);

    String newId = dao.save(newUser, null);
    if (Utils.stringIsEmpty(newId)) {
      throw new BasicException("Wrong Id of new user");
    }
  }
}
