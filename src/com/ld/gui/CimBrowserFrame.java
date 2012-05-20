package com.ld.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import com.ld.gui.generic.GuiUtils;
import com.ld.kernel.mgrs.CimBrowserMgr;


public class CimBrowserFrame extends JFrame {
  private static final String TITLE = "LD project: CIM Browser";
  private static final Dimension DEFAULT_SIZE = new Dimension(1200, 700);
  private static final long serialVersionUID = 1L;
  private ConnectionParametersPanel connectionParametersPanel;
  private CimClassesTreePanel cimClassesTreePanel;
  private CimTabbedPanel cimTabbedPanel;

  public CimBrowserFrame(CimBrowserMgr mainMgr) {
    connectionParametersPanel = new ConnectionParametersPanel(mainMgr);
    cimClassesTreePanel = new CimClassesTreePanel(mainMgr);
    cimTabbedPanel = new CimTabbedPanel(mainMgr);
    initFrame();
  }

  private void initFrame() {
    Container container = getContentPane();
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.setConstraints(connectionParametersPanel.createPanel(), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, GuiUtils.DEFAULT_INSETS, 0, 0));
    JComponent splitPane = createSouthPanel();
    gridBagLayout.setConstraints(splitPane, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, GuiUtils.DEFAULT_INSETS, 0, 0));
    container.setLayout(gridBagLayout);
    container.add(connectionParametersPanel.getPanel());
    container.add(splitPane);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setSize(DEFAULT_SIZE);
    setTitle(TITLE);
    GuiUtils.moveToCenter(this);
  }

  private JComponent createSouthPanel() {
    JSplitPane splitPane = GuiUtils.createHorizontalSplit(cimClassesTreePanel, cimTabbedPanel);
    splitPane.setDividerLocation(400);
    return splitPane;
  }
}
