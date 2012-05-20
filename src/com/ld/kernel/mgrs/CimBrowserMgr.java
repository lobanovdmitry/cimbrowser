package com.ld.kernel.mgrs;

import java.util.HashMap;
import java.util.Map;


public class CimBrowserMgr {

  @SuppressWarnings("rawtypes")
  private Map<Class, Manager> mgrs = new HashMap<Class, Manager>();
  
  public CimBrowserMgr() {
    mgrs.put(EventMgr.class, new EventMgr());
    mgrs.put(GuiMgr.class, new GuiMgr());
    mgrs.put(CimConnectionMgr.class, new CimConnectionMgr(this));
  }
  
  @SuppressWarnings("rawtypes")
  public Manager getMgr(Class manager) {
    return mgrs.get(manager);
  }
}
