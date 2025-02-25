package vn.com.unit.miragesql.miragesql.bean;

import java.lang.annotation.Annotation;
import java.util.*;

public class BeanDescImpl implements BeanDesc {

    private Class<?> clazz;
    private PropertyDesc[] propertyArray;
    // keep the order of original properties
    private final Map<Object, Object> propertyMap = Collections.synchronizedMap(new LinkedHashMap<>());

    public BeanDescImpl(Class<?> clazz, Map<String, PropertyWrapper> map){
        this.clazz = clazz;

        List<PropertyDesc> list = new ArrayList<>();

        for(PropertyWrapper propertyWrapper: map.values()){
            PropertyDesc pd = new PropertyDescImpl(propertyWrapper);

//			if(info.field == null){
//				pd = new PropertyDescImpl(this, info.name, info.type, null, info.getterMethod, info.setterMethod);
//			} else {
//				pd = new PropertyDescImpl(this, info.name, info.type, info.propertyWrapper);
//			}

            list.add(pd);
            // this.propertyMap.put(pd.getPropertyName(), pd);
            addToMap(pd.getPropertyName(),pd);
        }

//		// for Scala classes
//		while(clazz != null){
//			Field[] declaredFields = clazz.getDeclaredFields();
//			for(Field field: declaredFields){
//				if(Modifier.isPublic(field.getModifiers())){
//					continue;
//				}
//				String propertyName = field.getName();
//				try {
//					Method method = clazz.getMethod(propertyName, new Class<?>[0]);
//					PropertyInfo info = map.get(propertyName);
//					if(info == null){
//						info = new PropertyInfo();
//						info.name = propertyName;
//						info.getterMethod = method;
//						info.type = method.getReturnType();
//						map.put(propertyName, info);
//					} else if(info.type == method.getReturnType()){
//						info.getterMethod = method;
//					}
//				} catch(NoSuchMethodException e){
//				}
//				try {
//					Method method = clazz.getMethod(propertyName + "_$eq", field.getType());
//					PropertyInfo info = map.get(propertyName);
//					if(info == null){
//						info = new PropertyInfo();
//						info.name = propertyName;
//						info.setterMethod = method;
//						info.type = method.getParameterTypes()[0];
//						map.put(propertyName, info);
//					} else if(info.type == method.getParameterTypes()[0]){
//						info.setterMethod = method;
//					}
//				} catch(NoSuchMethodException e){
//				}
//			}
//			clazz = clazz.getSuperclass();
//		}

        this.propertyArray = list.toArray(new PropertyDesc[list.size()]);
    }

//	@Override
    /**{@inheritDoc}*/
    public Class<?> getType(){
        return clazz;
    }

//	@Override
    /**{@inheritDoc}*/
    public PropertyDesc getPropertyDesc(String name){
        return (PropertyDesc) propertyMap.get(name);
    }

//	@Override
    /**{@inheritDoc}*/
    public int getPropertyDescSize(){
        return propertyArray.length;
    }

//	@Override
    /**{@inheritDoc}*/
    public PropertyDesc getPropertyDesc(int i){
        return propertyArray[i];
    }

//	@Override
    /**{@inheritDoc}*/
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return clazz.getAnnotation(type);
    }

    /**{@inheritDoc}*/
    @Override
    public String toString() {
        return super.toString() + "[" + (clazz == null ? null : clazz.getSimpleName()) + "]";
    }

    private void addToMap(String propertyName, PropertyDesc pd) {
        synchronized (propertyMap) {
            propertyMap.put(propertyName,pd);
        }
    }
}
