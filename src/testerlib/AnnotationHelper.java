package testerlib;

import java.awt.Component;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import testerlib.annotations.Command;
import testerlib.annotations.Init;
import testerlib.annotations.Testable;

/**
 * @date Apr 12, 2012
 *
 * @author Edoardo Savoia
 */
public class AnnotationHelper {

    /**
     * Check if aClass is annotated with @Testable annotation
     * @param aClass
     * @return true if aClass is annotated with @Testable annotation
     */
    public static boolean isTestable(Class<?> aClass) {
	return isAnnotatedWith(aClass, Testable.class);
    }

    /**
     * Check if aClass ha a method annotated with @INIT annotation
     * INIT must be:
     *	    static
     *	    its return type must be of the same class of aClass
     *	    HOPEFULLY aClass.isAssignableFrom(anotherClass) suffice
     * @return the first "init" method found, null if no such method is present
     */
    public static Method getInitializer(Class<?> aClass) {
	Method[] initMethods = getAnnotatedMethods(aClass, Init.class);
	Method retMethod = null;
	for (Method method : initMethods) {
	    int mod = method.getModifiers();
	    Class<?> returnType = method.getReturnType();
	    if(Modifier.isStatic(mod) && (aClass.isAssignableFrom(aClass))){
		retMethod = method;
		break;
	    }
	}
	return retMethod;
    }


    public static boolean isAnnotatedWith(Class<?> aClass, Class<? extends Annotation> annot) {
	return aClass.isAnnotationPresent(annot);
    }
   
    public static <T extends Annotation> Map<Method,T> getAnnotionsAndMethods(Class<?> aClass,Class<T> annotation){
	Map<Method,T> map = new HashMap<Method, T>();
	for (Method method : aClass.getMethods()) {
	    T ann = method.getAnnotation(annotation);
	    if (ann != null) {
		map.put(method, ann);
	    }
	}
	return Collections.unmodifiableMap(map);
    }

    public static Field[] getAnnotatedFields(Class<?> aClass,Class<? extends Annotation> annotation) {
	List<Field> fields = new ArrayList<Field>();
	for (Field fld : aClass.getFields()) {
	    Annotation ann = fld.getAnnotation(annotation);
	    if (ann != null) {
		fields.add(fld);
	    }
	}
	return fields.toArray(new Field[fields.size()]);
    }

    public static Method[] getAnnotatedMethods(Class<?> aClass,Class<? extends Annotation> annCls) {
	List<Method> methods = new ArrayList<Method>();
	for (Method method : aClass.getMethods()) {
	    Annotation annotation = method.getAnnotation(annCls);
	    if (annotation != null) {
		methods.add(method);
	    }
	}
	return methods.toArray(new Method[methods.size()]);
    }

    /**
     *
     * @param aField
     * @param aObject
     * @return null if an exception has been thrown or if  aField of aObject is null
     */
    public static Object getField(Field aField, Object aObject){
	// aField && aObject are NOT null
	Object retObj = null;
	try {
	    // get aField from aObject
	    retObj = aField.get(aObject);
	} catch (IllegalArgumentException ex) {
	    Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
	    retObj = null;
	} catch (IllegalAccessException ex) {
	    Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
	    retObj = null;
	}

	return retObj;
    }

    /**
     *
     * @param aMethod
     * @param aObject
     * @return true if there weren't no exceptions thrown
     */
    public static boolean invokeMethod(Method aMethod, Object aObject){
	boolean ret = false;
	try {
	    // exec aMethod on aObject
	    aMethod.invoke(aObject, (Object[]) null);
	    ret = true;
	    // log action
	} catch (IllegalAccessException ex) {
	    Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
	    ret = false;
	} catch (IllegalArgumentException ex) {
	    Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
	    ret = false;
	} catch (InvocationTargetException ex) {
	    Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
	    ret = false;
	}
	return ret;
    }

}
