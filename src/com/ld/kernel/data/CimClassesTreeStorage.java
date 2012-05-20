package com.ld.kernel.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CimClassesTreeStorage {
  public static final CimClassesTreeStorage EMPTY = new CimClassesTreeStorage(new ArrayList<CimClassWrapper>(0)); 
  
  private List<CimClassWrapper> all;
  private List<CimClassWrapper> firstLevelClasses = new ArrayList<CimClassWrapper>();
  private Map<String, CimClassWrapper> allCimClasses = new HashMap<String, CimClassWrapper>();
  
  public CimClassesTreeStorage(List<CimClassWrapper> all) {
    this.all = all;
  }
  
  public CimClassWrapper getParent(CimClassWrapper child) {
    return allCimClasses.get(child.getSuperName());
  }
  
  public void process() {
    List<CimClassWrapper> classesToDelete = new ArrayList<CimClassWrapper>();
    for ( CimClassWrapper cimClass : all ) {
      if ( cimClass.getCIMClass().getSuperClassName() == null ) {
        firstLevelClasses.add(cimClass);
        allCimClasses.put(cimClass.getName(), cimClass);
        classesToDelete.add(cimClass);
      }
    }
    all.removeAll(classesToDelete);
    classesToDelete.clear();
    while(!all.isEmpty()) {
      for ( CimClassWrapper cimClass : all ) {
        CimClassWrapper node = allCimClasses.get(cimClass.getCIMClass().getSuperClassName());
        if ( node != null) {
          node.addChild(cimClass);
          allCimClasses.put(cimClass.getName(), cimClass);
          classesToDelete.add(cimClass);
        }
      }
      all.removeAll(classesToDelete);
      classesToDelete.clear();
    }
  }

  public List<CimClassWrapper> getUnderRootNodes() {
    return firstLevelClasses;
  }

  public CimClassWrapper findClass(String textToSearch) {
    return allCimClasses.get(textToSearch);
  }
}
