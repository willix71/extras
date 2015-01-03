package w.dao.populator.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import w.dao.populator.entity.FieldPopulator.AssignableFrom;
import w.dao.populator.entity.fields.DatePopulator;
import w.dao.populator.entity.fields.EnumPopulator;
import w.dao.populator.entity.fields.PrimitiveFieldPopulator;
import w.dao.populator.entity.fields.StringPopulator;

import com.google.common.base.Strings;

public class EntityMetaFactory {
   
   private Map<Class<?>, Constructor<? extends FieldPopulator<?>>> populatatorClasses = new LinkedHashMap<Class<?>, Constructor<? extends FieldPopulator<?>>>();
   
   private Constructor<? extends FieldPopulator<?>> defaultPopulatatorClass;
   
   public EntityMetaFactory() {
      setDefaultPopulatatorClasses(PrimitiveFieldPopulator.class);
      addPopulatatorClasses(StringPopulator.class, DatePopulator.class, EnumPopulator.class);          
   }

   public EntityMetaFactory(Class<?>... populatatorClasses) {
      this();
      addPopulatatorClasses(populatatorClasses);
   }
   
   public <T> EntityMeta<T>  getEntityMeta(Class<T> clazz) {
      EntityMeta<T> meta = new EntityMeta<T>(getTableName(clazz));
      meta.setPopulators(getFieldPopulators(clazz, meta));
      return meta;
   }
   
   
   public EntityMetaFactory addPopulatatorClasses(Class<?>... populatatorClasses) {
      for (Class<?> clazz : populatatorClasses) {
         addPopulatatorClass(clazz);
      }
      return this;
   }
   
   public EntityMetaFactory addPopulatatorClasses(Iterable<Class<?>> populatatorClasses) throws Exception {
      for (Class<?> clazz : populatatorClasses) {
         addPopulatatorClass(clazz);
      }
      return this;
   }
   
   public EntityMetaFactory addPopulatatorClass(Class<?> populatatorClass) {
      Class<?> type = populatatorClass.getAnnotation(AssignableFrom.class).type();
      Constructor<? extends FieldPopulator<?>> cnst = getConstructor(populatatorClass);      
      this.populatatorClasses.put(type, cnst);

      return this;
   }
   
   public EntityMetaFactory setDefaultPopulatatorClasses(Class<?> defaultPopulatatorClasses) {
      defaultPopulatatorClass = getConstructor(defaultPopulatatorClasses);
      return this;
   }
      
   protected Constructor<? extends FieldPopulator<?>> getConstructor(Class<?> populatatorClass)  {
      try {
         @SuppressWarnings("unchecked")
         Constructor<? extends FieldPopulator<?>> cnst = (Constructor<? extends FieldPopulator<?>>) populatatorClass.getConstructor(Class.class, Field.class, String.class);
         return cnst;
         
      } catch(NoSuchMethodException | SecurityException e) {
         throw new IllegalArgumentException("Cannot find required constructor for FieldPopulator "+ populatatorClass, e);
      }

   }
   
   protected <T> String getTableName(Class<T> clazz) {
      Table e = clazz.getAnnotation(Table.class);
      if (e == null) {
         return clazz.getSimpleName();
      } else {
         return Strings.isNullOrEmpty(e.schema()) ? e.name() : e.schema() + "." + e.name();
      }
   }
   
   /**
    * @return the list of FieldPopulator for the given class and and it's hierarchy
    */
   protected <T> Collection<FieldPopulator<T>> getFieldPopulators(Class<T> clazz, EntityMeta<T> meta) {
      Collection<FieldPopulator<T>> l = getFieldPopulatorsFor(clazz, meta);

      // get field from hierarchy
      Class<?> parent = clazz.getSuperclass();
      while (parent != null && parent != Object.class) {
         l.addAll(getFieldPopulatorsFor(parent, meta));
         parent = parent.getSuperclass();
      }
            
      return l;
   }
   
   /**
    * @return the list of FieldPopulator for the given class
    */
   protected <T> Collection<FieldPopulator<T>> getFieldPopulatorsFor(Class<?> clazz, EntityMeta<T> meta)  {
      
      List<FieldPopulator<T>> l = new ArrayList<FieldPopulator<T>>();
      FieldPopulator<T> fpp;
      
      for (Field field : clazz.getDeclaredFields()) {
         // do no process transient and synthetic (compiler-generated) field (the latter are notably used by inner classes to refer to their enclosing type)
         if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic() || field.isAnnotationPresent(Transient.class)) {
            // skip static, synthetic and transient fields
            continue;
         }
         
         String fieldname = field.getName();
         
         Column column = field.getAnnotation(Column.class);         
         if (column != null) { // TODO do something for column.insertable()
            fieldname = column.name();
         } else {
            JoinColumn jcolumn = field.getAnnotation(JoinColumn.class);
            if (jcolumn != null) { // TODO do something for jcolumn.insertable()
               fieldname = jcolumn.name();
            } else if (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class)) {
               continue;
            }
         }
         
         fpp = getFieldPopulator(clazz, field, fieldname);
            
         if (field.isAnnotationPresent(Id.class)) {
            meta.setIdPopulator(fpp);
         } else if (field.isAnnotationPresent(Version.class)) {
            meta.setVersionPopulator(fpp);
         } else {
            l.add(fpp);
         }
      }
      return l;
   }
   
   @SuppressWarnings("unchecked")
   protected <T> FieldPopulator<T> getFieldPopulator(final Class<?> clazz, final Field field, final String fieldName) {
      Constructor<? extends FieldPopulator<?>> populatatorClass = defaultPopulatatorClass;

      for (Map.Entry<Class<?>, Constructor<? extends FieldPopulator<?>>> entry : this.populatatorClasses.entrySet()) {
         if (entry.getKey().isAssignableFrom(field.getType())) {
            populatatorClass = entry.getValue();
            break;
         }
      }

      try {
         return (FieldPopulator<T>) populatatorClass.newInstance(clazz, field, fieldName);
      } catch (Exception e) {
         throw new IllegalArgumentException("Can't construct new instance of class " + populatatorClass, e);
      }
   }
}
