package com.ld.kernel.events.interfaces;

public interface Event {

  void dispatch(EventListener eventListener);
  
}
