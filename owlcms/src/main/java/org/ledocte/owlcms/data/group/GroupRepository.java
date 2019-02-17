/***
 * Copyright (c) 2018-2019 Jean-François Lamy
 * 
 * This software is licensed under the the Apache 2.0 License amended with the
 * Commons Clause.
 * License text at https://github.com/jflamy/owlcms4/master/License
 * See https://redislabs.com/wp-content/uploads/2018/10/Commons-Clause-White-Paper.pdf
 */
package org.ledocte.owlcms.data.group;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.ledocte.owlcms.data.jpa.JPAService;

/**
 * The Class GroupRepository.
 *
 * @author Alejandro Duarte
 */
public class GroupRepository {

	/**
	 * Gets the by id.
	 *
	 * @param id the id
	 * @param em the em
	 * @return the by id
	 */
	@SuppressWarnings("unchecked")
	public static Group getById(Long id, EntityManager em) {
		Query query = em.createQuery("select u from CompetitionGroup u where u.id=:id");
		query.setParameter("id", id);
		return (Group) query.getResultList()
			.stream()
			.findFirst()
			.orElse(null);
	}

	/**
	 * Save.
	 *
	 * @param Group the group
	 * @return the group
	 */
	public static Group save(Group Group) {
		return JPAService.runInTransaction(em -> em.merge(Group));
	}

	/**
	 * Delete.
	 *
	 * @param Group the group
	 */
	public static void delete(Group Group) {
		JPAService.runInTransaction(em -> {
			em.remove(getById(Group.getId(), em));
			return null;
		});
	}

	/**
	 * Find all.
	 *
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public static List<Group> findAll() {
		return JPAService.runInTransaction(em -> em.createQuery("select c from CompetitionGroup c")
			.getResultList());
	}

	@SuppressWarnings("unchecked")
	public static Group findByName(String name) {
		return JPAService.runInTransaction(em -> {
			Query query = em.createQuery("select u from CompetitionGroup u where u.name=:name");
			query.setParameter("name", name);
			return (Group) query.getResultList()
				.stream()
				.findFirst()
				.orElse(null);
		});
	}

}
