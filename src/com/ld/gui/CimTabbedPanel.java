package com.ld.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.ld.gui.generic.GuiUtils;
import com.ld.kernel.mgrs.CimBrowserMgr;
import com.ld.kernel.mgrs.EventMgr;

public class CimTabbedPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public CimTabbedPanel(CimBrowserMgr mainMgr) {
        JTabbedPane tabbedPane = new JTabbedPane();
        EventMgr eventMgr = EventMgr.getMgr(mainMgr);
        tabbedPane.addTab("Properties", new CimPropertiesTablePanel(eventMgr));
        tabbedPane.addTab("Methods", new CimMethodsTablePanel(eventMgr));
        tabbedPane.addTab("Qualifiers", new CimQualifiersTablePanel(eventMgr));
        tabbedPane.addTab("Instances", new CimInstancesPanel(mainMgr));
        add(tabbedPane);
        GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout); 
        gridBagLayout.addLayoutComponent(tabbedPane, 
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, 
                        GuiUtils.DEFAULT_INSETS, 0, 0));
    }
}
