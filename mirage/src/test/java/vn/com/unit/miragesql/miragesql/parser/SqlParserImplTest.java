package vn.com.unit.miragesql.miragesql.parser;

import junit.framework.TestCase;
import vn.com.unit.miragesql.miragesql.bean.BeanDescFactory;
import vn.com.unit.miragesql.miragesql.parser.Node;
import vn.com.unit.miragesql.miragesql.parser.SqlContext;
import vn.com.unit.miragesql.miragesql.parser.SqlContextImpl;
import vn.com.unit.miragesql.miragesql.parser.SqlParser;
import vn.com.unit.miragesql.miragesql.parser.SqlParserImpl;

public class SqlParserImplTest extends TestCase {

	public void testParse() {
		SqlParser parser = new SqlParserImpl(
				"SELECT * FROM USER /*IF user.type == 0*/WHERE USER_ID=/*user.userId*//*END*/", new BeanDescFactory());
		Node node = parser.parse();

		UserDto user = new UserDto();
		SqlContext context = new SqlContextImpl();
		context.addArg("user", user, UserDto.class);

		node.accept(context);

		assertEquals("SELECT * FROM USER WHERE USER_ID=?", context.getSql());
		assertEquals(1, context.getBindVariables().length);
		assertEquals("takezoe", context.getBindVariables()[0]);
	}
	
	public void testParseWithSingeItemMap() {
		SqlParser parser = new SqlParserImpl(
				"SELECT * FROM USER " +
				"/*BEGIN*/" +
				"WHERE" +
				" /*IF userId != null*/AND USER_ID=/*userId*//*END*/" +
				" /*IF userType != null*/AND USER_TYPE=/*userType*//*END*/" +
				"/*END*/",
				new BeanDescFactory());
		Node node = parser.parse();

		SqlContext context = new SqlContextImpl();
		context.addArg("userId", "takezoe", String.class);

		node.accept(context);

		assertEquals("SELECT * FROM USER WHERE USER_ID=?", context.getSql().trim());
		assertEquals(1, context.getBindVariables().length);
		assertEquals("takezoe", context.getBindVariables()[0]);
	}
	

	public static class UserDto {
		public int type = 0;
		public String userId = "takezoe";
	}

}
