package com.cubegames.engine.rest;

import com.cubegames.engine.auth.AuthService;
import com.cubegames.engine.consts.EntitySize;
import com.cubegames.engine.consts.RestCommonConsts;
import com.cubegames.engine.domain.entities.BasicDbEntity;
import com.cubegames.engine.domain.rest.PaginationInfo;
import com.cubegames.engine.domain.rest.SearchRequest;
import com.cubegames.engine.domain.rest.responses.CollectionResponse;
import com.cubegames.engine.domain.rest.responses.CountResponse;
import com.cubegames.engine.domain.rest.responses.IdResponse;
import com.cubegames.engine.exceptions.BasicException;
import com.cubegames.engine.exceptions.ValidationBasicException;
import com.cubegames.engine.services.AbstractServiceDb;
import com.cubegames.engine.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractController<T extends BasicDbEntity> {

  //protected final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  protected AuthService authService;

  @Autowired
  private HttpServletRequest request;

  protected String getTenantId() {
    return authService.getTenantId(request);
  }

  protected String getTenantId(String token) {
    return authService.getTenantId(token);
  }


  // find - one
  protected T findOneInternal(final String id, String tenantId) {
    if (Utils.stringIsEmpty(id)) {
      throw new BasicException("Id cannot be empty");
    }

    return getService().get(id, tenantId);
  }


  // find - all
  protected List<T> findAllInternal(String tenantId) {
    return getService().list(getPaginationInfo(), getSearchRequestInfo(), tenantId);
  }


  // save/create/persist
  protected T createInternalAndReturn(T resource, String tenantId) {
    Utils.checkNotNull(resource);
    getService().validateOnCreate(resource);
    return getService().saveNewAndReturn(resource, tenantId);
  }


  // save/create/persist
  protected String createInternal(T resource, String tenantId) {
    return createInternalAndReturn(resource, tenantId).getId();
  }


  // update
  protected void updateInternal(T resource, String tenantId) {
    Utils.checkNotNull(resource);
    getService().validateOnUpdate(resource);
    getService().saveExisting(resource, tenantId);
  }

  // delete/remove
  protected void deleteByIdInternal(String id, String tenantId) {
    if (Utils.stringIsEmpty(id)) {
      throw new BasicException("Id cannot be empty");
    }
    getService().delete(id, tenantId);
  }


  // count
  protected long countInternal(String tenantId) {
    return getService().count(tenantId);
  }


  // generic REST operations

  @RequestMapping(method = RequestMethod.GET, value = RestConst.URL_COUNT)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public CountResponse count() {
    checkRestUsageAllowed(RestUsage.COUNT);
    String tenantId = getTenantId();
    CountResponse res = new CountResponse();
    res.setCount(countInternal(tenantId));
    return res;
  }


  @RequestMapping(method = RequestMethod.POST, value = RestConst.URL_CREATE)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public IdResponse create(@RequestBody String resource) {
    T object = createFromString(resource, RestUsage.NEW);
    return new IdResponse(object.getId());
  }


  @RequestMapping(method = RequestMethod.POST, value = RestConst.URL_NEW)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public T createAndReturn(@RequestBody String resource) {
    return createFromString(resource, RestUsage.NEW);
  }


  protected T createObjFromString(String json) {
    Class clazz = this.getService().getDomain();
    ObjectMapper mapper = new ObjectMapper();
    try {
      Object obj = mapper.readValue(json, clazz);
      return (T) obj;
    } catch (IOException e) {
      e.printStackTrace();
      throw new BasicException("Cannot parse JSON: " + e.getMessage());
    }
  }

  private T createFromString(String json, RestUsage operation) {
    checkRestUsageAllowed(operation);
    T res = createObjFromString(json);
    return createInternalAndReturn(res, getTenantId());
  }


  @RequestMapping(method = RequestMethod.GET, value = RestConst.URL_FIND)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public T findOne(@PathVariable String itemId) {
    checkRestUsageAllowed(RestUsage.FIND);
    return findOneInternal(itemId, getTenantId());
  }


  @RequestMapping(method = RequestMethod.DELETE, value = RestConst.URL_DELETE)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public void delete(@PathVariable String itemId) {
    checkRestUsageAllowed(RestUsage.DELETE);
    deleteByIdInternal(itemId, getTenantId());
  }


//  @RequestMapping(method = RequestMethod.PUT, value = RestConst.URL_UPDATE)
//  @ResponseStatus(value = HttpStatus.OK)
//  @ResponseBody
//  public void update(@RequestBody T resource) {
//    checkRestUsageAllowed(RestUsage.UPDATE);
//    updateInternal(resource, getTenantId());
//  }


  @RequestMapping(method = RequestMethod.PUT, value = RestConst.URL_UPDATE)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public void update(@RequestBody String resource) {
    checkRestUsageAllowed(RestUsage.UPDATE);
    T obj = createObjFromString(resource);
    updateInternal(obj, getTenantId());
  }


  @RequestMapping(method = RequestMethod.GET, value = RestConst.URL_LIST)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public CollectionResponse<T> findAll() {
    checkRestUsageAllowed(RestUsage.LIST);
    List<T> list = findAllInternal(getTenantId());
    CollectionResponse<T> resp = new CollectionResponse<T>();
    resp.setCollection(list);
    return resp;
  }


  protected abstract AbstractServiceDb<T> getService();


  protected Set<RestUsage> getRestUsageOperations() {
    return new HashSet<RestUsage>(Arrays.asList(RestUsage.values()));
  }


  private void checkRestUsageAllowed(RestUsage operation) {
    Set<RestUsage> allowed = getRestUsageOperations();
    if (allowed == null) {
      throw new BasicException("Cannot check for allowed operations!");
    }

    if (!allowed.contains(operation)) {
      throw new BasicException("Operation " + operation + " cannot not be used for this request!");
    }
  }


  private PaginationInfo getPaginationInfo() {
    String headerSortBy = request.getHeader(RestCommonConsts.PAGE_SORT_BY_HEADER);
    String headerSort = request.getHeader(RestCommonConsts.PAGE_SORT_HEADER);
    String headerOffset = request.getHeader(RestCommonConsts.PAGE_OFFSET_HEADER);
    String headerLimit = request.getHeader(RestCommonConsts.PAGE_LIMIT_HEADER);

    boolean hasData = false;
    PaginationInfo info = new PaginationInfo();

    if (!Utils.stringIsEmpty(headerSortBy)) {
      info.setSortBy(headerSortBy);
      if (!Utils.stringIsEmpty(headerSort)) {
        if ("asc".equals(headerSort.toLowerCase())) {
          info.setSortOrder(Sort.Direction.ASC);
        } else {
          info.setSortOrder(Sort.Direction.DESC);
        }
      }
      hasData = true;
    }

    if (!Utils.stringIsEmpty(headerOffset)) {
      try {
        int offset = Integer.parseInt(headerOffset);
        if (offset >= 0) {
          hasData = true;
          info.setOffset(offset);
        }
      } catch (NumberFormatException e) {
        throw new ValidationBasicException("Wrong value for header " + RestCommonConsts.PAGE_OFFSET_HEADER + " : " + headerOffset);
      }
    }


    if (!Utils.stringIsEmpty(headerLimit)) {
      try {
        int limit = Integer.parseInt(headerLimit);
        if (limit >= 0) {
          hasData = true;
          info.setLimit(limit);
        }
      } catch (NumberFormatException e) {
        throw new ValidationBasicException("Wrong value for header " + RestCommonConsts.PAGE_LIMIT_HEADER + " : " + headerLimit);
      }
    }

    if (hasData) {
      return info;
    }

    return null;
  }


  private SearchRequest getSearchRequestInfo() {
    String headerFilterByName = request.getHeader(RestCommonConsts.FILTER_BY_NAME);
    String headerFilterBySize = request.getHeader(RestCommonConsts.FILTER_BY_SIZE);
    String headerFilterByPlayers = request.getHeader(RestCommonConsts.FILTER_BY_PLAYERS);

    boolean hasData = false;
    SearchRequest info = new SearchRequest();

    if (!Utils.stringIsEmpty(headerFilterByName)) {
      info.setName(headerFilterByName);
      hasData = true;
    }

    if (!Utils.stringIsEmpty(headerFilterBySize)) {
      try {
        EntitySize size = EntitySize.valueOf(headerFilterBySize);
        info.setSize(size);
        hasData = true;
      } catch (Exception e) {
        throw new ValidationBasicException("Wrong value for header '" + RestCommonConsts.FILTER_BY_SIZE + " : " + headerFilterBySize +
            "', error: " + e.getMessage());
      }
    }

    if (!Utils.stringIsEmpty(headerFilterByPlayers)) {
      try {
        int limit = Integer.parseInt(headerFilterByPlayers);
        if (limit >= 0) {
          hasData = true;
          info.setPlayers(limit);
        }
      } catch (NumberFormatException e) {
        throw new ValidationBasicException("Wrong value for header " + RestCommonConsts.FILTER_BY_PLAYERS + " : " + headerFilterByPlayers);
      }
    }

    if (hasData) {
      return info;
    }

    return null;
  }


  protected enum RestUsage {
    COUNT, CREATE, NEW, FIND, DELETE, UPDATE, LIST
  }

}