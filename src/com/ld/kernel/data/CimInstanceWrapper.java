package com.ld.kernel.data;

import javax.cim.CIMInstance;
import javax.cim.CIMProperty;

public class CimInstanceWrapper implements Comparable<CimInstanceWrapper>, CimPropertiesContainer {
  
  private CIMInstance instance;
  
  public CimInstanceWrapper(CIMInstance instance) {
    this.instance = instance;
  }

  public CIMInstance getInstance() {
    return instance;
  }
  
  public String toString() {
    
    return "CIM instance of " + instance.getClassName();
    
    //String result = instance.getProperty("Name").toString();
    
    //return result;
  }
  
  @Override
  public int compareTo(CimInstanceWrapper o) {
    return instance.getClassName().compareTo(o.getInstance().getClassName());
  }

  @Override
  public CIMProperty[] getProperties() {
    return instance.getProperties();
  }
}
