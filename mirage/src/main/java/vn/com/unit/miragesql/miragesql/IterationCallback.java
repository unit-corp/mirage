package vn.com.unit.miragesql.miragesql;

/**
 * Callback interface for iteration search.
 * 
 * @author Naoki Takezoe
 *
 * @param <T> the entity type
 * @param <R> the return type
 * 
 * @see SqlManager#iterate(Class, IterationCallback, SqlResource)
 * @see SqlManager#iterate(Class, IterationCallback, SqlResource, Object)
 */
public interface IterationCallback<T, R> {

    R iterate(T entity);
}
