package com.ld.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.cim.CIMMethod;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.ld.gui.generic.CimTabledPanel;
import com.ld.gui.generic.GuiUtils;
import com.ld.kernel.data.CimClassWrapper;
import com.ld.kernel.events.CimMethodSelectionEvent;
import com.ld.kernel.events.MultipleCimClassSelectionEvent;
import com.ld.kernel.events.SingleCimClassSelectionEvent;
import com.ld.kernel.mgrs.EventMgr;

public class CimMethodsTablePanel extends CimTabledPanel {
  private static final long serialVersionUID = 1L;

  private CIMMethod[] methods;
  private CimMethodsTableModel tableModel;
  private CimMethodDescriptionPanel description;

  public CimMethodsTablePanel(EventMgr eventMgr) {
    super(eventMgr);
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      
      @Override
      public void valueChanged(ListSelectionEvent event) {
        if (event.getValueIsAdjusting() ||
            (table.getSelectedRowCount() < 1 && methods.length > table.getSelectedRow())) {
          return;
        }
        methodSelected(methods[table.getSelectedRow()]);
      }
    });
  }
  
  public void classSelected(CimClassWrapper node) {
    resetTableModel();
    methods = node.getCIMClass().getMethods();
    super.classSelected(node);
  }
  
  @Override
  protected void registerListeners() {
    eventMgr.register(SingleCimClassSelectionEvent.class, this);
    eventMgr.register(MultipleCimClassSelectionEvent.class, this);
  }
  
  @Override
  protected DefaultTableModel getTableModel() {
    if ( tableModel == null) {
      tableModel = new CimMethodsTableModel();
    }
    return tableModel;
  }
  
  @Override
  protected void resetTableModel() {
    methods = new CIMMethod[0];
    table.clearSelection();
    description.clear();
  }
  
  @Override
  protected void initPanel() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.addLayoutComponent(classNameLabel, 
        new GridBagConstraints(0, 0,
            1, 1,
            1, 0,
            GridBagConstraints.NORTH,
            GridBagConstraints.CENTER,
            GuiUtils.DEFAULT_INSETS,
            0, 0));
    description = new CimMethodDescriptionPanel(eventMgr);
    JSplitPane splitPane = GuiUtils.createVerticalSplit(
        GuiUtils.createScrollPane(table),
        GuiUtils.createScrollPane(description));
    gridBagLayout.addLayoutComponent(splitPane,
        new GridBagConstraints(0, 1,
            1, 1,
            1, 1,
            GridBagConstraints.NORTH,
            GridBagConstraints.BOTH,
            GuiUtils.DEFAULT_INSETS,
            0, 0)); 
    add(classNameLabel);
    add(splitPane);
    setLayout(gridBagLayout);
  }
  
  private void methodSelected(CIMMethod method)  {
    eventMgr.dispatch(new CimMethodSelectionEvent(method));
  }
  
  private class CimMethodsTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;
    
    private static final String COLUMN_METHOD_NAME = "Name";
    private static final String COLUMN_TYPE = "Data type";
    private static final String COLUMN_ORIGIN_CLASS = "Origin class";
    
    private static final int COLUMN_METHOD_NAME_INDEX = 0;
    private static final int COLUMN_TYPE_INDEX = 1;
    private static final int COLUMN_ORIGIN_INDEX = 2;
    
    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
    
    @Override
    public int getColumnCount() {
      return 3;
    }
    
    @Override
    public int getRowCount() {
      return methods != null ? methods.length : 0;
    }
    
    @Override
    public String getColumnName(int column) {
      switch (column) {
      case COLUMN_METHOD_NAME_INDEX:
        return COLUMN_METHOD_NAME;
      case COLUMN_TYPE_INDEX:
        return COLUMN_TYPE;
      case COLUMN_ORIGIN_INDEX:
        return COLUMN_ORIGIN_CLASS;
      default:
        return null;
      }
    }
    
    @Override
    public Object getValueAt(int row, int column) {
      CIMMethod method = methods[row];
      switch (column) {
      case COLUMN_METHOD_NAME_INDEX:
        return method.getName();
      case COLUMN_TYPE_INDEX:
        return method.getDataType();
      case COLUMN_ORIGIN_INDEX:
        return method.getOriginClass();
      }
      return null;
    }
  }
}
