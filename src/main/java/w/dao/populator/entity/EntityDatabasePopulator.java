package w.dao.populator.entity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.init.DatabasePopulator;

import w.dao.DaoHelper;
import w.dao.populator.entity.AbstractEntityPopulator.FieldGenerator;

@SuppressWarnings("rawtypes")
public class EntityDatabasePopulator implements DatabasePopulator {

   private static final Logger LOGGER = LoggerFactory.getLogger(EntityDatabasePopulator.class);
   
   EntityMetaFactory metaFactory;

   FieldGenerator idGenerator;

   FieldGenerator versionGenerator;

   Map<Class, Collection<Object>> entities = new LinkedHashMap<Class, Collection<Object>>();

   Collection<String> preStatements = new ArrayList<String>();
   
   public EntityDatabasePopulator() {
      metaFactory = new EntityMetaFactory();
      
      idGenerator = versionGenerator = new SimpleFieldGenerator();
   }

   public EntityDatabasePopulator setMetaFactory(EntityMetaFactory metaFactory) {
      this.metaFactory = metaFactory;
      return this;
  }

   public EntityDatabasePopulator addPopulatatorClasses(Class<?>... populatatorClasses) {
      metaFactory.addPopulatatorClasses(populatatorClasses);
      return this;
   }

   public EntityDatabasePopulator setGenerators(FieldGenerator generator) {
      this.idGenerator = generator;
      this.versionGenerator = generator;
      return this;
  }

   public EntityDatabasePopulator setIdGenerator(FieldGenerator idGenerator) {
      this.idGenerator = idGenerator;
      return this;
  }

   public EntityDatabasePopulator setVersionGenerator(FieldGenerator versionGenerator) {
      this.versionGenerator = versionGenerator;
      return this;
   }

   public EntityDatabasePopulator addAllEntities(Iterable os) {
      for(Object o: os) {
         addEntity(o);
      }
      return this;
   }
   
   public EntityDatabasePopulator addEntities(Object ...os) {
      for(Object o: os) {
         addEntity(o);
      }
      return this;
   }
   
   public EntityDatabasePopulator addEntity(Object o) {
      Collection<Object> os = entities.get(o.getClass());
      if (os == null) {
         os = new ArrayList<Object>();
         entities.put(o.getClass(), os);
      }
      os.add(o);
      return this;   
   }
   
   public EntityDatabasePopulator addPreStatements(String ... preStatments) {
      this.preStatements.addAll(Arrays.asList(preStatments));
      return this;
   }
   
   @Override
   public void populate(Connection connection) throws SQLException {
      
      if (!preStatements.isEmpty()) {
         LOGGER.info("Executing pre statements");
         populate(connection, preStatements);
      }
      
      LOGGER.info("Inserting...");
      for (Map.Entry<Class, Collection<Object>> entry : entities.entrySet()) {
         populate(connection, entry.getKey(), entry.getValue());
      }
   }

   protected void populate(Connection connection, Iterable<String> statements) throws SQLException {
      Statement statement = connection.createStatement();
      try {
         // loop over rows
         for (String s: statements) {
            LOGGER.debug("Executing {}", s);
            statement.execute(s);
         }
      } finally {
         DaoHelper.close(statement);
      }  
   }
   
   @SuppressWarnings("unchecked")
   protected void populate(Connection connection, Class clazz, Iterable<Object> entities) throws SQLException {
      LOGGER.info("Inserting {}", clazz);
      EntityInserter inserter = new EntityInserter(metaFactory.getEntityMeta(clazz), idGenerator, versionGenerator);
      inserter.populate(connection, entities);
   }
}
