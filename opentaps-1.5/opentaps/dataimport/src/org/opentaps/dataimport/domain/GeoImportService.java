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
package org.opentaps.dataimport.domain;

import java.util.List;
import org.ofbiz.base.util.Debug;
import java.util.Locale;
import org.ofbiz.base.util.UtilDateTime;
import org.opentaps.base.constants.StatusItemConstants;
import org.opentaps.base.entities.DataImportGeo;
import org.opentaps.base.entities.Geo;
import org.opentaps.dataimport.UtilImport;
import org.opentaps.domain.DomainService;
import org.opentaps.domain.dataimport.GeoDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.GeoImportServiceInterface;
import org.opentaps.domain.ledger.LedgerRepositoryInterface;
import org.opentaps.foundation.entity.hibernate.Session;
import org.opentaps.foundation.entity.hibernate.Transaction;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.infrastructure.User;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.service.ServiceException;

/**
 * Import Geographics via intermediate DataImportGeo entity. Author: Jesus
 * Rodrigo Ruiz Merlin.
 */
public class GeoImportService extends DomainService implements
		GeoImportServiceInterface {

	private static final String MODULE = GeoImportService.class.getName();
	// session object, using to store/search pojos.
	private Session session;
	public String organizationPartyId;
	public int importedRecords;

	public GeoImportService() {
		super();
	}

	public GeoImportService(Infrastructure infrastructure, User user,
			Locale locale) throws ServiceException {
		super(infrastructure, user, locale);
	}

	/** {@inheritDoc} */
	public int getImportedRecords() {
		return importedRecords;
	}

	/** {@inheritDoc} */
	public void importGeo() throws ServiceException {
		try {
			this.session = this.getInfrastructure().getSession();

			GeoDataImportRepositoryInterface imp_repo = this
					.getDomainsDirectory().getDataImportDomain()
					.getGeoDataImportRepository();
			LedgerRepositoryInterface ledger_repo = this.getDomainsDirectory()
					.getLedgerDomain().getLedgerRepository();

			List<DataImportGeo> dataforimp = imp_repo
					.findNotProcessesDataImportGeoEntries();

			int imported = 0;
			Transaction imp_tx1 = null;
			for (DataImportGeo rowdata : dataforimp) {
				// import geo as many as possible
				try {
					imp_tx1 = null;

					// begin importing row data item

					Geo geo = new Geo();
					geo.setGeoId(rowdata.getGeoId());
					geo.setGeoTypeId(rowdata.getGeoTypeId());
					geo.setGeoName(rowdata.getGeoName());
					// geo.setGeoCode(rowdata.getGeoCode());
					geo.setAbbreviation(rowdata.getAbbreviation());
					geo.setNode(rowdata.getNode());

					imp_tx1 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(geo);
					imp_tx1.commit();
					String message = "Successfully imported GEO ["
							+ rowdata.getGeoId() + "].";
					this.storeImportGeoSuccess(rowdata, imp_repo);
					Debug.logInfo(message, MODULE);

					imported = imported + 1;

				} catch (Exception ex) {
					String message = "Failed to import GEO ["
							+ rowdata.getGeoId() + "], Error message : "
							+ ex.getMessage();
					storeImportGeoError(rowdata, message, imp_repo);

					// rollback all if there was an error when importing item
					if (imp_tx1 != null) {
						imp_tx1.rollback();
					}

					Debug.logError(ex, message, MODULE);
					throw new ServiceException(ex.getMessage());
				}
			}

			this.importedRecords = imported;

			getParentGeo(ledger_repo, dataforimp, imp_repo);

		} catch (InfrastructureException ex) {
			Debug.logError(ex, MODULE);
			throw new ServiceException(ex.getMessage());
		} catch (RepositoryException ex) {
			Debug.logError(ex, MODULE);
			throw new ServiceException(ex.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	private void getParentGeo(LedgerRepositoryInterface ledger_repo,
			List<DataImportGeo> dataforimp, GeoDataImportRepositoryInterface imp_repo) {

		Transaction imp_tx1 = null;
		try {

			for (DataImportGeo dataImportGeo : dataforimp) {

				if (dataImportGeo.getGeoCode() != null) {
					List<Geo> listGeo = ledger_repo.findList(Geo.class,
							ledger_repo.map(Geo.Fields.geoId,
									dataImportGeo.getGeoId(),
									Geo.Fields.geoName,
									dataImportGeo.getGeoName()));
					if (!listGeo.isEmpty()) {

						for (Geo geo : listGeo) {

							if (UtilImport.validaPadreGeo(ledger_repo,
									dataImportGeo.getGeoTypeId(),
									dataImportGeo.getGeoCode())) {
								Debug.log("Padre valido");
								geo.setGeoCode(dataImportGeo.getGeoCode());
							} else {
								Debug.log("Padre no valido");
								String message = "Failed to import Geo ["
										+ dataImportGeo.getGeoId()
										+ "], Error message : "
										+ "Padre no valido";
								storeImportGeoError(dataImportGeo, message,
										imp_repo);

								// rollback all if there was an error when
								// importing item
								if (imp_tx1 != null) {
									imp_tx1.rollback();
								}
								// Debug.logError(ex, message, MODULE);
								throw new ServiceException(message);
							}

							imp_tx1 = this.session.beginTransaction();
							ledger_repo.createOrUpdate(geo);
							imp_tx1.commit();

							String message = "Successfully to import Geo Code ["
									+ geo.getGeoId() + "].";
							Debug.logInfo(message, MODULE);
						}

					}

				}
			}

		} catch (Exception e) {
			String message = "Failed to import Geo Code, Error message : "
					+ e.getMessage();

			if (imp_tx1 != null) {
				imp_tx1.rollback();
			}
			Debug.logError(e, message, MODULE);
		}

	}

	/**
	 * Helper method to store Geo import success into <code>DataImportGeo</code>
	 * entity row.
	 * 
	 * @param rowdata
	 *            item of <code>DataImportGlAccount</code> entity that was
	 *            successfully imported
	 * @param imp_repo
	 *            repository of accounting
	 * @throws org.opentaps.foundation.repository.RepositoryException
	 */
	private void storeImportGeoSuccess(DataImportGeo rowdata,
			GeoDataImportRepositoryInterface imp_repo)
			throws RepositoryException {
		// mark as success
		rowdata.setImportStatusId(StatusItemConstants.Dataimport.DATAIMP_IMPORTED);
		rowdata.setImportError(null);
		rowdata.setProcessedTimestamp(UtilDateTime.nowTimestamp());
		imp_repo.createOrUpdate(rowdata);
	}

	/**
	 * Helper method to store Geo import error into <code>DataImportGeo</code>
	 * entity row.
	 * 
	 * @param rowdata
	 *            item of <code>DataImportGeo</code> entity that was
	 *            unsuccessfully imported
	 * @param message
	 *            error message
	 * @param imp_repo
	 *            repository of accounting
	 * @throws org.opentaps.foundation.repository.RepositoryException
	 */
	private void storeImportGeoError(DataImportGeo rowdata, String message,
			GeoDataImportRepositoryInterface imp_repo)
			throws RepositoryException {
		// store the exception and mark as failed
		rowdata.setImportStatusId(StatusItemConstants.Dataimport.DATAIMP_FAILED);
		rowdata.setImportError(message);
		rowdata.setProcessedTimestamp(UtilDateTime.nowTimestamp());
		imp_repo.createOrUpdate(rowdata);
	}

}
