/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.ExecuteServerReportRequest;
import com.eas.script.JsDoc;
import com.eas.server.PlatypusServerCore;
import com.eas.server.ServerReportRunner;
import com.eas.server.ServerScriptRunner;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;
import java.security.AccessControlException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pk
 */
public class ExecuteServerReportRequestHandler extends SessionRequestHandler<ExecuteServerReportRequest> {

    public static final String EXECUTE_REPORT_MSG = "Executing report {0}";
    public static final String MODEL_SAVE_ERROR_MSG = "While attempting to save model of unactual server report %s";
    public static final String MODULE_MISSING_MSG = "No report %s";
    public static final String MODULE_NOT_REPORT_MSG = "Module %s is not a report";
    public static final String RERUN_MSG = "About to re-run server report {0}";

    public ExecuteServerReportRequestHandler(PlatypusServerCore aServerCore, Session aSession, ExecuteServerReportRequest aRequest) {
        super(aServerCore, aSession, aRequest);
    }

    @Override
    public Response handle2() throws Exception {
        Session systemSession = getServerCore().getSessionManager().getSystemSession();
        Session moduleSession = null;
        String moduleName = getRequest().getModuleName();
        ServerScriptRunner srunner = getSession().getModule(moduleName);
        if (srunner == null) {
            // It's seems client wants resident module.
            // Let's try to look it up in system session.
            srunner = systemSession.getModule(getRequest().getModuleName());
            if (srunner != null) {
                moduleSession = systemSession;
            }
        } else {
            moduleSession = getSession();
        }

        if (srunner == null) {
            srunner = CreateServerModuleRequestHandler.runModule(getServerCore(), getSession(), moduleName);
            moduleSession = getSession();
        }
        if (srunner != null) {
            assert moduleSession != null;
            if (srunner instanceof ServerReportRunner) {
                ServerReportRunner runner = (ServerReportRunner) srunner;
                synchronized (runner) {// Same synchronization object as in module method executing code
                    if (!getServerCore().getDatabasesClient().getAppCache().isActual(runner.getApplicationElementId(), runner.getTxtContentLength(), runner.getTxtCrc32())) {
                        Logger.getLogger(ExecuteServerReportRequest.class.getName()).log(Level.FINE, RERUN_MSG, new Object[]{getRequest().getModuleName()});
                        try {
                            runner.getModel().save();
                        } catch (Exception ex) {
                            Logger.getLogger(ExecuteServerReportRequest.class.getName()).log(Level.SEVERE, String.format(MODEL_SAVE_ERROR_MSG, getRequest().getModuleName()), ex);
                        }
                        runner.refresh();
                    }
                    if (!runner.hasModuleAnnotation(JsDoc.Tag.PUBLIC_TAG)) {
                        throw new AccessControlException(String.format("Public access to report %s is denied.", moduleName));//NOI18N
                    }
                    runner.checkPrincipalPermission();// This call is here because of executing of the report is done here, rather than server module method wich permissions are checked while method call. 
                    if (getRequest().getArguments() != null) {
                        for (ExecuteServerReportRequest.NamedArgument arg : getRequest().getArguments()) {
                            runner.setValue(arg.getName(), arg.getValue());
                        }
                    }
                    Logger.getLogger(ExecuteServerReportRequest.class.getName()).log(Level.FINE, EXECUTE_REPORT_MSG, new Object[]{getRequest().getModuleName()});
                    byte[] result = runner.executeReport();
                    if (moduleSession != systemSession
                            && runner.hasModuleAnnotation(JsDoc.Tag.STATELESS_TAG)) {// reports may be stateless like other server modules, but system session.
                        moduleSession.unregisterModule(moduleName);
                    }
                    return new ExecuteServerReportRequest.Response(getRequest().getID(), result, runner.getFormat());
                }
            } else {
                throw new IllegalStateException(String.format(MODULE_NOT_REPORT_MSG, getRequest().getModuleName()));
            }
        } else {
            throw new Exception(String.format(MODULE_MISSING_MSG, getRequest().getModuleName()));
        }
    }
}
