package com.ld.kernel.events;

import javax.cim.CIMQualifier;

import com.ld.kernel.events.interfaces.Event;
import com.ld.kernel.events.interfaces.EventListener;

public class CimQualifierSelectionEvent implements Event {

  public interface Listener extends EventListener {

    void qualifierSelected(CIMQualifier qualifier);

  }

  private CIMQualifier qualifier;

  public CimQualifierSelectionEvent(CIMQualifier qualifier) {
    this.qualifier = qualifier;
  }

  @Override
  public void dispatch(EventListener eventListener) {
    ((Listener) eventListener).qualifierSelected(qualifier);
  }

}
