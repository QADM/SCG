package org.opentaps.financials.domain.ledger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.base.entities.AcctgTrans;
import org.opentaps.base.entities.AcctgTransEntry;
import org.opentaps.base.entities.AcctgTransPresupuestal;
import org.opentaps.base.entities.DataImportIngresoDiario;
import org.opentaps.base.entities.Enumeration;
import org.opentaps.base.entities.Geo;
import org.opentaps.base.entities.GlAccountOrganization;
import org.opentaps.base.entities.Party;
import org.opentaps.base.entities.PartyGroup;
import org.opentaps.base.entities.ProductCategory;
import org.opentaps.base.entities.TipoDocumento;
import org.opentaps.dataimport.UtilImport;
import org.opentaps.dataimport.domain.MotorContable;
import org.opentaps.domain.DomainService;
import org.opentaps.domain.dataimport.IngresoDiarioDataImportRepositoryInterface;
import org.opentaps.domain.ledger.LedgerRepositoryInterface;
import org.opentaps.foundation.entity.hibernate.Session;
import org.opentaps.foundation.entity.hibernate.Transaction;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.service.ServiceException;

import com.ibm.icu.util.Calendar;

public class OperacionIngresoService extends DomainService  {
	
	private Session session;
	
	public Map<String, Object> registraIngreso(Map<String, Object> context) throws ServiceException
	{
		Map<String,Object> output;
		String tipoDocumento= (String) context.get("tipoDocumento");
        Date fechaRegistro= (Date) context.get("fechaRegistro");
        Date fechaContable= (Date) context.get("fechaContable");
        BigDecimal monto= (BigDecimal) context.get("monto");
        String organizacionContable= (String) context.get("organizacionContable");
        String refDoc= (String) context.get("refDoc");
        String secuencia= (String) context.get("secuencia");
        String usuario= (String) context.get("usuario");
        String lote= (String) context.get("lote");
        String tipoCatalogo= (String) context.get("tipoCatalogo");
        String idPago= (String) context.get("idPago");
        String idProducto= (String) context.get("idProducto");
        String ciclo= (String) context.get("ciclo");
        String adm1= (String) context.get("adm1");
        String adm2= (String) context.get("adm2");
        String adm3= (String) context.get("adm3");
        String cri1= (String) context.get("cri1");
        String cri2= (String) context.get("cri2");
        String cri3= (String) context.get("cri3");
        String cri4= (String) context.get("cri4");
        String cri5= (String) context.get("cri5");
        String rec1= (String) context.get("rec1");
        String rec2= (String) context.get("rec2");
        String rec3= (String) context.get("rec3");
        String geo1= (String) context.get("geo1");
        String geo2= (String) context.get("geo2");
        String geo3= (String) context.get("geo3");
        String geo4= (String) context.get("geo4");
        String concatenacion= (String) context.get("concatenacion");
        
        try{
        	this.session = this.getInfrastructure().getSession();
        	IngresoDiarioDataImportRepositoryInterface imp_repo = this
					.getDomainsDirectory().getDataImportDomain()
					.getIngresoDiarioDataImportRepository();
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
			
			String mensaje = null;
			Debug.log("Empieza bloque de validaciones");
			mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					adm1, "UR");
			mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					adm2, "UO");
			mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					adm3, "UE");
			mensaje = UtilImport.validaProductCategory(mensaje,
					ledger_repo, cri1, "RU", "RUB");
			mensaje = UtilImport.validaProductCategory(mensaje,
					ledger_repo, cri2, "TI", "TIP");
			mensaje = UtilImport.validaProductCategory(mensaje,
					ledger_repo, cri3, "CL", "CLA");
			mensaje = UtilImport.validaProductCategory(mensaje,
					ledger_repo, cri4, "CO", "CON");
			mensaje = UtilImport.validaProductCategory(mensaje,
					ledger_repo, cri5, "N5", "N5");
			mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					geo1, "EF");
			mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					geo2, "REG");
			mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					geo3, "MUN");
			mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					geo4, "LOC");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					rec1, "CLAS_FR", "F");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					rec2, "CLAS_FR", "SF");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					rec3, "CLAS_FR", "SFE");
			mensaje = UtilImport.validaTipoDoc(mensaje, ledger_repo,
					tipoDocumento);
			mensaje = UtilImport.validaCiclo(mensaje, ciclo,
					fechaContable);

			if (mensaje == null) {
				String message = "Failed to import Ingreso Diario ["
						+ concatenacion + "], Error message : "
						+ mensaje;
				//storeImportIngresoDiarioError(rowdata, message, imp_repo);
			}
			
			// Creacion de objetos
			Debug.log("Empieza creacion de objetos");
			Party ur = UtilImport.obtenParty(ledger_repo, adm1);
			Party uo = UtilImport.obtenParty(ledger_repo, adm2);
			Party ue = UtilImport.obtenParty(ledger_repo, adm3);
			ProductCategory rub = UtilImport.obtenProductCategory(
					ledger_repo, cri1, "RU");
			ProductCategory tip = UtilImport.obtenProductCategory(
					ledger_repo, cri2, "TI");
			ProductCategory cla = UtilImport.obtenProductCategory(
					ledger_repo, cri3, "CL");
			ProductCategory con = UtilImport.obtenProductCategory(
					ledger_repo, cri4, "CO");
			ProductCategory n5 = UtilImport.obtenProductCategory(
					ledger_repo, cri5, "N5");
			Geo ef = UtilImport.obtenGeo(ledger_repo, geo1);
			Geo reg = UtilImport.obtenGeo(ledger_repo, geo2);
			Geo mun = UtilImport.obtenGeo(ledger_repo, geo3);
			Geo loc = UtilImport.obtenGeo(ledger_repo, geo4);
			Enumeration f = UtilImport.obtenEnumeration(ledger_repo,
					rec1, "CLAS_FR");
			Enumeration sf = UtilImport.obtenEnumeration(ledger_repo,
					rec2, "CLAS_FR");
			Enumeration sfe = UtilImport.obtenEnumeration(ledger_repo,
					rec3, "CLAS_FR");
			TipoDocumento tipoDoc = UtilImport.obtenTipoDocumento(
					ledger_repo, tipoDocumento);

			// Empieza bloque de vigencias
			Debug.log("Empieza bloque de vigencias");
			mensaje = UtilImport.validaVigencia(mensaje, "SFE", sfe,
					fechaContable);

			if (mensaje == null) {
				String message = "Failed to import Ingreso Diario ["
						+ concatenacion + "], Error message : "
						+ mensaje;
				//storeImportIngresoDiarioError(rowdata, message, imp_repo);
			}

			Debug.log("Motor Contable");
			MotorContable motor = new MotorContable(ledger_repo);
			Map<String, String> cuentas = motor.cuentasDiarias(
					tipoDoc.getAcctgTransTypeId(), null, null,
					organizacionContable, null,
					n5.getProductCategoryId(), tipoCatalogo,
					idPago, null, null, cri2,
					false, null, null, idProducto);

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

				AcctgTrans ingresoDiario = new AcctgTrans();
				Calendar cal = Calendar.getInstance();
				cal.setTime(fechaRegistro);
				ingresoDiario.setTransactionDate(new Timestamp(cal
						.getTimeInMillis()));
				ingresoDiario.setIsPosted("Y");
				cal.setTime(fechaContable);
				ingresoDiario.setPostedDate(new Timestamp(cal
						.getTimeInMillis()));
				ingresoDiario.setAcctgTransTypeId(tipoDoc
						.getAcctgTransTypeId());
				ingresoDiario.setLastModifiedByUserLogin(usuario);
				ingresoDiario.setPartyId(ue.getPartyId());
				ingresoDiario.setPostedAmount(monto);
				ingresoDiario.setDescription(tipoDoc.getDescripcion() + "-"
						+ refDoc + "-P");

				// ACCTG_TRANS_PRESUPUESTAL
				AcctgTransPresupuestal aux = new AcctgTransPresupuestal();
				aux.setCiclo(ciclo);
				aux.setUnidadResponsable(ur.getPartyId());
				aux.setUnidadOrganizacional(uo.getPartyId());
				aux.setUnidadEjecutora(ue.getPartyId());
				aux.setRubro(rub.getProductCategoryId());
				aux.setTipo(tip.getProductCategoryId());
				aux.setClase(cla.getProductCategoryId());
				aux.setConceptoRub(con.getProductCategoryId());
				aux.setNivel5(n5.getProductCategoryId());
				aux.setFuente(f.getEnumId());
				aux.setSubFuente(sf.getEnumId());
				aux.setSubFuenteEspecifica(sfe.getEnumId());
				aux.setEntidadFederativa(geo1);
				aux.setRegion(geo2);
				aux.setMunicipio(geo3);
				aux.setLocalidad(geo4);
				aux.setAgrupador(refDoc);
				aux.setIdTipoDoc(tipoDocumento);
				aux.setSecuencia(secuencia);
				aux.setLote(lote);
				aux.setClavePres(concatenacion);

				if (cuentas.get("Cuenta Cargo Presupuesto") != null) {
					Debug.log("Cuenta Presupuestal");
					ingresoDiario.setDescription(tipoDoc.getDescripcion()
							+ "-" + refDoc + "-P");

					// id Transaccion
					ingresoDiario.setAcctgTransId(UtilImport
							.getAcctgTransIdDiario(refDoc,
									secuencia, "P"));

					AcctgTrans trans = ledger_repo.findOne(
							AcctgTrans.class, ledger_repo.map(
									AcctgTrans.Fields.acctgTransId,
									ingresoDiario.getAcctgTransId()));

					if (trans != null) {
						Debug.log("Trans Modif");
						String message = "La transaccion con id: "
								+ ingresoDiario.getAcctgTransId()
								+ "ya existe.";
						Debug.log(message);
						//storeImportIngresoDiarioError(rowdata, message,
								//imp_repo);
					}

					Debug.log("Trans Nueva");
					ingresoDiario.setCreatedByUserLogin(usuario);

					ingresoDiario.setGlFiscalTypeId("BUDGET");
					imp_tx1 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(ingresoDiario);
					imp_tx1.commit();

					aux.setAcctgTransId(ingresoDiario.getAcctgTransId());
					imp_tx3 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(aux);
					imp_tx3.commit();

					AcctgTransEntry acctgentry = UtilImport
							.generaAcctgTransEntry(
									ingresoDiario,
									organizacionContable,
									"00001",
									"D",
									cuentas.get("Cuenta Cargo Presupuesto"),
									sfe.getEnumId());
					imp_tx5 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx5.commit();

					GlAccountOrganization glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(
									ledger_repo,
									monto,
									cuentas.get("Cuenta Cargo Presupuesto"),
									organizacionContable);
					imp_tx7 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx7.commit();

					acctgentry = UtilImport.generaAcctgTransEntry(
							ingresoDiario,
							organizacionContable, "00002", "C",
							cuentas.get("Cuenta Abono Presupuesto"),
							sfe.getEnumId());
					imp_tx9 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx9.commit();

					glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(
									ledger_repo,
									monto,
									cuentas.get("Cuenta Abono Presupuesto"),
									organizacionContable);
					imp_tx11 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx11.commit();

				}

				if (cuentas.get("Cuenta Cargo Contable") != null) {
					Debug.log("Cuenta Contable");
					ingresoDiario.setDescription(tipoDoc.getDescripcion()
							+ "-" + refDoc + "-C");

					// id Transaccion
					ingresoDiario.setAcctgTransId(UtilImport
							.getAcctgTransIdDiario(refDoc,
									secuencia, "C"));

					AcctgTrans trans = ledger_repo.findOne(
							AcctgTrans.class, ledger_repo.map(
									AcctgTrans.Fields.acctgTransId,
									ingresoDiario.getAcctgTransId()));

					if (trans != null) {
						Debug.log("Trans Modif");
						String message = "La transaccion con id: "
								+ ingresoDiario.getAcctgTransId()
								+ "ya existe.";
						Debug.log(message);
						//storeImportIngresoDiarioError(rowdata, message,
							//	imp_repo);
						//continue;
					}

					Debug.log("Trans Nueva");
					ingresoDiario.setCreatedByUserLogin(usuario);

					ingresoDiario.setGlFiscalTypeId(cuentas
							.get("GlFiscalType"));
					imp_tx2 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(ingresoDiario);
					imp_tx2.commit();

					aux.setAcctgTransId(ingresoDiario.getAcctgTransId());
					imp_tx4 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(aux);
					imp_tx4.commit();

					AcctgTransEntry acctgentry = UtilImport
							.generaAcctgTransEntry(ingresoDiario,
									organizacionContable,
									"00001", "D",
									cuentas.get("Cuenta Cargo Contable"),
									sfe.getEnumId());
					imp_tx6 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx6.commit();

					GlAccountOrganization glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(ledger_repo,
									monto,
									cuentas.get("Cuenta Cargo Contable"),
									organizacionContable);
					imp_tx8 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx8.commit();

					acctgentry = UtilImport.generaAcctgTransEntry(
							ingresoDiario,
							organizacionContable, "00002", "C",
							cuentas.get("Cuenta Abono Contable"),
							sfe.getEnumId());
					imp_tx10 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx10.commit();

					glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(ledger_repo,
									monto,
									cuentas.get("Cuenta Abono Contable"),
									organizacionContable);
					imp_tx12 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx12.commit();
				}

				String message = "Successfully imported Ingreso Diario ["
						+ concatenacion + "].";
				//this.storeImportIngresoDiarioSuccess(rowdata, imp_repo);
				//Debug.logInfo(message, MODULE);
				//imported = imported + 1;
				output = ServiceUtil.returnSuccess();
				output.put("messageOut", "Registro Exitoso");
				
		}
			catch (InfrastructureException ex) {
				//Debug.logError(ex, MODULE);
				output = ServiceUtil.returnError(ex.getMessage());
				throw new ServiceException(ex.getMessage());
			} catch (RepositoryException ex) {
				//Debug.logError(ex, MODULE);
				output = ServiceUtil.returnError(ex.getMessage());
				throw new ServiceException(ex.getMessage());
			} finally {
				if (session != null) {
					session.close();
				}
			}
			return output;
		}
	}
        

