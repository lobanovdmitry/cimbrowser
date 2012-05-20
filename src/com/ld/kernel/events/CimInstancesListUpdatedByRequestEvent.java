package com.ld.kernel.events;

import java.util.Collections;
import java.util.List;

import com.ld.kernel.CimSessionHolder.RequestResult;
import com.ld.kernel.data.CimInstanceWrapper;
import com.ld.kernel.events.interfaces.Event;
import com.ld.kernel.events.interfaces.EventListener;

public class CimInstancesListUpdatedByRequestEvent implements Event {

  private RequestResult result;
  private List<CimInstanceWrapper> instances = Collections.emptyList();
  
  public CimInstancesListUpdatedByRequestEvent(RequestResult result) {
    this.result = result;
  }
  
  public CimInstancesListUpdatedByRequestEvent(RequestResult result, List<CimInstanceWrapper> instances) {
    this(result);
    this.instances = instances;
  }
  
  public interface Listener extends EventListener {
    
    void instancesUpdated(RequestResult result, List<CimInstanceWrapper> instances);
    
  }
  
  @Override
  public void dispatch(EventListener eventListener) {
    ((Listener)eventListener).instancesUpdated(result, instances);
  }
}
