package com.ld.kernel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.cim.CIMClass;
import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.security.auth.Subject;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import javax.wbem.client.PasswordCredential;
import javax.wbem.client.UserPrincipal;
import javax.wbem.client.WBEMClient;
import javax.wbem.client.WBEMClientFactory;

import com.ld.kernel.data.CimClassWrapper;
import com.ld.kernel.data.CimInstanceWrapper;
import com.ld.kernel.mgrs.CimConnectionMgr;

public class CimSessionHolder {
  private static final String DEFAULT_PROTOCOL_TYPE = "CIM-XML";
  private String namespace;
  private WBEMClient wbemClient;
  private boolean alive = false;
  private CimConnectionMgr connectionMgr;
  
  public CimSessionHolder(CimConnectionMgr connectionMgr) {
    this.connectionMgr = connectionMgr;
  }

  public void createSession(String protocol, String host, int port, String login, String psswd, String namespace)
      throws MalformedURLException, IllegalArgumentException, WBEMException
  {
    URL cimomUrl = new URL( protocol + "://" + host + ":" + port);
    CIMObjectPath path = new CIMObjectPath(cimomUrl.getProtocol(), cimomUrl.getHost(), String.valueOf(cimomUrl.getPort()), null, null, null);
    Subject subject = new Subject();
    subject.getPrincipals().add(new UserPrincipal(login));
    subject.getPrivateCredentials().add(new PasswordCredential(psswd));
    wbemClient = WBEMClientFactory.getClient(DEFAULT_PROTOCOL_TYPE);
    wbemClient.initialize(path, subject, Locale.getAvailableLocales());
    this.alive = true;
    this.namespace = namespace;
  }
  
  public void enumerateClasses(String className) {
    new CimRequestExecutor().enumerateClasses(className);
  }
  
  public void enumerateInstances(String className) {
    new CimRequestExecutor().enumerateInstances(className);
  }
  
  public void closeConnection() {
    alive = false;
    wbemClient.close();
  }
  
  public boolean isAlive() {
    return alive ;
  }
  
  private class CimRequestExecutor {
    
    public void enumerateClasses(final String rootClassName) {
      Runnable request = new Runnable() {
        
        @Override
        public void run() {
          RequestResult requestResult = new RequestResult(RequestStatus.UNKNOWN);
          List<CimClassWrapper> classes = new ArrayList<CimClassWrapper>();
          boolean pDeep = true;
          boolean pPropagated = false;
          boolean pIncludeQualifiers = true;
          boolean pIncludeClassOrigin = true;
          CloseableIterator iteratorWithClasses = null;
          try {
            iteratorWithClasses = wbemClient.enumerateClasses(new CIMObjectPath(rootClassName, namespace), pDeep, pPropagated, pIncludeQualifiers, pIncludeClassOrigin);
            while (iteratorWithClasses.hasNext()) {
              classes.add(new CimClassWrapper((CIMClass) iteratorWithClasses.next()));
            }
            requestResult.setStatus(RequestStatus.SUCCESS);
          } catch (WBEMException e) {
            requestResult.setStatus(RequestStatus.FAIL);
            requestResult.setMessage(e.getMessage());
          } finally {
            if ( iteratorWithClasses != null) {
              iteratorWithClasses.close();
            }
          }
          connectionMgr.dispatchRequestedClasses(requestResult, classes);
        }
      };
      Thread thread = new Thread(request, "EnumerateClassesRequestThread");
      thread.start();
    }
    
    public void enumerateInstances(final String className) {
      Runnable request = new Runnable() {
        
        @Override
        public void run() {
          RequestResult requestResult = new RequestResult(RequestStatus.UNKNOWN);
          List<CimInstanceWrapper> instances = new ArrayList<CimInstanceWrapper>();
          boolean pDeep = false;
          boolean pPropagated = false;
          boolean pIncludeQualifiers = true;
          CloseableIterator iteratorWithInstances = null;
          try {
            iteratorWithInstances = wbemClient.enumerateInstances(new CIMObjectPath(className, namespace), pDeep, pPropagated, pIncludeQualifiers, new String[0]);
            while (iteratorWithInstances.hasNext()) {
              instances.add(new CimInstanceWrapper((CIMInstance) iteratorWithInstances.next()));
            }
            requestResult.setStatus(RequestStatus.SUCCESS);
          } catch (WBEMException e) {
            requestResult.setStatus(RequestStatus.FAIL);
            requestResult.setMessage(e.getMessage());
          } finally {
            if ( iteratorWithInstances != null) {
              iteratorWithInstances.close();
            }
          }
          connectionMgr.dispatchRequestedInstances(requestResult, instances);
        }
      };
      Thread thread = new Thread(request, "EnumerateClassesRequestThread");
      thread.start();
    }
  }
  
  public static enum RequestStatus {
    SUCCESS("success"),
    FAIL("fail"),
    UNKNOWN("unknown");
    
    private String status;
    
    private RequestStatus(String status) {
      this.status = status;
    }
    
    @Override
    public String toString() {
      return status;
    }
  }
  
  public static class RequestResult {
    private String message = "";
    private RequestStatus status;
    
    public RequestResult(RequestStatus status) {
      this.status = status;
    }
    
    public void setMessage(String message) {
      this.message = message;
    }
    
    public void setStatus(RequestStatus status) {
      this.status = status;
    }
    
    public RequestResult(RequestStatus status, String message) {
      this(status);
      this.message = message;
    }
    
    public RequestStatus getStatus() {
      return status;
    }
    
    public String getMessage() {
      return message;
    }
  }
}
