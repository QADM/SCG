package org.opentaps.dataimport.domain;

import java.util.List;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.opentaps.base.constants.StatusItemConstants;
import org.opentaps.base.entities.DataImportEgresoDiario;
import org.opentaps.domain.DomainRepository;
import org.opentaps.domain.dataimport.EgresoDiarioDataImportRepositoryInterface;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.User;
import org.opentaps.foundation.repository.RepositoryException;

/** {@inheritDoc}. */
public class EgresoDiarioDataImportRepository extends DomainRepository
		implements EgresoDiarioDataImportRepositoryInterface {

	/**
	 * Default constructor.
	 */
	public EgresoDiarioDataImportRepository() {
		super();
	}

	/**
	 * If you want the full infrastructure including the dispatcher, then you
	 * must have the User.
	 * 
	 * @param infrastructure
	 *            the domain infrastructure
	 * @param user
	 *            the domain user
	 * @throws RepositoryException
	 *             if an error occurs
	 */
	public EgresoDiarioDataImportRepository(Infrastructure infrastructure,
			User user) throws RepositoryException {
		super(infrastructure, user);
	}

	/** {@inheritDoc}. */
	public List<DataImportEgresoDiario> findNotProcessesDataImportEgresoDiarioEntries()
			throws RepositoryException {
		EntityCondition statusCond = EntityCondition.makeCondition(
				EntityOperator.OR, EntityCondition.makeCondition(
						"importStatusId", EntityOperator.EQUALS,
						StatusItemConstants.Dataimport.DATAIMP_NOT_PROC),
				EntityCondition.makeCondition("importStatusId",
						EntityOperator.EQUALS,
						StatusItemConstants.Dataimport.DATAIMP_FAILED),
				EntityCondition.makeCondition("importStatusId",
						EntityOperator.EQUALS, null));
		return this.findList(DataImportEgresoDiario.class, statusCond);
	}
}
