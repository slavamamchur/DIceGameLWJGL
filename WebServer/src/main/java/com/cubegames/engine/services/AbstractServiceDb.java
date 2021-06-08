package com.cubegames.engine.services;

import com.cubegames.engine.dao.AbstractDao;
import com.cubegames.engine.domain.entities.BasicDbEntity;
import com.cubegames.engine.domain.entities.BasicNamedDbEntity;
import com.cubegames.engine.domain.rest.PaginationInfo;
import com.cubegames.engine.domain.rest.SearchRequest;
import com.cubegames.engine.exceptions.ValidationBasicException;
import com.cubegames.engine.utils.Utils;

import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractServiceDb<T extends BasicDbEntity> {

  protected static final Logger log = Logger.getLogger(AbstractServiceDb.class.getName());

  public abstract Class<T> getDomain();

  public abstract AbstractDao<T> getDao();

  public AbstractServiceDb() {
    log.info("Creating a service for " + getDomain().getSimpleName());
  }

  public T get(String id, String tenantId) {
    Utils.checkNotNullOrEmpty(id);
    return getDao().get(id, tenantId);
  }

//  public List<T> list(String tenantId) {
//    return list(null, null, tenantId);
//  }


  public List<T> list(PaginationInfo pagination, SearchRequest search, String tenantId) {
    return getDao().list(pagination, search, tenantId);
  }

  //public List<T> listByIds(List<String> ids, String tenantId) {  }


  public T saveNewAndReturn(T element, String tenantId) {
    Utils.checkNotNull(element);
    Utils.checkMustNull(element.getId());
    return getDao().saveAndReturn(element, tenantId);
  }


  public String saveNew(T element, String tenantId) {
    return saveNewAndReturn(element, tenantId).getId();
  }


  public String saveExisting(T element, String tenantId) {
    Utils.checkNotNull(element);
    Utils.checkNotNullOrEmpty(element.getId());
    if (!getDao().exists(element.getId(), tenantId)) {
      throw new ValidationBasicException("You cannot edit entity of other user!");
    }
    return getDao().save(element, tenantId);
  }


  public int delete(String id, String tenantId) {
    Utils.checkNotNullOrEmpty(id);
    return getDao().delete(id, tenantId);
  }


  public int deleteAll(String tenantId) {
    return getDao().deleteAll(tenantId);
  }


  public long count(String tenantId) {
    return getDao().count(tenantId);
  }


  /**
   * Override if needed special validation
   *
   * @param element
   * @throws ValidationBasicException
   */
  public void validateOnCreate(T element) throws ValidationBasicException {
    Utils.checkNotNull(element);
    validateName(element);
    element.setDeleted(false);
  }


  /**
   * Override if needed special validation on Update
   *
   * @param element
   * @throws ValidationBasicException
   */
  public void validateOnUpdate(T element) throws ValidationBasicException {
    Utils.checkNotNull(element);
    validateName(element);
  }

  private void validateName(T element) {
    if (element instanceof BasicNamedDbEntity) {
      BasicNamedDbEntity named = (BasicNamedDbEntity) element;
      Utils.checkNotNullOrEmpty(named.getName(), "Name cannot be empty");
    }
  }

}
