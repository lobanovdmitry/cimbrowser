package com.ld.gui.events;

import com.ld.kernel.events.interfaces.Event;
import com.ld.kernel.events.interfaces.EventListener;

public class FilterChangedEvent implements Event {

  public interface Listener extends EventListener {
    
    void filterChanged(String newFilter);
    
  }
  
  private String newFilter;
  
  public FilterChangedEvent(String newFilter) {
    this.newFilter = newFilter;
  }
  
  @Override
  public void dispatch(EventListener eventListener) {
    ((Listener)eventListener).filterChanged(newFilter);
  }

}
