package org.opentaps.dataimport.domain;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.opentaps.base.constants.StatusItemConstants;
import org.opentaps.base.entities.AcctgTrans;
import org.opentaps.base.entities.AcctgTransEntry;
import org.opentaps.base.entities.AcctgTransPresupuestal;
import org.opentaps.base.entities.AcctgTransType;
import org.opentaps.base.entities.DataImportOperacionDiaria;
import org.opentaps.base.entities.GlAccountOrganization;
import org.opentaps.base.entities.Party;
import org.opentaps.base.entities.PaymentMethod;
import org.opentaps.base.entities.TipoDocumento;
import org.opentaps.domain.DomainService;
import org.opentaps.domain.dataimport.OperacionDiariaDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.OperacionDiariaImportServiceInterface;
import org.opentaps.domain.ledger.LedgerRepositoryInterface;
import org.opentaps.foundation.entity.hibernate.Session;
import org.opentaps.foundation.entity.hibernate.Transaction;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.infrastructure.User;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.service.ServiceException;

import com.ibm.icu.util.Calendar;

public class OperacionDiariaImportService extends DomainService implements
		OperacionDiariaImportServiceInterface {
	private static final String MODULE = PresupuestoIngresoImportService.class
			.getName();
	// session object, using to store/search pojos.
	private Session session;
	public int importedRecords;

	public OperacionDiariaImportService() {
		super();
	}

	public OperacionDiariaImportService(Infrastructure infrastructure,
			User user, Locale locale) throws ServiceException {
		super(infrastructure, user, locale);
	}

	/** {@inheritDoc} */
	public int getImportedRecords() {
		return importedRecords;
	}

	private String validaParty(
			OperacionDiariaDataImportRepositoryInterface imp_repo,
			LedgerRepositoryInterface ledger_repo,
			DataImportOperacionDiaria rowdata, String id, String campo)
			throws RepositoryException {
		List<Party> parties = ledger_repo.findList(Party.class,
				ledger_repo.map(Party.Fields.externalId, id));
		if (parties.isEmpty()) {
			Debug.log("Error, " + campo + " no existe");
			String message = "Failed to import Operacion Diaria ["
			// + rowdata.getClavePres() + "], Error message : " + campo
					+ " no existe";
			Debug.log(message);
			Debug.log("despues de message");
			storeImportOperacionDiariaError(rowdata, message, imp_repo);
			throw new RepositoryException("Error, " + campo + " no existe");
		}
		return parties.get(0).getPartyId();
	}

	private void validaTipoTrans(
			OperacionDiariaDataImportRepositoryInterface imp_repo,
			LedgerRepositoryInterface ledger_repo,
			DataImportOperacionDiaria rowdata, String tipo)
			throws RepositoryException {
		AcctgTransType type = ledger_repo.findOne(AcctgTransType.class,
				ledger_repo.map(AcctgTransType.Fields.acctgTransTypeId, tipo));
		if (type == null) {
			Debug.log("Error, tipoTrans no existe");
			String message = "Failed to import Operacion Diaria ["
			// + rowdata.getClavePres() + "], Error message : "
					+ "tipoTrans no existe";
			storeImportOperacionDiariaError(rowdata, message, imp_repo);
			throw new RepositoryException("Error, tipoTrans no existe");
		}
	}

	private TipoDocumento validaTipoDoc(
			OperacionDiariaDataImportRepositoryInterface imp_repo,
			LedgerRepositoryInterface ledger_repo,
			DataImportOperacionDiaria rowdata, String tipo)
			throws RepositoryException {
		TipoDocumento type = ledger_repo.findOne(TipoDocumento.class,
				ledger_repo.map(TipoDocumento.Fields.idTipoDoc, tipo));
		if (type == null) {
			Debug.log("Error, tipoDoc no existe");
			String message = "Failed to import Operacion Diaria ["
			// + rowdata.getClavePres() + "], Error message : "
					+ "tipoDoc no existe";
			storeImportOperacionDiariaError(rowdata, message, imp_repo);
			throw new RepositoryException("Error, tipoDoc no existe");
		}
		return type;
	}

	private void validaPago(
			OperacionDiariaDataImportRepositoryInterface imp_repo,
			LedgerRepositoryInterface ledger_repo,
			DataImportOperacionDiaria rowdata, String tipo)
			throws RepositoryException {
		PaymentMethod payment = ledger_repo.findOne(PaymentMethod.class,
				ledger_repo.map(PaymentMethod.Fields.paymentMethodId, tipo));
		if (payment == null) {
			Debug.log("Error, idPago no existe");
			String message = "Failed to import Operacion Diaria ["
			// + rowdata.getClavePres() + "], Error message : "
					+ "idPago no existe";
			storeImportOperacionDiariaError(rowdata, message, imp_repo);
			throw new RepositoryException("Error, idPago no existe");
		}
	}

	private AcctgTransEntry generaAcctgTransEntry(AcctgTrans transaccion,
			DataImportOperacionDiaria rowdata, String seqId, String flag,
			String cuenta) {
		// C/D
		Debug.log("Empieza AcctgTransEntry " + cuenta);

		AcctgTransEntry acctgentry = new AcctgTransEntry();
		acctgentry.setAcctgTransId(transaccion.getAcctgTransId());
		acctgentry.setAcctgTransEntrySeqId(seqId);
		acctgentry.setAcctgTransEntryTypeId("_NA_");
		acctgentry.setDescription(acctgentry.getDescription());
		acctgentry.setGlAccountId(cuenta);
		acctgentry.setOrganizationPartyId(rowdata.getOrganizationPartyId());
		acctgentry.setAmount(transaccion.getPostedAmount());
		acctgentry.setCurrencyUomId("MXN");
		acctgentry.setDebitCreditFlag(flag);
		acctgentry.setReconcileStatusId("AES_NOT_RECONCILED");
		return acctgentry;
	}

	private GlAccountOrganization actualizaGlAccountOrganization(
			LedgerRepositoryInterface ledger_repo,
			DataImportOperacionDiaria rowdata, String cuenta)
			throws RepositoryException {

		// GlAccountOrganization
		Debug.log("Empieza GlAccountOrganization " + cuenta);
		GlAccountOrganization glAccountOrganization = ledger_repo.findOne(
				GlAccountOrganization.class, ledger_repo.map(
						GlAccountOrganization.Fields.glAccountId, cuenta,
						GlAccountOrganization.Fields.organizationPartyId,
						rowdata.getOrganizationPartyId()));

		if (glAccountOrganization.getPostedBalance() == null) {
			glAccountOrganization.setPostedBalance(rowdata.getMonto());
		} else {
			glAccountOrganization.setPostedBalance(glAccountOrganization
					.getPostedBalance().add(rowdata.getMonto()));
		}
		return glAccountOrganization;
	}

	private void storeImportOperacionDiariaSuccess(
			DataImportOperacionDiaria rowdata,
			OperacionDiariaDataImportRepositoryInterface imp_repo)
			throws RepositoryException {
		// mark as success
		rowdata.setImportStatusId(StatusItemConstants.Dataimport.DATAIMP_IMPORTED);
		rowdata.setImportError(null);
		rowdata.setProcessedTimestamp(UtilDateTime.nowTimestamp());
		imp_repo.createOrUpdate(rowdata);
	}

	private void storeImportOperacionDiariaError(
			DataImportOperacionDiaria rowdata, String message,
			OperacionDiariaDataImportRepositoryInterface imp_repo)
			throws RepositoryException {
		// store the exception and mark as failed
		rowdata.setImportStatusId(StatusItemConstants.Dataimport.DATAIMP_FAILED);
		rowdata.setImportError(message);
		rowdata.setProcessedTimestamp(UtilDateTime.nowTimestamp());
		imp_repo.createOrUpdate(rowdata);
	}

	/** {@inheritDoc} */
	public void importOperacionDiaria() throws ServiceException {
		try {
			this.session = this.getInfrastructure().getSession();
			OperacionDiariaDataImportRepositoryInterface imp_repo = this
					.getDomainsDirectory().getDataImportDomain()
					.getOperacionDiariaDataImportRepository();
			LedgerRepositoryInterface ledger_repo = this.getDomainsDirectory()
					.getLedgerDomain().getLedgerRepository();

			List<DataImportOperacionDiaria> dataforimp = imp_repo
					.findNotProcessesDataImportOperacionDiariaEntries();

			int imported = 0;
			Transaction imp_tx1 = null;
			Transaction imp_tx2 = null;
			Transaction imp_tx3 = null;
			Transaction imp_tx4 = null;
			Transaction imp_tx5 = null;
			Transaction imp_tx6 = null;
			Transaction imp_tx7 = null;
			Transaction imp_tx8 = null;
			Transaction imp_tx9 = null;
			Transaction imp_tx10 = null;
			Transaction imp_tx11 = null;
			Transaction imp_tx12 = null;

			for (DataImportOperacionDiaria rowdata : dataforimp) {
				// Empieza bloque de validaciones
								Debug.log("Empieza bloque de validaciones");
				String ue = validaParty(imp_repo, ledger_repo, rowdata,
						rowdata.getOrganizacionEjecutora(),
						"Organizacion Ejecutora");
				Debug.log("Valida tipoDoc");
				TipoDocumento tipoDoc = validaTipoDoc(imp_repo, ledger_repo,
						rowdata, rowdata.getIdTipoDoc());
				Debug.log("Motor Contable");
//				if (rowdata.getIdC() != null) {
//					validaPago(imp_repo, ledger_repo, rowdata, rowdata.getIdC());
//				}
//
//				if (rowdata.getIdD() != null) {
//					validaPago(imp_repo, ledger_repo, rowdata, rowdata.getIdD());
//				}

				MotorContable motor = new MotorContable(ledger_repo);
				Map<String, String> cuentas = motor.cuentasDiarias(
						tipoDoc.getAcctgTransTypeId(), null, null,
						rowdata.getOrganizationPartyId(), null, null,
						rowdata.getIdTipoCatalogoC(), rowdata.getIdC(),
						rowdata.getIdTipoCatalogoD(), rowdata.getIdD(), null,
						false, rowdata.getConcepto(), rowdata.getSubconcepto(), null);
				try {

					// id maximo
					Debug.log("Busqueda idMax");
					String id = ledger_repo.getNextSeqId("AcctgTrans");
					imp_tx1 = null;
					imp_tx2 = null;
					imp_tx3 = null;
					imp_tx4 = null;
					imp_tx5 = null;
					imp_tx6 = null;
					imp_tx7 = null;
					imp_tx8 = null;
					imp_tx9 = null;
					imp_tx10 = null;
					imp_tx11 = null;
					imp_tx12 = null;

					AcctgTrans OperacionDiaria = new AcctgTrans();

					Calendar cal = Calendar.getInstance();
					cal.setTime(rowdata.getFechaRegistro());
					OperacionDiaria.setTransactionDate(new Timestamp(cal
							.getTimeInMillis()));
					OperacionDiaria.setIsPosted("Y");
					cal.setTime(rowdata.getFechaContable());
					OperacionDiaria.setPostedDate(new Timestamp(cal
							.getTimeInMillis()));
					OperacionDiaria.setAcctgTransTypeId(tipoDoc.getAcctgTransTypeId());
					OperacionDiaria.setLastModifiedByUserLogin(rowdata
							.getUsuario());
					OperacionDiaria.setPartyId(ue);
					OperacionDiaria.setPostedAmount(rowdata.getMonto());

					// ACCTG_TRANS_PRESUPUESTAL
					AcctgTransPresupuestal aux = new AcctgTransPresupuestal();
					aux.setUnidadEjecutora(ue);
					aux.setAgrupador(rowdata.getRefDoc());
					aux.setIdTipoDoc(rowdata.getIdTipoDoc());
					aux.setDescripcionTipoDoc(tipoDoc.getDescripcion());
					aux.setSecuencia(rowdata.getSecuencia());
					aux.setLote(rowdata.getLote());

					if (cuentas.get("Cuenta Cargo Presupuesto") != null) {
						Debug.log("Cuenta Presupuestal");
						OperacionDiaria.setDescription(tipoDoc.getDescripcion() + "-"
								+ rowdata.getRefDoc() + "-P");

						// id Transaccion
						List<AcctgTrans> trans = ledger_repo.findList(
								AcctgTrans.class, ledger_repo.map(
										AcctgTrans.Fields.description,
										OperacionDiaria.getDescription()));

						if (trans.isEmpty()) {
							Debug.log("Trans Nueva");
							OperacionDiaria.setAcctgTransId(id + "-P");
							OperacionDiaria.setCreatedByUserLogin(rowdata
									.getUsuario());
						} else {
							Debug.log("Trans Modif");
							OperacionDiaria.setAcctgTransId(trans.get(0)
									.getAcctgTransId());
							OperacionDiaria.setCreatedByUserLogin(trans.get(0)
									.getCreatedByUserLogin());
						}
						OperacionDiaria.setGlFiscalTypeId("BUDGET");
						imp_tx1 = this.session.beginTransaction();
						ledger_repo.createOrUpdate(OperacionDiaria);
						imp_tx1.commit();

						aux.setAcctgTransId(OperacionDiaria.getAcctgTransId());
						imp_tx3 = this.session.beginTransaction();
						ledger_repo.createOrUpdate(aux);
						imp_tx3.commit();

						AcctgTransEntry acctgentry = generaAcctgTransEntry(
								OperacionDiaria, rowdata, "00001", "D",
								cuentas.get("Cuenta Cargo Presupuesto"));
						imp_tx5 = this.session.beginTransaction();
						ledger_repo.createOrUpdate(acctgentry);
						imp_tx5.commit();

						GlAccountOrganization glAccountOrganization = actualizaGlAccountOrganization(
								ledger_repo, rowdata,
								cuentas.get("Cuenta Cargo Presupuesto"));
						imp_tx7 = this.session.beginTransaction();
						ledger_repo.createOrUpdate(glAccountOrganization);
						imp_tx7.commit();

						acctgentry = generaAcctgTransEntry(OperacionDiaria,
								rowdata, "00002", "C",
								cuentas.get("Cuenta Abono Presupuesto"));
						imp_tx9 = this.session.beginTransaction();
						ledger_repo.createOrUpdate(acctgentry);
						imp_tx9.commit();

						glAccountOrganization = actualizaGlAccountOrganization(
								ledger_repo, rowdata,
								cuentas.get("Cuenta Abono Presupuesto"));
						imp_tx11 = this.session.beginTransaction();
						ledger_repo.createOrUpdate(glAccountOrganization);
						imp_tx11.commit();

					}

					if (cuentas.get("Cuenta Cargo Contable") != null) {
						Debug.log("Cuenta Contable");
						OperacionDiaria.setDescription(tipoDoc.getDescripcion() + "-"
								+ rowdata.getRefDoc() + "-C");

						// id Transaccion
						List<AcctgTrans> trans = ledger_repo.findList(
								AcctgTrans.class, ledger_repo.map(
										AcctgTrans.Fields.description,
										OperacionDiaria.getDescription()));

						if (trans.isEmpty()) {
							Debug.log("Trans Nueva");
							OperacionDiaria.setAcctgTransId(id + "-C");
							OperacionDiaria.setCreatedByUserLogin(rowdata
									.getUsuario());
						} else {
							Debug.log("Trans Modif");
							OperacionDiaria.setAcctgTransId(trans.get(0)
									.getAcctgTransId());
							OperacionDiaria.setCreatedByUserLogin(trans.get(0)
									.getCreatedByUserLogin());
						}

						OperacionDiaria.setGlFiscalTypeId(cuentas
								.get("GlFiscalType"));
						imp_tx2 = this.session.beginTransaction();
						ledger_repo.createOrUpdate(OperacionDiaria);
						imp_tx2.commit();

						aux.setAcctgTransId(OperacionDiaria.getAcctgTransId());
						imp_tx4 = this.session.beginTransaction();
						ledger_repo.createOrUpdate(aux);
						imp_tx4.commit();

						AcctgTransEntry acctgentry = generaAcctgTransEntry(
								OperacionDiaria, rowdata, "00001", "D",
								cuentas.get("Cuenta Cargo Contable"));
						imp_tx6 = this.session.beginTransaction();
						ledger_repo.createOrUpdate(acctgentry);
						imp_tx6.commit();

						GlAccountOrganization glAccountOrganization = actualizaGlAccountOrganization(
								ledger_repo, rowdata,
								cuentas.get("Cuenta Cargo Contable"));
						imp_tx8 = this.session.beginTransaction();
						ledger_repo.createOrUpdate(glAccountOrganization);
						imp_tx8.commit();

						acctgentry = generaAcctgTransEntry(OperacionDiaria,
								rowdata, "00002", "C",
								cuentas.get("Cuenta Abono Contable"));
						imp_tx10 = this.session.beginTransaction();
						ledger_repo.createOrUpdate(acctgentry);
						imp_tx10.commit();

						glAccountOrganization = actualizaGlAccountOrganization(
								ledger_repo, rowdata,
								cuentas.get("Cuenta Abono Contable"));
						imp_tx12 = this.session.beginTransaction();
						ledger_repo.createOrUpdate(glAccountOrganization);
						imp_tx12.commit();
					}

					String message = "Successfully imported Operacion Diaria [";
					// + rowdata.getClavePres() + "].";
					this.storeImportOperacionDiariaSuccess(rowdata, imp_repo);
					Debug.logInfo(message, MODULE);
					imported = imported + 1;

				} catch (Exception ex) {
					String message = "Failed to import Operacion Diaria ["
					// + rowdata.getClavePres() + "], Error message : "
							+ ex.getMessage();
					storeImportOperacionDiariaError(rowdata, message, imp_repo);

					// rollback all if there was an error when importing item
					if (imp_tx1 != null) {
						imp_tx1.rollback();
					}
					if (imp_tx2 != null) {
						imp_tx2.rollback();
					}
					if (imp_tx3 != null) {
						imp_tx3.rollback();
					}
					if (imp_tx4 != null) {
						imp_tx4.rollback();
					}
					if (imp_tx5 != null) {
						imp_tx5.rollback();
					}
					if (imp_tx6 != null) {
						imp_tx6.rollback();
					}
					if (imp_tx7 != null) {
						imp_tx7.rollback();
					}
					if (imp_tx8 != null) {
						imp_tx8.rollback();
					}
					if (imp_tx9 != null) {
						imp_tx9.rollback();
					}
					if (imp_tx10 != null) {
						imp_tx10.rollback();
					}
					if (imp_tx11 != null) {
						imp_tx11.rollback();
					}
					if (imp_tx12 != null) {
						imp_tx12.rollback();
					}

					Debug.logError(ex, message, MODULE);
					throw new ServiceException(ex.getMessage());
				}
			}
			this.importedRecords = imported;

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
}
