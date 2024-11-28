package com.sippy.wrapper.parent.database;

import com.sippy.wrapper.parent.database.dao.TnbDao;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class DatabaseConnection {

  @PersistenceContext(unitName = "CustomDB")
  private EntityManager entityManager;

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnection.class);

  public int countTheEntries() {
    return ((Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM tnbs").getSingleResult())
        .intValue();
  }

  @SuppressWarnings("unchecked")
  public List<TnbDao> getAllTnbs() {
    Query query = entityManager.createNativeQuery("SELECT * FROM tnbs", TnbDao.class);
    return query.getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<TnbDao> getNumberTnbs(String number) {
    Query query = entityManager.createNativeQuery(String.format("SELECT tnb FROM tnbs WHERE tnb = %", number), TnbDao.class);
    return query.getResultList();
  }
}
