package vn.com.unit.miragesql.miragesql.bean;

import junit.framework.TestCase;
import vn.com.unit.miragesql.miragesql.annotation.Enumerated;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.Transient;

public class BeanDescImplTest extends TestCase {

	public void testBeanDescImpl() {
		PropertyExtractor propertyExtractor = new DefaultPropertyExtractor();
		BeanDesc bd = new BeanDescImpl(Book.class, propertyExtractor.extractProperties(Book.class));

		assertEquals(3, bd.getPropertyDescSize());
		assertNotNull(bd.getPropertyDesc("bookId").getAnnotation(PrimaryKey.class));
		assertNotNull(bd.getPropertyDesc("bookName").getAnnotation(Transient.class));
		assertNotNull(bd.getPropertyDesc("bookType").getAnnotation(Enumerated.class));
	}

}
