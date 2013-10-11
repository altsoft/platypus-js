/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import com.eas.debugger.jmx.server.BreakpointsMBean;
import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.designer.application.indexer.PlatypusPathRecognizer;
import java.io.IOException;
import java.util.concurrent.Future;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.netbeans.api.debugger.ActionsManager;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerEngine.Destructor;
import org.netbeans.api.debugger.DebuggerInfo;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.Line;

/**
 *
 * @author mg
 */
public class DebuggerUtils {

    public static void attachDebugger(DebuggerEnvironment env) throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + env.host + ":" + String.valueOf(env.port) + "/jmxrmi");
        ObjectName mBeanDebuggerName = new ObjectName(DebuggerMBean.DEBUGGER_MBEAN_NAME);
        ObjectName mBeanBreakpointsName = new ObjectName(BreakpointsMBean.BREAKPOINTS_MBEAN_NAME);
        MBeanDebuggerListener listener = new MBeanDebuggerListener(env.project);

        JMXConnector jmxc = null;
        MBeanServerConnection jmxConnection = null;
        DebuggerMBean debugger = null;
        BreakpointsMBean breakpoints = null;
        // let's wait until program register it's mbeans.
        int ioCounter = 0;
        while (true) {
            try {
                jmxc = JMXConnectorFactory.connect(url, null);
                jmxConnection = jmxc.getMBeanServerConnection();
                jmxConnection.addNotificationListener(mBeanDebuggerName, listener, null, null);
                debugger = JMX.newMBeanProxy(jmxConnection, mBeanDebuggerName, DebuggerMBean.class);
                breakpoints = JMX.newMBeanProxy(jmxConnection, mBeanBreakpointsName, BreakpointsMBean.class);
                break;
            } catch (InstanceNotFoundException ex) {
                Thread.sleep(500);
            } catch (IOException ex) {
                if (ioCounter <= 16) {
                    ioCounter++;
                    Thread.sleep(500);
                } else {
                    throw ex;
                }
            }
        }
        env.mBreakpoints = breakpoints;
        env.mDebugger = debugger;
        env.mDebuggerListener = listener;
        DebuggerInfo di = DebuggerInfo.create(DebuggerConstants.DEBUGGER_SERVICERS_PATH, new Object[]{env, jmxConnection});
        DebuggerEngine[] dEngines = DebuggerManager.getDebuggerManager().startDebugging(di);
        if (env.runningProgram != null) {
            startProcessWaiting(env.runningProgram, dEngines);
        } else {
            startDebugging(env);
            listener.debuggingStarted = true;
        }
    }

    public static void startDebugging(DebuggerEnvironment env) throws Exception {
        FileObject file = env.mDebuggerListener.getCurrentAppFile();
        int lineNumber = env.mDebuggerListener.getCurrentLineNumber();
        boolean haveBreakpoint = false;
        Breakpoint[] breaks = DebuggerManager.getDebuggerManager().getBreakpoints();
        for (Breakpoint breakPoint : breaks) {
            if (breakPoint instanceof PlatypusBreakpoint) {
                PlatypusBreakpoint pBreak = (PlatypusBreakpoint) breakPoint;
                Line line = pBreak.getLine();
                FileObject bFile = line.getLookup().lookup(FileObject.class);
                if (lineNumber == line.getLineNumber()
                        && bFile == file) {
                    haveBreakpoint = true;
                    break;
                }
                pBreak.remoteAdd(env.mBreakpoints);
            }
        }
        // if the debugger has stopped the program, but breakpoint is absent, we have to run program.
        if (env.runningProgram != null && !env.mDebuggerListener.isRunning() && !haveBreakpoint) {
            env.mDebuggerListener.cancelStoppedAnnotation();
            env.mDebugger.continueRun();
        }
    }
    
    public static void killEngine(DebuggerEngine engine) throws Exception {
        DebuggerEnvironment env = engine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class);
        if (env.runningProgram == null) {// Debugger was attached to external program
            if (!env.mDebuggerListener.positionedOnSource()) {
                for (Breakpoint breakpoint : DebuggerManager.getDebuggerManager().getBreakpoints()) {
                    if (breakpoint instanceof PlatypusBreakpoint) {
                        PlatypusBreakpoint pbreak = (PlatypusBreakpoint) breakpoint;
                        pbreak.remoteRemove(env.mBreakpoints);
                    }
                }
                env.mDebugger.continueRun();
            }
        }
        Destructor d = engine.new Destructor();
        d.killEngine();
    }
    
    public static void startProcessWaiting(final Future<Integer> runningProgram, final DebuggerEngine[] dEngines) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runningProgram.get();
                } catch (Exception ex) {
                    // no op
                }
                for (DebuggerEngine engine : dEngines) {
                    Destructor d = engine.new Destructor();
                    d.killEngine();
                }
            }
        });
        thread.start();
    }

    public static String getUrlAsRelativePath(FileObject sourceFile) {
        Project p = FileOwnerQuery.getOwner(sourceFile);
        FileObject rootFileObject = getSourcesRoot(p);
        assert FileUtil.isParentOf(rootFileObject, sourceFile) : String.format("Source file %s root is not found in project's source roots.", sourceFile.getPath());
        return FileUtil.getRelativePath(rootFileObject, sourceFile);
    }

    public static FileObject getFileObjectByUrl(Project project, String url) {
        FileObject rootFileObject = getSourcesRoot(project);
        return rootFileObject.getFileObject(url);
    }
    
    private static FileObject getSourcesRoot(Project project) {
        ClassPathProvider cpp = project.getLookup().lookup(ClassPathProvider.class);
        if (cpp != null) {
            FileObject[] roots = cpp.findClassPath(null, PlatypusPathRecognizer.SOURCE_CP).getRoots();
            if (roots.length != 1) {
                throw new IllegalStateException("Only one root supported for now.");
            }
            return roots[0];
        } else {
            throw new IllegalStateException("ClassPathProvider is not found in project's lookup.");
        }
    }
}
