package org.opentaps.dataimport.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.opentaps.base.entities.AcctgTrans;
import org.opentaps.base.entities.AcctgTransEntry;
import org.opentaps.base.entities.AcctgTransPresupuestal;
import org.opentaps.base.entities.DataImportOperacionDiaria;
import org.opentaps.base.entities.GlAccountOrganization;
import org.opentaps.base.entities.Party;
import org.opentaps.base.entities.TipoDocumento;
import org.opentaps.dataimport.UtilImport;
import org.opentaps.domain.DomainService;
import org.opentaps.domain.dataimport.OperacionDiariaDataImportRepositoryInterface;
import org.opentaps.domain.ledger.LedgerRepositoryInterface;
import org.opentaps.foundation.entity.hibernate.Session;
import org.opentaps.foundation.entity.hibernate.Transaction;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.service.ServiceException;

import com.ibm.icu.util.Calendar;

public class OperacionDiariaService extends DomainService{
	private Session session;
	
	public Map<String, Object> registraDiario(Map<String, Object> context) throws ServiceException{
	
		Map<String,Object> output = null;
		String tTrans_S= (String) context.get("tTrans");
		Date fechaRegistro_S= (Date) context.get("fechaRegistro");
		Date fechaContable_S= (Date) context.get("fechaContable");
		BigDecimal monto_S= (BigDecimal) context.get("monto");
		String organizacionContable_S= (String) context.get("organizacionContable");
		String organizacionEjecutora_S= (String) context.get("organizacionEjecutora");
		String tipoDocumento_S= (String) context.get("tipoDocumento");
		String refDoc_S= (String) context.get("refDoc");
		String secuencia_S= (String) context.get("secuencia");
		String usuario_S= (String) context.get("usuario");
		String lote_S= (String) context.get("lote");
		String concepto_S= (String) context.get("concepto");
		String subConcepto_S= (String) context.get("subConcepto");
		String tipoCatalogoC_S= (String) context.get("tipoCatalogoC");
		String idC_S= (String) context.get("idC");
		String tipoCatalogoD_S= (String) context.get("tipoCatalogoD");
		String idD_S= (String) context.get("idD");
		
		try {
		this.session = this.getInfrastructure().getSession();
		OperacionDiariaDataImportRepositoryInterface imp_repo = this
				.getDomainsDirectory().getDataImportDomain()
				.getOperacionDiariaDataImportRepository();
		LedgerRepositoryInterface ledger_repo = this.getDomainsDirectory()
				.getLedgerDomain().getLedgerRepository();

		
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

		
			// Empieza bloque de validaciones
			String mensaje = "";
			Debug.log("Empieza bloque de validaciones");
			mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					organizacionEjecutora_S,
					"Organizacion Ejecutora");
			mensaje = UtilImport.validaTipoDoc(mensaje, ledger_repo,
					tipoDocumento_S);

			if (!mensaje.isEmpty()) {
				String message = "Failed to import Operacion Diaria ["
						+ refDoc_S + secuencia_S
						+ "], Error message : " + mensaje;
				//storeImportOperacionDiariaError(rowdata, message, imp_repo);
				//continue;
			}

			// Creacion de objetos
			Debug.log("Empieza creacion de objetos");
			Party ue = UtilImport.obtenParty(ledger_repo,
					organizacionEjecutora_S);
			TipoDocumento tipoDoc = UtilImport.obtenTipoDocumento(
					ledger_repo, tipoDocumento_S);

			Debug.log("Motor Contable");
			MotorContable motor = new MotorContable(ledger_repo);
			Map<String, String> cuentas = motor.cuentasDiarias(
					tipoDoc.getAcctgTransTypeId(), null, null,
					organizacionContable_S, null, null,
					tipoCatalogoC_S, idC_S,
					tipoCatalogoD_S, idD_S, null,
					false, concepto_S, subConcepto_S,
					null);
			try {

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
				cal.setTime(fechaRegistro_S);
				OperacionDiaria.setTransactionDate(new Timestamp(cal
						.getTimeInMillis()));
				OperacionDiaria.setIsPosted("Y");
				cal.setTime(fechaContable_S);
				OperacionDiaria.setPostedDate(new Timestamp(cal
						.getTimeInMillis()));
				OperacionDiaria.setAcctgTransTypeId(tipoDoc
						.getAcctgTransTypeId());
				OperacionDiaria.setLastModifiedByUserLogin(usuario_S);
				OperacionDiaria.setPartyId(ue.getPartyId());
				OperacionDiaria.setPostedAmount(monto_S);

				// ACCTG_TRANS_PRESUPUESTAL
				AcctgTransPresupuestal aux = new AcctgTransPresupuestal();
				aux.setUnidadEjecutora(ue.getPartyId());
				aux.setAgrupador(refDoc_S);
				aux.setIdTipoDoc(tipoDocumento_S);
				aux.setSecuencia(secuencia_S);
				aux.setLote(lote_S);

				if (cuentas.get("Cuenta Cargo Presupuesto") != null) {
					Debug.log("Cuenta Presupuestal");
					OperacionDiaria.setDescription(tipoDoc.getDescripcion()
							+ "-" + refDoc_S + "-P");

					// id Transaccion
					OperacionDiaria.setAcctgTransId(UtilImport
							.getAcctgTransIdDiario(refDoc_S,
									secuencia_S, "P"));

					AcctgTrans trans = ledger_repo.findOne(
							AcctgTrans.class, ledger_repo.map(
									AcctgTrans.Fields.acctgTransId,
									OperacionDiaria.getAcctgTransId()));

					if (trans != null) {
						Debug.log("Trans Modif");
						String message = "La transaccion con id: "
								+ OperacionDiaria.getAcctgTransId()
								+ "ya existe.";
						Debug.log(message);
						//storeImportOperacionDiariaError(rowdata, message,
							//	imp_repo);
						//continue;
					}

					Debug.log("Trans Nueva");
					OperacionDiaria.setCreatedByUserLogin(usuario_S);
					OperacionDiaria.setGlFiscalTypeId(cuentas
							.get("GlFiscalTypePresupuesto"));
					imp_tx1 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(OperacionDiaria);
					imp_tx1.commit();

					aux.setAcctgTransId(OperacionDiaria.getAcctgTransId());
					imp_tx3 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(aux);
					imp_tx3.commit();

					AcctgTransEntry acctgentry = UtilImport
							.generaAcctgTransEntry(
									OperacionDiaria,
									organizacionContable_S,
									"00001",
									"D",
									cuentas.get("Cuenta Cargo Presupuesto"),
									null);
					imp_tx5 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx5.commit();

					GlAccountOrganization glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(
									ledger_repo,
									monto_S,
									cuentas.get("Cuenta Cargo Presupuesto"),
									organizacionContable_S);
					imp_tx7 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx7.commit();

					acctgentry = UtilImport.generaAcctgTransEntry(
							OperacionDiaria,
							organizacionContable_S, "00002", "C",
							cuentas.get("Cuenta Abono Presupuesto"), null);
					imp_tx9 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx9.commit();

					glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(
									ledger_repo,
									monto_S,
									cuentas.get("Cuenta Abono Presupuesto"),
									organizacionContable_S);
					imp_tx11 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx11.commit();

				}

				if (cuentas.get("Cuenta Cargo Contable") != null) {
					Debug.log("Cuenta Contable");
					OperacionDiaria.setDescription(tipoDoc.getDescripcion()
							+ "-" + refDoc_S + "-C");

					// id Transaccion
					OperacionDiaria.setAcctgTransId(UtilImport
							.getAcctgTransIdDiario(refDoc_S,
									secuencia_S, "P"));

					AcctgTrans trans = ledger_repo.findOne(
							AcctgTrans.class, ledger_repo.map(
									AcctgTrans.Fields.acctgTransId,
									OperacionDiaria.getAcctgTransId()));

					if (trans != null) {
						Debug.log("Trans Modif");
						String message = "La transaccion con id: "
								+ OperacionDiaria.getAcctgTransId()
								+ "ya existe.";
						Debug.log(message);
						//storeImportOperacionDiariaError(rowdata, message,
							//	imp_repo);
						//continue;
					}

					Debug.log("Trans Nueva");
					OperacionDiaria.setCreatedByUserLogin(usuario_S);
					OperacionDiaria.setGlFiscalTypeId(cuentas
							.get("GlFiscalTypeContable"));
					imp_tx2 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(OperacionDiaria);
					imp_tx2.commit();

					aux.setAcctgTransId(OperacionDiaria.getAcctgTransId());
					imp_tx4 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(aux);
					imp_tx4.commit();

					AcctgTransEntry acctgentry = UtilImport
							.generaAcctgTransEntry(OperacionDiaria,
									organizacionContable_S,
									"00001", "D",
									cuentas.get("Cuenta Cargo Contable"),
									null);
					imp_tx6 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx6.commit();

					GlAccountOrganization glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(ledger_repo,
									monto_S,
									cuentas.get("Cuenta Cargo Contable"),
									organizacionContable_S);
					imp_tx8 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx8.commit();

					acctgentry = UtilImport.generaAcctgTransEntry(
							OperacionDiaria,
							organizacionContable_S, "00002", "C",
							cuentas.get("Cuenta Abono Contable"), null);
					imp_tx10 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx10.commit();

					glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(ledger_repo,
									monto_S,
									cuentas.get("Cuenta Abono Contable"),
									organizacionContable_S);
					imp_tx12 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx12.commit();
				}

				if (mensaje.isEmpty()) {
					String message = "Successfully imported Operacion Diaria [";
					// + rowdata.getClavePres() + "].";
					//this.storeImportOperacionDiariaSuccess(rowdata,
						//	imp_repo);
					//Debug.logInfo(message, MODULE);
					//imported = imported + 1;
				}
			} catch (Exception ex) {
				String message = "Failed to import Operacion Diaria ["
				// + rowdata.getClavePres() + "], Error message : "
						+ ex.getMessage();
				//storeImportOperacionDiariaError(rowdata, message, imp_repo);

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

				//Debug.logError(ex, message, MODULE);
				throw new ServiceException(ex.getMessage());
			}
		
		//this.importedRecords = imported;

	} catch (InfrastructureException ex) {
		//Debug.logError(ex, MODULE);
		throw new ServiceException(ex.getMessage());
	} catch (RepositoryException ex) {
		//Debug.logError(ex, MODULE);
		throw new ServiceException(ex.getMessage());
	} finally {
		if (session != null) {
			session.close();
		}
	}
		return output;
}

}
