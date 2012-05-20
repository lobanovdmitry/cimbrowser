package com.ld.launcher;

import javax.swing.SwingUtilities;

import com.ld.gui.CimBrowserFrame;
import com.ld.gui.generic.GuiUtils;
import com.ld.kernel.mgrs.CimBrowserMgr;

public class CimBrowserLauncher {

  public static void main(String[] args) {
    GuiUtils.setLookAndFeel();
    final CimBrowserMgr cimBrowserMgr = new CimBrowserMgr();
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new CimBrowserFrame(cimBrowserMgr).setVisible(true);
      }
    });
  }
}
