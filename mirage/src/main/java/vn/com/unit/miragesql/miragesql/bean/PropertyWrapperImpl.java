package vn.com.unit.miragesql.miragesql.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import vn.com.unit.miragesql.miragesql.annotation.Transient;

public class PropertyWrapperImpl implements PropertyWrapper {

    private String name;
    private Field field;
    private Method getter;
    private Method setter;

    public PropertyWrapperImpl(String name, Method getter, Method setter, Field field){
        this.name = name;
        this.getter = getter;
        this.setter = setter;
        this.field = field;
    }

    /**{@inheritDoc}*/
    public String getName(){
        return this.name;
    }

    /**{@inheritDoc}*/
    public Class<?> getType(){
        if(setter != null){
            return setter.getParameterTypes()[0];
        }
        if(getter != null){
            return getter.getReturnType();
        }
        if(field != null){
            return field.getType();
        }
        return null;
    }

    /**{@inheritDoc}*/
    public void set(Object instance, Object value) throws IllegalAccessException, InvocationTargetException {
        if(setter != null){
            this.setter.invoke(instance, value);
            return;
        }
        if(field != null){
            this.field.set(instance, value);
            return;
        }
        // TODO Should it throw exception?
    }

    /**{@inheritDoc}*/
    public Object get(Object instance) throws IllegalAccessException, InvocationTargetException {
        if(getter != null){
            return this.getter.invoke(instance, new Object[0]);
        }
        if(field != null){
            return field.get(instance);
        }
        // TODO Should it throw exception?
        return null;
    }

    /**{@inheritDoc}*/
    public boolean isReadable(){
        return getter != null || (field != null && Modifier.isPublic(field.getModifiers()));
    }

    /**{@inheritDoc}*/
    public boolean isWritable(){
        return setter != null || (field != null && Modifier.isPublic(field.getModifiers()));
    }

    /**{@inheritDoc}*/
    public boolean isTransient() {
        Transient ann = getAnnotation(Transient.class);
        if(ann != null){
            return true;
        }
        if(field != null){
            return Modifier.isTransient(field.getModifiers());
        }

        return false;
    }

    /**{@inheritDoc}*/
    public <T extends Annotation> T getAnnotation(Class<T> type){
        T ann = null;
        if(setter != null){
            ann = setter.getAnnotation(type);
        }
        if(ann == null && getter != null){
            ann = getter.getAnnotation(type);
        }
        if(ann == null && field != null){
            ann = field.getAnnotation(type);
        }
        return ann;
    }

    /**{@inheritDoc}*/
    public Method getGetterMethod(){
        return this.getter;
    }

    /**{@inheritDoc}*/
    public void setGetterMethod(Method getter){
        this.getter = getter;
    }

    /**{@inheritDoc}*/
    public Method getSetterMethod(){
        return this.setter;
    }

    /**{@inheritDoc}*/
    public void setSetterMethod(Method setter){
        this.setter = setter;
    }

    /**{@inheritDoc}*/
    public Field getField(){
        return this.field;
    }

    /**{@inheritDoc}*/
    public void setField(Field field){
        this.field = field;
    }

    /**{@inheritDoc}*/
    @Override
    public String toString() {
        return "PropertyWrapperImpl [name=" + name + "]";
    }
}
