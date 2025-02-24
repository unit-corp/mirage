package vn.com.unit.miragesql.miragesql.dialect;

import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.type.OracleResultSetValueType;
import vn.com.unit.miragesql.miragesql.type.ValueType;

public class OracleDialect extends StandardDialect {

    private OracleResultSetValueType valueType = new OracleResultSetValueType();

    /**{@inheritDoc}*/
    @Override
    public String getName() {
        return "oracle";
    }

    /**{@inheritDoc}*/
    @Override
    public boolean needsParameterForResultSet() {
        return true;
    }

    /**{@inheritDoc}*/
    @Override
    public ValueType<?> getValueType() {
        return valueType;
    }

    /**{@inheritDoc}*/
    @Override
    public String getSequenceSql(String sequenceName) {
        return String.format("SELECT %s.NEXTVAL FROM DUAL", sequenceName);
    }

    /**{@inheritDoc}*/
    @Override
    public boolean supportsGenerationType(GenerationType generationType) {
        if(generationType == GenerationType.IDENTITY){
            return false;
        }
        return true;
    }

}
