package com.ld.gui;

import javax.cim.CIMInstance;

import com.ld.gui.generic.GuiUtils;
import com.ld.kernel.data.CimClassWrapper;
import com.ld.kernel.events.MultipleCimClassSelectionEvent;
import com.ld.kernel.events.SingleCimClassSelectionEvent;
import com.ld.kernel.events.SingleCimInstanceSelectionEvent;
import com.ld.kernel.mgrs.EventMgr;

public class CimInstanceDescriptionPanel extends CimPropertiesTablePanel {
  private static final long serialVersionUID = 1L;

  public CimInstanceDescriptionPanel(EventMgr eventMgr) {
    super(eventMgr);
  }
  
  @Override
  protected void registerListeners() {
    eventMgr.register(SingleCimInstanceSelectionEvent.class, this);
    eventMgr.register(SingleCimClassSelectionEvent.class, this);
    eventMgr.register(MultipleCimClassSelectionEvent.class, this);
  }
  
  @Override
  public void instanceSelected(CIMInstance instance) {
    super.instanceSelected(instance);
    classNameLabel.setText(GuiUtils.wrapInHTMLHeader(instance.getClassName()));
  }
  
  @Override
  public void classesSelected() {
    classNameLabel.setText("");
  }
  
  @Override
  public void classSelected(CimClassWrapper node) {
    classNameLabel.setText("");
  }
}
