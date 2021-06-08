package com.cubegames.engine.dao;

import com.cubegames.engine.config.MainConfig;
import com.cubegames.engine.consts.CollectionConsts;
import com.cubegames.engine.domain.entities.GameMap;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConnectionManager implements IConnectionManager {

  public static final String DATABASE_NAME = "cubegames";

  private final String PROP_DB_HOST = "db.host";
  private final String PROP_DB_PORT = "db.port";
  private final String PROP_DB_PASS = "db.pass";  //TODO: implement auth connection to DB

  private static final Logger log = Logger.getLogger(ConnectionManager.class.getName());

  private Mongo mongo;
  //private Map<String, MongoTemplate> mongoTemplates = new ConcurrentHashMap<>();
  private MongoTemplate mongoTemplate;


  public ConnectionManager() {
    log.info("Creating DB Connection Manager...");
    String dbHost = getDbHost();
    int dbPort = getDbPort();
    log.info("... connecting to " + dbHost + ":" + dbPort);
    try {
        mongo = new Mongo(getDbHost(), getDbPort()); ///new Mongo(new MongoURI(MongoURI.MONGODB_PREFIX + "admin:admin@localhost/"));
        mongoTemplate = new MongoTemplate(mongo, DATABASE_NAME);
    } catch (UnknownHostException e) {
        log.log(Level.SEVERE, "Cannot find DB Host", e);
    }

    ///createKeys();
  }


  @Override
  public MongoTemplate getConnection(String tenantId) {
    return mongoTemplate;

//    String key = tenantId;
//    if (Strings.isNullOrEmpty(key)) {
//      key = "basic";
//    }
//
//    if (!mongoTemplates.containsKey(key)) {
//      MongoTemplate template = new MongoTemplate(mongo, DATABASE_NAME);
//      mongoTemplates.put(key, template);
//    }
//    return mongoTemplates.get(key);
  }


  private void createKeys() {
    //TODO: set unique values and performance keys!
    log.info("Ensure collections keys...");
    MongoTemplate template = getConnection(null);

    DBCollection collection = template.getCollection(CollectionConsts.COLLECTION_GAME_MAP_NAME);
    BasicDBObject key = new BasicDBObject(GameMap.FIELD_NAME, 1);
    key.put("unique", Boolean.TRUE);
    collection.createIndex(key);
  }


  private String getDbHost() {
    Properties properties = MainConfig.getMainProperties();
    return properties.getProperty(PROP_DB_HOST, "localhost");
  }

  private int getDbPort() {
    Properties properties = MainConfig.getMainProperties();
    String value = properties.getProperty(PROP_DB_PORT, "27017");
    return Integer.parseInt(value);
  }
}
