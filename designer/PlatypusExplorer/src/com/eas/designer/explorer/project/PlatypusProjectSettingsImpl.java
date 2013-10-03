/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.designer.application.project.ClientType;
import com.eas.designer.application.project.AppServerType;
import com.eas.designer.application.project.PlatypusProjectSettings;
import com.eas.deploy.BaseDeployer;
import com.eas.deploy.project.PlatypusSettings;
import com.eas.designer.application.PlatypusUtils;
import com.eas.util.StringUtils;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import org.openide.filesystems.FileObject;
import org.openide.util.EditableProperties;
import org.w3c.dom.Document;

/**
 * Facade for all project's settings.
 *
 * @author vv
 */
public class PlatypusProjectSettingsImpl implements PlatypusProjectSettings {

    public static final int DEFAULT_PLATYPUS_SERVER_PORT = 8500;
    public static final int CLIENT_APP_DEFAULT_DEBUG_PORT = 8900;
    public static final int SERVER_APP_DEFAULT_DEBUG_PORT = 8901;
    public static final Level DEFAULT_LOG_LEVEL = Level.INFO;
    public static final String PROJECT_SETTINGS_FILE = "project.properties"; //NOI18N
    public static final String PROJECT_PRIVATE_SETTINGS_FILE = "private.properties"; //NOI18N
    public static final String PROJECT_DISPLAY_NAME_KEY = "projectDisplayName"; //NOI18N
    public static final String RUN_USER_KEY = "runUser"; //NOI18N
    public static final String RUN_PASSWORD_KEY = "runPassword"; //NOI18N
    public static final String RUN_CLIENT_OPTIONS_KEY = "runClientOptions"; //NOI18N
    public static final String RUN_CLIENT_VM_OPTIONS_KEY = "runClientVmOptions"; //NOI18N
    public static final String RUN_SERVER_OPTIONS_KEY = "runServerOptions"; //NOI18N
    public static final String RUN_SERVER_VM_OPTIONS_KEY = "runServerVmOptions"; //NOI18N
    public static final String DB_APP_SOURCES_KEY = "dbAppSources"; //NOI18N
    public static final String SERVER_PORT_KEY = "serverPort";//NOI18N
    public static final String CLIENT_URL_KEY = "clientUrl";//NOI18N
    public static final String NOT_START_SERVER_KEY = "notStartServer"; //NOI18N
    public static final String DEBUG_CLIENT_PORT_KEY = "debugClientPort"; //NOI18N
    public static final String DEBUG_SERVER_PORT_KEY = "debugServerPort"; //NOI18N
    public static final String CLIENT_LOG_LEVEL = "clientLogLevel"; //NOI18N
    public static final String SERVER_LOG_LEVEL = "serverLogLevel"; //NOI18N
    public static final String J2EE_SERVER_ID_KEY = "j2eeServerId"; //NOI18N
    public static final String SERVER_CONTEXT_KEY = "context";//NOI18N
    public static final String ENABLE_SECURITY_REALM_KEY = "enableSecurityRealm";//NOI18N
    public static final String CLIENT_TYPE_KEY = "clientType"; //NOI18N
    public static final String SERVER_TYPE_KEY = "serverType"; //NOI18N
    protected final PlatypusSettings platypusSettings;
    protected final FileObject projectDir;
    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    protected EditableProperties projectProperties;
    protected EditableProperties projectPrivateProperties;
    private boolean platypusSettingsIsDirty;
    private boolean projectPropertiesIsDirty;
    private boolean projectPrivatePropertiesIsDirty;

