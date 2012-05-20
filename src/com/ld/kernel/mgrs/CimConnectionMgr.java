package com.ld.kernel.mgrs;

import java.util.List;

import com.ld.kernel.CimSessionHolder;
import com.ld.kernel.CimSessionHolder.RequestResult;
import com.ld.kernel.data.CimClassWrapper;
import com.ld.kernel.data.CimInstanceWrapper;
import com.ld.kernel.events.CimClassesTreeUpdatedByRequestEvent;
import com.ld.kernel.events.CimInstancesListUpdatedByRequestEvent;

public class CimConnectionMgr implements Manager {

  private CimBrowserMgr mainMgr;
  private CimSessionHolder sessionHolder;
  
  public static CimConnectionMgr getMgr(CimBrowserMgr mgr) {
    return (CimConnectionMgr) mgr.getMgr(CimConnectionMgr.class);
  }
  
  public CimConnectionMgr(CimBrowserMgr mainMgr) {
    this.mainMgr = mainMgr;
    sessionHolder = new CimSessionHolder(this);
  }
  
  public CimSessionHolder getSessionHolder() {
    return sessionHolder;
  }
  
  public void dispatchRequestedClasses(RequestResult requestResult, List<CimClassWrapper> classes) {
    EventMgr.getMgr(mainMgr).dispatch(new CimClassesTreeUpdatedByRequestEvent(requestResult, classes));
  }

  public void dispatchRequestedInstances(RequestResult requestResult, List<CimInstanceWrapper> instances) {
    EventMgr.getMgr(mainMgr).dispatch(new CimInstancesListUpdatedByRequestEvent(requestResult, instances));
  }
}
