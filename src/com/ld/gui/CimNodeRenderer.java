package com.ld.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.ld.gui.events.FilterChangedEvent;
import com.ld.gui.events.FilterChangedEvent.Listener;
import com.ld.gui.generic.GuiUtils;
import com.ld.kernel.data.CimClassWrapper;
import com.ld.kernel.mgrs.EventMgr;

public class CimNodeRenderer extends DefaultTreeCellRenderer
  implements Listener {
  private static final long serialVersionUID = 1L;
  private ImageIcon iconLockedRoot = GuiUtils.createImageIcon("./resources/images/lockedRoot.png");
  private ImageIcon iconUnlockedRoot = GuiUtils.createImageIcon("./resources/images/unlockedRoot.png");
  private ImageIcon iconKeyed = GuiUtils.createImageIcon("./resources/images/password.png");
  private ImageIcon iconMiddle = GuiUtils.createImageIcon("./resources/images/assoc.png");
  private ImageIcon iconKeyAndAssoc = GuiUtils.createImageIcon("./resources/images/blockdevice.png");
  private ImageIcon iconAssoc = GuiUtils.createImageIcon("./resources/images/middle.gif");
  
  private String filter = "";
  
  public CimNodeRenderer(EventMgr eventMgr) {
    eventMgr.register(FilterChangedEvent.class, this);
  }
  
  @Override
  public Component getTreeCellRendererComponent( JTree tree,
      Object value, boolean sel, boolean expanded, boolean leaf,
      int row, boolean hasFocus) {
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
    CimClassWrapper cimClass = (CimClassWrapper)treeNode.getUserObject();
    if ( cimClass == CimClassWrapper.ROOT && leaf) {
      setIcon(iconLockedRoot);
    } else
    if ( cimClass == CimClassWrapper.ROOT && !leaf) {
      setIcon(iconUnlockedRoot);
    } else
    if (!cimClass.isKeyed() && !cimClass.isAssociation()) {
      setIcon(iconMiddle);
    } else 
    if (cimClass.isKeyed() && !cimClass.isAssociation()) {
      setIcon(iconKeyed);
    } else
    if (!cimClass.isKeyed() && cimClass.isAssociation()) {
      setIcon(iconAssoc);
    } else {
      setIcon(iconKeyAndAssoc);
    }
    if ( !filter.isEmpty() && !cimClass.getName().contains(filter)) {
      setForeground(Color.lightGray);
    }
    return this;
  }

  @Override
  public void filterChanged(String newFilter) {
    filter = newFilter;
  }
}