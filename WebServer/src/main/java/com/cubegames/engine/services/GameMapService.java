package com.cubegames.engine.services;

import com.cubegames.engine.dao.AbstractDao;
import com.cubegames.engine.dao.GameMapDao;
import com.cubegames.engine.domain.entities.GameMap;
import com.cubegames.engine.exceptions.ValidationBasicException;
import com.cubegames.engine.utils.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;

@Service
public class GameMapService extends AbstractServiceDb<GameMap> {

  @Autowired
  GameMapDao dao;

  @Override
  public Class<GameMap> getDomain() {
    return GameMap.class;
  }

  @Override
  public AbstractDao<GameMap> getDao() {
    return dao;
  }

  @Override
  public void validateOnCreate(GameMap element) throws ValidationBasicException {
    super.validateOnCreate(element);

//    validateRelief(element);

    long timestamp = System.currentTimeMillis();
    element.setCreatedDate(timestamp);
    element.setLastUsedDate(timestamp);
  }


  @Override
  public void validateOnUpdate(GameMap element) throws ValidationBasicException {
    super.validateOnUpdate(element);

//    validateRelief(element);

    element.setLastUsedDate(System.currentTimeMillis());
  }


  public int updateName(GameMap element, String tenantId) {
    return dao.updateName(element.getId(), element.getName(), tenantId);
  }


  public GameMap getSmall(String id, String tenantId) {
    Utils.checkNotNullOrEmpty(id);
    return getDao().get(id, tenantId);
  }


//  private void validateRelief(GameMap element) {
//    byte[] relief = element.getBinaryDataRelief();
//    if (relief != null) {
//      if (!isPicture256(relief)) {
//        throw new ValidationBasicException("Relief image must be sized to 256x256 pixels");
//      }
//    }
//  }


//  private boolean isPicture256(byte[] bytes) {
//    try {
//      BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
//      return (img != null) && (img.getHeight() == 256) && (img.getWidth() == 256);
//    } catch (IOException e) {
//      log.severe("Cannot convert relief image: " + e.getMessage());
//      e.printStackTrace();
//      return false;
//    }
//  }

}
