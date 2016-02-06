package w.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class CollectionBeanUtilsTest {
	
	static NestedBeanUtils collectionBeanUtils = new NestedBeanUtils(true, new CollectionBeanUtils());
	static class A {
		int size;
		int[] arrayOfInts;
		B[] arrayOfBs;
		List<B> listOfBs;
		Set<B> setOfBs;
		Map<String, B> mapOfBs;
		public A(){}
		public A(B ...bs) {
			this.size = bs.length;
			this.arrayOfInts = new int[this.size];
			for(int i=0;i<this.size;i++) this.arrayOfInts[i] = i;
			
			this.arrayOfBs = bs;
			this.listOfBs = new ArrayList<B>(Arrays.asList(bs)); // need a editable list
			this.setOfBs = new HashSet<B>(listOfBs);
			this.mapOfBs = new HashMap<String, B>();
			for(B b:bs) { this.mapOfBs.put(b.getName(), b);}
		}
		public int getSize() {return size;}
		public void setSize(int size) {this.size = size;}
		public int[] getArrayOfInts() {return arrayOfInts;}
		public void setArrayOfInts(int[] is) {this.arrayOfInts = is;}
		public B[] getArrayOfBs() {return arrayOfBs;}
		public void setArrayOfBs(B[] bs) {this.arrayOfBs = bs;}
		public List<B> getListOfBs() {return listOfBs;}
		public void setListOfBs(List<B> bs) {this.listOfBs = bs;}
		public Set<B> getSetOfBs() {return setOfBs;}
		public void setSetOfBs(Set<B> bs) {this.setOfBs = bs;}
		public Map<String, B> getMapOfBs() {return mapOfBs;}
		public void setMapOfBs(Map<String, B> bs) {this.mapOfBs = bs;}
    }
	
	static class B {
    	String name;
    	public B(){}
		public B(String name) {this.name = name;}
		public String getName() {return name;}
		public void setName(String name) {this.name = name;}
		@Override
		public int hashCode() {
			return name==null?0:name.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || getClass() != obj.getClass()) return false;
			B other = (B) obj;
			if (name == null) {	if (other.name != null)	return false; }
			else if (!name.equals(other.name)) return false;
			return true;
		}
    }

	
	@Test
	public void testSplit() {
		String propertyName = "hello[1]";
		int delimiter = propertyName.indexOf("[");
		String[] parts = new CollectionBeanUtils().split(propertyName, delimiter);
		Assert.assertEquals("hello", parts[0]);
		Assert.assertEquals("1", parts[1]);
	}
	
	@Test
	public void testNonCollection() {
		Assert.assertEquals(Integer.TYPE, collectionBeanUtils.getPropertyType(A.class, "size"));
		A a = new A(new B("one"), new B("two"), new B("three"));
		Assert.assertEquals(3, collectionBeanUtils.getPropertyValue(a, "size"));		
		collectionBeanUtils.setPropertyValue(a, "size",4);
		Assert.assertEquals(4, collectionBeanUtils.getPropertyValue(a, "size"));
	}
	
	@Test
	public void testGetter() {
		A a = new A(new B("one"), new B("two"), new B("three"));
		
		Assert.assertEquals("two", collectionBeanUtils.getPropertyValue(a, "listOfBs[1].name"));
		
		Assert.assertEquals("three", collectionBeanUtils.getPropertyValue(a, "setOfBs[2].name"));
		
		Assert.assertEquals("one", collectionBeanUtils.getPropertyValue(a, "arrayOfBs[0].name"));
		
		Assert.assertEquals(1, collectionBeanUtils.getPropertyValue(a, "arrayOfInts[1]"));
		
		Assert.assertEquals("three", collectionBeanUtils.getPropertyValue(a, "mapOfBs[three].name"));
	}
	
	@Test
	public void testGetLast() {
		A a = new A(new B("one"), new B("two"), new B("three"));
		
		Assert.assertEquals("three", collectionBeanUtils.getPropertyValue(a, "listOfBs[].name"));
		
		Assert.assertEquals("three", collectionBeanUtils.getPropertyValue(a, "setOfBs[].name"));
		
		Assert.assertEquals("three", collectionBeanUtils.getPropertyValue(a, "arrayOfBs[].name"));
		
		Assert.assertEquals(2, collectionBeanUtils.getPropertyValue(a, "arrayOfInts[]"));
	}
	
	@Test
	public void testGetNegatifIndex() {
		A a = new A(new B("one"), new B("two"), new B("three"));
		
		Assert.assertEquals("two", collectionBeanUtils.getPropertyValue(a, "listOfBs[-2].name"));
		
		// negatif index for sets always return the last element only
		Assert.assertEquals("three", collectionBeanUtils.getPropertyValue(a, "setOfBs[-2].name"));
		
		Assert.assertEquals("two", collectionBeanUtils.getPropertyValue(a, "arrayOfBs[-2].name"));
		
		Assert.assertEquals(1, collectionBeanUtils.getPropertyValue(a, "arrayOfInts[-2]"));
	}
	
	@Test
	public void testTypeByClass() {
		//Assert.assertEquals(B.class, collectionBeanUtils.getPropertyType(A.class, "listOfBs[1]"));
		//Assert.assertEquals(B.class, collectionBeanUtils.getPropertyType(A.class, "setOfBs[1]"));
		//Assert.assertEquals(B.class, collectionBeanUtils.getPropertyType(A.class, "mapOfBs[one]"));
		
		Assert.assertEquals(B.class, collectionBeanUtils.getPropertyType(A.class, "arrayOfBs[1]"));
		
		Assert.assertEquals(Integer.TYPE, collectionBeanUtils.getPropertyType(A.class, "arrayOfInts[1]"));
	}
	
	@Test
	public void testTypeByValue() {
		A a = new A(new B("one"));
		
		Assert.assertEquals(B.class, collectionBeanUtils.getPropertyType(a, "listOfBs[1]"));
		
		Assert.assertEquals(B.class, collectionBeanUtils.getPropertyType(a, "setOfBs[1]"));
		
		Assert.assertEquals(B.class, collectionBeanUtils.getPropertyType(a, "arrayOfBs[1]"));
		
		Assert.assertEquals(Integer.TYPE, collectionBeanUtils.getPropertyType(a, "arrayOfInts[1]"));
		
		Assert.assertEquals(B.class, collectionBeanUtils.getPropertyType(a, "mapOfBs[one]"));
	}
	
	@Test
	public void testSetter() {
		A a = new A(new B("one"), new B("two"), new B("three"));
		
		collectionBeanUtils.setPropertyValue(a, "listOfBs[1]", new B("deux"));		
		Assert.assertEquals("deux", collectionBeanUtils.getPropertyValue(a, "listOfBs[1].name"));
		
		// not possible
		try {
			collectionBeanUtils.setPropertyValue(a, "setOfBs[2]", new B("trois"));
			Assert.fail("Setting value on a set should not be possible");
		} catch(IllegalArgumentException e) {}

		
		collectionBeanUtils.setPropertyValue(a, "arrayOfBs[0]", new B("un"));
		Assert.assertEquals("un", collectionBeanUtils.getPropertyValue(a, "arrayOfBs[0].name"));
		
		collectionBeanUtils.setPropertyValue(a, "arrayOfInts[1]",11);
		Assert.assertEquals(11, collectionBeanUtils.getPropertyValue(a, "arrayOfInts[1]"));
		
		collectionBeanUtils.setPropertyValue(a, "mapOfBs[three]",new B("trois"));
		Assert.assertEquals("trois", collectionBeanUtils.getPropertyValue(a, "mapOfBs[three].name"));
	}
	
	@Test
	public void testExpandAndSet() {
		A a = new A(new B("one"), new B("two"), new B("three"));
		
		collectionBeanUtils.setPropertyValue(a, "listOfBs[5]", new B("six"));		
		Assert.assertEquals("six", collectionBeanUtils.getPropertyValue(a, "listOfBs[5].name"));
		
		collectionBeanUtils.setPropertyValue(a, "setOfBs[5]", new B("six"));
		// not really possible since we adding more that one empty component will trim to one!!!
		// Assert.assertEquals(6, ((Set) collectionBeanUtils.getPropertyValue(a, "setOfBs")).size());
		
		collectionBeanUtils.setPropertyValue(a, "arrayOfBs[5]", new B("six"));
		Assert.assertEquals("six", collectionBeanUtils.getPropertyValue(a, "arrayOfBs[5].name"));
		Assert.assertEquals(6, ((B[]) collectionBeanUtils.getPropertyValue(a, "arrayOfBs")).length);
		
		collectionBeanUtils.setPropertyValue(a, "arrayOfInts[5]",55);
		Assert.assertEquals(55, collectionBeanUtils.getPropertyValue(a, "arrayOfInts[5]"));
		Assert.assertEquals(6, ((int[]) collectionBeanUtils.getPropertyValue(a, "arrayOfInts")).length);
	}
	
	@Test
	public void testSetterAppend() {
		A a = new A(new B("one"), new B("two"), new B("three"));
		
		collectionBeanUtils.setPropertyValue(a, "listOfBs[]", new B("quatre"));		
		Assert.assertEquals("quatre", collectionBeanUtils.getPropertyValue(a, "listOfBs[3].name"));
		
		collectionBeanUtils.setPropertyValue(a, "setOfBs[]", new B("quatre"));	
		Assert.assertEquals(4, ((Set<?>) collectionBeanUtils.getPropertyValue(a, "setOfBs")).size());
		
		collectionBeanUtils.setPropertyValue(a, "arrayOfBs[]", new B("quatre"));
		Assert.assertEquals("quatre", collectionBeanUtils.getPropertyValue(a, "arrayOfBs[3].name"));
		
		collectionBeanUtils.setPropertyValue(a, "arrayOfInts[]",4);
		Assert.assertEquals(4, collectionBeanUtils.getPropertyValue(a, "arrayOfInts[3]"));
	}
	
	@Test
	public void testEmptyCollections() {
		A a = new A();
		
		Assert.assertNull(collectionBeanUtils.getPropertyValue(a, "listOfBs[0]"));
		collectionBeanUtils.setPropertyValue(a, "listOfBs[]", new B("one"));
		Assert.assertEquals("one", collectionBeanUtils.getPropertyValue(a, "listOfBs[0].name"));
		
		Assert.assertNull(collectionBeanUtils.getPropertyValue(a, "setOfBs[0]")); 
		collectionBeanUtils.setPropertyValue(a, "setOfBs[]", new B("one"));
		Assert.assertEquals("one", collectionBeanUtils.getPropertyValue(a, "setOfBs[0].name"));
		
		Assert.assertNull(collectionBeanUtils.getPropertyValue(a, "mapOfBs[test]"));
		collectionBeanUtils.setPropertyValue(a, "mapOfBs[test]", new B("one"));
		Assert.assertEquals("one", collectionBeanUtils.getPropertyValue(a, "mapOfBs[test].name"));
		
		collectionBeanUtils.setPropertyValue(a, "arrayOfBs[]", new B("quatre"));
		Assert.assertEquals("quatre", collectionBeanUtils.getPropertyValue(a, "arrayOfBs[0].name"));
		
		collectionBeanUtils.setPropertyValue(a, "arrayOfInts[]",4);
		Assert.assertEquals(4, collectionBeanUtils.getPropertyValue(a, "arrayOfInts[0]"));
	}
	
	@Test
	public void testNestedAndExpanded() {
		A a = new A();
		
		// easy with arrays
		Assert.assertNull(collectionBeanUtils.getPropertyValue(a, "arrayOfBs[0].name"));
		collectionBeanUtils.setPropertyValue(a, "arrayOfBs[0].name", "quatre");
		Assert.assertEquals("quatre", collectionBeanUtils.getPropertyValue(a, "arrayOfBs[0].name"));
		
		Assert.assertNull(collectionBeanUtils.getPropertyValue(a, "arrayOfInts[0]"));
		collectionBeanUtils.setPropertyValue(a, "arrayOfInts[0]",4);
		Assert.assertEquals(4, collectionBeanUtils.getPropertyValue(a, "arrayOfInts[0]"));

		a = new A(new B("one"));
		
		// there is one entity in my collection
		Assert.assertEquals(1,a.getListOfBs().size());
		Assert.assertNull(collectionBeanUtils.getPropertyValue(a, "listOfBs[1].name"));
		collectionBeanUtils.setPropertyValue(a, "listOfBs[1].name", "two");
		Assert.assertEquals(2,a.getListOfBs().size());
		Assert.assertEquals("two", collectionBeanUtils.getPropertyValue(a, "listOfBs[1].name"));
		
		
		Assert.assertEquals(1,a.getSetOfBs().size());
		Assert.assertNull(collectionBeanUtils.getPropertyValue(a, "setOfBs[1].name")); 
		collectionBeanUtils.setPropertyValue(a, "setOfBs[1].name", "two");
		Assert.assertEquals(2,a.getSetOfBs().size());
		Assert.assertEquals("two", collectionBeanUtils.getPropertyValue(a, "setOfBs[0].name"));
		
		// there is one entity in my map
		Assert.assertEquals(1,a.getMapOfBs().size());
		Assert.assertNull(collectionBeanUtils.getPropertyValue(a, "mapOfBs[test]"));
		collectionBeanUtils.setPropertyValue(a, "mapOfBs[test]", new B("one"));
		Assert.assertEquals(2,a.getMapOfBs().size());
		Assert.assertEquals("one", collectionBeanUtils.getPropertyValue(a, "mapOfBs[test].name"));
	}

}
