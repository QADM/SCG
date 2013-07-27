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

import javolution.util.FastList;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.opentaps.base.constants.StatusItemConstants;

customersProcessed = 0;
customersNotProcessed = 0;
productsProcessed = 0;
productsNotProcessed = 0;
inventoryProcessed = 0;
inventoryNotProcessed = 0;
orderHeadersProcessed = 0;
orderHeadersNotProcessed = 0;
orderItemsProcessed = 0;
orderItemsNotProcessed = 0;
glAccountsProcessed = 0;
glAccountsNotProcessed = 0;

tagProcessed=0;
tagNotProcessed=0;
partyProcessed = 0;
partyNotProcessed = 0;

projectProcessed = 0;
projectNotProcessed = 0;
geoProcessed = 0;
geoNotProcessed = 0;
categoryProcessed = 0;
categoryNotProcessed = 0;

presupuestoIngresoProcessed = 0;
presupuestoIngresoNotProcessed = 0;
presupuestoEgresoProcessed = 0;
presupuestoEgresoNotProcessed = 0;

ingresoDiarioProcessed = 0;
ingresoDiarioNotProcessed = 0;
egresoDiarioProcessed = 0;
egresoDiarioNotProcessed = 0;
operacionDiariaProcessed = 0;
operacionDiariaNotProcessed = 0;


matrizEgrProcessed = 0;
matrizEgrNotProcessed = 0;
matrizIngProcessed = 0;
matrizIngNotProcessed = 0;


/*
  GET PROCESSED
*/
searchConditions = FastList.newInstance();
searchConditions.add(new EntityExpr("importStatusId", EntityOperator.EQUALS, StatusItemConstants.Dataimport.DATAIMP_IMPORTED));
allConditions = new EntityConditionList(searchConditions, EntityOperator.AND);

suppliersProcessed = delegator.findCountByCondition("DataImportSupplier", allConditions, null);
customersProcessed = delegator.findCountByCondition("DataImportCustomer", allConditions, null);
productsProcessed = delegator.findCountByCondition("DataImportProduct", allConditions, null);
inventoryProcessed = delegator.findCountByCondition("DataImportInventory", allConditions, null);
orderHeadersProcessed = delegator.findCountByCondition("DataImportOrderHeader", allConditions, null);
orderItemsProcessed = delegator.findCountByCondition("DataImportOrderItem", allConditions, null);
glAccountsProcessed = delegator.findCountByCondition("DataImportGlAccount", allConditions, null);

tagProcessed= delegator.findCountByCondition("DataImportTag", allConditions, null);
partyProcessed = delegator.findCountByCondition("DataImportParty", allConditions, null);


projectProcessed = delegator.findCountByCondition("DataImportProject", allConditions, null);
geoProcessed = delegator.findCountByCondition("DataImportGeo", allConditions, null);
categoryProcessed = delegator.findCountByCondition("DataImportCategory", allConditions, null);

presupuestoIngresoProcessed = delegator.findCountByCondition("DataImportPresupuestoIngreso", allConditions, null); 
presupuestoEgresoProcessed = delegator.findCountByCondition("DataImportPresupuestoEgreso", allConditions, null);

egresoDiarioProcessed = delegator.findCountByCondition("DataImportEgresoDiario", allConditions, null);
ingresoDiarioProcessed = delegator.findCountByCondition("DataImportIngresoDiario", allConditions, null);
operacionDiariaProcessed = delegator.findCountByCondition("DataImportOperacionDiaria", allConditions, null);

matrizEgrProcessed = delegator.findCountByCondition("DataImportMatrizEgr", allConditions, null);
matrizIngProcessed = delegator.findCountByCondition("DataImportMatrizIng", allConditions, null);



/*
  GET NOT-PROCESSED
*/

EntityCondition statusCond = EntityCondition.makeCondition(EntityOperator.OR,
         EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, StatusItemConstants.Dataimport.DATAIMP_NOT_PROC),
         EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, StatusItemConstants.Dataimport.DATAIMP_FAILED),
         EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, null));

