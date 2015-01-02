package w.dao.populator.entity;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Types;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;

import w.dao.populator.entity.fields.AbstractFieldPopulator;

public class EntityDatabasePopulatorTest {

   abstract class TestIdable {
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
   class TestEntityA extends TestIdable {
      @Column(name = "NAME", length = 100)
      private String name;

      public TestEntityA(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }
   }

   @Table(name = "TEST_TABLE_B")
   class TestEntityB extends TestIdable {
      @JoinColumn(name = "a_id")
      private TestEntityA a;

      @Column(name = "index")
      private int index;

      public TestEntityB(int index, TestEntityA a) {
         this.index = index;
         this.a = a;

      }

      public TestEntityA getA() {
         return a;
      }

      public int getIndex() {
         return index;
      }
   }

   @Test
	public void testWithEmbeddedDatabase() throws SQLException {   
	   TestEntityA a = new TestEntityA("entityA");
	   TestEntityB b1 = new TestEntityB(1, a);
	   TestEntityB b2 = new TestEntityB(2, null);
	   
	   EntityDatabasePopulator entityPopulator = new EntityDatabasePopulator();
	   entityPopulator.addPopulatatorClasses(TestIdablePopulator.class);
	   entityPopulator.addEntities(a, b1, b2);
	   entityPopulator.addPreStatements(
	         "SET DATABASE SQL SYNTAX ORA TRUE;", // make sure HSQL behaves as closely to Oracle as it can
	         "create table TEST_TABLE_A (KEY_ID NUMBER(38) NOT NULL, Version NUMBER(38), NAME VARCHAR2(100));",
	         "create table TEST_TABLE_B (KEY_ID NUMBER(38) NOT NULL, Version NUMBER(38), Index NUMBER(38), A_ID NUMBER(38));");
	   
	   EmbeddedDatabaseFactory dbFactory = new EmbeddedDatabaseFactory();
	   dbFactory.setDatabasePopulator(entityPopulator);   
	   
	   JdbcTemplate template = new JdbcTemplate(dbFactory.getDatabase());
	   
		// make a few checks		
		int count = template.queryForObject("select count(*) from TEST_TABLE_A", Integer.class);
		Assert.assertEquals(1, count);
		count = template.queryForObject("select count(*) from TEST_TABLE_B", Integer.class);
      Assert.assertEquals(2, count);
      
	}
}
