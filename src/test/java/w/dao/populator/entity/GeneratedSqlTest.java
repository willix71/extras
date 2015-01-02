package w.dao.populator.entity;

import static org.mockito.Mockito.times;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class GeneratedSqlTest {

   static enum Answer {
      YES, NO, MAYBE;
   }

   class ParentEntity {
      @Id
      @Column(name = "KeyId")
      public Long keyId;
   }

   @Table(name = "MyTable")
   class TestEntity extends ParentEntity {
      @Column(name = "f1")
      public Boolean testBoolean;

      @Column(name = "f2")
      public boolean testboolean = true;

      @Column(name = "f3")
      public Integer testInteger;

      @Column(name = "f4")
      public int testint = 0;

      @Column(name = "f5")
      public String testString;

      @Column(name = "f6")
      public StringBuilder testStringBuilder;

      @Column(name = "f7")
      public Date testDate;

      @Column(name = "f8")
      @Temporal(TemporalType.TIME)
      public Date testTime;

      @Column(name = "f9")
      public Answer testEnum = Answer.YES;

      @Column(name = "f10")
      @Enumerated(EnumType.STRING)
      public Answer testEnumString = Answer.YES;

      @Column(name = "f11")
      public double testdbl = 0;

      // column name = field name
      public char letter = 'A';

      @Transient
      // character are actually not usually used
      public char testTransient;

      @Column(name = "trimmed", length = 3)
      public String trimmedString = "12345";
   }

   @Table(name = "MyVersionTable")
   class VersionEntity extends TestEntity {
      @Version
      long version;
   }

   @Test
   public void testMeta() {
      EntityMeta<TestEntity> meta = new EntityMetaFactory().getEntityMeta(TestEntity.class);
      Assert.assertNotNull(meta.getIdPopulator());
      Assert.assertEquals("KeyId", meta.getIdPopulator().getName());
      
      EntityMeta<VersionEntity> vmeta = new EntityMetaFactory().getEntityMeta(VersionEntity.class);
      Assert.assertNotNull(vmeta.getIdPopulator());
      Assert.assertEquals("KeyId", vmeta.getIdPopulator().getName());

      Assert.assertNotNull(vmeta.getVersionPopulator());
      Assert.assertEquals("version", vmeta.getVersionPopulator().getName());      
   }
   
   @Test
   public void testInsert() throws SQLException {
      EntityInserter<TestEntity> inserter = new EntityInserter<TestEntity>(
            new EntityMetaFactory().getEntityMeta(TestEntity.class));

      Assert.assertEquals(
            "INSERT INTO MyTable (f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,letter,trimmed,KeyId) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            inserter.getSql());

      // prepare mocks
      PreparedStatement ps = Mockito.mock(PreparedStatement.class);
      Mockito.when(ps.executeBatch()).thenReturn(new int[] { 1 });
      Connection connection = Mockito.mock(Connection.class);
      Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(ps);

      // mock insertion
      inserter.populate(connection, Arrays.asList(new TestEntity()));

      // verify mocks
      Mockito.verify(ps, times(7)).setNull(Mockito.anyInt(), Mockito.anyInt()); // all non-initialized fields
      Mockito.verify(ps, times(1)).setObject(Mockito.eq(2), Mockito.eq(true), Mockito.eq(Types.NUMERIC)); // boolean
      Mockito.verify(ps, times(1)).setObject(Mockito.eq(4), Mockito.eq(0), Mockito.eq(Types.NUMERIC)); // int
      Mockito.verify(ps, times(1)).setObject(Mockito.eq(9), Mockito.eq(0), Mockito.eq(Types.NUMERIC)); // enum as ordinal
      Mockito.verify(ps, times(1)).setObject(Mockito.eq(10), Mockito.eq("YES"), Mockito.eq(Types.VARCHAR)); // enum as string
      Mockito.verify(ps, times(1)).setObject(Mockito.eq(11), Mockito.eq(0.0), Mockito.eq(Types.DOUBLE)); // character
      Mockito.verify(ps, times(1)).setObject(Mockito.eq(12), Mockito.eq('A'), Mockito.eq(Types.CHAR)); // character
      Mockito.verify(ps, times(1)).setObject(Mockito.eq(13), Mockito.eq("123"), Mockito.eq(Types.VARCHAR)); // trimmed string
   }

   @Test
   public void testUpdate() throws Exception {
      EntityUpdater<TestEntity> inserter = new EntityUpdater<TestEntity>(new EntityMetaFactory().getEntityMeta(TestEntity.class));

      Assert.assertEquals(
            "UPDATE MyTable SET f1=?,f2=?,f3=?,f4=?,f5=?,f6=?,f7=?,f8=?,f9=?,f10=?,f11=?,letter=?,trimmed=? WHERE KeyId=?",
            inserter.getSql());
      
      EntityUpdater<VersionEntity> inserter2 = new EntityUpdater<VersionEntity>(new EntityMetaFactory().getEntityMeta(VersionEntity.class), new SimpleFieldGenerator<VersionEntity>());

      Assert.assertEquals(
            "UPDATE MyVersionTable SET f1=?,f2=?,f3=?,f4=?,f5=?,f6=?,f7=?,f8=?,f9=?,f10=?,f11=?,letter=?,trimmed=?,version=? WHERE KeyId=? AND version=?",
            inserter2.getSql());
   }

   @Test
   public void testDelete() throws Exception {
      EntityDeleter<TestEntity> inserter = new EntityDeleter<TestEntity>(new EntityMetaFactory().getEntityMeta(TestEntity.class));

      Assert.assertEquals("DELETE FROM MyTable WHERE KeyId=?", inserter.getSql());
      
      EntityDeleter<VersionEntity> inserter2 = new EntityDeleter<VersionEntity>(new EntityMetaFactory().getEntityMeta(VersionEntity.class));

      Assert.assertEquals("DELETE FROM MyVersionTable WHERE KeyId=? AND version=?", inserter2.getSql());

   }
}
