package com.sadgames.gl3dengine.gamelogic.server.rest_api;


import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;

import java.util.Collection;
import java.util.Map;

public interface EntityControllerInterface {

    BasicEntity iGetEntity(String id);
    void iSendPOSTRequest(String action, Object entity);
    BasicEntity iGetResponse(String action, Object ... args);
    BasicEntity iUpdateEntity(BasicNamedDbEntity entity);
    void iDeleteEntity(BasicNamedDbEntity entity);
    Collection iGetEntityList();
    byte[] iGetBinaryData(BasicNamedDbEntity entity, String dataUrl, String mediaType);
    String iUploadFile(BasicNamedDbEntity entity, String keyParamName, String uploadActionNAme, String fileName);
    void iAddChild(String id, String childName, Object child);
    void iRemoveChild(String id, String childName, int index);
    BasicEntity iGetResponseWithParams(String action, int method, Object entity, Class<?> responseType, Object ... args);
    BasicEntity iGetResponseWithGetParams(String action, Object entity, Class<?> responseType, Object ... args);
    BasicEntity iGetResponseWithPostParams(String action, Object entity, Class<?> responseType, Object ... args);
    void iThrowWebServiceException(int HTTPStatus, String errorMessage);
    void iSetParams(Map<String, String> params);
}
