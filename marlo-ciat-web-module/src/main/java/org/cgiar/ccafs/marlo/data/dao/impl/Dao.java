/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

/**
 * 
 */
package org.cgiar.ccafs.marlo.data.dao.impl;

import org.cgiar.ccafs.marlo.data.dao.IDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

/**
 * Modified by @author nmatovu last on Sep 29, 2016
 */
public class Dao<T> implements IDao<T> {

  private Class<T> clazz;
  private Provider<EntityManager> emp;
  private Provider<EntityManagerFactory> emfp;

  @SuppressWarnings("unchecked")
  @Inject
  protected Dao(TypeLiteral<T> type, Provider<EntityManager> emp, Provider<EntityManagerFactory> emfp) {
    // this.clazz = (Class<T>) checkNotNull(type, "type cannot be null.")
    // .getRawType();
    // this.emp = checkNotNull(emp, "emp cannot be null.");
    // this.emfp = checkNotNull(emfp, "emfp cannot be null.");
  }

  // @Override
  // public <K> T find(K id) {
  // // checkNotNull(id, "id cannot be null.");
  // return emp.get().find(clazz, id);
  // }
  //
  // @Override
  // public <K> List<T> find(SingularAttribute<T, K> property, K value) {
  // // checkNotNull(property, "properties cannot be null.");
  // // checkNotNull(value, "properties cannot be null.");
  // return this.find(property, value, Optional.<Integer>absent(), Optional.<Integer>absent(),
  // Optional.<Order>absent());
  // }

  // @Override
  // public <K> List<T> find(SingularAttribute<T, K> property, K value, Optional<Integer> offset, Optional<Integer>
  // limit,
  // Optional<Order> order) {
  // // checkNotNull(property, "properties cannot be null.");
  // // checkNotNull(value, "properties cannot be null.");
  // // checkNotNull(offset, "offset cannot be null.");
  // // checkNotNull(limit, "limit cannot be null");
  // // checkNotNull(order, "order cannot be null");
  // Map<SingularAttribute<T, K>, K> properties =
  // ImmutableMap.<SingularAttribute<T, K>, K>builder().put(property, value).build();
  // TypedQuery<T> query = this.getTypedQuery(properties, order);
  // this.setQueryLimits(query, offset, limit);
  // return query.getResultList();
  // }

  // @Override
  // public List<T> findAll() {
  // TypedQuery<T> q = emp.get().createQuery("select e from " + clazz.getSimpleName() + " e", clazz);
  // return q.getResultList();
  // }
  //
  // private <Y> TypedQuery<T> getTypedQuery(Map<SingularAttribute<T, Y>, Y> properties, Optional<Order> order) {
  // CriteriaBuilder cb = emp.get().getCriteriaBuilder();
  // CriteriaQuery<T> cq = cb.createQuery(this.clazz);
  // Root<T> from = cq.from(this.clazz);
  // for (SingularAttribute<T, Y> property : properties.keySet()) {
  // cq.where(cb.equal(from.get(property), properties.get(property)));
  // }
  // if (order.isPresent()) {
  // cq.orderBy(order.get());
  // }
  // TypedQuery<T> tQuery = emp.get().createQuery(cq);
  // return tQuery;
  // }

  // @Transactional
  // @Override
  // public void remove(T t) {
  // checkNotNull(t, "t cannot be null.");
  // emp.get().remove(t);
  // emp.get().flush();
  // }
  //
  // @Transactional
  // @Override
  // public T save(T t) {
  // checkNotNull(t, "t cannot be null.");
  // emp.get().persist(t);
  // return t;
  // }
  //
  // @Transactional
  // @Override
  // public T saveOrUpdate(T t) {
  // checkNotNull(t, "t cannot be null.");
  // Object id = emfp.get().getPersistenceUnitUtil().getIdentifier(t);
  // if (id != null) {
  // t = emp.get().merge(t);
  // } else {
  // emp.get().persist(t);
  // }
  // return t;
  // }

  // private void setQueryLimits(Query q, Optional<Integer> offset, Optional<Integer> count) {
  // if (offset.isPresent()) {
  // q.setFirstResult(offset.get());
  // }
  // if (count.isPresent()) {
  // q.setMaxResults(count.get());
  // }
  // }
}
