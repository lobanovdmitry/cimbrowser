package com.ld.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.ImageIcon;
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
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

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
  private static String SEARCH_BUTTON = "Search";
  
  private DefaultMutableTreeNode root = new DefaultMutableTreeNode(CimClassWrapper.ROOT, true);
  private Map<String, DefaultMutableTreeNode> nodes = new HashMap<String, DefaultMutableTreeNode >();
  private JTree classesTree = new JTree(root);
  private JTextField searchField = new JTextField();
  private JButton searchButton = new JButton(SEARCH_BUTTON);
  
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
      addAllChildren(root, storage.getUnderRootNodes());
    }
    classesTree.collapsePath(new TreePath(root));
    classesTree.repaint();
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

  private void selectClassAtTree(CimClassWrapper foundClass) {
    TreePath path = getTreePathForClass(foundClass);
    classesTree.setSelectionPath(path);
    classesTree.scrollRowToVisible(classesTree.getRowForPath(path));
  }
  
  private TreePath getTreePathForClass(CimClassWrapper foundClass) {
    Stack<DefaultMutableTreeNode> stack = new Stack<DefaultMutableTreeNode>();
    for(CimClassWrapper node = foundClass; node != null; node = storage.getParent(node)) {
      stack.push(nodes.get(node.getName()));
    }
    DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[stack.size() + 1];
    int i = 0;
    nodes[i++] = root;
    while (!stack.isEmpty()) {
      nodes[i++] = stack.pop();      
    }
    return new TreePath(nodes);
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
    classesTree.setCellRenderer(new CimNodeRenderer());
    TreeSelectionModel model = new DefaultTreeSelectionModel();
    model.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    classesTree.setSelectionModel(model);
  }

  private void initLayout() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    JPanel searchPanel = createSearchPanel();
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
    searchButton.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent event) {
        String textToSearch = searchField.getText();
        CimClassWrapper foundClass = storage.findClass(textToSearch);
        if ( foundClass != null) {
          selectClassAtTree(foundClass);
        }
      }
    });
  }

  private JPanel createSearchPanel() {
    FormBuilder formBuilder = new FormBuilder();
    formBuilder.addComponent(searchField, true);
    formBuilder.addComponent(searchButton, false);
    return formBuilder.build();
  }
  
  private void enumerateClassesForTree() {
    sessionHolder.enumerateClasses(ROOT_CLASS_NAME);
  }

  private void addAllChildren(DefaultMutableTreeNode node, List<CimClassWrapper> children) {
    Collections.sort(children);
    for ( CimClassWrapper child : children ) {
      DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(child);
      node.add(treeNode);
      nodes.put(child.getName(), treeNode);
      if (!child.isLeaf()) {
        addAllChildren(treeNode, child.getCimChildren());
      }
    }
  }
  
  private void clearAllData() {
    root.removeAllChildren();
    nodes.clear();
    storage = CimClassesTreeStorage.EMPTY;
  }
  
  public class CimNodeRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 1L;
    private ImageIcon iconLockedRoot = GuiUtils.createImageIcon("./resources/images/lockedRoot.png");
    private ImageIcon iconUnlockedRoot = GuiUtils.createImageIcon("./resources/images/unlockedRoot.png");
    private ImageIcon iconKeyed = GuiUtils.createImageIcon("./resources/images/password.png");
    private ImageIcon iconMiddle = GuiUtils.createImageIcon("./resources/images/assoc.png");
    private ImageIcon iconKeyAndAssoc = GuiUtils.createImageIcon("./resources/images/blockdevice.png");
    private ImageIcon iconAssoc = GuiUtils.createImageIcon("./resources/images/middle.gif");
    
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
      return this;
    }
  }
}
