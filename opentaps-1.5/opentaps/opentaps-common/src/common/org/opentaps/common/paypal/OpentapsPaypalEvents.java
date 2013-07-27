/*
 * Copyright (c) Open Source Strategies, Inc.
 * 
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */

/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/

/* This file has been modified by Open Source Strategies, Inc. */

package org.opentaps.common.paypal;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class OpentapsPaypalEvents {
    
    public static final String module = OpentapsPaypalEvents.class.getName();
    
    /** PayPal Call-Back Event */
    public static String payPalIPN(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");   
        
        // get the webSiteId
        String webSiteId = CatalogWorker.getWebSiteId(request);

        // get the product store
        GenericValue productStore = ProductStoreWorker.getProductStore(request);

        // get the payment properties file
        GenericValue paymentConfig = ProductStoreWorker.getProductStorePaymentSetting(delegator, productStore.getString("productStoreId"), "EXT_PAYPAL", null, true);
        String configString = null;
        if (paymentConfig != null) {
            configString = paymentConfig.getString("paymentPropertiesPath");
        }

        if (configString == null) {
            configString = "payment.properties";
        }
               
        // get the confirm URL
        String confirmUrl = UtilProperties.getPropertyValue(configString, "payment.paypal.confirm");
        if (confirmUrl == null) {
            Debug.logError("Payment properties is not configured properly, no confirm URL defined!", module);
            request.setAttribute("_ERROR_MESSAGE_", "PayPal has not been configured, please contact customer service.");
            return "error";
        }
                
        // first verify this is valid from PayPal
        Map parametersMap = UtilHttp.getParameterMap(request);
        parametersMap.put("cmd", "_notify-validate");  

        // send off the confirm request     
        StringBuffer confirmResp = new StringBuffer("");

        try {
            String str = UtilHttp.urlEncodeArgs(parametersMap, false);
            URL u = new URL(confirmUrl);
            URLConnection uc = u.openConnection();
            uc.setDoOutput(true);
            uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            PrintWriter pw = new PrintWriter(uc.getOutputStream());
            pw.println(str);
            pw.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line = in.readLine();
            while (line != null) {
                confirmResp.append(line);
                line = in.readLine();
            }
            in.close();
            Debug.logVerbose("PayPal Verification Response: " + confirmResp.toString(), module);
        } catch (IOException e) {
            Debug.logError(e, "Problems sending verification message", module);
        }

        if (confirmResp.toString().trim().equals("VERIFIED")) {
            // we passed verification
            Debug.logInfo("Got verification from PayPal, processing..", module);
        } else {
            Debug.logError("###### PayPal did not verify this request, need investigation!", module);
            Set keySet = parametersMap.keySet();
            Iterator i = keySet.iterator();
            while (i.hasNext()) {
                String name = (String) i.next();
                String value = request.getParameter(name);
                Debug.logError("### Param: " + name + " => " + value, module);
            }
        }
        
        // get the user
        GenericValue userLogin = null;
        String userLoginId = "system";
        try {
            userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get UserLogin for: " + userLoginId + "; cannot continue", module);
            request.setAttribute("_ERROR_MESSAGE_", "Problems getting authentication user.");
            return "error";
        }
                               
        // get the orderId
        String orderId = request.getParameter("invoice");

        // get the transaction status
        String paymentStatus = request.getParameter("payment_status");

        // attempt to start a transaction
        boolean okay = false;
        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();

            if (paymentStatus.equals("Completed")) {
                okay = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
            } else if (paymentStatus.equals("Failed") || paymentStatus.equals("Denied")) {
                okay = OrderChangeHelper.rejectOrder(dispatcher, userLogin, orderId);
            } else if (paymentStatus.equals("Pending")) {
                OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
            }


            if (okay) {
                // set the payment preference
                okay = setPaymentPreferences(delegator, dispatcher, userLogin, orderId, request);
            }
        } catch (Exception e) {
            String errMsg = "Error handling PayPal notification";
            Debug.logError(e, errMsg, module);
            try {
                TransactionUtil.rollback(beganTransaction, errMsg, e);
            } catch (GenericTransactionException gte2) {
                Debug.logError(gte2, "Unable to rollback transaction", module);
            }
        } finally {
            if (!okay) {
                try {
                    TransactionUtil.rollback(beganTransaction, "Failure in processing PayPal callback", null);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback transaction", module);
                }
            } else {
                try {
                    TransactionUtil.commit(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to commit transaction", module);
                }
            }
        }


        if (okay) {
            // attempt to release the offline hold on the order (workflow)
            OrderChangeHelper.releaseInitialOrderHold(dispatcher, orderId);

            // call the email confirm service
            Map emailContext = UtilMisc.toMap("orderId", orderId);
            try {
                Map emailResult = dispatcher.runSync("sendOrderConfirmation", emailContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problems sending email confirmation", module);
            }
        }

        return "success";
    }

    private static boolean setPaymentPreferences(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String orderId, ServletRequest request) {
        Debug.logVerbose("Setting payment prefrences..", module);
        List paymentPrefs = null;
        try {
            Map paymentFields = UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_NOT_RECEIVED");
            paymentPrefs = delegator.findByAnd("OrderPaymentPreference", paymentFields);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get payment preferences for order #" + orderId, module);
            return false;
        }
        if (paymentPrefs != null && paymentPrefs.size() > 0) {
            Iterator i = paymentPrefs.iterator();
            while (i.hasNext()) {
                GenericValue pref = (GenericValue) i.next();
                boolean okay = setPaymentPreference(dispatcher, userLogin, pref, request);
                if (!okay)
                    return false;
            }
        }
        return true;
    }  
        
    private static boolean setPaymentPreference(LocalDispatcher dispatcher, GenericValue userLogin, GenericValue paymentPreference, ServletRequest request) {
        // get the orderId
        String orderId = request.getParameter("invoice");

        String emailPaypal = request.getParameter("payer_email");
        String paymentDate = request.getParameter("payment_date");
        String paymentType = request.getParameter("payment_type");
        String paymentAmount = request.getParameter("mc_gross");
        String mcCurrency = request.getParameter("mc_currency");
        String paymentStatus = request.getParameter("payment_status");
        String transactionId = request.getParameter("txn_id");

        List toStore = new LinkedList();

        // PayPal returns the timestamp in the format 'hh:mm:ss Jan 1, 2000 PST'
        // Parse this into a valid Timestamp Object
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss MMM d, yyyy z");
        java.sql.Timestamp authDate = null;
        try {        
            authDate = new java.sql.Timestamp(sdf.parse(paymentDate).getTime());
        } catch (ParseException e) {
            Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
            authDate = UtilDateTime.nowTimestamp();
        } catch (NullPointerException e) {
            Debug.logError(e, "Cannot parse date string: " + paymentDate, module);
            authDate = UtilDateTime.nowTimestamp();
        }

        Delegator delegator = paymentPreference.getDelegator();

        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get the order header for order: " + orderId, module);
            request.setAttribute("_ERROR_MESSAGE_", "Problems getting order header.");
            return false;
        }
        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        GenericValue billToParty = orh.getBillToParty();
        if (UtilValidate.isEmpty(mcCurrency))
            mcCurrency = orh.getCurrency();

        // set the payToParty
        GenericValue payToParty = orh.getBillFromParty();

        paymentPreference.set("maxAmount", new Double(paymentAmount));
        if (paymentStatus.equals("Completed")) {
            paymentPreference.set("statusId", "PAYMENT_SETTLED");  // settled is consistent with other credit card payments
        } else {
            paymentPreference.set("statusId", "PAYMENT_CANCELLED");
        }

        // Creation of paymentMethod
        Map results = null;
        try {
            // 
            results = dispatcher.runSync("financials.createPaymentMethod",
                                         UtilMisc.toMap("userLogin", userLogin,
                                                        "paymentMethodTypeId", "EXT_PAYPAL",
                                                        "partyId", billToParty.getString("partyId"),
                                                        "emailAddress", emailPaypal));
        } catch (GenericServiceException e) {
            Debug.logError(e, "Failed to execute service financials.createPaymentMethod", module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return false;
        }
        String paymentMethodId = (String) results.get("paymentMethodId");
        paymentPreference.set("paymentMethodId", paymentMethodId);

        toStore.add(paymentPreference);

        // create the PaymentGatewayResponse
        String responseId = delegator.getNextSeqId("PaymentGatewayResponse");
        GenericValue response = delegator.makeValue("PaymentGatewayResponse");
        response.set("paymentGatewayResponseId", responseId);
        response.set("paymentServiceTypeEnumId", "PRDS_PAY_EXTERNAL");
        response.set("orderPaymentPreferenceId", paymentPreference.get("orderPaymentPreferenceId"));
        response.set("paymentMethodTypeId", "EXT_PAYPAL");
        response.set("paymentMethodId", paymentMethodId);
        response.set("transCodeEnumId", "PGT_CAPTURE");
        response.set("currencyUomId", mcCurrency);

        // set the auth info
        response.set("amount", new Double(paymentAmount));
        response.set("referenceNum", transactionId);
        response.set("gatewayCode", paymentStatus);
        response.set("gatewayFlag", paymentStatus.substring(0,1));
        response.set("gatewayMessage", paymentType);
        response.set("transactionDate", authDate);
        toStore.add(response);

        // create the payment record too
        Map paymentParams = UtilMisc.toMap("userLogin", userLogin);
        paymentParams.put("paymentTypeId", "CUSTOMER_PAYMENT");
        paymentParams.put("paymentMethodTypeId", "EXT_PAYPAL");
        paymentParams.put("paymentPreferenceId", paymentPreference.getString("orderPaymentPreferenceId"));
        paymentParams.put("amount", new Double(paymentAmount));
        paymentParams.put("statusId", "PMNT_RECEIVED");
        paymentParams.put("effectiveDate", UtilDateTime.nowTimestamp());
        paymentParams.put("partyIdFrom", billToParty.getString("partyId"));
        paymentParams.put("currencyUomId", mcCurrency);
        paymentParams.put("partyIdTo", payToParty.getString("partyId"));
        paymentParams.put("paymentMethodId", paymentMethodId);
        paymentParams.put("comments", "Payment receive via PayPal");
        paymentParams.put("paymentGatewayResponseId", responseId);
        paymentParams.put("paymentRefNum", transactionId);

        try {
            delegator.storeAll(toStore);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot set payment preference/payment info", module);
            return false;
        }

        // create a payment record too
        results = null;
        try {
            // 
            results = dispatcher.runSync("createPayment", paymentParams);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Failed to execute service createPayment", module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return false;
        }

        if ((results == null) || (results.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))) {
            Debug.logError((String) results.get(ModelService.ERROR_MESSAGE), module); 
            request.setAttribute("_ERROR_MESSAGE_", (String) results.get(ModelService.ERROR_MESSAGE));
            return false;
        }

        return true;             
    }

}
