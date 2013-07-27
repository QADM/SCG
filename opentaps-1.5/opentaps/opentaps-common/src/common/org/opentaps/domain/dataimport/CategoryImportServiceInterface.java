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
package org.opentaps.domain.dataimport;

import org.opentaps.foundation.service.ServiceException;
import org.opentaps.foundation.service.ServiceInterface;

/**
 * Import Categories via intermediate DataImportCategory entity.
 * Author: Jesus Rodrigo Ruiz Merlin
 */
public interface CategoryImportServiceInterface extends ServiceInterface{
    
    /**
     * Gets imported records count by service {@link #importCategory}.
     * @return imported records count
     */
    public int getImportedRecords();
    
    /**
     * Import products using <code>DataImportGlAccount</code>.
     * Note that this service is not wrapped in a transaction.
     * Each product record imported is in its own transaction, so it can store as many good records as possible.
     * <code>DataImportGlAccount</code>.classification attribute corespond to 
     * <code>GlAccountClassTypeMap</code>.glAccountClassTypeKey entity attribute.
     * If organizationPartyId input is not null, then creating <code>GlAccountOrganization</code> 
     * entity items when importing.
     *
     * @throws ServiceException if an error occurs
     */
    public void importCategory() throws ServiceException;

}
