/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer;

import com.eas.client.DbClient;
import com.eas.designer.application.HandlerRegistration;
import com.eas.designer.application.project.PlatypusProject;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.RequestProcessor;

/**
 * Base class for Platypus Data Objects.
 *
 * @author vv
 */
public abstract class PlatypusDataObject extends MultiDataObject {

    private static RequestProcessor RP = new RequestProcessor(PlatypusDataObject.class.getName(), 10);
    private Set<Runnable> clientListeners = new HashSet<>();
    protected HandlerRegistration projectClientListener;
    protected DbClient.QueriesListener.Registration projectClientQueriesListener;

    public PlatypusDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException {
        super(pf, loader);
        if (getProject() != null) {
            projectClientListener = getProject().addClientChangeListener(new Runnable() {
                @Override
                public void run() {
                    clientChanged();
                    signOnQueries();
                    fireClientChanged();
                }
            });
            signOnQueries();
        }
    }

    /**
     * WARNING!!! This method is executed in a separate thread.
     * @throws java.lang.Exception
     */
    protected abstract void validateModel() throws Exception;

    protected void unsignFromQueries(){
        if (projectClientQueriesListener != null) {
            projectClientQueriesListener.remove();
            projectClientQueriesListener = null;
        }
    }
    
    protected void signOnQueries() {
        unsignFromQueries();
        if (getClient() != null) {
            projectClientQueriesListener = getClient().addQueriesListener(new DbClient.QueriesListener() {
                @Override
                public void cleared() {
                    if (isModelValid()) {
                        setModelValid(false);
                        startModelValidating();
                    }
                }
            });
        }
    }
    protected boolean validationStarted;

    protected void startModelValidating() {
        if (!isModelValid() && !validationStarted) {
            validationStarted = true;
            RP.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        validateModel();
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusDataObject.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
                    } finally {
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                validationStarted = false;
                                setModelValid(true);
                            }
                        });
                    }
                }
            });
        }
    }

    protected abstract void clientChanged();

    @Override
    protected void handleDelete() throws IOException {
        dispose();
        super.handleDelete();
    }

    @Override
    protected void dispose() {
        if (projectClientListener != null) {
            projectClientListener.remove();
            projectClientListener = null;
        }
        if (projectClientQueriesListener != null) {
            projectClientQueriesListener.remove();
            projectClientQueriesListener = null;
        }
        super.dispose();
    }

    public HandlerRegistration addClientChangeListener(final Runnable onChange) {
        clientListeners.add(onChange);
        return new HandlerRegistration() {
            @Override
            public void remove() {
                clientListeners.remove(onChange);
            }
        };
    }

    private void fireClientChanged() {
        for (Runnable onChange : clientListeners.toArray(new Runnable[]{})) {
            onChange.run();
        }
    }

    public final PlatypusProject getProject() {
        Project pr = FileOwnerQuery.getOwner(getPrimaryFile());
        if (pr instanceof PlatypusProject) {
            return (PlatypusProject) pr;
        } else {
            return null;
        }
    }

    public DbClient getClient() {
        PlatypusProject project = getProject();
        if (project != null) {
            return project.getClient();
        }
        return null;
    }
    private Set<Runnable> modelValidListeners = new HashSet<>();
    protected boolean modelValid = true;

    public boolean isModelValid() {
        return modelValid;
    }

    public void setModelValid(boolean aValue) {
        boolean oldValue = modelValid;
        modelValid = aValue;
        if (oldValue != modelValid) {
            fireModelValidChanged();
        }
    }

    public HandlerRegistration addModelValidChangeListener(final Runnable onChange) {
        modelValidListeners.add(onChange);
        return new HandlerRegistration() {
            @Override
            public void remove() {
                modelValidListeners.remove(onChange);
            }
        };
    }

    private void fireModelValidChanged() {
        for (Runnable onChange : modelValidListeners.toArray(new Runnable[]{})) {
            onChange.run();
        }
    }

    public FileObject getAppRoot() throws Exception {
        PlatypusProject project = getProject();
        if (project != null) {
            return project.getSrcRoot();
        } else {
            return null;
        }
    }
}
