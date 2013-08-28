package com.opentaps.financials.Helpers;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class OperacionDiariaHelper {
	
	
    public static final String MODULE = OperacionDiariaHelper.class.getName();
    
	public static String getProductCategoryName(Delegator delegator, String productCategoryId) {
        GenericValue productCategoryObject = null;
        try {
            productCategoryObject = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error al buscar productCategory ", MODULE);
        }
        if (productCategoryObject == null) {
            return productCategoryId;
        } else {
            return productCategoryObject.getString("description");
        }
    }
	
	public static String getEnumName(Delegator delegator, String enumId) {
        GenericValue enumerationObject = null;
        try {
            enumerationObject = delegator.findByPrimaryKey("Enumeration", UtilMisc.toMap("enumId", enumId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error al buscar enumeration ", MODULE);
        }
        if (enumerationObject == null) {
            return enumId;
        } else {
            return enumerationObject.getString("description");
        }
    }

	public static String getGeoName(Delegator delegator, String geoId) {
        GenericValue geoObject = null;
        try {
            geoObject = delegator.findByPrimaryKey("Geo", UtilMisc.toMap("geoId", geoId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error al buscar Geo ", MODULE);
        }
        if (geoObject == null) {
            return geoId;
        } else {
            return geoObject.getString("geoName");
        }
    }
	
	public static String getTipoDocName(Delegator delegator, String idTipoDoc) {
        GenericValue tipoDocObject = null;
        try {
            tipoDocObject = delegator.findByPrimaryKey("TipoDocumento", UtilMisc.toMap("idTipoDoc", idTipoDoc));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error al buscar TipoDocumento ", MODULE);
        }
        if (tipoDocObject == null) {
            return idTipoDoc;
        } else {
            return tipoDocObject.getString("descripcion");
        }
    }
	
	public static String getWorkEffortName(Delegator delegator, String workEffortId) {
        GenericValue workEffortObject = null;
        try {
            workEffortObject = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error al buscar WorkEffort ", MODULE);
        }
        if (workEffortObject == null) {
            return workEffortId;
        } else {
            return workEffortObject.getString("description");
        }
    }		

	public static String getPaymenthMethodName(Delegator delegator, String paymentMethodId) {
        GenericValue paymenthMethodObject = null;
        try {
            paymenthMethodObject = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId", paymentMethodId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error al buscar PaymentMethod ", MODULE);
        }
        if (paymenthMethodObject == null) {
            return paymentMethodId;
        } else {
            return paymenthMethodObject.getString("description");
        }
    }
	
	public static String getProductName(Delegator delegator, String productId) {
        GenericValue paymenthMethodObject = null;
        try {
            paymenthMethodObject = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error al buscar Product ", MODULE);
        }
        if (paymenthMethodObject == null) {
            return productId;
        } else {
            return paymenthMethodObject.getString("productName");
        }
    }	
    
}
