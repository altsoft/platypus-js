/* Datamodel license
 * Exclusive rights on this code in any form
 * are belong to it's athor.
 * This code was developed for commercial purposes only 
 * For any questions and any actions with this code in any form
 * you have to contact it's athor.
 * All rights reserved
 */
package com.eas.client.settings;

import com.bearsoft.rowset.resourcepool.BearResourcePool;
import com.eas.client.ConnectionSettingsVisitor;
import com.eas.xml.dom.Source2XmlDom;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class DbConnectionSettings extends ConnectionSettings {

    protected int maxConnections = BearResourcePool.DEFAULT_MAXIMUM_SIZE;
    protected int maxStatements = BearResourcePool.DEFAULT_MAXIMUM_SIZE * 5;
    protected int resourceTimeout = BearResourcePool.WAIT_TIMEOUT;
    protected String schema;
    protected Properties props = new Properties();

    public DbConnectionSettings() {
        super();
    }

    public DbConnectionSettings(String anUrl, String anUser, String aPassword) throws Exception {
        this(anUrl, anUser, aPassword, null, null);
    }

    public DbConnectionSettings(String anUrl, String anUser, String aPassword, String aSchema, Properties aProperties) throws Exception {
        this();
        url = anUrl;
        user = anUser;
        password = aPassword;
        schema = aSchema;
        if (aProperties != null) {
            props.putAll(aProperties);
        }
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String aValue) {
        schema = aValue;
    }

    public Properties getProperties() {
        return props;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int aMaxConnections) {
        maxConnections = aMaxConnections;
    }

    public int getMaxStatements() {
        return maxStatements;
    }

    public void setMaxStatements(int aMaxStatements) {
        maxStatements = aMaxStatements;
    }

    public int getResourceTimeout() {
        return resourceTimeout;
    }

    public void setResourceTimeout(int aResourceTimeout) {
        resourceTimeout = aResourceTimeout;
    }

    @Override
    public void accept(ConnectionSettingsVisitor v) {
        v.visit(this);
    }

    public static DbConnectionSettings read(Document doc) throws Exception {
        if (doc != null) {
            ConnectionSettings lsettings = XmlDom2ConnectionSettings.document2Settings(doc);
            if (lsettings instanceof DbConnectionSettings) {
                return (DbConnectionSettings) lsettings;
            }
        }
        return null;
    }

    public static DbConnectionSettings read(String aContent) throws Exception {
        if (aContent != null && !aContent.isEmpty()) {
            return read(Source2XmlDom.transform(aContent));
        } else {
            return null;
        }
    }

    public static DbConnectionSettings read(Reader aContentReader) throws IOException, Exception {
        if (aContentReader != null && aContentReader.ready()) {
            Document doc = Source2XmlDom.transform(aContentReader);
            ConnectionSettings lsettings = XmlDom2ConnectionSettings.document2Settings(doc);
            if (lsettings instanceof DbConnectionSettings) {
                return (DbConnectionSettings) lsettings;
            }
        }
        return null;
    }
}
