package w.dao.populator.entity;

public class EntityMeta<T> {

   private final String tableName;   
   private FieldPopulator<T> idPopulator;
   private FieldPopulator<T> versionPopulator;
   private Iterable<FieldPopulator<T>> populators;
   
   public EntityMeta(String tableName) {
      this.tableName = tableName;
   }

   public String getTableName() {
      return tableName;
   }
   
   public FieldPopulator<T> getIdPopulator() {
      return idPopulator;
   }
   
   public FieldPopulator<T> getVersionPopulator() {
      return versionPopulator;
   }
   
   public Iterable<FieldPopulator<T>> getPopulators() {
      return populators;
   }

   public void setIdPopulator(FieldPopulator<T> idPopulator) {
      this.idPopulator = idPopulator;
   }

   public void setVersionPopulator(FieldPopulator<T> versionPopulator) {
      this.versionPopulator = versionPopulator;
   }

   public void setPopulators(Iterable<FieldPopulator<T>> populators) {
      this.populators = populators;
   }
}
