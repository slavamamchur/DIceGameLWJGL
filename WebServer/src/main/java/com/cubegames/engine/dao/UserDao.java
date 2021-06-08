package com.cubegames.engine.dao;

import com.cubegames.engine.auth.Cypher;
import com.cubegames.engine.domain.auth.User;
import com.cubegames.engine.utils.Utils;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository(value = "userDao")
public class UserDao extends AbstractDao<User> {

  private static final String PASS_PREFIX = "=";

  @Override
  public Class<User> getDomain() {
    return User.class;
  }


  public User findByName(String name, String tenantId) {
    Query query = createTenantQuery(tenantId);
    query.addCriteria(new Criteria(User.FIELD_NAME).is(name));
    User res = getTemplate(tenantId).findOne(query, getDomain());
    afterRead(res);
    return res;
  }


  @Override
  protected void beforeSave(User element) {
    super.beforeSave(element);
    String pass = element.getPassword();
    if (Utils.stringIsEmpty(pass)) {
      return;
    }

    if (pass.startsWith(PASS_PREFIX)) {
      return;
    }

    String encodedPass = PASS_PREFIX + Cypher.encode(pass);
    element.setPassword(encodedPass);
  }


  @Override
  protected void afterRead(User element) {
    super.afterRead(element);

    if (element == null) {
      return;
    }

    String pass = element.getPassword();
    if (Utils.stringIsEmpty(pass)) {
      return;
    }

    if (pass.startsWith(PASS_PREFIX)) {
      String purePass = pass.replaceFirst(PASS_PREFIX, "");
      String decodedPass = Cypher.decode(purePass);
      element.setPassword(decodedPass);
    }
  }
}
