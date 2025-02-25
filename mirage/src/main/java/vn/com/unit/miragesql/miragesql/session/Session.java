package vn.com.unit.miragesql.miragesql.session;

import vn.com.unit.miragesql.miragesql.SqlManager;
import vn.com.unit.miragesql.miragesql.exception.SessionException;
import vn.com.unit.miragesql.miragesql.filter.OpenSessionInViewFilter;

/**
 * The entry point of Mirage-SQL in stand-alone use.
 * <p>
 * You can control transactions and access to {@link SqlManager} through this interface.
 * <p>
 * In addition, you can manage transactions automatically using this interface with {@link OpenSessionInViewFilter}.
 * <code>OpenSessionInViewFilter</code> begin and commit transaction per request in web applications.
 *
 * It rollbacks it transaction if it catches an exception. So you can focus on operation of <code>SqlManager</code>.
 *
 * @author Naoki Takezoe
 */
public interface Session {

	/**
	 * Begins the transaction.
	 *
	 * @throws SessionException if something goes wrong.
	 */
	void begin();

	/**
	 * Commits the transaction.
	 *
	 * @throws SessionException if something goes wrong.
	 */
	void commit();

	/**
	 * Rollbacks the transaction.
	 *
	 * @throws SessionException if something goes wrong.
	 */
	void rollback();

	/**
	 * Releases this transaction.
	 *
	 * @throws SessionException if something goes wrong.
	 */
	void release();

	/**
	 * Marks the current transaction as rollback-only.
	 */
	void setRollbackOnly();

	/**
	 * Returns whether the current transaction has been marked as rollback-only or not marked.
	 *
	 * @return If marked true; otherwise false
	 */
	boolean isRollbackOnly();

	/**
	 * Returns the instance of <code>SqlManager</code>.
	 *
	 * @return the instance of <code>SqlManager</code>
	 * @throws SessionException if something goes wrong.
	 */
	SqlManager getSqlManager();

}
