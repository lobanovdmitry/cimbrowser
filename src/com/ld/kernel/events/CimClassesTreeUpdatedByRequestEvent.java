package com.ld.kernel.events;

import java.util.Collections;
import java.util.List;

import com.ld.kernel.CimSessionHolder.RequestResult;
import com.ld.kernel.data.CimClassWrapper;
import com.ld.kernel.events.interfaces.Event;
import com.ld.kernel.events.interfaces.EventListener;

public class CimClassesTreeUpdatedByRequestEvent implements Event {

  private RequestResult result;
  private List<CimClassWrapper> classes = Collections.emptyList();
  
  public CimClassesTreeUpdatedByRequestEvent(RequestResult result) {
    this.result = result;
  }
  
  public CimClassesTreeUpdatedByRequestEvent(RequestResult result, List<CimClassWrapper> classes) {
    this(result);
    this.classes = classes;
  }
  
  public interface Listener extends EventListener {
    void classesChanged(RequestResult result, List<CimClassWrapper> classes);
  }
  
  @Override
  public void dispatch(EventListener eventListener) {
    ((Listener)eventListener).classesChanged(result, classes);
  }
}
