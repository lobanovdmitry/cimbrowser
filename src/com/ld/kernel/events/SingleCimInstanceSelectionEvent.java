package com.ld.kernel.events;

import javax.cim.CIMInstance;

import com.ld.kernel.events.interfaces.Event;
import com.ld.kernel.events.interfaces.EventListener;

public class SingleCimInstanceSelectionEvent implements Event {

  public interface Listener extends EventListener {
    
    void instanceSelected(CIMInstance instance);
    
  }
  
  private CIMInstance instance;
  
  public SingleCimInstanceSelectionEvent(CIMInstance instance) {
    this.instance = instance;
  }
  
  @Override
  public void dispatch(EventListener eventListener) {
    ((Listener)eventListener).instanceSelected(instance);
  }
}
