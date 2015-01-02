package w.dao.populator.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import w.dao.DaoHelper;

import com.google.common.base.Strings;

public class EntityInserter<T> extends AbstractEntityPopulator<T> {

   EntityMeta<T> entityMeta;

   FieldGenerator<T> idGenerator;
   
   FieldGenerator<T> versionGenerator;
   
   public EntityInserter(EntityMeta<T> entityMeta) {
      this(entityMeta, null, null);
   }
   
   public EntityInserter(EntityMeta<T> entityMeta, FieldGenerator<T> idGenerator) {
      this(entityMeta, idGenerator, null);
   }
   
   public EntityInserter(EntityMeta<T> entityMeta, FieldGenerator<T> idGenerator, FieldGenerator<T> versionGenerator) {
      super();
      this.entityMeta = entityMeta;
      this.idGenerator = idGenerator;
      this.versionGenerator = versionGenerator;
   }

   @Override
   protected String getSql() {
      return getInsertSql(
            entityMeta.getTableName(), 
            entityMeta.getIdPopulator(), 
            entityMeta.getVersionPopulator(),
            entityMeta.getPopulators());
   }

   @Override
   protected boolean populateStatement(PreparedStatement preparedStatement, int rowNumber, T entity) throws SQLException {
      FieldPopulator<T> idFpp = entityMeta.getIdPopulator();
      FieldPopulator<T> verdionFpp = entityMeta.getVersionPopulator();
      
      if (idGenerator != null && idFpp != null && idFpp.getValue(entity) == null) {
         idFpp.setValue(entity, idGenerator.generate(entity));
      }

      if (versionGenerator != null && verdionFpp != null && verdionFpp.getValue(entity) == null) {
         verdionFpp.setValue(entity, versionGenerator.generate(entity));
      }

      int index = 1;
      for (FieldPopulator<T> fpp : entityMeta.getPopulators()) {
         setValue(preparedStatement, index++, fpp.getValue(entity), fpp.getSqlType());
      }
      if (idFpp != null) { 
         setValue(preparedStatement, index++, idFpp.getValue(entity), idFpp.getSqlType());
      }
      if (verdionFpp != null) { 
         setValue(preparedStatement, index++, verdionFpp.getValue(entity), verdionFpp.getSqlType());
      }
      return true;
   }

   public static String getInsertSql(String tableName, FieldPopulator<?> idName, FieldPopulator<?> versionName, Iterable<? extends FieldPopulator<?>> fieldNames) {
      StringBuilder sb = new StringBuilder("INSERT INTO ").append(tableName);
      int size = 0;

      Object linker = DaoHelper.criteriaLinker(" (", ",");
      for (FieldPopulator<?> fieldName : fieldNames) {
         sb.append(linker).append(fieldName.getName());
         size++;
      }

      if (idName != null) {
         sb.append(linker).append(idName.getName());
         size++;
      }

      if (versionName != null) {
         sb.append(linker).append(versionName.getName());
         size++;
      }

      sb.append(") VALUES (?").append(Strings.repeat(",?", size - 1)).append(")");
      return sb.toString();
   }
}
