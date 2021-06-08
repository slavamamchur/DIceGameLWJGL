package com.cubegames.engine.dao;

import com.cubegames.engine.consts.EntitySize;
import com.cubegames.engine.domain.entities.BasicDbEntity;
import com.cubegames.engine.domain.entities.BasicNamedDbEntity;
import com.cubegames.engine.domain.entities.BasicSizedNamedDbEntity;
import com.cubegames.engine.domain.rest.PaginationInfo;
import com.cubegames.engine.domain.rest.SearchRequest;
import com.cubegames.engine.exceptions.BasicException;
import com.cubegames.engine.utils.Utils;
import com.mongodb.WriteResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.logging.Logger;


public abstract class AbstractDao<T extends BasicDbEntity> {

  private static final Logger log = Logger.getLogger(AbstractDao.class.getName());

  @Autowired
  private IConnectionManager connectionManager;

  public AbstractDao() {
    log.info("Creating a DAO for " + getDomain().getSimpleName());
  }


  public void setConnectionManager(IConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }


  public abstract Class<T> getDomain();

  public boolean allowSharedEntities() {
    return true;
  }


  public T get(String id, String tenantId) {
    Query query = createTenantQuery(tenantId);
    Criteria crId = Criteria.where(BasicDbEntity.FIELD_ID).is(id);
    query.addCriteria(crId);
    T res = getTemplate(tenantId).findOne(query, getDomain());
    afterRead(res);
    return res;
  }


  public boolean exists(String id, String tenantId) {
    Query query = createTenantOnlyQuery(tenantId);
    Criteria crId = Criteria.where(BasicDbEntity.FIELD_ID).is(id);
    query.addCriteria(crId);
    return getTemplate(tenantId).exists(query, getDomain());
  }


//  public List<T> list(String tenantId) {
//    return list(null, null, tenantId);
//  }


  public List<T> list(PaginationInfo pagination, SearchRequest search, String tenantId) {
    Query query;

    if (allowSharedEntities()) {
      query = createTenantQuery(tenantId);
    } else {
      query = createTenantOnlyQuery(tenantId);
    }

    query = updateQueryWithSearch(query, search);

    query = query.with(pagination);  // WITH() supports NULL value

    List<T> list = getTemplate(tenantId).find(query, getDomain());
    if (list != null) {
      for (T item : list) {
        afterRead(item);
      }
    }
    return list;
  }

  //public abstract List<T> listByIds(List<String> ids, String tenantId);

  public T saveAndReturn(T element, String tenantId) {
    beforeSave(element);
    element.setTenantId(tenantId);
    getTemplate(tenantId).save(element);
    return element;
  }


  public String save(T element, String tenantId) {
    return saveAndReturn(element, tenantId).getId();
  }

  public int delete(String id, String tenantId) {
    Query query = createTenantOnlyQuery(tenantId);
    Criteria crId = Criteria.where(BasicDbEntity.FIELD_ID).is(id);
    query.addCriteria(crId);
    WriteResult res = getTemplate(tenantId).remove(query, getDomain());
    return res.getN();
  }

  public int deleteAll(String tenantId) {
    WriteResult res = getTemplate(tenantId).remove(createTenantOnlyQuery(tenantId), getDomain());
    return res.getN();
  }

  public long count(String tenantId) {
    return getTemplate(tenantId).count(createTenantQuery(tenantId), getDomain());
  }


  protected MongoTemplate getTemplate(String tenantId) {
    return connectionManager.getConnection(tenantId);
  }


  protected Query createTenantQuery(String tenantId) {
    Query query = new Query();
    Criteria c = Criteria.where(BasicDbEntity.FIELD_TENANT_ID);
    if (Utils.stringIsEmpty(tenantId)) {
      c = c.exists(false);
    } else {
      c = c.in(tenantId, null);
    }
    query.addCriteria(c);
    return query;
  }


  protected Query createTenantOnlyQuery(String tenantId) {
    Query query = new Query();
    Criteria c = Criteria.where(BasicDbEntity.FIELD_TENANT_ID).is(tenantId);
    query.addCriteria(c);
    return query;
  }


  protected void beforeSave(T element) {
    // override this method if needed
    if (element instanceof BasicSizedNamedDbEntity) {
      BasicSizedNamedDbEntity sized = (BasicSizedNamedDbEntity) element;
      sized.setEntitySize(sized.calculateEntitySize());
    }
  }

  protected void afterRead(T element) {
    // override this method if needed
  }

  private Query updateQueryWithSearch(Query query, SearchRequest search) {
    if (search == null) {
      return query;
    }

    Query res = query;

    if (!Utils.stringIsEmpty(search.getName())) {
      Criteria cr = Criteria.where(BasicNamedDbEntity.FIELD_NAME).regex(search.getName());
      res = res.addCriteria(cr);
    }

    if (search.getSize() != null) {
      int min = 0;
      int max = Integer.MAX_VALUE;
      EntitySize size = search.getSize();
      switch (size) {
        case SMALL:
          max = 20;
          break;
        case MEDIUM:
          min = 21;
          max = 50;
          break;
        case LARGE:
          min = 51;
          break;
        default:
          throw new BasicException("Unsupported Entity Size: " + size);
      }

      Criteria cr = Criteria.where(BasicSizedNamedDbEntity.FIELD_ENTITY_SIZE).exists(true).andOperator(
          Criteria.where(BasicSizedNamedDbEntity.FIELD_ENTITY_SIZE).gte(min),
          Criteria.where(BasicSizedNamedDbEntity.FIELD_ENTITY_SIZE).lte(max)
      );
      res = res.addCriteria(cr);
    }

    return res;
  }

}
