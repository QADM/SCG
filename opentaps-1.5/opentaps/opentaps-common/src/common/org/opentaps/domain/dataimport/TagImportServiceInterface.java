package org.opentaps.domain.dataimport;

import org.opentaps.foundation.service.ServiceException;
import org.opentaps.foundation.service.ServiceInterface;

public interface TagImportServiceInterface extends ServiceInterface {
	
    /**
     * Gets imported records count by service {@link #importTag}.
     * @return imported records count
     */
    public int getImportedRecords();
    
    /**
     * Import products using <code>DataImportTag</code>.
     * Note that this service is not wrapped in a transaction.
     * Each product record imported is in its own transaction, so it can store as many good records as possible.
     * <code>DataImportTag</code>.classification attribute corespond to 
     * <code>TagClassTypeMap</code>.tagClassTypeKey entity attribute.
     * If organizationPartyId input is not null, then creating <code>TagOrganization</code> 
     * entity items when importing.
     *
     * @throws ServiceException if an error occurs
     */
    public void importTag() throws ServiceException;
}
