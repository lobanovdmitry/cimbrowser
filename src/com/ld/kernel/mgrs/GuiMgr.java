package com.ld.kernel.mgrs;

import javax.swing.JFrame;

public class GuiMgr implements Manager {

  private JFrame mainFrame = null;
  
  public static GuiMgr getMgr(CimBrowserMgr mgr) {
    return (GuiMgr)mgr.getMgr(GuiMgr.class);
  }
  
  public void registerMainFrame(JFrame mainFrame) {
    this.mainFrame = mainFrame;
  }
  
  public JFrame getMainFrame() {
    return mainFrame;
  }
  
}
