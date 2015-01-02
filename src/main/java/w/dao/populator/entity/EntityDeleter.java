package w.dao.populator.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EntityDeleter<T> extends AbstractEntityPopulator<T> {
   
   EntityMeta<T> entityMeta;

   public EntityDeleter(EntityMeta<T> entityMeta) {
      super();
      this.entityMeta = entityMeta;
   }

   @Override
   protected String getSql() {
      return getDeleteSql(
            entityMeta.getTableName(), 
            entityMeta.getIdPopulator(), 
            entityMeta.getVersionPopulator());
   }
   
   @Override
   protected boolean populateStatement(PreparedStatement preparedStatement, int rowNumber, T entity) throws SQLException {
      FieldPopulator<T> idFpp = entityMeta.getIdPopulator();
      FieldPopulator<T> verdionFpp = entityMeta.getVersionPopulator();
      
      // where id
      setValue(preparedStatement, 1, idFpp.getValue(entity), idFpp.getSqlType());

      // where version
      if (verdionFpp!=null) {
         setValue(preparedStatement, 2, verdionFpp.getValue(entity), verdionFpp.getSqlType());
      }

      return true;
   }
   
   public static String getDeleteSql(String tableName, FieldPopulator<?> idName, FieldPopulator<?> versionName) {
      StringBuilder sb = new StringBuilder("DELETE FROM ").append(tableName);
      
      sb.append(" WHERE ").append(idName.getName()).append("=?");
      
      if (versionName != null) {
         sb.append(" AND ").append(versionName.getName()).append("=?");
      }
      
      return sb.toString();
   }
}
