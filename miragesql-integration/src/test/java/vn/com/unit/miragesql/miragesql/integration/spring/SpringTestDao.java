package vn.com.unit.miragesql.miragesql.integration.spring;

import vn.com.unit.miragesql.miragesql.SqlManager;
import vn.com.unit.miragesql.miragesql.StringSqlResource;
import vn.com.unit.miragesql.miragesql.SqlManagerImplTest.Book;

import org.springframework.transaction.annotation.Transactional;

public class SpringTestDao {

	private SqlManager sqlManager;

	public SqlManager getSqlManager() {
		return sqlManager;
	}

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	@Transactional
	public void insert(Book book, boolean throwException){
		sqlManager.insertEntity(book);
		if(throwException){
			throw new RuntimeException();
		}
	}

	@Transactional
	public int getCount(){
		return sqlManager.getSingleResult(
				Integer.class, new StringSqlResource("SELECT COUNT(*) FROM BOOK"));
	}

}
