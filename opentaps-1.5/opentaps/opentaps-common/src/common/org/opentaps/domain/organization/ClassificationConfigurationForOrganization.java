/* Interfaz para mostrar las clasificaciones 
 * en las 4 pantallas de transacciones
 */

package org.opentaps.domain.organization;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
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
public class ClassificationConfigurationForOrganization extends Entity {

    public static enum Fields implements EntityFieldInterface<ClassificationConfigurationForOrganization> {
        index("index"),
        type("type"),
        description("description"),
        tagValues("tagValues"),
        activeTagValues("activeTagValues"),
        isRequired("isRequired");
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
    private String type;
    private String description;
    private String isRequired;
    private String defaultValue;
    private ClasifPresupuestal defaultValueTag;
    private List<ClasifPresupuestal> tagValues;
    private List<NivelPresupuestal> activeTagValues;
    

    /**
     * Default constructor.
     */
    public ClassificationConfigurationForOrganization() {
        super();
        this.baseEntityName = null;
        this.isView = true;
        this.isRequired = "N";
    }

    /**
     * Constructor with a repository.
     * @param repository a <code>RepositoryInterface</code> value
     */
    public ClassificationConfigurationForOrganization(RepositoryInterface repository) {
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

    public void setTagValues(List<ClasifPresupuestal> tagValues) {
        this.tagValues = tagValues;
    }

    public void setActiveTagValues(List<NivelPresupuestal> activeTagValues) {
        this.activeTagValues = activeTagValues;
    }  
    

    public void setIsRequired(String isRequired) {
        this.isRequired = isRequired;
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

    public List<ClasifPresupuestal> getTagValues() {
        return tagValues;
    }

    public List<NivelPresupuestal> getActiveTagValues() {
        return activeTagValues;
    }

    public String getIsRequired() {
        return isRequired;
    }

    public boolean isRequired() {
        return "Y".equals(isRequired);
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ClasifPresupuestal getDefaultValueTag() {
        return defaultValueTag;
    }

    public void setDefaultValueTag(ClasifPresupuestal defaultValueTag) {
        this.defaultValueTag = defaultValueTag;
    }  
   

    public boolean hasDefaultValue() {
        return UtilValidate.isNotEmpty(defaultValue);
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
        setTagValues((List<ClasifPresupuestal>) mapValue.get("tagValues"));
        setActiveTagValues((List<NivelPresupuestal>) mapValue.get("activeTagValues"));
        setIsRequired((String) mapValue.get("isRequired"));
        postInit();
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> mapValue = new FastMap<String, Object>();
        mapValue.put("index", getIndex());
        mapValue.put("type", getType());
        mapValue.put("description", getDescription());
        mapValue.put("tagValues", getTagValues());
        mapValue.put("activeTagValues", getActiveTagValues());
        mapValue.put("isRequired", getIsRequired());
        return mapValue;
    }
}
