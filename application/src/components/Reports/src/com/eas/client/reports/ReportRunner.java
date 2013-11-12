/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.bearsoft.rowset.compacts.CompactBlob;
import com.eas.client.Client;
import com.eas.client.events.ScriptSourcedEvent;
import com.eas.client.login.PrincipalHost;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.ScriptRunner;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class ReportRunner extends ScriptRunner {

    public static final String BEFORE_RENDER_HANDLER_NAME = "onBeforeRender"; //NOI18N
    protected byte[] template;
    private Function onBeforRender;
    protected String format;
    

    public ReportRunner(String aReportId, Client aClient, Scriptable aScope, PrincipalHost aPrincipalHost, CompiledScriptDocumentsHost aCompiledScriptDocumentsHost, Object[] args) throws Exception {
        super(aReportId, aClient, aScope, aPrincipalHost, aCompiledScriptDocumentsHost, args);
        setPrototype(ReportRunnerPrototype.getInstance());
    }

    @Override
    protected void prepare(ScriptDocument scriptDoc, Object[] args) throws Exception {
        assert scriptDoc instanceof ReportDocument;
        template = ((ReportDocument) scriptDoc).getTemplate();
        format = ((ReportDocument) scriptDoc).getFormat();
        super.prepare(scriptDoc, args);
    }

    @Override
    protected void shrink() throws Exception {
        template = null;
        super.shrink();
    }

    @Override
    public String getClassName() {
        return ReportRunner.class.getName();
    }

    @Override
    protected void definePropertiesAndMethods() {
        super.definePropertiesAndMethods();
        defineFunctionProperties(new String[]{
                    "show",
                    "print",
                    "save"}, ReportRunner.class, EMPTY);
        defineProperty(BEFORE_RENDER_HANDLER_NAME, ReportRunner.class, EMPTY);
    }
    private static final String SHOW_JSDOC = ""
            + "/**\n"
            + " * Shows report as Excel application.\n"
            + " */";

    @ScriptFunction(jsDoc = SHOW_JSDOC)
    public void show() throws Exception {
        if (template != null) {
            execute();
            preRender();
            ExcelReport xlsReport = new ExcelReport(model, this);
            xlsReport.setTemplate(new CompactBlob(template));
            xlsReport.show();
        }
    }
    private static final String PRINT_JSDOC = ""
            + "/**\n"
            + " * Runs printing.\n"
            + " */";

    @ScriptFunction(jsDoc = PRINT_JSDOC)
    public void print() throws Exception {
        if (template != null) {
            execute();
            preRender();
            ExcelReport xlsReport = new ExcelReport(model, this);
            xlsReport.setTemplate(new CompactBlob(template));
            xlsReport.print();
        }
    }

    public void save(String aFileName) throws Exception {
        if (template != null) {
            execute();
            preRender();
            ExcelReport xlsReport = new ExcelReport(model, this);
            xlsReport.setTemplate(new CompactBlob(template));
            xlsReport.save(aFileName);
        }
    }

    /**
     * Invokes handler for the Report pre-render event
     */
    private void preRender() {
        Context cx = ScriptUtils.enterContext();
        try {
            Function preRendreHandler = getOnBeforeRender();
            if (preRendreHandler != null) {
                preRendreHandler.call(cx, this, this, new Object[]{ Context.javaToJS(new ScriptSourcedEvent(this), this)});
            }
        } finally {
            Context.exit();
        }
    }
    
    @ScriptFunction
    public Function getOnBeforeRender() {
        return onBeforRender;
    }

    @ScriptFunction
    public void setOnBeforeRender(Function aValue) {
        onBeforRender = aValue;
    }
}
