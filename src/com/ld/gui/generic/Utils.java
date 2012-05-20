package com.ld.gui.generic;


public class Utils {
  
  public static String convertValue(Object value) {
    if ( value == null ){
      return "";
    }
    if (value.getClass().isArray()) {
      StringBuffer buffer = new StringBuffer();
      Object[] arrray = (Object[]) value;
      for (int i = 0; i < arrray.length; i++) {
        if ( i != 0 ) {
          buffer.append(", ");
        }
        buffer.append(arrray[i].toString());
      }
      return buffer.toString();
    }
    return value.toString();
  }

}
