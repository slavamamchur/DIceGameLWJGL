package com.cubegames.engine.dao;

import com.cubegames.engine.domain.entities.BasicDbEntity;
import com.cubegames.engine.domain.entities.BasicNamedDbEntity;
import com.cubegames.engine.domain.entities.GameMap;
import com.mongodb.WriteResult;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository(value = "gameMapDao")
public class GameMapDao extends AbstractDao<GameMap> {

  @Override
  public Class<GameMap> getDomain() {
    return GameMap.class;
  }


  @Override
  protected void beforeSave(GameMap element) {
    super.beforeSave(element);
    element.setLastUsedDate(System.currentTimeMillis());
  }


  public int updateName(String id, String newName, String tenantId) {
    Query query = createTenantOnlyQuery(tenantId);
    query.addCriteria(Criteria.where(BasicDbEntity.FIELD_ID).is(id));

    Update update = Update
        .update(BasicNamedDbEntity.FIELD_NAME, newName)
        .set(GameMap.FIELD_LAST_USED_DATE, System.currentTimeMillis());

    WriteResult res = getTemplate(tenantId).updateFirst(query, update, getDomain());
    return res.getN();
  }
}
