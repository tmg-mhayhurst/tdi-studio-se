// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.ui.swt.utils;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.ui.swt.dialogs.ErrorDialogWidthDetailArea;
import org.talend.core.CorePlugin;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.language.LanguageManager;
import org.talend.core.model.metadata.IMetadataContextModeManager;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.connection.SalesforceSchemaConnection;
import org.talend.core.model.process.AbstractNode;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.prefs.ITalendCorePrefConstants;
import org.talend.repository.RepositoryPlugin;
import org.talend.repository.i18n.Messages;
import org.talend.repository.ui.wizards.metadata.connection.files.salesforce.SalesforceModuleParseAPI;

import com.sforce.soap.enterprise.DescribeGlobalResult;
import com.sforce.soap.enterprise.SoapBindingStub;
import com.sforce.soap.enterprise.fault.UnexpectedErrorFault;

/**
 * DOC YeXiaowei class global comment. Detailled comment <br/>
 * 
 */
public abstract class AbstractSalesforceStepForm extends AbstractForm {

    protected int maximumRowsToPreview = CorePlugin.getDefault().getPreferenceStore().getInt(
            ITalendCorePrefConstants.PREVIEW_LIMIT);

    protected SalesforceSchemaConnection connection;

    protected AbstractNode fakeSalesforceNode = null;

    private final String tSalesforceUniqueName = "tSalesforceInput"; //$NON-NLS-1$

    private SalesforceModuleParseAPI salesforceAPI = null;

    private IMetadataContextModeManager contextModeManager;

    private SoapBindingStub binding = null;

    public static final String TSALESFORCE_INPUT_URL = "https://www.salesforce.com/services/Soap/u/10.0"; //$NON-NLS-1$

    public static final String TSALESFORCE_PARTNER_INPUT_URL = "https://test.salesforce.com/services/Soap/u/10.0"; //$NON-NLS-1$

    // note that tSalesforceInput use a different url, if the web service is called by wizard we should use
    // DEFAULT_WEB_SERVICE_URL, if the web service is called by tSalesforceInput we should use TSALESFORCE_INPUT_URL
    public static final String DEFAULT_WEB_SERVICE_URL = "https://www.salesforce.com/services/Soap/u/8.0"; //$NON-NLS-1$

    public static final String DEFAULT_WEB_SERVICE_FOR_SOQL_URL = "https://www.salesforce.com/services/Soap/c/8.0"; //$NON-NLS-1$

    public static final String TSALESFORCE_CUSTOM_MODULE = "org.talend.salesforce.custom.module"; //$NON-NLS-1$

    public static final String TSALESFORCE_CUSTOM_MODULE_SPILT = ","; //$NON-NLS-1$

    public AbstractSalesforceStepForm(Composite parent, ConnectionItem connectionItem, String[] existingNames,
            SalesforceModuleParseAPI salesforceAPI) {
        super(parent, SWT.NONE, existingNames);
        setConnectionItem(connectionItem);
        this.salesforceAPI = salesforceAPI;
    }

    public AbstractSalesforceStepForm(Composite parent, ConnectionItem connectionItem, SalesforceModuleParseAPI salesforceAPI) {
        this(parent, connectionItem, null, salesforceAPI);
    }

    public AbstractSalesforceStepForm(Composite parent, ConnectionItem connectionItem, MetadataTable metadataTable,
            String[] existingNames, SalesforceModuleParseAPI salesforceAPI) {
        super(parent, SWT.NONE, existingNames);
        setConnectionItem(connectionItem);
        this.salesforceAPI = salesforceAPI;
    }

    protected SalesforceSchemaConnection getConnection() {
        return (SalesforceSchemaConnection) connectionItem.getConnection();
    }

    public boolean isPerlProject() {
        ECodeLanguage codeLanguage = LanguageManager.getCurrentLanguage();
        return (codeLanguage == ECodeLanguage.PERL);
    }

    /**
     * 
     * DOC YeXiaowei Comment method "getSalesforceComponent".
     * 
     * @return Always not null
     */
    public INode getSalesforceNode() {
        return RepositoryPlugin.getDefault().getDesignerCoreService().getRefrenceNode(tSalesforceUniqueName);
    }

