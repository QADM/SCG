/*
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
 */
/* This file has been modified by Open Source Strategies, Inc. */

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;

andExprs = [];
entityName = context.entityName;
searchFields = context.searchFields;
displayFields = context.displayFields ?: searchFields;
searchValueFieldName = parameters.searchValueField;
if (searchValueFieldName) fieldValue = parameters.get(searchValueFieldName);
searchType = context.searchType;

if (searchFields && fieldValue) {
    searchFieldsList = StringUtil.toList(searchFields);
    displayFieldsSet = StringUtil.toSet(displayFields);
    returnField = searchFieldsList[0]; //default to first element of searchFields
    displayFieldsSet.add(returnField); //add it to select fields, in case it is missing
    context.returnField = returnField;
    context.displayFieldsSet = displayFieldsSet;
    if ("STARTS_WITH".equals(searchType)) {
        searchValue = fieldValue.toUpperCase() + "%";
    } else if ("EQUALS".equals(searchType)) {
        searchValue = fieldValue;
    } else {//default is CONTAINS
        searchValue = "%" + fieldValue.toUpperCase() + "%";
    }
    searchFieldsList.each { fieldName ->
        if ("EQUALS".equals(searchType)) {
            andExprs.add(EntityCondition.makeCondition(EntityFieldValue.makeFieldValue(returnField), EntityOperator.EQUALS, searchValue));    
            return;//in case of EQUALS, we search only a match for the returned field
        } else {
            andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue(fieldName)), EntityOperator.LIKE, searchValue));
        }        
    }
}

if (andExprs && entityName && displayFieldsSet) {
    entityConditionList = EntityCondition.makeCondition(andExprs, EntityOperator.OR);

    //if there is an extra condition, add it to main condition
    if (context.andCondition && context.andCondition  instanceof EntityCondition) {
        entityConditionList = EntityCondition.makeCondition(context.andCondition, entityConditionList);
    }

    Integer autocompleterViewSize = Integer.valueOf(context.autocompleterViewSize ?: 10);
    EntityFindOptions findOptions = new EntityFindOptions();
    findOptions.setMaxRows(autocompleterViewSize);
    autocompleteOptions = delegator.findList(entityName, entityConditionList, displayFieldsSet, StringUtil.toList(displayFields), findOptions, false);
    if (autocompleteOptions) {
        context.autocompleteOptions = autocompleteOptions;
    }
}
