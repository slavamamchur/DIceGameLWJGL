package com.cubegames.engine.rest;

import com.cubegames.engine.domain.entities.GameMap;
import com.cubegames.engine.exceptions.ValidationBasicException;
import com.cubegames.engine.services.AbstractServiceDb;
import com.cubegames.engine.services.GameMapService;
import com.cubegames.engine.utils.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.function.BiConsumer;

@Controller
@RequestMapping(value = RestConst.URL_GAME_MAP, produces = MediaType.APPLICATION_JSON_VALUE)
public class GameMapController extends AbstractController<GameMap> {

  @Autowired
  private GameMapService service;

  @Override
  protected AbstractServiceDb<GameMap> getService() {
    return service;
  }


  @RequestMapping(method = RequestMethod.POST, value = RestConst.URL_GAME_MAP_IMAGE_SIMPLE)
  @ResponseBody
  public String handleFileUpload2(@RequestParam("file") MultipartFile file,
                                  @RequestParam("token") String token,
                                  @RequestParam("mapid") String mapId) {
    return handleFile(file, token, mapId, new BiConsumer<GameMap, byte[]>() {
      @Override
      public void accept(GameMap gameMap, byte[] bytes) {
        gameMap.setBinaryData(bytes);
      }
    });
  }


  @RequestMapping(method = RequestMethod.POST, value = RestConst.URL_GAME_MAP_IMAGE_RELIEF)
  @ResponseBody
  public String handleFileRelief2(@RequestParam("file") MultipartFile file,
                                  @RequestParam("token") String token,
                                  @RequestParam("mapid") String mapId) {
    return handleFile(file, token, mapId, new BiConsumer<GameMap, byte[]>() {
      @Override
      public void accept(GameMap gameMap, byte[] bytes) {
        gameMap.setBinaryDataRelief(bytes);
      }
    });
  }


  private String handleFile(MultipartFile file, String token, String mapId, BiConsumer<GameMap, byte[]> consumer) {
    String tenantId;
    if (Utils.stringIsEmpty(token)) {
      tenantId = getTenantId();
    } else {
      tenantId = getTenantId(token);
    }

    if (!file.isEmpty()) {
      try {
        GameMap map = service.get(mapId, tenantId);
        if (map == null) {
          throw new ValidationBasicException("Cannot find Map with id: " + mapId);
        }

        //file.getSize()  //TODO: set file size limit for free users

        //map.setBinaryData(file.getBytes());
        consumer.accept(map, file.getBytes());
        service.validateOnUpdate(map);
        service.saveExisting(map, tenantId);

        //BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(name + "-uploaded")));
        //stream.write(bytes);
        //stream.close();

        //return "You successfully uploaded " + name + " into " + name + "-uploaded !";
        return "You successfully uploaded image for map " + mapId;
      } catch (Exception e) {
        return "You failed to upload " + mapId + " => " + e.getMessage();
      }
    } else {
      return "You failed to upload " + mapId + " because the file was empty.";
    }
  }


  @RequestMapping(method = RequestMethod.PUT, value = RestConst.URL_GAME_MAP_IMAGE_JSON)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public void handleFileImageJson(@RequestBody String json) {
    updateMapData(json, true);
  }


  @RequestMapping(method = RequestMethod.PUT, value = RestConst.URL_GAME_MAP_RELIEF_JSON)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public void handleFileReliefJson(@RequestBody String json) {
    updateMapData(json, false);
  }


  private void updateMapData(String json, boolean isImage) {
    Utils.checkNotNullOrEmpty(json, "JSON cannot be empty");
    GameMap newMap = createObjFromString(json);
    final String mapId = newMap.getId();
    Utils.checkNotNullOrEmpty(mapId, "Map Id cannot be empty");
    final String tenantId = getTenantId();
    GameMap map = service.get(mapId, tenantId);
    if (map == null) {
      throw new ValidationBasicException("Cannot find Map with id: " + mapId);
    }

    if (isImage) {
      map.setBinaryData(newMap.getBinaryData());
    } else {
      map.setBinaryDataRelief(newMap.getBinaryDataRelief());
    }

    service.validateOnUpdate(map);
    service.saveExisting(map, tenantId);
  }


  @RequestMapping(method = RequestMethod.GET, value = RestConst.URL_GAME_MAP_IMAGE, produces = MediaType.IMAGE_JPEG_VALUE)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public byte[] getImage(@PathVariable String mapId) {
    GameMap map = service.get(mapId, getTenantId());
    Utils.checkNotNull(map);
    return map.getBinaryData();
  }


  @RequestMapping(method = RequestMethod.GET, value = RestConst.URL_GAME_MAP_IMAGE_SMALL, produces = MediaType.IMAGE_JPEG_VALUE)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public byte[] getImageSmall(@PathVariable String mapId) {
    GameMap map = service.get(mapId, getTenantId());
    Utils.checkNotNull(map);
    return map.getBinaryData();
  }


  @RequestMapping(method = RequestMethod.GET, value = RestConst.URL_GAME_MAP_RELIEF, produces = MediaType.IMAGE_JPEG_VALUE)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public byte[] getImageRelief(@PathVariable String mapId) {
    GameMap map = service.get(mapId, getTenantId());
    Utils.checkNotNull(map);
    return map.getBinaryDataRelief();
  }


  @Override
  public List<GameMap> findAllInternal(String tenantId) {
    List<GameMap> list = super.findAllInternal(tenantId);
    for (GameMap map : list) {
      map.setBinaryData(null);
    }
    return list;
  }


  @Override
  public GameMap findOneInternal(final String id, String tenantId) {
    GameMap map = super.findOneInternal(id, tenantId);
    if (map != null) {
      map.setBinaryData(null);
    }
    return map;
  }


  @Override
  protected void updateInternal(GameMap resource, String tenantId) {
    Utils.checkNotNull(resource);
    service.validateOnUpdate(resource);
    service.updateName(resource, tenantId);
  }

}
