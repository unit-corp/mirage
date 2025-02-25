package vn.com.unit.miragesql.miragesql.exception;

import java.lang.reflect.InvocationTargetException;

import vn.com.unit.miragesql.miragesql.bean.ReflectiveOperationFailedException;

public class BeanDescException extends ReflectiveOperationFailedException {

    private static final long serialVersionUID = 1L;

    public BeanDescException(InvocationTargetException e) {
        super(e);
    }

    public BeanDescException(IllegalAccessException e) {
        super(e);
    }

}
