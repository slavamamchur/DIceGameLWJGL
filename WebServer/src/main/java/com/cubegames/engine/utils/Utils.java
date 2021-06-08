package com.cubegames.engine.utils;

import com.cubegames.engine.exceptions.ValidationBasicException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

  public static void checkNotNull(Object value, String message) {
    if (value == null) {
      throw new ValidationBasicException(message);
    }
  }


  public static void checkNotNull(Object value) {
    checkNotNull(value, "Value cannot be empty");
  }


  public static void checkMustNull(Object value) {
    if (value != null) {
      throw new ValidationBasicException("Value must be empty");
    }
  }


  public static boolean stringIsEmpty(String str) {
    if (str == null) {
      return true;
    }

    if (str.isEmpty()) {
      return true;
    }

    if (str.trim().isEmpty()) {
      return true;
    }

    return false;
  }



  public static void checkNotNullOrEmpty(String value, String message, int minLength) {
    if (stringIsEmpty(value)) {
      throw new ValidationBasicException(message);
    }

    if (minLength > 0) {
      if (value.length() < minLength) {
        throw new ValidationBasicException(message);
      }
    }
  }


  public static void checkNotNullOrEmpty(String value, String message) {
    checkNotNullOrEmpty(value, message, -1);
  }

  public static void checkNotNullOrEmpty(String value) {
    checkNotNullOrEmpty(value, "Value cannot be empty");
  }




//  public static String longToDate(long value) {
//    Date dt = new Date(value);
//    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
//    return df2.format(dt);
//  }

  public static String longToDateTime(long value) {
    Date dt = new Date(value);
    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    return df2.format(dt);
  }

}
