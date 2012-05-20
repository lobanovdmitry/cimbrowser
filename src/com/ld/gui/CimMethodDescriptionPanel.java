package com.ld.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.cim.CIMMethod;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.ld.gui.generic.GuiUtils;
import com.ld.kernel.events.CimMethodSelectionEvent;
import com.ld.kernel.mgrs.EventMgr;

public class CimMethodDescriptionPanel extends JPanel implements CimMethodSelectionEvent.Listener {
  private static final long serialVersionUID = 1L;
  private static final String METHOD_PANEL_TITLE = "Description";
  
  private JTextArea description = new JTextArea();
  
  public CimMethodDescriptionPanel(EventMgr eventMgr) {
    initPanel();
    description.setEditable(false);
    description.setBackground(new JPanel().getBackground());
    eventMgr.register(CimMethodSelectionEvent.class, this);
  }

  @Override
  public void methodSelected(CIMMethod method) {
    description.setText(method.toString());
    description.setLineWrap(true);
  }

  public void clear() {
    description.setText("");
  }
  
  private void initPanel() {
    setBorder(BorderFactory.createTitledBorder(" " + METHOD_PANEL_TITLE + " "));
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.setConstraints(description,
        new GridBagConstraints(
            0, 0,
            1, 1,
            1, 1,
            GridBagConstraints.NORTH,
            GridBagConstraints.BOTH,
            GuiUtils.DEFAULT_INSETS,
            0, 0));
    add(description);
    setLayout(gridBagLayout);
  }
}
