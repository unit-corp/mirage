package vn.com.unit.miragesql.miragesql.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import vn.com.unit.miragesql.miragesql.exception.BeanDescException;

public class PropertyDescImpl implements PropertyDesc {

    private PropertyWrapper propertyWrapper;

    public PropertyDescImpl(PropertyWrapper propertyWrapper){
        this.propertyWrapper = propertyWrapper;
    }

//	@Override
    /**{@inheritDoc}*/
    public Object getValue(Object entity){
        try {
            return propertyWrapper.get(entity);
        } catch (IllegalAccessException ex) {
            throw new BeanDescException(ex);

        } catch (InvocationTargetException ex) {
            throw new BeanDescException(ex);

        }
    }

//	@Override
    /**{@inheritDoc}*/
    public void setValue(Object entity, Object value){
        try {
            propertyWrapper.set(entity, value);
        } catch (IllegalAccessException ex) {
            throw new BeanDescException(ex);

        } catch (InvocationTargetException ex) {
            throw new BeanDescException(ex);

        }
    }

//	@Override
    /**{@inheritDoc}*/
    public boolean isReadable(){
        return propertyWrapper.isReadable();
    }

//	@Override
    /**{@inheritDoc}*/
    public boolean isWritable(){
        return propertyWrapper.isWritable();
    }

//	@Override
    /**{@inheritDoc}*/
    public Class<?> getPropertyType(){
        return propertyWrapper.getType();
    }

//	@Override
    /**{@inheritDoc}*/
    public String getPropertyName(){
        return propertyWrapper.getName();
    }

//	@Override
    /**{@inheritDoc}*/
    public Field getField(){
        return propertyWrapper.getField();
    }

//	@Override
    /**{@inheritDoc}*/
    public <T extends Annotation> T getAnnotation(Class<T> type){
        return propertyWrapper.getAnnotation(type);
    }

//	@Override
    /**{@inheritDoc}*/
    public boolean isTransient() {
        return propertyWrapper.isTransient();
    }

    /**{@inheritDoc}*/
    @Override
    public String toString() {
        return super.toString() +
                "[" + getPropertyName() + ":" + (propertyWrapper.getType() == null ? null :
                propertyWrapper.getType().getSimpleName()) +
                "]";
    }
}
