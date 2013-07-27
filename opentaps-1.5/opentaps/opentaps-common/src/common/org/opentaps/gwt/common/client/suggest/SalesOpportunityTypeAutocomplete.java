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
package org.opentaps.gwt.common.client.suggest;

import org.opentaps.gwt.common.client.lookup.configuration.OpportunityTypeLookupConfiguration;

/**
 * Creates a new <code>SalesOpportunityTypeAutocomplete</code> instance.
 * Unlike other autocompleters, this widget offer no input, just a list of the possible values.
 */
public class SalesOpportunityTypeAutocomplete extends EntityStaticAutocomplete {

    /**
     * Creates a new <code>SalesOpportunityStageAutocomplete</code> instance.
     * Unlike other autocompleters, this widget offer no input, just a list of the possible values.
     * @param fieldLabel the field label
     * @param name the field name used in the form
     * @param fieldWidth the field size in pixels
     */
    public SalesOpportunityTypeAutocomplete(String fieldLabel, String name, int fieldWidth) {
        super(fieldLabel, name, fieldWidth, OpportunityTypeLookupConfiguration.URL_SUGGEST_CLASSIFICATIONS , OpportunityTypeLookupConfiguration.OUT_SEQUENCE_ID);
    }
}
