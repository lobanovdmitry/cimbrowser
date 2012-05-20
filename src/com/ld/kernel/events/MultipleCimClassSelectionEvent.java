package com.ld.kernel.events;

import com.ld.kernel.events.interfaces.Event;
import com.ld.kernel.events.interfaces.EventListener;

public class MultipleCimClassSelectionEvent implements Event {

  public interface Listener extends EventListener {

    void classesSelected();

  }

  @Override
  public void dispatch(EventListener eventListener) {
    ((Listener) eventListener).classesSelected();
  }
}