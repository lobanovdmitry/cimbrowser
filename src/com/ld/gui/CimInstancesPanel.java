package com.ld.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ld.gui.generic.GuiUtils;
import com.ld.kernel.CimSessionHolder;
import com.ld.kernel.CimSessionHolder.RequestResult;
import com.ld.kernel.CimSessionHolder.RequestStatus;
import com.ld.kernel.data.CimClassWrapper;
import com.ld.kernel.data.CimInstanceWrapper;
import com.ld.kernel.events.CimInstancesListUpdatedByRequestEvent;
import com.ld.kernel.events.MultipleCimClassSelectionEvent;
import com.ld.kernel.events.SingleCimClassSelectionEvent;
import com.ld.kernel.events.SingleCimInstanceSelectionEvent;
import com.ld.kernel.mgrs.CimBrowserMgr;
import com.ld.kernel.mgrs.CimConnectionMgr;
import com.ld.kernel.mgrs.EventMgr;
import com.ld.kernel.mgrs.GuiMgr;

public class CimInstancesPanel extends JPanel implements
    SingleCimClassSelectionEvent.Listener,
    MultipleCimClassSelectionEvent.Listener,
    CimInstancesListUpdatedByRequestEvent.Listener{
  private static final long serialVersionUID = 1L;
  private static final CimInstanceWrapper[] EMPTY_INSTANCES_LIST = new CimInstanceWrapper[0];

  private CimBrowserMgr mainMgr;
  private JButton refreshButton = new JButton("Refresh...");
  private JList instancesList = new JList();
  private CimInstanceDescriptionPanel instancesDescription;
  private CimClassWrapper selectedCimClass = CimClassWrapper.ROOT;
  private JDialog lockDialog = null;
  
  public CimInstancesPanel(final CimBrowserMgr mainMgr) {
    this.mainMgr = mainMgr;
    instancesDescription = new CimInstanceDescriptionPanel(EventMgr.getMgr(mainMgr));
    initPanel();
    initButtons();
    registerListeners();
    instancesList.setSelectionMode(0);
    instancesList.addListSelectionListener(new ListSelectionListener() {
      
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        if ( !instancesList.isSelectionEmpty()) {
          CimInstanceWrapper wrapper = (CimInstanceWrapper) instancesList.getSelectedValue();
          EventMgr.getMgr(mainMgr).dispatch(
              new SingleCimInstanceSelectionEvent(wrapper.getInstance()));
        }
      }
    });
  }
  
  @Override
  public void classSelected(CimClassWrapper node) {
    if ( CimClassWrapper.ROOT != node ) {
      refreshButton.setEnabled(true);
    } else {
      refreshButton.setEnabled(false);
    }
    selectedCimClass = node;
    releasePanel();
  }

  @Override
  public void instancesUpdated(RequestResult result, List<CimInstanceWrapper> instances) {
    unlockPanel();
    releasePanel();
    if ( RequestStatus.FAIL == result.getStatus()) {
      JOptionPane.showMessageDialog(GuiMgr.getMgr(mainMgr).getMainFrame(), result.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    if ( RequestStatus.SUCCESS == result.getStatus() ){
      instancesList.setListData(instances.toArray(new CimInstanceWrapper[instances.size()]));
    }
    instancesList.repaint();
  }
  
  @Override
  public void classesSelected() {
    instancesList.removeAll();
  }
  
  private void initButtons() {
    refreshButton.setEnabled(false);
    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        if ( CimClassWrapper.ROOT == selectedCimClass ) {
          return;
        }
        CimSessionHolder sessionHolder = CimConnectionMgr.getMgr(mainMgr).getSessionHolder();
        sessionHolder.enumerateInstances(selectedCimClass.getName());
        lockPanel();
      }
    });
  }
  
  private void lockPanel() {
    if ( lockDialog == null ) {
      lockDialog = GuiUtils.createWaitWindow(GuiMgr.getMgr(mainMgr).getMainFrame());
    }
    lockDialog.setVisible(true);
  }
  
  private void unlockPanel() {
    lockDialog.dispose();
  }

  private void initPanel() {
    JSplitPane pane = GuiUtils.createHorizontalSplit(getPanelWithInstances(), getPanelWithDetails());
    pane.setDividerLocation(350);
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.addLayoutComponent(pane, 
        new GridBagConstraints(
            0, 0,
            1, 1,
            1, 1,
            GridBagConstraints.CENTER,
            GridBagConstraints.BOTH,
            GuiUtils.DEFAULT_INSETS,
            0, 0));
    add(pane);
    setLayout(gridBagLayout);
  }

  private JPanel getPanelWithInstances() {
    JPanel panelWithInstancesList = new JPanel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.addLayoutComponent(refreshButton, 
        new GridBagConstraints(
            0, 0,
            1, 1,
            1, 0,
            GridBagConstraints.NORTH,
            GridBagConstraints.NONE,
            GuiUtils.DEFAULT_INSETS,
            0, 0));
    JScrollPane instances = GuiUtils.createScrollPane(instancesList);
    gridBagLayout.addLayoutComponent(instances, 
        new GridBagConstraints(
            0, 1,
            1, 1,
            1, 1,
            GridBagConstraints.NORTH,
            GridBagConstraints.BOTH,
            GuiUtils.DEFAULT_INSETS,
            0, 0));
    panelWithInstancesList.add(refreshButton);
    panelWithInstancesList.add(instances);
    panelWithInstancesList.setLayout(gridBagLayout);
    return panelWithInstancesList;
  }
  
  private JPanel getPanelWithDetails() {
    JPanel panelWithDetails = new JPanel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.addLayoutComponent(instancesDescription, 
        new GridBagConstraints(
            0, 0,
            1, 1,
            1, 1,
            GridBagConstraints.CENTER,
            GridBagConstraints.BOTH,
            GuiUtils.DEFAULT_INSETS,
            0, 0));
    panelWithDetails.add(instancesDescription);
    panelWithDetails.setLayout(gridBagLayout);
    return panelWithDetails;
  }
  
  private void registerListeners() {
    EventMgr.getMgr(mainMgr).register(SingleCimClassSelectionEvent.class, this);
    EventMgr.getMgr(mainMgr).register(MultipleCimClassSelectionEvent.class, this);
    EventMgr.getMgr(mainMgr).register(CimInstancesListUpdatedByRequestEvent.class, this);
  }
  
  private void releasePanel() {
    instancesList.clearSelection();
    instancesList.setListData(EMPTY_INSTANCES_LIST);
    instancesDescription.resetTableModel();
  }
}
