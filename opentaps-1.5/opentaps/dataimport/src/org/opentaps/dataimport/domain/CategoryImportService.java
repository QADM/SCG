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
import org.opentaps.base.entities.DataImportCategory;
import org.opentaps.base.entities.ProductCategory;
import org.opentaps.dataimport.UtilImport;
import org.opentaps.domain.DomainService;
import org.opentaps.domain.dataimport.CategoryDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.CategoryImportServiceInterface;
import org.opentaps.domain.ledger.LedgerRepositoryInterface;
import org.opentaps.foundation.entity.hibernate.Session;
import org.opentaps.foundation.entity.hibernate.Transaction;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.infrastructure.User;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.service.ServiceException;

/**
 * Import Categories via intermediate DataImportCategory entity.
 */
public class CategoryImportService extends DomainService implements
		CategoryImportServiceInterface {

	private static final String MODULE = CategoryImportService.class.getName();
	// session object, using to store/search pojos.
	private Session session;
	public String organizationPartyId;
	public int importedRecords;

	public CategoryImportService() {
		super();
	}

	public CategoryImportService(Infrastructure infrastructure, User user,
			Locale locale) throws ServiceException {
		super(infrastructure, user, locale);
	}

	/** {@inheritDoc} */
	public int getImportedRecords() {
		return importedRecords;
	}

	/** {@inheritDoc} */
	public void importCategory() throws ServiceException {
		try {
			this.session = this.getInfrastructure().getSession();

			CategoryDataImportRepositoryInterface imp_repo = this
					.getDomainsDirectory().getDataImportDomain()
					.getCategoryDataImportRepository();
			LedgerRepositoryInterface ledger_repo = this.getDomainsDirectory()
					.getLedgerDomain().getLedgerRepository();

			List<DataImportCategory> dataforimp = imp_repo
					.findNotProcessesDataImportCategoryEntries();

			int imported = 0;
			Transaction imp_tx1 = null;
			for (DataImportCategory rowdata : dataforimp) {
				// import categories as many as possible
				try {
					imp_tx1 = null;

					// begin importing row data item
					ProductCategory category = new ProductCategory();

					// Buscar Parent Id del tag
					List<ProductCategory> listcategory = ledger_repo.findList(
							ProductCategory.class, ledger_repo.map(
									ProductCategory.Fields.productCategoryId,
									rowdata.getProductCategoryId()));

					if (listcategory.isEmpty()) {
						Debug.log("Registro Nuevo");
						category.setProductCategoryId(rowdata
								.getProductCategoryId());
					} else {
						category.setProductCategoryId(listcategory.get(0)
								.getProductCategoryId());
					}

					category.setProductCategoryId(rowdata
							.getProductCategoryId());
					category.setProductCategoryTypeId(rowdata
							.getProductCategoryTypeId());
					category.setCategoryName(rowdata.getCode());
					category.setDescription(rowdata.getCategoryName());
					category.setNode(rowdata.getNode());

					imp_tx1 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(category);
					imp_tx1.commit();
					String message = "Successfully imported Category ["
							+ rowdata.getProductCategoryId() + "].";
					this.storeImportCategorySuccess(rowdata, imp_repo);
					Debug.logInfo(message, MODULE);

					imported = imported + 1;

				} catch (Exception ex) {
					String message = "Failed to import Category ["
							+ rowdata.getProductCategoryId()
							+ "], Error message : " + ex.getMessage();
					storeImportCategoryError(rowdata, message, imp_repo);

					// rollback all if there was an error when importing item
					if (imp_tx1 != null) {
						imp_tx1.rollback();
					}

					Debug.logError(ex, message, MODULE);
					throw new ServiceException(ex.getMessage());
				}
			}

			this.importedRecords = imported;

			// ingresar padre
			getParentCategory(ledger_repo, dataforimp, imp_repo);

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

	/*
	 * Impactar Parent en ProductCategory
	 */
	private void getParentCategory(LedgerRepositoryInterface ledger_repo,
			List<DataImportCategory> dataforimp, CategoryDataImportRepositoryInterface imp_repo) {

		Transaction imp_tx1 = null;

		try {

			for (DataImportCategory dataImportCategory : dataforimp) {

				if (dataImportCategory.getPrimaryParentCategoryId() != null) {
					List<ProductCategory> listcategory = ledger_repo
							.findList(
									ProductCategory.class,
									ledger_repo
											.map(ProductCategory.Fields.productCategoryId,
													dataImportCategory
															.getProductCategoryId(),
													ProductCategory.Fields.productCategoryTypeId,
													dataImportCategory
															.getProductCategoryTypeId()));

					if (!listcategory.isEmpty()) {
						for (ProductCategory productCategory : listcategory) {

							if (UtilImport.validaPadreProductCategory(
									ledger_repo, dataImportCategory
											.getProductCategoryTypeId(),
									dataImportCategory
											.getPrimaryParentCategoryId())) {
								Debug.log("Padre valido");
								productCategory
										.setPrimaryParentCategoryId(dataImportCategory
												.getPrimaryParentCategoryId());
							} else {
								Debug.log("Padre no valido");
								String message = "Failed to import Category ["
										+ dataImportCategory
												.getProductCategoryTypeId()
										+ "], Error message : "
										+ "Padre no valido";
								storeImportCategoryError(dataImportCategory,
										message, imp_repo);

								// rollback all if there was an error when
								// importing item
								if (imp_tx1 != null) {
									imp_tx1.rollback();
								}
								// Debug.logError(ex, message, MODULE);
								throw new ServiceException(message);
							}

							imp_tx1 = this.session.beginTransaction();
							ledger_repo.createOrUpdate(productCategory);
							imp_tx1.commit();

							String message = "Successfully to import Parent Category Id ["
									+ productCategory.getProductCategoryId()
									+ "].";
							Debug.logInfo(message, MODULE);
						}
					}
				}
			}
		} catch (Exception e) {
			String message = "Failed to import Parent Category Id, Error message : "
					+ e.getMessage();

			if (imp_tx1 != null) {
				imp_tx1.rollback();
			}
			Debug.logError(e, message, MODULE);
		}

	}

	/**
	 * Helper method to store Category import success into
	 * <code>DataImportCategory</code> entity row.
	 * 
	 * @param rowdata
	 *            item of <code>DataImportCategory</code> entity that was
	 *            successfully imported
	 * @param imp_repo
	 *            repository of accounting
	 * @throws org.opentaps.foundation.repository.RepositoryException
	 */
	private void storeImportCategorySuccess(DataImportCategory rowdata,
			CategoryDataImportRepositoryInterface imp_repo)
			throws RepositoryException {
		// mark as success
		rowdata.setImportStatusId(StatusItemConstants.Dataimport.DATAIMP_IMPORTED);
		rowdata.setImportError(null);
		rowdata.setProcessedTimestamp(UtilDateTime.nowTimestamp());
		imp_repo.createOrUpdate(rowdata);
	}

	/**
	 * Helper method to store Category import error into
	 * <code>DataImportCategory</code> entity row.
	 * 
	 * @param rowdata
	 *            item of <code>DataImportCategory</code> entity that was
	 *            unsuccessfully imported
	 * @param message
	 *            error message
	 * @param imp_repo
	 *            repository of accounting
	 * @throws org.opentaps.foundation.repository.RepositoryException
	 */
	private void storeImportCategoryError(DataImportCategory rowdata,
			String message, CategoryDataImportRepositoryInterface imp_repo)
			throws RepositoryException {
		// store the exception and mark as failed
		rowdata.setImportStatusId(StatusItemConstants.Dataimport.DATAIMP_FAILED);
		rowdata.setImportError(message);
		rowdata.setProcessedTimestamp(UtilDateTime.nowTimestamp());
		imp_repo.createOrUpdate(rowdata);
	}

}
