package com.ld.gui;

import javax.cim.CIMInstance;
import javax.cim.CIMProperty;
import javax.swing.table.DefaultTableModel;

import com.ld.gui.generic.CimTabledPanel;
import com.ld.gui.generic.Utils;
import com.ld.kernel.data.CimClassWrapper;
import com.ld.kernel.events.MultipleCimClassSelectionEvent;
import com.ld.kernel.events.SingleCimClassSelectionEvent;
import com.ld.kernel.mgrs.EventMgr;

public class CimPropertiesTablePanel extends CimTabledPanel {
  private static final long serialVersionUID = 1L;

  private CIMProperty[] properties;
  private CimPropertiesTableModel tableModel;

  public CimPropertiesTablePanel(EventMgr eventMgr) {
    super(eventMgr);
  }
  
  @Override
  public void classSelected(CimClassWrapper node) {
    properties = node.getProperties();
    super.classSelected(node);
  }
  
  @Override
  public void instanceSelected(CIMInstance instance) {
    properties = instance.getProperties();
    super.instanceSelected(instance);
  }
  
  @Override
  protected void registerListeners() {
    eventMgr.register(SingleCimClassSelectionEvent.class, this);
    eventMgr.register(MultipleCimClassSelectionEvent.class, this);
  }
  
  @Override
  protected DefaultTableModel getTableModel() {
    if ( tableModel == null) {
      tableModel = new CimPropertiesTableModel();
    }
    return tableModel;
  }
  
  @Override
  protected void resetTableModel() {
    properties = new CIMProperty[0];
    tableModel.fireTableDataChanged();
  }
  
  private class CimPropertiesTableModel extends DefaultTableModel {
    
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_TYPE = "Type";
    private static final String COLUMN_VALUE = "Value";
    private static final String COLUMN_ORIGIN_CLASS = "Origin class";
    
    private static final int COLUMN_NAME_INDEX = 0;
    private static final int COLUMN_TYPE_INDEX = 1;
    private static final int COLUMN_VALUE_INDEX = 2;
    private static final int COLUMN_ORIGIN_INDEX = 3;
    
    private static final long serialVersionUID = 1L;

    @Override
    public int getColumnCount() {
      return 4;
    }
    
    @Override
    public int getRowCount() {
      return properties != null ? properties.length : 0;
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
      case COLUMN_ORIGIN_INDEX:
        return COLUMN_ORIGIN_CLASS;
      default:
        return null;
      }
    }
    
    @Override
    public Object getValueAt(int row, int column) {
      CIMProperty property = properties[row];
      switch (column) {
      case COLUMN_NAME_INDEX:
        return property.getName();
      case COLUMN_TYPE_INDEX:
        return property.getDataType();
      case COLUMN_VALUE_INDEX:
        return Utils.convertValue(property.getValue());
      case COLUMN_ORIGIN_INDEX:
        return property.getOriginClass();
      default:
        return null;
      }
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
    
    
  }
}
