package vn.com.unit.miragesql.miragesql.naming;

import vn.com.unit.miragesql.miragesql.SqlManager;

/**
 * The interface of the converter which converts table / column names and entity / property names.
 *
 * @author Naoki Takezoe
 * @see SqlManager#setNameConverter(NameConverter)
 */
public interface NameConverter {

    /**
     * Converts the property name to the column name.
     *
     * @param propertyName the property name
     * @return the column name
     */
    String propertyToColumn(String propertyName);

    /**
     * Converts the column name to the property name.
     *
     * @param columnName the column name
     * @return the property name
     */
    String columnToProperty(String columnName);

    /**
     * Converts the entity name to the table name.
     *
     * @param entityName the entity name
     * @return the table name
     */
    String entityToTable(String entityName);

//	public String tableToEntity(String tableName);

}
