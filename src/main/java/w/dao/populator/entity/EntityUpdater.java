package w.dao.populator.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import w.dao.DaoHelper;


public class EntityUpdater<T> extends AbstractEntityPopulator<T> {

   EntityMeta<T> entityMeta;
   
   FieldGenerator<T> versionGenerator;
   
   public EntityUpdater(EntityMeta<T> entityMeta) {
      this(entityMeta, null);
   }
   
   public EntityUpdater(EntityMeta<T> entityMeta, FieldGenerator<T> versionGenerator) {
      super();
      this.entityMeta = entityMeta;
      this.versionGenerator = versionGenerator;
   }
   
   @Override
   protected String getSql() {
      return getUpdateSql(
            entityMeta.getTableName(), 
            entityMeta.getIdPopulator(), 
            entityMeta.getVersionPopulator(),
            entityMeta.getPopulators());
   }

   @Override
   protected boolean populateStatement(PreparedStatement preparedStatement, int rowNumber, T entity) throws SQLException {
      FieldPopulator<T> idFpp = entityMeta.getIdPopulator();
      FieldPopulator<T> verdionFpp = entityMeta.getVersionPopulator();
      
      Object oldVersion = null;

      int index = 1;
      for (FieldPopulator<T> accessor : entityMeta.getPopulators()) {
         if (idFpp != null && idFpp.equals(accessor)) {
            continue;
         }

         // New Version
         if (versionGenerator != null && verdionFpp != null) {
            // remember old version
            oldVersion = verdionFpp.getValue(entity);

            // set new version
            verdionFpp.setValue(entity,versionGenerator.generate(entity));
         }

         setValue(preparedStatement, index++, accessor.getValue(entity), accessor.getSqlType());        
      }

      // set new version
      if (verdionFpp!=null) {
         setValue(preparedStatement, index++, verdionFpp.getValue(entity), verdionFpp.getSqlType());
      }
      
      // where id
      setValue(preparedStatement, index++, idFpp.getValue(entity), idFpp.getSqlType());
      
      // where old version
      if (verdionFpp!=null) {
         setValue(preparedStatement, index++, oldVersion, verdionFpp.getSqlType());
      }

      return true;
   }

   public static String getUpdateSql(String tableName, FieldPopulator<?> idName, FieldPopulator<?> versionName, Iterable<? extends FieldPopulator<?>> fieldNames) {
      StringBuilder sb = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
      
      Object linker = DaoHelper.criteriaLinker("", ",");
      for(FieldPopulator<?> fieldName: fieldNames) {
         sb.append(linker).append(fieldName.getName()).append("=?");

      }
      if (versionName != null) {
         sb.append(",").append(versionName.getName()).append("=?");
      }
      
      sb.append(" WHERE ").append(idName.getName()).append("=?");
      
      if (versionName != null) {
         sb.append(" AND ").append(versionName.getName()).append("=?");
      }
      return sb.toString();
   }
}
