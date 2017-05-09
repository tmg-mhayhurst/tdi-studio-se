// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.dbmap.external.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.talend.core.model.process.node.AbstractExternalMapTable;
import org.talend.core.model.process.node.IExternalMapEntry;

/**
 * DOC amaumont class global comment. Detailled comment <br/>
 * 
 * $Id: ExternalMapperTable.java 898 2006-12-07 11:06:17Z amaumont $
 * 
 */
public class ExternalDbMapTable extends AbstractExternalMapTable implements Serializable, Cloneable {

    /**
     * 
     */
    private static final long serialVersionUID = 8768004929161302382L;

    private List<ExternalDbMapEntry> customWhereConditionsEntries;

    private List<ExternalDbMapEntry> customOtherConditionsEntries;

    private List<ExternalDbMapEntry> metadataTableEntries;

    private String name;

    private boolean minimized;

    /**
     * Used only for inputs.
     */
    private String joinType;

    /**
     * Used only for inputs.
     */
    private String alias;

    /**
     * Used only for inputs.
     */
    private String tableName;

    /**
     * Used only for inputs.
     */
    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String medataTableName) {
        this.name = medataTableName;
    }

    public boolean isMinimized() {
        return this.minimized;
    }

    public void setMinimized(boolean minimized) {
        this.minimized = minimized;
    }

    public List<ExternalDbMapEntry> getMetadataTableEntries() {
        return this.metadataTableEntries;
    }

    @Override
    public List<? extends IExternalMapEntry> returnTableEntries() {
        return getMetadataTableEntries();
    }

    public void setMetadataTableEntries(List<ExternalDbMapEntry> tableEntries) {
        this.metadataTableEntries = tableEntries;
    }

    public List<ExternalDbMapEntry> getCustomWhereConditionsEntries() {
        return this.customWhereConditionsEntries;
    }

    public void setCustomWhereConditionsEntries(List<ExternalDbMapEntry> constraintWhereTableEntries) {
        this.customWhereConditionsEntries = constraintWhereTableEntries;
    }

    public List<ExternalDbMapEntry> getCustomOtherConditionsEntries() {
        return this.customOtherConditionsEntries;
    }

    public void setCustomOtherConditionsEntries(List<ExternalDbMapEntry> constraintOtherTableEntries) {
        this.customOtherConditionsEntries = constraintOtherTableEntries;
    }

    /**
     * Getter for joinType.
     * 
     * @return the joinType
     */
    public String getJoinType() {
        return this.joinType;
    }

    /**
     * Sets the joinType.
     * 
     * @param joinType the joinType to set
     */
    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    /**
     * Getter for alias.
     * 
     * @return the alias
     */
    public String getAlias() {
        return this.alias;
    }

    /**
     * Sets the alias.
     * 
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Getter for tableName.
     * 
     * @return the tableName
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * Sets the tableName.
     * 
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object clone() throws CloneNotSupportedException {
        ExternalDbMapTable cloned = (ExternalDbMapTable) super.clone();
        if (customWhereConditionsEntries != null) {
            cloned.customWhereConditionsEntries = (List<ExternalDbMapEntry>) ((ArrayList) customWhereConditionsEntries).clone();
            int listSizecustomConditionsEntries = customWhereConditionsEntries.size();
            for (int i = 0; i < listSizecustomConditionsEntries; i++) {
                cloned.customWhereConditionsEntries.set(i, (ExternalDbMapEntry) cloned.customWhereConditionsEntries.get(i)
                        .clone());
            }
        }
        if (customOtherConditionsEntries != null) {
            cloned.customOtherConditionsEntries = (List<ExternalDbMapEntry>) ((ArrayList) customOtherConditionsEntries).clone();
            int listSizecustomConditionsEntries = customOtherConditionsEntries.size();
            for (int i = 0; i < listSizecustomConditionsEntries; i++) {
                cloned.customOtherConditionsEntries.set(i, (ExternalDbMapEntry) cloned.customOtherConditionsEntries.get(i)
                        .clone());
            }
        }
        if (metadataTableEntries != null) {
            cloned.metadataTableEntries = (List<ExternalDbMapEntry>) ((ArrayList) metadataTableEntries).clone();
            int listSizemetadataTableEntries = metadataTableEntries.size();
            for (int i = 0; i < listSizemetadataTableEntries; i++) {
                cloned.metadataTableEntries.set(i, (ExternalDbMapEntry) cloned.metadataTableEntries.get(i).clone());
            }
        }
        return cloned;
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExternalDbMapTable other = (ExternalDbMapTable) obj;
        
        if (this.minimized != other.minimized) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.joinType == null) {
            if (other.joinType != null) {
                return false;
            }
        } else if (!this.joinType.equals(other.joinType)) {
            return false;
        }
        if (this.alias == null) {
            if (other.alias != null) {
                return false;
            }
        } else if (!this.alias.equals(other.alias)) {
            return false;
        }
        if (this.tableName == null) {
            if (other.tableName != null) {
                return false;
            }
        } else if (!this.tableName.equals(other.tableName)) {
            return false;
        }
       
        if (this.customOtherConditionsEntries == null) {
            if (other.customOtherConditionsEntries != null) {
                return false;
            }
        } else if (!this.customOtherConditionsEntries.equals(other.customOtherConditionsEntries)) {
            return false;
        }
        
        if (this.customWhereConditionsEntries == null) {
            if (other.customWhereConditionsEntries != null) {
                return false;
            }
        } else if (!this.customWhereConditionsEntries.equals(other.customWhereConditionsEntries)) {
            return false;
        }
        
        if (this.metadataTableEntries == null) {
            if (other.metadataTableEntries != null) {
                return false;
            }
        } else if (!this.metadataTableEntries.equals(other.metadataTableEntries)) {
            return false;
        }
        
        if(customOtherConditionsEntries!=null && other.customOtherConditionsEntries!=null){
            if(customOtherConditionsEntries.size() != other.customOtherConditionsEntries.size()){
                return false;
            }
            for(ExternalDbMapEntry oriObj:customOtherConditionsEntries){
                boolean found = false;
                for(ExternalDbMapEntry otherObj:other.customOtherConditionsEntries){
                    if(oriObj.getName().equals(otherObj.getName())){
                        found = true;
                        if(!oriObj.equals(otherObj)){
                            return false;
                        }
                        break;
                    }
                }
                if(found == false){
                    return false;
                }
            } 
        }
        
        if(customWhereConditionsEntries!=null && other.customWhereConditionsEntries!=null){
            if(customWhereConditionsEntries.size() != other.customWhereConditionsEntries.size()){
                return false;
            }
            for(ExternalDbMapEntry oriObj:customWhereConditionsEntries){
                boolean found = false;
                for(ExternalDbMapEntry otherObj:other.customWhereConditionsEntries){
                    if(oriObj.getName().equals(otherObj.getName())){
                        found = true;
                        if(!oriObj.equals(otherObj)){
                            return false;
                        }
                        break;
                    }
                }
                if(found == false){
                    return false;
                }
            }
        }
        
        if(metadataTableEntries!=null && other.metadataTableEntries!=null){
            if(metadataTableEntries.size() != other.metadataTableEntries.size()){
                return false;
            }
            for(ExternalDbMapEntry oriObj:metadataTableEntries){
                boolean found = false;
                for(ExternalDbMapEntry otherObj:other.metadataTableEntries){
                    if(oriObj.getName().equals(otherObj.getName())){
                        found = true;
                        if(!oriObj.equals(otherObj)){
                            return false;
                        }
                        break;
                    }
                }
                if(found == false){
                    return false;
                }
            }
        }
        
        return true;
    }

}
