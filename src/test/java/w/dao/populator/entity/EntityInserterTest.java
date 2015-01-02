package w.dao.populator.entity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class EntityInserterTest {

	static enum Answer {
		YES,
		NO,
		MAYBE;
	}

	@Table(name = "TEST_TABLE2")
	class TestEntity2 {
		@Column(name = "KEY_ID")
		private Long keyId;

		@Column(name = "CREATION_DATE")
		@Temporal(TemporalType.TIMESTAMP)
		private Date creationDate;

		@Column(name = "NAME", length = 100)
		private String name;

		@Column(name = "Answer", length = 3)
		@Enumerated(EnumType.STRING)
		private Answer answer;

		@Column(name = "Big_Value")
		private BigDecimal bigValue;

		@Column(name = "deleted")
		private boolean deleted = false;

		@Column(name = "grade")
		private char grade = 'A';

		@Column(name = "weight")
		private Double weight;

		@Column(name = "share")
		private Float share;

		public TestEntity2(Long keyId, Date creationDate, String name, Answer answer, String value, Double weight, Float share) {
			this.keyId = keyId;
			this.creationDate = creationDate;
			this.name = name;
			this.answer = answer;
			this.bigValue = value == null ? null : new BigDecimal(value);
			this.weight = weight;
			this.share = share;
		}

		public Long getKeyId() {
			return this.keyId;
		}

		public Date getCreationDate() {
			return this.creationDate;
		}

		public String getName() {
			return this.name;
		}

		public Answer getAnswer() {
			return this.answer;
		}

		public BigDecimal getBigValue() {
			return this.bigValue;
		}

		public boolean isDeleted() {
			return this.deleted;
		}

		public char getGrade() {
			return this.grade;
		}

		public Double getWeight() {
			return this.weight;
		}

		public Float getShare() {
			return this.share;
		}
	}

	@Test
	public void testWithEmbeddedDatabase() throws Exception {

		DataSource ds = new EmbeddedDatabaseBuilder(new DefaultResourceLoader() {
	      public Resource getResource(String statement) {
	         return new ByteArrayResource(statement.getBytes());
	      }         
	   })		
		   .addScript("SET DATABASE SQL SYNTAX ORA TRUE;") // make sure HSQL behaves as closely to Oracle as it can
		   .addScript("create table TEST_TABLE2 (KEY_ID NUMBER(38),CREATION_DATE DATE,NAME VARCHAR2(100),ANSWER VARCHAR2(3), BIG_VALUE NUMBER(38,10), DELETED NUMBER(1), GRADE CHAR, WEIGHt NUMBER, SHARE NUMBER );")
		   .setName("EntityInserterTest").build();

	   EntityInserter<TestEntity2> inserter = new EntityInserter<TestEntity2>(new EntityMetaFactory().getEntityMeta(TestEntity2.class));

	   
      inserter.populate(ds, Arrays.asList(
                  new TestEntity2(Long.valueOf(12), new Date(), "test1", Answer.MAYBE, "1234567890123456789012345678.0123456789", 0.000001, 0.0001f), // regular data
                  new TestEntity2(Long.valueOf(13), null, null, null, null, null, null), // all nulls
                  new TestEntity2(Long.valueOf(14), null, null, null, null, Double.NaN, Float.NaN), // weird values
                  new TestEntity2(Long.valueOf(15), null, null, null, null, Double.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)));

		// make a few checks
		JdbcTemplate template = new JdbcTemplate(ds);
		int count = template.queryForObject("select count(*) from TEST_TABLE2", Integer.class);
		Assert.assertEquals(4, count);

		String answer = template.queryForObject("select answer from TEST_TABLE2 where key_id=12", String.class);
		Assert.assertEquals("MAY", answer);

		BigDecimal big = template.queryForObject("select Big_Value from TEST_TABLE2 where key_id=12", BigDecimal.class);
		Assert.assertEquals(new BigDecimal("1234567890123456789012345678.0123456789"), big);

		// an SQL CHAR is actually mapped to a String when reading !!!
		String grade = template.queryForObject("select GRADE from TEST_TABLE2 where key_id=12", String.class);
		Assert.assertEquals("A", grade);

		Double nan = template.queryForObject("select WEIGHt from TEST_TABLE2 where key_id=14", Double.class);
		Assert.assertTrue(nan.isNaN());

		Double plus8 = template.queryForObject("select WEIGHt from TEST_TABLE2 where key_id=15", Double.class);
		Assert.assertNotNull(plus8);
		Assert.assertTrue(plus8.isInfinite());
	}
}
