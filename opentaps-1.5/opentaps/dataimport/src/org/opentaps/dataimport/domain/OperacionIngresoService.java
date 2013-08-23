package org.opentaps.dataimport.domain;

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
		Map<String,Object> output = null;
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
        String ue_S= (String) context.get("ue");
        String n5_S= (String) context.get("n5");
        String sfe_S= (String) context.get("sfe");
        String loc_S= (String) context.get("loc");
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
			mensaje = UtilImport.validaTipoDoc(mensaje, ledger_repo,
					tipoDocumento);
			mensaje = UtilImport.validaCiclo(mensaje, ciclo,
					fechaContable);

			mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					ue_S, "ADMINISTRATIVA");
			mensaje = UtilImport
					.validaProductCategory(mensaje, ledger_repo,
							n5_S, "N5", "RUBRO DEL INGRESO");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					sfe_S, "CLAS_FR", "FUENTE DE LOS RECURSOS");
			mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					loc_S, "GEOGRAFICA");

			if (mensaje == null) {
				String message = "Failed to import Ingreso Diario ["
						+ concatenacion + "], Error message : "
						+ mensaje;
				//storeImportIngresoDiarioError(rowdata, message, imp_repo);
			}
			
			// Creacion de objetos
			Debug.log("Empieza creacion de objetos");
			TipoDocumento tipoDoc = UtilImport.obtenTipoDocumento(
					ledger_repo, tipoDocumento);

			Party ue = UtilImport.obtenParty(ledger_repo, ue_S);
			ProductCategory n5 = UtilImport.obtenProductCategory(
					ledger_repo, n5_S, "N5");
			Enumeration sfe = UtilImport.obtenEnumeration(ledger_repo,
					sfe_S, "CLAS_FR");
			Geo loc = UtilImport.obtenGeo(ledger_repo, loc_S);

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
			
			// Obtenemos los padres de cada nivel.
			String uo = UtilImport.obtenPadreParty(ledger_repo,
					ue.getPartyId());
			String ur = UtilImport.obtenPadreParty(ledger_repo, uo);
			String con = UtilImport.obtenPadreProductCategory(
					ledger_repo, n5.getProductCategoryId());
			String cla = UtilImport.obtenPadreProductCategory(
					ledger_repo, con);
			String tip = UtilImport.obtenPadreProductCategory(
					ledger_repo, cla);
			String rub = UtilImport.obtenPadreProductCategory(
					ledger_repo, tip);
			String sf = UtilImport.obtenPadreEnumeration(
					ledger_repo, sfe.getEnumId());
			String f = UtilImport.obtenPadreEnumeration(
					ledger_repo, sf);
			String mun = UtilImport.obtenPadreGeo(ledger_repo,
					loc.getGeoId());
			String reg = UtilImport.obtenPadreGeo(ledger_repo, mun);
			String ef = UtilImport.obtenPadreGeo(ledger_repo, reg);

			Debug.log("Motor Contable");
			MotorContable motor = new MotorContable(ledger_repo);
			Map<String, String> cuentas = motor.cuentasDiarias(
					tipoDoc.getAcctgTransTypeId(), null, null,
					organizacionContable, null,
					n5.getProductCategoryId(), tipoCatalogo,
					idPago, null, null, tip,
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
				aux.setUnidadResponsable(ur);
				aux.setUnidadOrganizacional(uo);
				aux.setUnidadEjecutora(ue.getPartyId());
				aux.setRubro(rub);
				aux.setTipo(tip);
				aux.setClase(cla);
				aux.setConceptoRub(con);
				aux.setNivel5(n5.getProductCategoryId());
				aux.setFuente(f);
				aux.setSubFuente(sf);
				aux.setSubFuenteEspecifica(sfe.getEnumId());
				aux.setEntidadFederativa(ef);
				aux.setRegion(reg);
				aux.setMunicipio(mun);
				aux.setLocalidad(loc.getGeoId());
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

					ingresoDiario.setGlFiscalTypeId(cuentas
							.get("GlFiscalTypePresupuesto"));
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
									organizacionContable,"D");
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
									organizacionContable,"A");
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
							.get("GlFiscalTypeContable"));
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
									organizacionContable,"D");
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
									organizacionContable,"A");
					imp_tx12 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx12.commit();
				}

				if (mensaje.isEmpty()) {
				String message = "Successfully imported Ingreso Diario ["
						+ concatenacion + "].";
				//this.storeImportIngresoDiarioSuccess(rowdata, imp_repo);
				//Debug.logInfo(message, MODULE);
				//imported = imported + 1;
				output = ServiceUtil.returnSuccess();
				output.put("messageOut", "Registro Exitoso");
				}
				
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
        

