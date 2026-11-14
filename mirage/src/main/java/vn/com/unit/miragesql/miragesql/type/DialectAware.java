package vn.com.unit.miragesql.miragesql.type;

import vn.com.unit.miragesql.miragesql.dialect.Dialect;

/**
 * A {@link ValueType} whose behavior depends on the {@link Dialect} in use.
 * <p>
 * The executors pass their dialect to every registered value type implementing this interface.
 */
public interface DialectAware {

    void setDialect(Dialect dialect);

}
