/* Interfaz para mostrar las clasificaciones 
 * en las 4 pantallas de transacciones
 */

package org.opentaps.domain.organization;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.opentaps.common.util.UtilAccountingTags;
import org.opentaps.base.entities.ClasifPresupuestal;
import org.opentaps.base.entities.Enumeration;
import org.opentaps.base.entities.NivelPresupuestal;
import org.opentaps.foundation.entity.Entity;
import org.opentaps.foundation.entity.EntityFieldInterface;
import org.opentaps.foundation.repository.RepositoryInterface;

/**
 * A virtual entity used to store a Tag configuration for an organization and usage type.
 */
public class ReadConfigurationForOrganization extends Entity {

    public static enum Fields implements EntityFieldInterface<ReadConfigurationForOrganization> {
        index("index"),
        type("type"),
        description("description");       
        
        private final String fieldName;
        
        private Fields(String name) { fieldName = name; }
        /** {@inheritDoc} */
        public String getName() { return fieldName; }
        /** {@inheritDoc} */
        public String asc() { return fieldName + " ASC"; }
        /** {@inheritDoc} */
        public String desc() { return fieldName + " DESC"; }
    }

    private Integer index;
    private String invoiceItem;
    private String type;
    private String description;
    
    
    

    /**
     * Default constructor.
     */
    public ReadConfigurationForOrganization() {
        super();
        this.baseEntityName = null;
        this.isView = true;
        
    }

    /**
     * Constructor with a repository.
     * @param repository a <code>RepositoryInterface</code> value
     */
    public ReadConfigurationForOrganization(RepositoryInterface repository) {
        this();
        initRepository(repository);
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    } 
    
    public void setInvoiceItem(String invoiceItem) {
        this.invoiceItem = invoiceItem;
    } 
    
    public Integer getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }  
    
    public String getInvoiceItem() {
       return invoiceItem;
    }

    public String getPrefixedName(String prefix) {
        return prefix + index;
    }

    public String getEntityFieldName() {
        return UtilAccountingTags.ENTITY_TAG_PREFIX + index;
    }

    /** {@inheritDoc} */
    @Override
    public void fromMap(Map<String, Object> mapValue) {
        preInit();
        setIndex((Integer) mapValue.get("index"));
        setType((String) mapValue.get("type"));
        setDescription((String) mapValue.get("description"));
        setInvoiceItem((String) mapValue.get("invoiceItem"));
        postInit();
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> mapValue = new FastMap<String, Object>();
        mapValue.put("index", getIndex());
        mapValue.put("type", getType());
        mapValue.put("description", getDescription());   
        mapValue.put("invoiceItem", getInvoiceItem());   
        return mapValue;
    }
}
