package com.ld.kernel.mgrs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ld.kernel.events.interfaces.Event;
import com.ld.kernel.events.interfaces.EventListener;

public class EventMgr implements Manager {
  /*
   * Map stores an event interface and a list of related listeners
   */
  @SuppressWarnings("rawtypes")
  private Map<Class, List<EventListener>> listeners = new HashMap<Class, List<EventListener>>();

  
  public static EventMgr getMgr(CimBrowserMgr mgr) {
    return (EventMgr)mgr.getMgr(EventMgr.class);
  }
  
  EventMgr() {
    //reducing visibility
  }
  
  public void register(@SuppressWarnings("rawtypes") Class event, EventListener eventListener) {
    List<EventListener> concreteListenters = listeners.get(event);
    if (concreteListenters == null) {
      concreteListenters = new ArrayList<EventListener>(1);
      listeners.put(event, concreteListenters);
    }
    concreteListenters.add(eventListener);
  }

  public void dispatch(Event event) {
    List<EventListener> concreteListenters = listeners.get(event.getClass());
    if (concreteListenters == null) {
      return;
    }
    for (EventListener eventListener : concreteListenters) {
      event.dispatch(eventListener);
    }
  }
}
