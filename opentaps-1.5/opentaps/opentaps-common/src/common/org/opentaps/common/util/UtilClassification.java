/* 
 */

package org.opentaps.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.opentaps.base.entities.AcctgTagEnumType;
import org.opentaps.base.entities.Enumeration;
import org.opentaps.base.entities.EnumerationType;
import org.opentaps.common.domain.organization.OrganizationRepository;
import org.opentaps.domain.organization.AccountingTagConfigurationForOrganizationAndUsage;
import org.opentaps.foundation.entity.Entity;
import org.opentaps.foundation.entity.EntityInterface;
import org.opentaps.foundation.entity.hibernate.Session;
import org.opentaps.foundation.repository.RepositoryException;

/**
 * UtilAccountingTags - Utilities for the accounting tag system.
 */
public final class UtilClassification {

    @SuppressWarnings("unused")
    private static final String MODULE = UtilClassification.class.getName();

    /** Number of tags defined in <code>AcctgTagEnumType</code>. */
    public static final int TAG_COUNT = 10;
    
    
    public static final String INGRESO_TAG = "INGRESO";

    private UtilClassification() { }

    public static String buscaHojaNivelPresupuestal(String tipo, Session session)
			throws RepositoryException {
		return session
				.createQuery(
						"select NIVEL_ID from NIVEL_PRESUPUESTAL"
								+ "where NIVEL_ID not in (select NIVEL_PADRE_ID from NIVEL_PRESUPUESTAL"
								+ "where CLASIFICACION_ID = "
								+ tipo
								+ " and NIVEL_PADRE_ID is not null) and CLASIFICACION_ID = "
								+ tipo).list().get(0).toString();
	}
    
}
