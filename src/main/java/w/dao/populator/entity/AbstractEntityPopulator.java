package w.dao.populator.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.dao.DaoHelper;
import w.utils.MathUtils;

public abstract class AbstractEntityPopulator<T> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntityPopulator.class);  
   
   public static interface FieldGenerator<T> {
      Object generate(T t);
   }
   
   protected abstract String getSql();
   
   protected abstract boolean populateStatement(PreparedStatement preparedStatement, int rowNumber, T entity) throws SQLException;
  
   public void populate(DataSource dataSource, Iterable<T> entities) throws SQLException {
      Connection connection = dataSource.getConnection();
      try {
         populate(connection,entities);
         
      } finally {
         DaoHelper.close(connection);
      }
   }

   public void populate(Connection connection, Iterable<T> entities) throws SQLException {
      String sql = getSql();
      LOGGER.debug("Executing {}", sql);

      PreparedStatement preparedStatement = null;
      T current = null;
      int rowNumber = 0;
      int[] inserted;
      try {

         preparedStatement = connection.prepareStatement(sql);

         // loop over rows
         for (T entity : entities) {
            current = entity;
            rowNumber++;

            if (!populateStatement(preparedStatement, rowNumber, entity)) {
               // cancel populating
               return;
            }

            preparedStatement.addBatch();
         }

         // Execute batch
         inserted = preparedStatement.executeBatch();

      } catch (SQLException e) {
         String errorMessage = "Could not populate statement number " + rowNumber + " for entity " + current;
         LOGGER.error(errorMessage, e);

         throw new SQLException(errorMessage, e);
      } finally {
         DaoHelper.close(preparedStatement);
      }  
      
      // do checks
      if (inserted.length != rowNumber) {
         // TODO
      }
      
      if (MathUtils.sum(inserted) != rowNumber) {
         // TODO
      }
   }

   protected void setValue(PreparedStatement preparedStatement, int columnIndex, Object value, int sqlType) throws SQLException {
      if (value == null) {
         preparedStatement.setNull(columnIndex, sqlType);
      } else {
         preparedStatement.setObject(columnIndex, value, sqlType);
      }
   }
}
