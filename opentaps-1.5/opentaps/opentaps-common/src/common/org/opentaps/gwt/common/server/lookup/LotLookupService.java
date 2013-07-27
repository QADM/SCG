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
package org.opentaps.gwt.common.server.lookup;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opentaps.base.entities.Lot;
import org.opentaps.foundation.entity.EntityInterface;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.gwt.common.client.lookup.configuration.LotLookupConfiguration;
import org.opentaps.gwt.common.server.HttpInputProvider;
import org.opentaps.gwt.common.server.InputProviderInterface;

/**
 * The RPC service used to populate Lot autocompleters widgets.
 */
public class LotLookupService extends EntityLookupAndSuggestService {

    protected LotLookupService(InputProviderInterface provider) {
        super(provider,
              Arrays.asList(LotLookupConfiguration.OUT_LOT_ID,
                      LotLookupConfiguration.OUT_SUPPLIER_PARTY_ID,
                      LotLookupConfiguration.OUT_CREATION_DATE,
                      LotLookupConfiguration.OUT_EXPIRATION_DATE));
    }

    /**
     * AJAX event to suggest Case Priority.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the resulting JSON response
     * @throws InfrastructureException if an error occurs
     */
    public static String suggestLots(HttpServletRequest request, HttpServletResponse response) throws InfrastructureException {
        InputProviderInterface provider = new HttpInputProvider(request);
        JsonResponse json = new JsonResponse(response);
        LotLookupService service = new LotLookupService(provider);
        service.suggestLots();
        return json.makeSuggestResponse(LotLookupConfiguration.OUT_LOT_ID, service);
    }

    /**
     * Gets all Case status.
     * @return the list of Case status <code>StatusItem</code>
     */
    public List<Lot> suggestLots() {
        return findAll(Lot.class);
    }

    @Override
    public String makeSuggestDisplayedText(EntityInterface suggestStatus) {
        return suggestStatus.getString(LotLookupConfiguration.OUT_LOT_ID);
    }
}
