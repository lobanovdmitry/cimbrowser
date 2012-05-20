package com.ld.kernel.events;

import com.ld.kernel.data.CimClassWrapper;
import com.ld.kernel.events.interfaces.Event;
import com.ld.kernel.events.interfaces.EventListener;

public class SingleCimClassSelectionEvent implements Event {
  
  private CimClassWrapper selectedNode;
  
  public SingleCimClassSelectionEvent(CimClassWrapper selectedNode) {
    this.selectedNode = selectedNode;
  }

  public interface Listener extends EventListener {
    
    void classSelected(CimClassWrapper node);
    
  }
  
  @Override
  public void dispatch(EventListener eventListener) {
    ((Listener)eventListener).classSelected(selectedNode);
  }
}