suppliersNotProcessed = delegator.findCountByCondition("DataImportSupplier", statusCond, null);
customersNotProcessed = delegator.findCountByCondition("DataImportCustomer", statusCond, null);
productsNotProcessed = delegator.findCountByCondition("DataImportProduct", statusCond, null);
inventoryNotProcessed = delegator.findCountByCondition("DataImportInventory", statusCond, null);
orderHeadersNotProcessed = delegator.findCountByCondition("DataImportOrderHeader", statusCond, null);
orderItemsNotProcessed = delegator.findCountByCondition("DataImportOrderItem", statusCond, null);
glAccountsNotProcessed = delegator.findCountByCondition("DataImportGlAccount", statusCond, null);

tagNotProcessed = delegator.findCountByCondition("DataImportTag", statusCond, null);
partyNotProcessed = delegator.findCountByCondition("DataImportParty", statusCond, null);

projectNotProcessed = delegator.findCountByCondition("DataImportProject", statusCond, null);
geoNotProcessed = delegator.findCountByCondition("DataImportGeo", statusCond, null);
categoryNotProcessed = delegator.findCountByCondition("DataImportCategory", statusCond, null);

presupuestoIngresoNotProcessed = delegator.findCountByCondition("DataImportPresupuestoIngreso", statusCond, null);
presupuestoEgresoNotProcessed = delegator.findCountByCondition("DataImportPresupuestoEgreso", statusCond, null);

egresoDiarioNotProcessed = delegator.findCountByCondition("DataImportEgresoDiario", statusCond, null);
ingresoDiarioNotProcessed = delegator.findCountByCondition("DataImportIngresoDiario", statusCond, null);
operacionDiariaNotProcessed = delegator.findCountByCondition("DataImportOperacionDiaria", statusCond, null);

matrizEgrNotProcessed = delegator.findCountByCondition("DataImportMatrizEgr", statusCond, null);
matrizIngNotProcessed = delegator.findCountByCondition("DataImportMatrizIng", statusCond, null);

context.put("suppliersProcessed", suppliersProcessed);
context.put("suppliersNotProcessed", suppliersNotProcessed);
context.put("customersProcessed", customersProcessed);
context.put("customersNotProcessed", customersNotProcessed);
context.put("productsProcessed", productsProcessed);
context.put("productsNotProcessed", productsNotProcessed);
context.put("inventoryProcessed", inventoryProcessed);
context.put("inventoryNotProcessed", inventoryNotProcessed);
context.put("orderHeadersProcessed", orderHeadersProcessed);
context.put("orderHeadersNotProcessed", orderHeadersNotProcessed);
context.put("orderItemsProcessed", orderItemsProcessed);
context.put("orderItemsNotProcessed", orderItemsNotProcessed);
context.put("glAccountsProcessed", glAccountsProcessed);
context.put("glAccountsNotProcessed", glAccountsNotProcessed);

context.put("tagProcessed", tagProcessed);
context.put("tagNotProcessed", tagNotProcessed);
context.put("partyProcessed", partyProcessed);
context.put("partyNotProcessed", partyNotProcessed);

context.put("projectProcessed", projectProcessed);
context.put("projectNotProcessed", projectNotProcessed);
context.put("geoProcessed", geoProcessed);
context.put("geoNotProcessed", geoNotProcessed);
context.put("categoryProcessed", categoryProcessed);
context.put("categoryNotProcessed", categoryNotProcessed);

context.put("presupuestoIngresoProcessed", presupuestoIngresoProcessed);
context.put("presupuestoIngresoNotProcessed", presupuestoIngresoNotProcessed);
context.put("presupuestoEgresoProcessed", presupuestoEgresoProcessed);
context.put("presupuestoEgresoNotProcessed", presupuestoEgresoNotProcessed);

context.put("egresoDiarioProcessed", egresoDiarioProcessed);
context.put("egresoDiarioNotProcessed", egresoDiarioNotProcessed);
context.put("ingresoDiarioProcessed", ingresoDiarioProcessed);
context.put("ingresoDiarioNotProcessed", ingresoDiarioNotProcessed);
context.put("operacionDiariaProcessed", operacionDiariaProcessed);
context.put("operacionDiariaNotProcessed", operacionDiariaNotProcessed);
 
context.put("matrizEgrProcessed", matrizEgrProcessed);
context.put("matrizEgrNotProcessed", matrizEgrNotProcessed);
context.put("matrizIngProcessed", matrizIngProcessed);
context.put("matrizIngNotProcessed", matrizIngNotProcessed);