    public IMetadataTable getMetadatasForSalesforce(String endPoint, String user, String pass, String moduleName,
            String betchSize, boolean useProxy, boolean useHttp, String proxyHost, String proxyPort, String proxyUsername,
            String proxyPassword, boolean update) {
        // TSALESFORCE_INPUT_URL is only used by tSalesForceInput, the wizard doesn't work with this url
        if (endPoint.equals(TSALESFORCE_INPUT_URL)) {
            endPoint = DEFAULT_WEB_SERVICE_URL;
        }
        IMetadataTable result = null;

        if (!moduleName.equals(salesforceAPI.getCurrentModuleName())) {
            result = getMetadataTableBySalesforceServerAPI(endPoint, user, pass, moduleName);
            if (result == null) {
                result = getMetadataTableFromConfigFile(moduleName);
            }
            return result;
        } else {
            if (update) {
                result = getMetadataTableBySalesforceServerAPI(endPoint, user, pass, moduleName);
                if (result == null) {
                    result = getMetadataTableFromConfigFile(moduleName);
                }
                return result;
            } else {
                IMetadataTable metadataTable = new org.talend.core.model.metadata.MetadataTable();
                metadataTable.setListColumns(salesforceAPI.getCurrentMetadataColumns());
                return metadataTable;
            }
        }

    }

    private IMetadataTable getMetadataTableBySalesforceServerAPI(final String endPoint, final String user, final String pass,
            final String moduleName) {
        IMetadataTable metadataTable = new org.talend.core.model.metadata.MetadataTable();

        if (user == null || pass == null || user.equals("") || pass.equals("") || moduleName == null || moduleName.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return null;
        }

        ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
        try {
            dialog.run(true, false, new IRunnableWithProgress() {

                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

                    monitor.beginTask(Messages.getString("AbstractSalesforceStepForm.fetchModule", moduleName), //$NON-NLS-1$
                            IProgressMonitor.UNKNOWN);

                    try {

                        ArrayList loginList = salesforceAPI.login(endPoint, user, pass);
                        for (int i = 0; i < loginList.size(); i++) {
                            if (loginList.get(i) instanceof SoapBindingStub) {
                                binding = (SoapBindingStub) loginList.get(i);
                            }
                        }
                    } catch (Throwable e) {
                        ExceptionHandler.process(e);
                    }
                    salesforceAPI.fetchMetaDataColumns(moduleName);
                    monitor.done();
                }
            });
        } catch (InvocationTargetException e1) {
            ExceptionHandler.process(e1);
        } catch (InterruptedException e2) {
            ExceptionHandler.process(e2);
        }

        if (salesforceAPI.getCurrentMetadataColumns() == null) {
            return null;
        }

