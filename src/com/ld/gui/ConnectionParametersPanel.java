package com.ld.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.ld.exceptions.ConnectionParametersException;
import com.ld.gui.generic.GuiUtils;
import com.ld.gui.generic.GuiUtils.FormBuilder;
import com.ld.kernel.CimSessionHolder;
import com.ld.kernel.CimSessionHolder.RequestResult;
import com.ld.kernel.CimSessionHolder.RequestStatus;
import com.ld.kernel.events.CimClassesTreeUpdatedByRequestEvent;
import com.ld.kernel.events.CimSessionChangedEvent;
import com.ld.kernel.mgrs.CimBrowserMgr;
import com.ld.kernel.mgrs.CimConnectionMgr;
import com.ld.kernel.mgrs.EventMgr;

public class ConnectionParametersPanel {
  private static final String[] PROTOCOL_STUB = new String[]{ "http", "https"};
  private static final String NAMESPACES_STUB = "root/cimv2";
  private static final String LOCALHOST = "localhost";
  private static final String PORT = "5988";
  private static final String LOGIN = "root";
  private static final String PSSWD = "dlobanov";

  private static final String LOGIN_PANEL_TITLE = "Connection settings";
  private static final String PROTOCOL_LABEL = "Protocol:";
  private static final String HOST_LABEL = "Host:";
  private static final String PORT_LABEL = "Port:";
  private static final String NAMESPACE_LABEL = "Namespace:";
  private static final String LOGIN_LABEL = "Login:";
  private static final String PASSWORD_LABEL = "Password:";
  private static final String REFRESH_BUTTON = "Refresh...";

  private JComboBox protocolList = new JComboBox(PROTOCOL_STUB);
  private JTextField hostTextField = new JTextField(LOCALHOST, GuiUtils.DEFAULT_TEXT_FIELD_LENGHT);
  private JTextField portTextField = new JTextField(PORT, GuiUtils.DEFAULT_TEXT_FIELD_LENGHT);
  private JTextField namespaceField = new JTextField(NAMESPACES_STUB, GuiUtils.DEFAULT_TEXT_FIELD_LENGHT);
  private JTextField loginTextField = new JTextField(LOGIN, GuiUtils.DEFAULT_TEXT_FIELD_LENGHT);
  private JPasswordField passwordField = new JPasswordField(PSSWD, GuiUtils.DEFAULT_TEXT_FIELD_LENGHT);
  private JButton refreshButton = new JButton(REFRESH_BUTTON);
  private JPanel panel;
  private CimSessionHolder sessionHolder;
  private EventMgr eventMgr;

  public ConnectionParametersPanel(CimBrowserMgr mainMgr) {
    this.sessionHolder = CimConnectionMgr.getMgr(mainMgr).getSessionHolder();
    this.eventMgr = EventMgr.getMgr(mainMgr);
    initButtons();
  }

  public JPanel createPanel() {
    FormBuilder formBuilder = new FormBuilder();
    formBuilder.addComponentWithLabelAtLeft(PROTOCOL_LABEL, protocolList);
    formBuilder.addComponentWithLabelAtLeft(HOST_LABEL, hostTextField);
    formBuilder.addComponentWithLabelAtLeft(PORT_LABEL, portTextField);
    formBuilder.addComponentWithLabelAtLeft(NAMESPACE_LABEL, namespaceField);
    formBuilder.addComponentWithLabelAtLeft(LOGIN_LABEL, loginTextField);
    formBuilder.addComponentWithLabelAtLeft(PASSWORD_LABEL, passwordField);
    formBuilder.addComponent(refreshButton, false);
    formBuilder.setBorder(BorderFactory.createTitledBorder(" " + LOGIN_PANEL_TITLE + " "));
    panel = formBuilder.build();
    return panel;
  }

  public JPanel getPanel() {
    return panel;
  }

  public String getProtocol() {
    return (String)protocolList.getSelectedItem();
  }
  
  public String getHost() {
    return hostTextField.getText();
  }

  public String getLogin() {
    return loginTextField.getText();
  }

  public String getPassword() {
    return String.valueOf(passwordField.getPassword());
  }

  public int getPort() throws ConnectionParametersException {
    try {
      int port = Integer.valueOf(portTextField.getText());
      if (port < 0) {
        throw new NumberFormatException();
      }
      return port;
    } catch (NumberFormatException e) {
      throw new ConnectionParametersException(ConnectionParametersException.WRONG_PORT_MESSAGE);
    }
  }

  public String getNamespace() {
    return namespaceField.getText();
  }

  private void initButtons() {
    refreshButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent event) {
        if (hasParametersFailures()) {
          JOptionPane.showMessageDialog(null, "Connection parameters are incorrect!", "Wrong parameters", JOptionPane.ERROR_MESSAGE);
          return;
        }
        try {
          if ( sessionHolder.isAlive()) {
            sessionHolder.closeConnection();
          }
          sessionHolder.createSession(getProtocol(), getHost(), getPort(), getLogin(), getPassword(), getNamespace());
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, "Error during connection: " + e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
          eventMgr.dispatch(new CimClassesTreeUpdatedByRequestEvent(new RequestResult(RequestStatus.FAIL)));
          e.printStackTrace();
        }
        eventMgr.dispatch(new CimSessionChangedEvent());
      }
    });
  }

  private boolean hasParametersFailures() {
    if (!(getHost().isEmpty() || getLogin().isEmpty() || getPassword().isEmpty() || getNamespace().isEmpty())) {
      try {
        getPort();
        return false;
      } catch (ConnectionParametersException e) {
        // nothing to do
      }
    }
    return true;
  }
}
