package com.ld.gui.generic;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;

import javax.cim.CIMInstance;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.ld.kernel.data.CimClassWrapper;
import com.ld.kernel.events.MultipleCimClassSelectionEvent;
import com.ld.kernel.events.SingleCimInstanceSelectionEvent;
import com.ld.kernel.events.SingleCimClassSelectionEvent;
import com.ld.kernel.mgrs.EventMgr;

public abstract class CimTabledPanel extends JPanel implements
    SingleCimClassSelectionEvent.Listener,
    MultipleCimClassSelectionEvent.Listener,
    SingleCimInstanceSelectionEvent.Listener {
  private static final long serialVersionUID = 1L;
  
  protected JTable table;
  protected DefaultTableModel tableModel;
  protected JLabel classNameLabel = new JLabel();
  protected EventMgr eventMgr;
  
  public CimTabledPanel(EventMgr eventMgr) {
    this.eventMgr = eventMgr;
    tableModel = getTableModel();
    table = new JTable(tableModel) {
      private static final long serialVersionUID = 1L;

      public String getToolTipText(MouseEvent e) {
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        int realColumnIndex = convertColumnIndexToModel(colIndex);
        return getToolTips(rowIndex, realColumnIndex);
      };
    };
    table.setSelectionMode(0);
    initPanel();
    registerListeners();
  }
  
  @Override
  public void classSelected(CimClassWrapper node) {
    classNameLabel.setText(GuiUtils.wrapInHTMLHeader(node.getName()));
    tableModel.fireTableDataChanged();
  }
  
  @Override
  public void classesSelected() {
    classNameLabel.setText(GuiUtils.wrapInHTMLHeader("Several classes are selected"));
    resetTableModel();
  }
  
  @Override
  public void instanceSelected(CIMInstance instance) {
    tableModel.fireTableDataChanged();
  }

  protected abstract DefaultTableModel getTableModel();
  
  protected abstract void resetTableModel();
  
  protected abstract void registerListeners();
  
  protected String getToolTips(int rowIdx, int columnIdx) {
    Object value = tableModel.getValueAt(rowIdx, columnIdx);
    return value != null ? GuiUtils.splitStringForTooTip(value.toString()) : null;
  }

  protected void initPanel() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.addLayoutComponent(classNameLabel, 
        new GridBagConstraints(0, 0,
            1, 1,
            0, 0,
            GridBagConstraints.NORTH,
            GridBagConstraints.CENTER,
            GuiUtils.DEFAULT_INSETS,
            0, 0));
    JScrollPane tablePane = GuiUtils.createScrollPane(table);
    gridBagLayout.addLayoutComponent(tablePane, 
        new GridBagConstraints(0, 1,
            1, 1,
            1, 1,
            GridBagConstraints.NORTH,
            GridBagConstraints.BOTH,
            GuiUtils.DEFAULT_INSETS,
            0, 0));
    add(classNameLabel);
    add(tablePane);
    setLayout(gridBagLayout);
  }
}
