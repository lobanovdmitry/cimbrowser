package com.ld.kernel.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.cim.CIMClass;
import javax.cim.CIMClassProperty;
import javax.cim.CIMMethod;
import javax.cim.CIMProperty;
import javax.cim.CIMQualifier;

public class CimClassWrapper implements Comparable<CimClassWrapper>, CimPropertiesContainer {
    
    public static CimClassWrapper ROOT = new CimClassWrapper(
        new CIMClass("Classes", null, new CIMQualifier[0], new CIMClassProperty[0], new CIMMethod[0])) {
      
          public String getName() {
            return "Classes";
          };
    };
    
    private CIMClass cimClass;
    private List<CimClassWrapper> cimChildren = Collections.emptyList();
    
    public CimClassWrapper(CIMClass cimClass) {
      this.cimClass = cimClass;
    }
    
    public String getName() {
      return cimClass.getName();
    }
    
    public String getSuperName() {
      return cimClass.getSuperClassName();
    }
    
    public CIMClass getCIMClass() {
      return cimClass;
    }
    
    public void addChild(CimClassWrapper cimClassNode) {
      if ( cimChildren ==  Collections.EMPTY_LIST ) {
        cimChildren = new ArrayList<CimClassWrapper>();
      }
      cimChildren.add(cimClassNode);
    }
    
    public List<CimClassWrapper> getCimChildren() {
      return cimChildren;
    }
    
    public boolean isLeaf() {
      return cimChildren.isEmpty();
    }

    @Override
    public int compareTo(CimClassWrapper o) {
      return getName().compareTo(o.getName());
    }
    
    @Override
    public String toString() {
      return getName();
    }
    
    public boolean isKeyed() {
      return cimClass.isKeyed();
    }
    
    public boolean isAssociation() {
      return cimClass.isAssociation();
    }

    @Override
    public CIMProperty[] getProperties() {
      return cimClass.getProperties();
    }
  }