    public PlatypusProjectSettingsImpl(FileObject aProjectDir) throws Exception {
        if (aProjectDir == null) {
            throw new IllegalArgumentException("Project directory file object is null."); //NOI18N
        }
        projectDir = aProjectDir;
        String appSettingsStr = new String(getPlatypusSettingsFileObject().asBytes(), PlatypusUtils.COMMON_ENCODING_NAME);
        if (!appSettingsStr.trim().isEmpty()) {
            Document doc = Source2XmlDom.transform(appSettingsStr);
            platypusSettings = PlatypusSettings.valueOf(doc);
        } else {
            platypusSettings = new PlatypusSettings();
        }
        platypusSettings.getChangeSupport().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                platypusSettingsIsDirty = true;
                changeSupport.firePropertyChange(evt);
            }
        });
        projectProperties = new EditableProperties(false);
        try (InputStream is = getProjectSettingsFileObject().getInputStream()) {
            projectProperties.load(is);
        }
        projectPrivateProperties = new EditableProperties(false);
        try (InputStream is = getProjectPrivateSettingsFileObject().getInputStream()) {
            projectPrivateProperties.load(is);
        }
    }

    @Override
    public PlatypusSettings getAppSettings() {
        return platypusSettings;
    }

    /**
     * Gets the project's display name.
     *
     * @return title for the project
     */
    @Override
    public String getDisplayName() {
        return projectProperties.get(PROJECT_DISPLAY_NAME_KEY);
    }

    /**
     * Sets the project's display name.
     *
     * @param aValue title for the project
     */
    @Override
    public void setDisplayName(String aValue) {
        if (aValue == null) {
            throw new NullPointerException("The Display name parameter cannot be null."); // NOI18N
        }
        String oldValue = getDisplayName();
        projectProperties.setProperty(PROJECT_DISPLAY_NAME_KEY, aValue);
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(PROJECT_DISPLAY_NAME_KEY, oldValue, aValue);
    }

    /**
     * Gets username for the Platypus user to login on application run.
     *
     * @return Platypus user name
     */
    @Override
    public String getRunUser() {
        return projectPrivateProperties.get(RUN_USER_KEY);
    }

    /**
     * Sets username for the Platypus user to login on application run.
     *
     * @param aValue Platypus user name
     */
    @Override
    public void setRunUser(String aValue) {
        String oldValue = getRunUser();
        if (aValue != null) {
            projectPrivateProperties.setProperty(RUN_USER_KEY, aValue);
        } else {
            projectPrivateProperties.remove(RUN_USER_KEY);
        }
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(RUN_USER_KEY, oldValue, aValue);
    }

    /**
     * Gets password for the Platypus user to login on application run.
     *
     * @return Platypus user name
     */
    @Override
    public String getRunPassword() {
        return projectPrivateProperties.get(RUN_PASSWORD_KEY);
    }

    /**
     * Sets password for the Platypus user to login on application run.
     *
     * @param aValue Platypus user name
     */
    @Override
    public void setRunPassword(String aValue) {
        String oldValue = getRunPassword();
        if (aValue != null) {
            projectPrivateProperties.setProperty(RUN_PASSWORD_KEY, aValue);
        } else {
            projectPrivateProperties.remove(RUN_PASSWORD_KEY);
        }
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(RUN_PASSWORD_KEY, oldValue, aValue);
    }

    /**
     * Gets optional parameters provided to Platypus Client.
     *
     * @return parameters string
     */
    @Override
    public String getRunClientOptions() {
        return projectPrivateProperties.get(RUN_CLIENT_OPTIONS_KEY);
    }

    /**
     * Sets optional parameters provided to Platypus Client.
     *
     * @param aValue
     */
    @Override
    public void setClientOptions(String aValue) {
        String oldValue = getRunClientOptions();
        if (aValue != null) {
            projectPrivateProperties.setProperty(RUN_CLIENT_OPTIONS_KEY, aValue);
        } else {
            projectPrivateProperties.remove(RUN_CLIENT_OPTIONS_KEY);
        }
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(RUN_CLIENT_OPTIONS_KEY, oldValue, aValue);
    }

    /**
     * Gets JVM options provided to Platypus Client.
     *
     * @return parameters string
     */
    @Override
    public String getRunClientVmOptions() {
        return projectPrivateProperties.get(RUN_CLIENT_VM_OPTIONS_KEY);
    }

    /**
     * Sets JVM options provided to Platypus Client.
     *
     * @param aValue
     */
    @Override
    public void setClientVmOptions(String aValue) {
        String oldValue = getRunClientVmOptions();
        if (aValue != null) {
            projectPrivateProperties.setProperty(RUN_CLIENT_VM_OPTIONS_KEY, aValue);
        } else {
            projectPrivateProperties.remove(RUN_CLIENT_VM_OPTIONS_KEY);
        }
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(RUN_CLIENT_VM_OPTIONS_KEY, oldValue, aValue);
    }

    /**
     * Gets optional parameters provided to Platypus Application Server.
     *
     * @return parameters string
     */
    @Override
    public String getRunServerOptions() {
        return projectPrivateProperties.get(RUN_SERVER_OPTIONS_KEY);
    }

    /**
     * Sets optional parameters provided to Platypus Application Server.
     *
     * @param aValue
     */
    @Override
    public void setServerOptions(String aValue) {
        String oldValue = getRunServerOptions();
        if (aValue != null) {
            projectPrivateProperties.setProperty(RUN_SERVER_OPTIONS_KEY, aValue);
        } else {
            projectPrivateProperties.remove(RUN_SERVER_OPTIONS_KEY);
        }
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(RUN_SERVER_OPTIONS_KEY, oldValue, aValue);
    }

    /**
     * Gets JVM options provided to Platypus Application Server.
     *
     * @return parameters string
     */
    @Override
    public String getRunServerVmOptions() {
        return projectPrivateProperties.get(RUN_SERVER_VM_OPTIONS_KEY);
    }

    /**
     * Sets JVM options provided to Platypus Application Server.
     *
     * @param aValue
     */
    @Override
    public void setServerVmOptions(String aValue) {
        String oldValue = getRunServerVmOptions();
        if (aValue != null) {
            projectPrivateProperties.setProperty(RUN_SERVER_VM_OPTIONS_KEY, aValue);
        } else {
            projectPrivateProperties.remove(RUN_SERVER_VM_OPTIONS_KEY);
        }
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(RUN_SERVER_VM_OPTIONS_KEY, oldValue, aValue);
    }

    /**
     * Checks if runtime to use application from database.
     *
     * @return true if run application from database
     */
    @Override
    public boolean isDbAppSources() {
        return Boolean.valueOf(projectPrivateProperties.get(DB_APP_SOURCES_KEY));
    }

    /**
     * Sets flag for runtime to use application from database.
     *
     * @param aValue true if run application from database
     */
    @Override
    public void setDbAppSources(boolean aValue) {
        boolean oldValue = isDbAppSources();
        projectPrivateProperties.setProperty(DB_APP_SOURCES_KEY, Boolean.valueOf(aValue).toString());
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(DB_APP_SOURCES_KEY, oldValue, aValue);
    }

    /**
     * Gets application server's host.
     *
     * @return Url string
     */
    @Override
    public String getClientUrl() {
        return projectPrivateProperties.get(CLIENT_URL_KEY);
    }

    /**
     * Sets application's server host.
     *
     * @param aValue Url string
     */
    @Override
    public void setClientUrl(String aValue) {
        String oldValue = getClientUrl();
        if (aValue != null) {
            projectPrivateProperties.setProperty(CLIENT_URL_KEY, aValue);
        } else {
            projectPrivateProperties.remove(CLIENT_URL_KEY);
        }
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(CLIENT_URL_KEY, oldValue, aValue);
    }

    /**
     * Gets application's server port.
     *
     * @return server port
     */
    @Override
    public int getServerPort() {
        return StringUtils.parseInt(projectPrivateProperties.get(SERVER_PORT_KEY), DEFAULT_PLATYPUS_SERVER_PORT);
    }

    /**
     * Sets application's server port.
     *
     * @param aValue server port
     */
    @Override
    public void setServerPort(int aValue) {
        int oldValue = getServerPort();
        projectPrivateProperties.setProperty(SERVER_PORT_KEY, String.valueOf(aValue));
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(SERVER_PORT_KEY, Integer.valueOf(oldValue), Integer.valueOf(aValue));
    }

    /**
     * Checks if NOT to start local development application server on
     * application run.
     *
     * @return true not to start server
     */
    @Override
    public boolean isNotStartServer() {
        return Boolean.valueOf(projectPrivateProperties.get(NOT_START_SERVER_KEY));
    }

    /**
     * Sets flag NOT to start local development application server on
     * application run.
     *
     * @param aValue true not to start server
     */
    @Override
    public void setNotStartServer(boolean aValue) {
        boolean oldValue = isNotStartServer();
        projectPrivateProperties.setProperty(NOT_START_SERVER_KEY, String.valueOf(aValue));
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(NOT_START_SERVER_KEY, oldValue, aValue);
    }

    /**
     * Gets JMX debugging port for Platypus Client on local computer on
     * development if null or empty, use default value.
     *
     * @return JMX debugging port
     */
    @Override
    public int getDebugClientPort() {
        return StringUtils.parseInt(projectPrivateProperties.get(DEBUG_CLIENT_PORT_KEY), CLIENT_APP_DEFAULT_DEBUG_PORT);
    }

    /**
     * Sets JMX debugging port for Platypus Client on local computer on
     * development.
     *
     * @param aValue JMX debugging port
     */
    @Override
    public void setDebugClientPort(int aValue) {
        int oldValue = getDebugClientPort();
        projectPrivateProperties.setProperty(DEBUG_CLIENT_PORT_KEY, String.valueOf(aValue));
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(DEBUG_CLIENT_PORT_KEY, Integer.valueOf(oldValue), Integer.valueOf(aValue));
    }

    /**
     * Gets JMX debugging port for Platypus Application Server on local computer
     * on development if null or empty, use default value.
     *
     * @return JMX debugging port
     */
    @Override
    public int getDebugServerPort() {
        return StringUtils.parseInt(projectPrivateProperties.get(DEBUG_SERVER_PORT_KEY), SERVER_APP_DEFAULT_DEBUG_PORT);
    }

    /**
     * Sets JMX debugging port for Platypus Application Server on local computer
     * on development.
     *
     * @param aValue JMX debugging port
     */
    @Override
    public void setDebugServerPort(int aValue) {
        int oldValue = getDebugServerPort();
        projectPrivateProperties.setProperty(DEBUG_SERVER_PORT_KEY, String.valueOf(aValue));
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(DEBUG_SERVER_PORT_KEY, Integer.valueOf(oldValue), Integer.valueOf(aValue));
    }

    /**
     * Gets J2EE server instance ID.
     *
     * @return J2EE server ID
     */
    @Override
    public String getJ2eeServerId() {
        return projectPrivateProperties.get(J2EE_SERVER_ID_KEY);
    }

    /**
     * Sets J2EE server instance ID.
     *
     * @param aValue J2EE server ID
     */
    @Override
    public void setJ2eeServerId(String aValue) {
        String oldValue = getJ2eeServerId();
        if (aValue != null) {
            projectPrivateProperties.setProperty(J2EE_SERVER_ID_KEY, aValue);
        } else {
            projectPrivateProperties.remove(J2EE_SERVER_ID_KEY);
        }
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(J2EE_SERVER_ID_KEY, aValue, oldValue);
    }

    /**
     * Gets application's context name.
     *
     * @return The name of the context string
     */
    @Override
    public String getServerContext() {
        return projectProperties.get(SERVER_CONTEXT_KEY);
    }

    /**
     * Sets application's context name.
     *
     * @param aValue The name of the context string
     */
    @Override
    public void setServerContext(String aValue) {
        String oldValue = getServerContext();
        if (aValue != null) {
            projectProperties.setProperty(SERVER_CONTEXT_KEY, aValue);
        } else {
            projectProperties.remove(SERVER_CONTEXT_KEY);
        }
        projectPropertiesIsDirty = true;
        changeSupport.firePropertyChange(SERVER_CONTEXT_KEY, oldValue, aValue);
    }

    /**
     * Checks if security realm to be configured on J2EE server startup.
     *
     * @return true to enable configure security realm
     */
    @Override
    public boolean isWebSecurityEnabled() {
        return Boolean.valueOf(projectPrivateProperties.get(ENABLE_SECURITY_REALM_KEY));
    }

    /**
     * Sets if security realm to be configured on J2EE server startup.
     *
     * @param aValue true to enable configure security realm
     */
    @Override
    public void setSecurityRealmEnabled(boolean aValue) {
        boolean oldValue = isWebSecurityEnabled();
        projectPrivateProperties.setProperty(ENABLE_SECURITY_REALM_KEY, Boolean.valueOf(aValue).toString());
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(ENABLE_SECURITY_REALM_KEY, oldValue, aValue);
    }

    /**
     * Gets client type to be run.
     *
     * @return ClientType instance
     */
    @Override
    public ClientType getRunClientType() {
        ClientType val = ClientType.getById(projectPrivateProperties.get(CLIENT_TYPE_KEY));
        return val != null ? val : ClientType.PLATYPUS_CLIENT;
    }

    /**
     * Sets client type to be run.
     *
     * @param aValue ClientType instance
     */
    @Override
    public void setRunClientType(ClientType aValue) {
        ClientType oldValue = getRunClientType();
        if (aValue != null) {
            projectPrivateProperties.setProperty(CLIENT_TYPE_KEY, aValue.getId());
        } else {
            projectPrivateProperties.remove(CLIENT_TYPE_KEY);
        }
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(CLIENT_TYPE_KEY, aValue, oldValue);
    }

    /**
     * Gets application server type to be run.
     *
     * @return AppServerType instance
     */
    @Override
    public AppServerType getRunAppServerType() {
        AppServerType val = AppServerType.getById(projectPrivateProperties.get(SERVER_TYPE_KEY));
        return val != null ? val : AppServerType.NONE;
    }

    /**
     * Sets application server type to be run.
     *
     * @param aValue AppServerType instance
     */
    @Override
    public void setRunAppServerType(AppServerType aValue) {
        AppServerType oldValue = getRunAppServerType();
        if (aValue != null) {
            projectPrivateProperties.setProperty(SERVER_TYPE_KEY, aValue.getId());
        } else {
            projectPrivateProperties.remove(SERVER_TYPE_KEY);
        }
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(SERVER_TYPE_KEY, aValue, oldValue);
    }

    @Override
    public void save() throws Exception {
        if (platypusSettingsIsDirty) {
            try (OutputStream os = getPlatypusSettingsFileObject().getOutputStream()) {
                String sData = XmlDom2String.transform(platypusSettings.toDocument());
                os.write(sData.getBytes(PlatypusUtils.COMMON_ENCODING_NAME));
            }
            platypusSettingsIsDirty = false;
        }
        if (projectPropertiesIsDirty) {
            try (OutputStream os = getProjectSettingsFileObject().getOutputStream()) {
                projectProperties.store(os);
            }
            projectPropertiesIsDirty = false;
        }
        if (projectPrivatePropertiesIsDirty) {
            try (OutputStream os = getProjectPrivateSettingsFileObject().getOutputStream()) {
                projectPrivateProperties.store(os);
            }
            projectPrivatePropertiesIsDirty = false;
        }
    }

    @Override
    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    protected final FileObject getPlatypusSettingsFileObject() throws IOException {
        FileObject fo = projectDir.getFileObject(BaseDeployer.PLATYPUS_SETTINGS_FILE);
        if (fo == null) {
            fo = projectDir.createData(BaseDeployer.PLATYPUS_SETTINGS_FILE);
        }
        return fo;
    }

    protected final FileObject getProjectSettingsFileObject() throws IOException {
        FileObject fo = projectDir.getFileObject(PROJECT_SETTINGS_FILE);
        if (fo == null) {
            fo = projectDir.createData(PROJECT_SETTINGS_FILE);
        }
        return fo;
    }

    protected final FileObject getProjectPrivateSettingsFileObject() throws IOException {
        FileObject fo = projectDir.getFileObject(PROJECT_PRIVATE_SETTINGS_FILE);
        if (fo == null) {
            fo = projectDir.createData(PROJECT_PRIVATE_SETTINGS_FILE);
        }
        return fo;
    }

    /**
     * Gets the log level for Platypus Client.
     * @return Log level value
     */
    @Override
    public Level getClientLogLevel() {
        String logLevel = projectPrivateProperties.get(CLIENT_LOG_LEVEL);
        if (logLevel == null || logLevel.isEmpty()) {
            return DEFAULT_LOG_LEVEL;
        }
        try {
            return Level.parse(logLevel);
        } catch (IllegalArgumentException ex) {
            return DEFAULT_LOG_LEVEL;
        }
    }

    /**
      * Sets a log level for Platypus Client.
      * @param aValue Log level value
      */
    @Override
    public void setClientLogLevel(Level aValue) {
        Level oldValue = getClientLogLevel();
        if (aValue != null) {
            projectPrivateProperties.setProperty(CLIENT_LOG_LEVEL, aValue.getName());
        } else {
            projectPrivateProperties.remove(CLIENT_LOG_LEVEL);
        }
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(CLIENT_LOG_LEVEL, aValue, oldValue);
    }

    /**
     * Gets the log level for Platypus Server.
     * @return Log level value
     */
    @Override
    public Level getServerLogLevel() {
        String logLevel = projectPrivateProperties.get(SERVER_LOG_LEVEL);
        if (logLevel == null || logLevel.isEmpty()) {
            return DEFAULT_LOG_LEVEL;
        }
        try {
            return Level.parse(logLevel);
        } catch (IllegalArgumentException ex) {
            return DEFAULT_LOG_LEVEL;
        }
    }

    /**
      * Sets a log level for Platypus Server.
      * @param aValue Log level value
      */
    @Override
    public void setServerLogLevel(Level aValue) {
        Level oldValue = getServerLogLevel();
        if (aValue != null) {
            projectPrivateProperties.setProperty(SERVER_LOG_LEVEL, aValue.getName());
        } else {
            projectPrivateProperties.remove(SERVER_LOG_LEVEL);
        }
        projectPrivatePropertiesIsDirty = true;
        changeSupport.firePropertyChange(SERVER_LOG_LEVEL, aValue, oldValue);
    }
}