        metadataTable.setListColumns(salesforceAPI.getCurrentMetadataColumns());
        return metadataTable;
    }

    protected boolean toCheckSalesfoceLogin(final String endPoint, final String username, final String password) {
        salesforceAPI.setLogin(false);
        try {

            ArrayList loginList = salesforceAPI.login(endPoint, username, password);
            for (int i = 0; i < loginList.size(); i++) {
                if (loginList.get(i) instanceof SoapBindingStub) {
                    binding = (SoapBindingStub) loginList.get(i);
                }
            }
            salesforceAPI.setLogin(true);
        } catch (Throwable e) {
            ExceptionHandler.process(e);
        }
        return salesforceAPI.isLogin();
    }

    protected ArrayList checkSalesfoceLogin(final String theProxy, final String endPoint, final String username,
            final String password, final String proxyHost, final String proxyPort, final String proxyUsername,
            final String proxyPassword, final String mouleName) {
        final List<String> errors = new ArrayList<String>();
        final ArrayList arraylist = new ArrayList<String>();

        salesforceAPI.setLogin(false);

        ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
        try {
            dialog.run(true, false, new IRunnableWithProgress() {

                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

                    monitor.beginTask(Messages.getString("AbstractSalesforceStepForm.tryToLogin"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$

                    if (salesforceAPI == null) {
                        try {
                            salesforceAPI = new SalesforceModuleParseAPI();
                        } catch (Throwable e) {
                            ExceptionHandler.process(e);
                        }
                    }

                    try {
                        // binding ;
                        ArrayList loginList = salesforceAPI.login(theProxy, endPoint, username, password, proxyHost, proxyPort,
                                proxyUsername, proxyPassword, mouleName);
                        if (loginList != null) {
                            for (int i = 0; i < loginList.size(); i++) {
                                if (loginList.get(i) instanceof SoapBindingStub) {
                                    binding = (SoapBindingStub) loginList.get(i);
                                }
                                if (loginList.get(i) instanceof ArrayList) {
                                    ArrayList realtionShipObjects = (ArrayList) loginList.get(i);
                                    if (realtionShipObjects != null && realtionShipObjects.get(0) != null) {
                                        arraylist.add(realtionShipObjects);
                                    }
                                }
                            }

                        }

                        salesforceAPI.setLogin(true);
                    } catch (com.sforce.soap.partner.fault.ApiFault ex) {
                        errors.add(ex.getExceptionMessage());
                    } catch (Throwable e) {
                        errors.add(e.getMessage());
                        ExceptionHandler.process(e);
                    }
                    monitor.done();
                }
            });
        } catch (InvocationTargetException e1) {
            ExceptionHandler.process(e1);
        } catch (InterruptedException e2) {
            ExceptionHandler.process(e2);
        }

        if (salesforceAPI.isLogin()) {
            MessageDialog.openInformation(getShell(), Messages.getString("SalesforceForm.checkConnectionTitle"), //$NON-NLS-1$ 
                    Messages.getString("SalesforceForm.checkIsDone")); //$NON-NLS-1$ 
        } else {
            String mainMsg = Messages.getString("SalesforceForm.checkFailure") + " " //$NON-NLS-1$ //$NON-NLS-2$
                    + Messages.getString("SalesforceForm.checkFailureTip"); //$NON-NLS-1$
            String error = errors.size() > 0 ? errors.get(0) : ""; //$NON-NLS-1$
            new ErrorDialogWidthDetailArea(getShell(), PID, mainMsg, error);
        }
        arraylist.add(salesforceAPI.isLogin());

        return arraylist;
    }

    protected DescribeGlobalResult describeGlobal() throws UnexpectedErrorFault, RemoteException {
        if (salesforceAPI.isLogin()) {
            return binding.describeGlobal();
        }
        return null;
    }

    private IMetadataTable getMetadataTableFromConfigFile(String moduleName) {

        INode node = getSalesforceNode();

        IElementParameter currentModuleNameParam = node.getElementParameter("MODULENAME"); //$NON-NLS-1$
        currentModuleNameParam.setValue(moduleName);

        node.getComponent().createElementParameters(node);

        IElementParameter schemaParam = node.getElementParameter("SCHEMA"); //$NON-NLS-1$

        if (schemaParam == null) {
            return null;
        }

        schemaParam.setValueToDefault(node.getElementParameters()); // Call this method to recompute some parameters
        // value.

        IMetadataTable metadataTable = (IMetadataTable) schemaParam.getValue();

        return metadataTable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#adaptFormToReadOnly()
     */
    @Override
    protected void adaptFormToReadOnly() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#addFields()
     */
    @Override
    protected void addFields() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#addFieldsListeners()
     */
    @Override
    protected void addFieldsListeners() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#addUtilsButtonListeners()
     */
    @Override
    protected void addUtilsButtonListeners() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#checkFieldsValue()
     */
    @Override
    protected boolean checkFieldsValue() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.swt.utils.AbstractForm#initialize()
     */
    @Override
    protected void initialize() {
        // TODO Auto-generated method stub
    }

    /**
     * Getter for salesforceAPI.
     * 
     * @return the salesforceAPI
     */
    public SalesforceModuleParseAPI getSalesforceAPI() {
        return this.salesforceAPI;
    }

    /**
     * Sets the salesforceAPI.
     * 
     * @param salesforceAPI the salesforceAPI to set
     */
    public void setSalesforceAPI(SalesforceModuleParseAPI salesforceAPI) {
        this.salesforceAPI = salesforceAPI;
    }

    public IMetadataContextModeManager getContextModeManager() {
        return this.contextModeManager;
    }

    public void setContextModeManager(IMetadataContextModeManager contextModeManager) {
        this.contextModeManager = contextModeManager;
    }
}
