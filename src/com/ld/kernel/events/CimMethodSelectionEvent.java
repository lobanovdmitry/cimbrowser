package com.ld.kernel.events;

import javax.cim.CIMMethod;

import com.ld.kernel.events.interfaces.Event;
import com.ld.kernel.events.interfaces.EventListener;

public class CimMethodSelectionEvent implements Event {
  
  private CIMMethod method;
  
  public CimMethodSelectionEvent(CIMMethod method) {
    this.method = method;
  }
  
  public interface Listener extends EventListener {
    void methodSelected(CIMMethod method);
  }
  
  @Override
  public void dispatch(EventListener eventListener) {
    ((Listener)eventListener).methodSelected(method);
  }
}
