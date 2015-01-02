package w.dao.populator.entity;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import w.dao.populator.entity.AbstractEntityPopulator.FieldGenerator;
import w.dao.populator.entity.fields.AbstractFieldPopulator;
import w.junit.extras.OrderedJUnit4ClassRunner;

@RunWith(OrderedJUnit4ClassRunner.class)
public class EntityInserterAndUpdateTest {

   static DataSource ds = new EmbeddedDatabaseBuilder(new DefaultResourceLoader() {
      public Resource getResource(String statement) {
         return new ByteArrayResource(statement.getBytes());
      }         
   }) 
      .addScript("SET DATABASE SQL SYNTAX ORA TRUE;") // make sure HSQL behaves as closely to Oracle as it can
      .addScript("create table TEST_TABLE_A (KEY_ID NUMBER(38) NOT NULL, Version NUMBER(38), NAME VARCHAR2(100));")
      .addScript("create table TEST_TABLE_B (KEY_ID NUMBER(38) NOT NULL, Version NUMBER(38), Index NUMBER(38), A_ID NUMBER(38));")
      .setName("EntityInserterAndUpdateTest").build();
   
   @SuppressWarnings("rawtypes")
   static FieldGenerator idGenerator = new SimpleFieldGenerator();
   
   static EntityMetaFactory metaFactory = new EntityMetaFactory().addPopulatatorClass(TestIdablePopulator.class);

   static abstract class TestIdable {
      @Id
      @Column(name = "KEY_ID")
      private Long keyId;

      @Version
      @Column(name = "Version")
      private Long version;

      public Long getKeyId() {
         return keyId;
      }

      public Long getVersion() {
         return version;
      }
   }

   @FieldPopulator.AssignableFrom(type=TestIdable.class)
   public static class TestIdablePopulator<T> extends AbstractFieldPopulator<T> {

      public TestIdablePopulator(Class<?> clazz, Field field, String name) {
         super(clazz, field, name, Types.NUMERIC);
      }

      @Override
      public Object getValue(T entity) {
         TestIdable idable = (TestIdable) super.getValue(entity);
         if (idable == null) return null;
         return idable.getKeyId();
      }
   }
   
   @Table(name = "TEST_TABLE_A")
   static class TestEntityA extends TestIdable {
      @Column(name = "NAME", length = 100)
      private String name;

      public TestEntityA(String name) {this.name = name;}

      public String getName() {return this.name;}
   }

   @Table(name = "TEST_TABLE_B")
   static class TestEntityB extends TestIdable {
      @JoinColumn(name = "a_id")
      private TestEntityA a;

      @Column(name = "index")
      private int index;

      public TestEntityB(int index, TestEntityA a) {
         this.index = index;
         this.a = a;
      }

      public TestEntityA getA() {return a;}

      public int getIndex() {return index;}
   }

   static TestEntityA a = new TestEntityA("entityA");
   static TestEntityB b1 = new TestEntityB(1, a);
   static TestEntityB b2 = new TestEntityB(2, null);
   
   @Test
   @Order(1)
	public void testInsert() throws SQLException {		      
 	   EntityInserter<TestEntityA> inserterA = new EntityInserter<TestEntityA>(metaFactory.getEntityMeta(TestEntityA.class), idGenerator ,idGenerator);
	   EntityInserter<TestEntityB> inserterB = new EntityInserter<TestEntityB>(metaFactory.getEntityMeta(TestEntityB.class), idGenerator ,idGenerator);
	   
	   Assert.assertNull(a.getKeyId());
	   Assert.assertNull(a.getVersion());
	   inserterA.populate(ds, Arrays.asList(a));	   
	   inserterB.populate(ds, Arrays.asList(b1, b2));
	   
	   Assert.assertEquals(Long.valueOf(1), a.getKeyId());
	   Assert.assertEquals(Long.valueOf(2), a.getVersion());
	   
		// make a few checks
		JdbcTemplate template = new JdbcTemplate(ds);
		int count = template.queryForObject("select count(*) from TEST_TABLE_A", Integer.class);
		Assert.assertEquals(1, count);
		count = template.queryForObject("select count(*) from TEST_TABLE_B", Integer.class);
      Assert.assertEquals(2, count);
      
      long keyIdA = template.queryForObject("select key_id from TEST_TABLE_A where name = 'entityA'", Integer.class);
      Assert.assertEquals(1, keyIdA);
      
      long version = template.queryForObject("select version from TEST_TABLE_A where key_id = 1", Integer.class);
      Assert.assertEquals(2, version);
      
      Assert.assertEquals(Long.valueOf(7), idGenerator.generate(null));
	}
   
   @Test
   @Order(2)
   public void testUpdate() throws SQLException {   
      EntityUpdater<TestEntityA> inserterA = new EntityUpdater<TestEntityA>(metaFactory.getEntityMeta(TestEntityA.class), idGenerator);

      JdbcTemplate template = new JdbcTemplate(ds);
      
      long version = template.queryForObject("select version from TEST_TABLE_A where key_id = 1", Integer.class);
      Assert.assertEquals(2, version);
         
      Assert.assertEquals(Long.valueOf(1), a.getKeyId());
      Assert.assertEquals(Long.valueOf(2), a.getVersion());

      inserterA.populate(ds, Arrays.asList(a));
      Assert.assertEquals(Long.valueOf(8), a.getVersion());
      
      version = template.queryForObject("select version from TEST_TABLE_A where key_id = 1", Integer.class);
      Assert.assertEquals(8, version);
   }
   
   @Test
   @Order(3)
   public void testDelete() throws SQLException {
      EntityDeleter<TestEntityB> deleterB = new EntityDeleter<TestEntityB>(metaFactory.getEntityMeta(TestEntityB.class));

      JdbcTemplate template = new JdbcTemplate(ds);
     
      deleterB.populate(ds, Arrays.asList(b2));
      
      int count = template.queryForObject("select count(*) from TEST_TABLE_B", Integer.class);
      Assert.assertEquals(1, count);
   }
}
