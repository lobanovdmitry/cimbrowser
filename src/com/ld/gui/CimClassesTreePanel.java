package com.ld.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.ld.gui.events.FilterChangedEvent;
import com.ld.gui.generic.GuiUtils;
import com.ld.gui.generic.GuiUtils.FormBuilder;
import com.ld.kernel.CimSessionHolder;
import com.ld.kernel.CimSessionHolder.RequestResult;
import com.ld.kernel.CimSessionHolder.RequestStatus;
import com.ld.kernel.data.CimClassWrapper;
import com.ld.kernel.data.CimClassesTreeStorage;
import com.ld.kernel.events.CimClassesTreeUpdatedByRequestEvent;
import com.ld.kernel.events.CimSessionChangedEvent;
import com.ld.kernel.events.MultipleCimClassSelectionEvent;
import com.ld.kernel.events.SingleCimClassSelectionEvent;
import com.ld.kernel.mgrs.CimBrowserMgr;
import com.ld.kernel.mgrs.CimConnectionMgr;
import com.ld.kernel.mgrs.EventMgr;
import com.ld.kernel.mgrs.GuiMgr;

public class CimClassesTreePanel extends JPanel implements 
  CimSessionChangedEvent.Listener,
  CimClassesTreeUpdatedByRequestEvent.Listener,
  MultipleCimClassSelectionEvent.Listener{

  private static final String ROOT_CLASS_NAME = "";
  private static final long serialVersionUID = 1L;
  private static String FILTER_BUTTON = "  Filter  ";
  private static String RELEASE_BUTTON = "Release";
  
  private DefaultMutableTreeNode root = new DefaultMutableTreeNode(CimClassWrapper.ROOT, true);
  private Map<String, DefaultMutableTreeNode> nodes = new HashMap<String, DefaultMutableTreeNode >();
  private JTree classesTree = new JTree(root);
  private JTextField searchField = new JTextField();
  private JButton filterButton = new JButton(FILTER_BUTTON);
  private JButton releaseButton = new JButton(RELEASE_BUTTON);
  
  private CimSessionHolder sessionHolder;
  private CimClassesTreeStorage storage = CimClassesTreeStorage.EMPTY;
  private JDialog lockDialog = null;
  private CimBrowserMgr mainMgr;

  public CimClassesTreePanel(CimBrowserMgr mainMgr) {
    this.sessionHolder = CimConnectionMgr.getMgr(mainMgr).getSessionHolder();
    this.mainMgr = mainMgr;
    initTree();
    initButtons();
    EventMgr eventMgr = EventMgr.getMgr(mainMgr);
    eventMgr.register(CimSessionChangedEvent.class, this);
    eventMgr.register(CimClassesTreeUpdatedByRequestEvent.class, this);
  }
  
  @Override
  public void classesChanged(RequestResult result, List<CimClassWrapper> classes) {
    clearAllData();
    unlockPanel();
    if ( RequestStatus.FAIL == result.getStatus()) {
      JOptionPane.showMessageDialog(GuiMgr.getMgr(mainMgr).getMainFrame(), result.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    if ( RequestStatus.SUCCESS == result.getStatus() ){
      storage = new CimClassesTreeStorage(classes);
      storage.process();
      addAllChildren(root, storage.getUnderRootNodes());//fill tree with all classes
    }
    TreePath rootPath = new TreePath(root.getPath());
    classesTree.expandPath(rootPath);
    classesTree.updateUI();
  }

  @Override
  public void classesSelected() {
    //Nothing to do
  }
  
  @Override
  public void sessionChanged() {
    enumerateClassesForTree();
    lockPanel();
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
  
  private void initTree() {
    initLayout();
    classesTree.setShowsRootHandles(true);
    classesTree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent event) {
        if (classesTree.getSelectionCount() > 1) {
          EventMgr.getMgr(mainMgr).dispatch(new MultipleCimClassSelectionEvent());
          return;
        }
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)event.getPath().getLastPathComponent();
        EventMgr.getMgr(mainMgr).dispatch(
            new SingleCimClassSelectionEvent((CimClassWrapper)treeNode.getUserObject()));
      }
    });
    classesTree.setCellRenderer(new CimNodeRenderer(EventMgr.getMgr(mainMgr)));
    TreeSelectionModel model = new DefaultTreeSelectionModel();
    model.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    classesTree.setSelectionModel(model);
  }

  private void initLayout() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    JPanel searchPanel = createFilterPanel();
    JScrollPane paneForTree = GuiUtils.createScrollPane(classesTree);
    gridBagLayout.addLayoutComponent(searchPanel, 
        new GridBagConstraints(0, 0, 1, 1, 1, 0,
            GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, GuiUtils.DEFAULT_INSETS, 0, 0));
    gridBagLayout.addLayoutComponent(paneForTree,
        new GridBagConstraints(0, 1, 1, 1, 1, 1,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH, GuiUtils.DEFAULT_INSETS, 0, 0));
    add(paneForTree);
    add(searchPanel);
    setLayout(gridBagLayout);
  }
  
  private void initButtons() {
    filterButton.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent event) {
        String targetClassName = searchField.getText();
        if ( targetClassName.isEmpty() ) {
          return;
        }
        root.removeAllChildren();
        Map<DefaultMutableTreeNode, Set<DefaultMutableTreeNode>> filteredNodes = new HashMap<DefaultMutableTreeNode, Set<DefaultMutableTreeNode>>();
        for ( Map.Entry<String, DefaultMutableTreeNode> mapEntry : nodes.entrySet() ) {
          if ( mapEntry.getKey().contains(targetClassName) ) {
            DefaultMutableTreeNode underRootNode = nodes.get(mapEntry.getKey().split("/")[0]);
            Set<DefaultMutableTreeNode> nodesToExpand = filteredNodes.get(underRootNode);
            if ( nodesToExpand == null ) {
              nodesToExpand = new HashSet<DefaultMutableTreeNode>();
            }
            String[] classesNamesInPath = mapEntry.getKey().split("/");
            DefaultMutableTreeNode targetNode = mapEntry.getValue();
            for (int i = classesNamesInPath.length - 1; i > -1; i-- ) {
              if ( targetNode.getUserObject().toString().contains(targetClassName) ) {
                nodesToExpand.add(targetNode);
              } else {
                targetNode = (DefaultMutableTreeNode) targetNode.getParent();
              }
            }
            filteredNodes.put(underRootNode, nodesToExpand);
          }
        }
        for ( Map.Entry<DefaultMutableTreeNode, Set<DefaultMutableTreeNode>> filteredNode : filteredNodes.entrySet() ) {
          root.add(filteredNode.getKey());
          for ( DefaultMutableTreeNode nodeToExpand : filteredNode.getValue() ){
            classesTree.expandPath(new TreePath(nodeToExpand.getPath()));
          }
        }
        EventMgr.getMgr(mainMgr).dispatch(new FilterChangedEvent(targetClassName));
        classesTree.updateUI();
      }
    });
    
    releaseButton.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent arg0) {
        root.removeAllChildren();
        addAllChildren(root, storage.getUnderRootNodes());//fill tree with all classes
        EventMgr.getMgr(mainMgr).dispatch(new FilterChangedEvent(""));
        classesTree.updateUI();
      }
    });
  }

  private JPanel createFilterPanel() {
    FormBuilder formBuilder = new FormBuilder();
    formBuilder.addComponent(searchField, true);
    formBuilder.addComponent(filterButton, false);
    formBuilder.addComponent(releaseButton, false);
    return formBuilder.build();
  }
  
  private void enumerateClassesForTree() {
    sessionHolder.enumerateClasses(ROOT_CLASS_NAME);
  }

  private void addAllChildren(DefaultMutableTreeNode parentNode, List<CimClassWrapper> children) {
    Collections.sort(children);
    for ( CimClassWrapper child : children ) {
      DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
      parentNode.add(childNode);
      nodes.put(child.getFullPath(), childNode);
      if (!child.isLeaf()) {
        addAllChildren(childNode, child.getCimChildren());
      }
    }
  }
  
  private void clearAllData() {
    root.removeAllChildren();
    nodes.clear();
    storage = CimClassesTreeStorage.EMPTY;
//    TreePath rootPath = new TreePath(root.getPath());
//    classesTree.setSelectionPath(rootPath);
//    classesTree.scrollPathToVisible(rootPath);
    classesTree.updateUI();
  }
}
