package com.ld.gui.generic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class GuiUtils {

  public static final Insets DEFAULT_INSETS = new Insets(4, 4, 4, 4);
  public static final int DEFAULT_TEXT_FIELD_LENGHT = 10;

  public static void moveToCenter(Window window) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    window.setLocation((int) (screenSize.getWidth() / 2.0 - window.getWidth() / 2.0), (int) (screenSize.getHeight() / 2.0 - window.getHeight() / 2.0));
  }

  public static JSplitPane createHorizontalSplit(Component left, Component right) {
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
    split.setDividerSize(5);
    split.setBorder(null);
    return split;
  }

  public static JSplitPane createVerticalSplit(Component top, Component bottom) {
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
    split.setDividerSize(5);
    split.setBorder(null);
    return split;
  }

  public static JScrollPane createScrollPane(Component component) {
    return new JScrollPane(component);
  }

  public static JDialog createWaitWindow(JFrame parent) {
    JDialog dialog = new JDialog(parent, "Wait, please...", true);
    dialog.add(new JLabel("Wait, please, operation is in progress..."));
    dialog.setSize(new Dimension(300, 100));
    dialog.setResizable(false);
    moveToCenter(dialog);
    dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    return dialog;
  }

  public static String wrapInHTMLHeader(String text) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<html>").append("<h1>-").append(text).append("-</h1>").append("</html>");
    return buffer.toString();
  }

  public static String splitStringForTooTip(String string) {
    int DEFAULT_LINE_LENGHT = 100;
    StringBuffer buffer = new StringBuffer();
    buffer.append("<html>");
    int offset = 0;
    for (offset = 0; (offset + DEFAULT_LINE_LENGHT) < string.length(); offset = offset + DEFAULT_LINE_LENGHT) {
      buffer.append(string.substring(offset, offset + DEFAULT_LINE_LENGHT));
      buffer.append("<p>");
    }
    buffer.append(string.substring(offset, string.length()));
    buffer.append("</html>");
    return buffer.toString();
  }

  public static ImageIcon createImageIcon(String path) {
    return new ImageIcon(path);
  }

  public static class FormBuilder extends JPanel {
    private static final long serialVersionUID = 1L;
    public static int GRID_WIDTH = 1;
    public static int GRID_HEIGHT = 1;
    public static double WEIGHT_X = 1;
    public static double WEIGHT_Y = 1;

    private GridBagLayout gridBagLayout;
    private int rowIdx = 0;
    private int columnIdx = 0;

    public FormBuilder() {
      gridBagLayout = new GridBagLayout();
      setLayout(gridBagLayout);
    }

    public void addRow() {
      rowIdx++;
    }

    public void addComponentWithLabelAtLeft(String labelText, JComponent component) {
      JLabel label = new JLabel(labelText);
      gridBagLayout.addLayoutComponent(label, new GridBagConstraints(columnIdx++, rowIdx, GRID_WIDTH, GRID_HEIGHT, 0, WEIGHT_Y, GridBagConstraints.EAST, GridBagConstraints.NONE, GuiUtils.DEFAULT_INSETS, 0, 0));
      gridBagLayout.addLayoutComponent(component, new GridBagConstraints(columnIdx++, rowIdx, GRID_WIDTH, GRID_HEIGHT, WEIGHT_X, WEIGHT_Y, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, GuiUtils.DEFAULT_INSETS, 0, 0));
      add(label);
      add(component);
    }

    public void addComponent(JComponent component, boolean resize) {
      gridBagLayout.addLayoutComponent(component, new GridBagConstraints(columnIdx++, rowIdx, GRID_WIDTH, GRID_HEIGHT, resize ? 1 : 0, WEIGHT_Y, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, GuiUtils.DEFAULT_INSETS, 0, 0));
      add(component);
    }

    public JPanel build() {
      return this;
    }
  }

  public static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(new MetalLookAndFeel());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}