package w.dao.populator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeMap;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import w.dao.DaoHelper;
import w.dao.populator.TablePopulator;
import w.dao.populator.TablePopulator.RowPopulator;

/**
 * @author wkeyser
 *
 */
public class TablePopulatorTest {
	private enum Code {
		ONE,
		TWO,
		THREE
	};

	@Test
	public void testInsertSql() {
		String sql = TablePopulator.getInsertSql("MyTable", Arrays.asList("col1"), null);
		Assert.assertEquals("INSERT INTO MyTable (col1) VALUES (?)", sql);

		sql = TablePopulator.getInsertSql("MyTable", Arrays.asList("col1", "col2"), null);
		Assert.assertEquals("INSERT INTO MyTable (col1,col2) VALUES (?,?)", sql);
	}

	@Test
	public void testInsertSqlWithSqlFields() {
		TreeMap<String, String> sqlFields = new TreeMap<String, String>();

		String sql = TablePopulator.getInsertSql("MyTable", Arrays.asList("col1", "col2"), sqlFields);
		Assert.assertEquals("INSERT INTO MyTable (col1,col2) VALUES (?,?)", sql);

		sqlFields.put("key_id", "(next key_id)");
		sql = TablePopulator.getInsertSql("MyTable", Arrays.asList("col1", "col2"), sqlFields);
		Assert.assertEquals("INSERT INTO MyTable (col1,col2,key_id) VALUES (?,?,(next key_id))", sql);

		sqlFields.put("key_id2", "(next key_id2)");
		sql = TablePopulator.getInsertSql("MyTable", Arrays.asList("col1", "col2"), sqlFields);
		Assert.assertEquals("INSERT INTO MyTable (col1,col2,key_id,key_id2) VALUES (?,?,(next key_id),(next key_id2))", sql);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testWrongType() {
		TablePopulator tblPp = new TablePopulator("tableName");

		RowPopulator rp1 = tblPp.newRowPopulator();
		rp1.setString("string", "something long");

		RowPopulator rp2 = tblPp.newRowPopulator();
		rp2.setLong("string", 1l); // not the same type

		Assert.fail();
	}

	@Test
	public void testSimpleTablePopulator() throws SQLException {
		TablePopulator tblPp = new TablePopulator("tableName");

		// adding auto generated fields
		tblPp.addSqlField("keyid", "KEYIDSEQ.NEXTVAL");
		tblPp.addSqlField("active", "1");

		// adding first row
		RowPopulator rp1 = tblPp.newRowPopulator();
		rp1.setString("string", "something long");
		rp1.setLong("long", 1l);
		rp1.setDouble("double", 1.2345678901234567890);
		rp1.setBoolean("boolean", false);
		rp1.setDate("date", new Date());

		// adding second row
		RowPopulator rp2 = tblPp.newRowPopulator();
		rp2.setString("string", "something very loog", 15);
		rp2.setEnum("code", Code.THREE, 3);
		rp2.setLong("long", 2l);
		rp2.setBoolean("boolean", null, true);

		// adding nulls
		RowPopulator rp3 = tblPp.newRowPopulator();
		rp3.setString("string", null, 15);
		rp3.setEnum("code", null, 3);
		rp3.setLong("long", null);
		rp3.setDate("date", null);
		rp3.setDouble("double", null);

		// exception
		RowPopulator rp4 = tblPp.newRowPopulator("context", null, new Date());
		rp4.setLong("long", 4l);
		rp4.setDouble("double", Double.longBitsToDouble(0x7ff0000000012304L)); // this is a NAN

		// exception
		RowPopulator rp5 = tblPp.newRowPopulator("context", null, new Date());
		rp5.setLong("long", 5l);
		rp5.setDouble("double", Double.POSITIVE_INFINITY);

		// checking the sql
		String sql = tblPp.getInsertSql();
		Assert.assertEquals("INSERT INTO tableName (boolean,code,date,double,long,string,active,keyid) VALUES (?,?,?,?,?,?,1,KEYIDSEQ.NEXTVAL)", sql);

		
		
		// build a database
      EmbeddedDatabase ds = new EmbeddedDatabaseBuilder(new DefaultResourceLoader() {
         public Resource getResource(String statement) {
            return new ByteArrayResource(statement.getBytes());
         }         
      }) 
         //the following statment is needed because HSQL does not support the sequence.NEXTVAL syntax by default so we need to tell him to be 'oracle' compatible
         .addScript("SET DATABASE SQL SYNTAX ORA TRUE;")
         .addScript("create sequence KEYIDSEQ start with 1;")
         .addScript("create table tableName (keyid NUMBER(38) NOT NULL,active NUMBER(1),boolean NUMBER(1),long NUMBER(38),double NUMBER,string VARCHAR2(14),code VARCHAR2(3),date DATE);")
         .build();

		try {
			Connection conn = ds.getConnection();
			// populate the table
			tblPp.populate(conn);
			DaoHelper.close(conn);

			// check number of inserted rows
			JdbcTemplate template = new JdbcTemplate(ds);
			int inserted = template.queryForObject("select count(*) from tableName", Integer.class);
			Assert.assertEquals("Wrong number of rows inserted", 5, inserted);

			// check trimming of long strings
			String trim = template.queryForObject("select string from tableName where long = 2", String.class);
			Assert.assertEquals("something very", trim);

			// check default values
			Boolean defaultBoolean = template.queryForObject("select boolean from tableName where long = 2", Boolean.class);
			Assert.assertNotNull(defaultBoolean);
			Assert.assertTrue(defaultBoolean);

			// check enum value
			String code = template.queryForObject("select code from tableName where long = 2", String.class);
			Assert.assertEquals("THR", code);

			// check NAN are converted to nulls (because oracle doesn't like them)
			Double nan = template.queryForObject("select double from tableName where long = 4", Double.class);
			Assert.assertTrue(nan.isNaN());

			// check infinity
			Double pinfinity = template.queryForObject("select double from tableName where long = 5", Double.class);
			Assert.assertNotNull(pinfinity);
			Assert.assertTrue(pinfinity.isInfinite());

		} finally {
			ds.shutdown();
		}
	}

}
