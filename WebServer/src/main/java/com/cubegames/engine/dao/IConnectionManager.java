package com.cubegames.engine.dao;

import org.springframework.data.mongodb.core.MongoTemplate;

public interface IConnectionManager {

  MongoTemplate getConnection(String tenantId);

}
