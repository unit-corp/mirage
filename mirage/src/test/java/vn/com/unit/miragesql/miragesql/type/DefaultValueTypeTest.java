package vn.com.unit.miragesql.miragesql.type;

import junit.framework.TestCase;
import vn.com.unit.miragesql.miragesql.type.DefaultValueType;

@Deprecated
public class DefaultValueTypeTest extends TestCase {

	public void testIsSupport() {
		DefaultValueType valueType = new DefaultValueType();

		byte[] bytes = new byte[0];
		Object[] objs = new Object[0];

		assertTrue(valueType.isSupport(bytes.getClass(), null));
		assertFalse(valueType.isSupport(objs.getClass(), null));
	}

}
