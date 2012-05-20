package com.ld.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.cim.CIMQualifier;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.ld.gui.generic.CimTabledPanel;
import com.ld.gui.generic.GuiUtils;
import com.ld.gui.generic.Utils;
import com.ld.kernel.data.CimClassWrapper;
import com.ld.kernel.events.CimQualifierSelectionEvent;
import com.ld.kernel.events.MultipleCimClassSelectionEvent;
import com.ld.kernel.events.SingleCimClassSelectionEvent;
import com.ld.kernel.mgrs.EventMgr;

public class CimQualifiersTablePanel extends CimTabledPanel {
  private static final long serialVersionUID = 1L;

  private CIMQualifier[] qualifiers;
  private CimMethodsTableModel tableModel;
  private CimQualifiersDescriptionPanel description;

  public CimQualifiersTablePanel(EventMgr eventMgr) {
    super(eventMgr);
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      
      @Override
      public void valueChanged(ListSelectionEvent event) {
        if (event.getValueIsAdjusting() ||
            (table.getSelectedRowCount() < 1 && qualifiers.length > table.getSelectedRow())) {
          return;
        }
        qualifierSelected(qualifiers[table.getSelectedRow()]);
      }
    });
  }
  
  public void classSelected(CimClassWrapper node) {
    qualifiers = node.getCIMClass().getQualifiers();
    super.classSelected(node);
  }
  
  @Override
  protected void registerListeners() {
    eventMgr.register(SingleCimClassSelectionEvent.class, this);
    eventMgr.register(MultipleCimClassSelectionEvent.class, this);
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
    description = new CimQualifiersDescriptionPanel(eventMgr);
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
  
  @Override
  protected DefaultTableModel getTableModel() {
    if ( tableModel == null) {
      tableModel = new CimMethodsTableModel();
    }
    return tableModel;
  }
  
  @Override
  protected void resetTableModel() {
    qualifiers = new CIMQualifier[0];
  }
  
  private void qualifierSelected(CIMQualifier qualifier) {
    eventMgr.dispatch(new CimQualifierSelectionEvent(qualifier));
  }
  
  private class CimMethodsTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;
    
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_TYPE = "Type";
    private static final String COLUMN_VALUE = "Value";

    private static final int COLUMN_NAME_INDEX = 0; 
    private static final int COLUMN_TYPE_INDEX = 1;
    private static final int COLUMN_VALUE_INDEX = 2;
    
    @Override
    public int getColumnCount() {
      return 3;
    }
    
    @Override
    public int getRowCount() {
      return qualifiers != null ? qualifiers.length : 0;
    }
    
    @Override
    public String getColumnName(int column) {
      switch (column) {
      case COLUMN_NAME_INDEX:
        return COLUMN_NAME;
      case COLUMN_TYPE_INDEX:
        return COLUMN_TYPE;
      case COLUMN_VALUE_INDEX:
        return COLUMN_VALUE;
      default:
        return null;
      }
    }
    
    @Override
    public Object getValueAt(int row, int column) {
      CIMQualifier qualifier = qualifiers[row];
      switch (column) {
      case COLUMN_NAME_INDEX:
        return qualifier.getName();
      case COLUMN_TYPE_INDEX:
        return qualifier.getDataType();
      case COLUMN_VALUE_INDEX:
        return Utils.convertValue(qualifier.getValue());
      }
      return null;
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
  }
}
