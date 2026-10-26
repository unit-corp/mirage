package com.miragesql.miragesql.util;

import java.util.Arrays;

import com.miragesql.miragesql.bean.BeanDescFactory;
import com.miragesql.miragesql.exception.OgnlRuntimeException;
import com.miragesql.miragesql.parser.Node;
import com.miragesql.miragesql.parser.SqlContext;
import com.miragesql.miragesql.parser.SqlContextImpl;
import com.miragesql.miragesql.parser.SqlParserImpl;
import junit.framework.TestCase;

public class OgnlUtilTest extends TestCase {

	public void testGetValueOfPublicProperty() {
		assertEquals("pub", OgnlUtil.getValue(OgnlUtil.parseExpression("name"), new Bean()));
	}

	public void testGetValueOfNonPublicMembers() {
		Bean bean = new Bean();
		assertEquals("secret", OgnlUtil.getValue(OgnlUtil.parseExpression("secret"), bean));
		assertEquals("prot", OgnlUtil.getValue(OgnlUtil.parseExpression("prot"), bean));
		assertEquals("pkg", OgnlUtil.getValue(OgnlUtil.parseExpression("pkg"), bean));
	}

	public void testInvalidExpressionReportsPathAndLine() {
		try {
			OgnlUtil.parseExpression("a +", "sample.sql", 3);
			fail();
		} catch (OgnlRuntimeException ex) {
			assertTrue(ex.getMessage(), ex.getMessage().contains("sample.sql"));
			assertTrue(ex.getMessage(), ex.getMessage().contains("line:3"));
		}
	}

	public void testEvaluationErrorReportsPathAndLine() {
		Object expression = OgnlUtil.parseExpression("missing.foo");
		try {
			OgnlUtil.getValue(expression, new Bean(), "sample.sql", 7);
			fail();
		} catch (OgnlRuntimeException ex) {
			assertTrue(ex.getMessage(), ex.getMessage().contains("sample.sql"));
			assertTrue(ex.getMessage(), ex.getMessage().contains("line:7"));
		}
	}

	public void testSqlContextPropertyAccessor() {
		SqlContext context = new SqlContextImpl();
		context.addArg("userId", "takezoe", String.class);

		assertEquals("takezoe", OgnlUtil.getValue(OgnlUtil.parseExpression("userId"), context));
		assertEquals(Boolean.TRUE, OgnlUtil.getValue(OgnlUtil.parseExpression("has_userId"), context));
		assertEquals(Boolean.FALSE, OgnlUtil.getValue(OgnlUtil.parseExpression("has_other"), context));
	}

	public void testParenBindVariable() {
		Node node = new SqlParserImpl(
				"SELECT * FROM USER WHERE USER_ID IN /*ids*/(1, 2)", new BeanDescFactory()).parse();

		SqlContext context = new SqlContextImpl();
		context.addArg("ids", Arrays.asList("a", "b", "c"), java.util.List.class);

		node.accept(context);

		assertEquals("SELECT * FROM USER WHERE USER_ID IN (?, ?, ?)", context.getSql());
		assertEquals(3, context.getBindVariables().length);
		assertEquals("c", context.getBindVariables()[2]);
	}

	public void testIfWithHasPrefix() {
		Node node = new SqlParserImpl(
				"SELECT * FROM USER /*IF has_userId*/WHERE USER_ID=/*userId*/'x'/*END*/", new BeanDescFactory()).parse();

		SqlContext withArg = new SqlContextImpl();
		withArg.addArg("userId", "takezoe", String.class);
		node.accept(withArg);
		assertEquals("SELECT * FROM USER WHERE USER_ID=?", withArg.getSql());

		SqlContext withoutArg = new SqlContextImpl();
		node.accept(withoutArg);
		assertEquals("SELECT * FROM USER", withoutArg.getSql().trim());
	}

	public static class Bean {
		public String name = "pub";
		private String secret = "secret";
		protected String prot = "prot";
		String pkg = "pkg";
	}

}
