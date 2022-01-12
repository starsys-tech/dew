package group.idealworld.dew.core.hi.process;


import group.idealworld.dew.core.hi.exception.RTAPINotFoundException;
import group.idealworld.dew.core.hi.exception.RTReflectiveOperationException;
import group.idealworld.dew.core.hi.ext.SpringBeanDetector;
import group.idealworld.dew.core.hi.helper.ScanHelper;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Hi Builder.
 *
 * Lightweight and custom IoC operation
 * to find {@code Hi.class} annotation and automatically assemble the corresponding implementation classes.
 *
 * @author gudaoxuri
 */
public class HiBuilder {

    // Mapping of API classes to implementation classes
    // key : API classes (interface or abstract class)
    // value : corresponding implementation class
    private static final Map<Class<?>, Class<?>> METHOD_CLAZZ_IMPL = new HashMap<>();
    // Spring environment dependency identification
    private static boolean enableSpringSupport = hasDependency("org.springframework.context.ApplicationContext");

    /**
     * Build.
     *
     * @param basicPackage the basic package
     */
    public static void build(String basicPackage) {
        Class<Hi> hiClass = Hi.class;
        Class<HiRef> hiRefClass = HiRef.class;
        // Hi Info
        // key : API classes (interface or abstract class)
        Map<Class<?>, Set<HiScanInfo>> hiInfo = new HashMap<>();
        // Scan Hi annotations
        ScanHelper.scan(basicPackage, clazz -> {
            Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(hiClass))
                    .forEach(field -> {
                        if (!hiInfo.containsKey(field.getType())) {
                            hiInfo.put(field.getType(), new HashSet<>());
                        }
                        hiInfo.get(field.getType()).add(new HiScanInfo(clazz, field.getType(), field));
                    });
            Arrays.stream(clazz.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(hiClass))
                    .forEach(method -> {
                        if (!hiInfo.containsKey(method.getReturnType())) {
                            hiInfo.put(method.getReturnType(), new HashSet<>());
                        }
                        hiInfo.get(method.getReturnType()).add(new HiScanInfo(clazz, method.getReturnType(), method));
                    });
        });
        // Find the implementation class of each object
        ScanHelper.scan(basicPackage, clazz -> {
            if (clazz.isInterface() || clazz.isEnum()
                    || Modifier.isAbstract(clazz.getModifiers())
                    || clazz.isAnnotation() || clazz.isArray()) {
                // Ignore interfaces, enumerations, abstract classes, etc.
                return;
            }
            hiInfo.forEach((fieldOrMethodTypeClazz, scanInfoSet) -> {
                if (fieldOrMethodTypeClazz.isAssignableFrom(clazz)) {
                    // Find a matching implementation
                    scanInfoSet.forEach(scanInfo -> scanInfo.implClazz = clazz);
                } else if (clazz.isAnnotationPresent(hiRefClass)
                        && clazz.getAnnotation(hiRefClass).value().isAssignableFrom(fieldOrMethodTypeClazz)) {
                    // Find the implementation referenced via @HiRef
                    scanInfoSet.forEach(scanInfo -> {
                        scanInfo.implClazz = clazz;
                        scanInfo.isRefMode = true;
                    });
                }
            });
        });
        // Building from Hi
        Set<HiScanInfo> hiScanInfoSet = hiInfo.values().stream()
                .flatMap(Collection::stream)
                // Exclude not implemented (package isn‘t imported) functions
                .filter(hiScanInfo -> hiScanInfo.implClazz != null)
                .collect(Collectors.toSet());
        doBuilding(hiScanInfoSet, group.idealworld.dew.core.hi.Hi.class, null);
    }

    private static void doBuilding(Set<HiScanInfo> hiScanInfoSet, Class<?> clazz, Object clazzInst) {
        hiScanInfoSet.stream()
                .filter(hiScanInfo -> hiScanInfo.belongClazz == clazz)
                .forEach(hiScanInfo -> {
                    // Field building
                    hiScanInfo.fieldOpt.ifPresent(field -> {
                        try {
                            Object newInst = null;
                            // Using spring bean first
                            Optional<Object> instOpt = getBean(hiScanInfo.fieldOrMethodTypeClazz);
                            if (instOpt.isPresent()) {
                                newInst = instOpt.get();
                            }
                            if (newInst == null) {
                                if (hiScanInfo.implClazz.getName().contains("$") &&
                                        !Modifier.isStatic(hiScanInfo.implClazz.getModifiers())) {
                                    // Internal non-static class
                                    Constructor<?> constructor = hiScanInfo.implClazz.getDeclaredConstructor(clazzInst.getClass());
                                    constructor.setAccessible(true);
                                    newInst = constructor.newInstance(clazzInst);
                                } else {
                                    Constructor<?> constructor = hiScanInfo.implClazz.getDeclaredConstructor();
                                    constructor.setAccessible(true);
                                    newInst = constructor.newInstance();
                                }
                            }
                            if (!hiScanInfo.isRefMode) {
                                //  Normal implementation mode
                                if (Modifier.isStatic(field.getModifiers())) {
                                    field.set(null, newInst);
                                } else {
                                    field.set(clazzInst, newInst);
                                }
                            } else {
                                // @HiRef reference mode
                                if (Modifier.isStatic(field.getModifiers())) {
                                    field.set(null, proxyInst(field.getType(), newInst));
                                } else {
                                    field.set(clazzInst, proxyInst(field.getType(), newInst));
                                }
                            }
                            doBuilding(hiScanInfoSet, hiScanInfo.fieldOrMethodTypeClazz, newInst);
                        } catch (IllegalAccessException | InstantiationException
                                | NoSuchMethodException | InvocationTargetException e) {
                            throw new RTReflectiveOperationException(e);
                        }
                    });
                    // Method building
                    hiScanInfo.methodOpt.ifPresent(method ->
                            METHOD_CLAZZ_IMPL.put(hiScanInfo.fieldOrMethodTypeClazz, hiScanInfo.implClazz));
                });
    }

    /**
     * Instantiates a new  proxy objects.
     *
     * @param interfaceClazz Interface type
     * @param implObj        Implementing Class Object
     * @return Proxy Object
     */
    private static Object proxyInst(Class<?> interfaceClazz, Object implObj) {
        Class<?> implClazz = implObj.getClass();
        Arrays.asList(interfaceClazz.getMethods())
                .forEach(interfaceMethod -> {
                            try {
                                implClazz.getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
                            } catch (NoSuchMethodException e) {
                                throw new RTReflectiveOperationException(
                                        "Can't find the public [" + interfaceMethod.getName() + "] method needed " +
                                                "for the [" + interfaceClazz.getName() + "] interface " +
                                                "in the [" + implClazz.getName() + "] implementation class", e);
                            }
                        }
                );
        Object newProxyInstance = Proxy.newProxyInstance(
                interfaceClazz.getClassLoader(),
                new Class[]{interfaceClazz},
                new DynamicProxy(implObj));
        return newProxyInstance;
    }

    /**
     * Dynamic proxy.
     *
     * Automatically match implementations with the same interface method name + parameter type.
     */
    private static class DynamicProxy implements InvocationHandler {

        private static final Map<Method, Method> METHOD_MAPPING = new ConcurrentHashMap<>();

        private Object implObj;
        private Class<?> implClazz;

        public DynamicProxy(final Object implObj) {
            this.implObj = implObj;
            implClazz = implObj.getClass();
        }

        @Override
        public Object invoke(Object proxy, Method interfaceMethod, Object[] args) throws Throwable {
            // Cache
            if (!METHOD_MAPPING.containsKey(interfaceMethod)) {
                METHOD_MAPPING.put(
                        interfaceMethod,
                        implClazz.getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes())
                );
            }
            return METHOD_MAPPING.get(interfaceMethod).invoke(implObj, args);
        }

    }

    /**
     * Gets bean.
     *
     * @param clazz the class
     * @return the bean
     */
    public static Optional<Object> getBean(Class<?> clazz) {
        if (!enableSpringSupport) {
            return Optional.empty();
        }
        return SpringBeanDetector.getBean(clazz);
    }

    /**
     * New inst.
     *
     * @param <E>        the type parameter
     * @param clazz      the class
     * @param parameters the parameters
     * @return the e
     */
    public static <E> E newInst(Class<E> clazz, Object... parameters) {
        if (!METHOD_CLAZZ_IMPL.containsKey(clazz)) {
            throw new RTAPINotFoundException("This method isn't implemented (please check dependencies)");
        }
        try {
            List<Class<?>> parameterClasses = Arrays.stream(parameters)
                    .map(Object::getClass)
                    .collect(Collectors.toList());
            Constructor<?> constructor = METHOD_CLAZZ_IMPL.get(clazz)
                    .getDeclaredConstructor(parameterClasses.toArray(new Class[parameterClasses.size()]));
            constructor.setAccessible(true);
            return (E) constructor.newInstance(parameters);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new RTReflectiveOperationException(e);
        }
    }

    private static boolean hasDependency(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static class HiScanInfo {

        /**
         * The Belong class.
         */
        public Class<?> belongClazz;
        /**
         * The Field or method type class.
         */
        public Class<?> fieldOrMethodTypeClazz;
        /**
         * The Field opt.
         */
        public Optional<Field> fieldOpt;
        /**
         * The Method opt.
         */
        public Optional<Method> methodOpt;
        /**
         * The Impl clazz.
         */
        public Class<?> implClazz;

        /**
         * The Is ref mode.
         */
        public boolean isRefMode;

        /**
         * Instantiates a new Hi scan info.
         *
         * @param belongClazz            the belong class
         * @param fieldOrMethodTypeClazz the field or method type class
         * @param field                  the field
         */
        public HiScanInfo(Class<?> belongClazz, Class<?> fieldOrMethodTypeClazz,
                          Field field) {
            this.belongClazz = belongClazz;
            this.fieldOrMethodTypeClazz = fieldOrMethodTypeClazz;
            this.fieldOpt = Optional.of(field);
            this.methodOpt = Optional.empty();
        }

        /**
         * Instantiates a new Hi scan info.
         *
         * @param belongClazz            the belong class
         * @param fieldOrMethodTypeClazz the field or method type class
         * @param method                 the method
         */
        public HiScanInfo(Class<?> belongClazz, Class<?> fieldOrMethodTypeClazz,
                          Method method) {
            this.belongClazz = belongClazz;
            this.fieldOrMethodTypeClazz = fieldOrMethodTypeClazz;
            this.fieldOpt = Optional.empty();
            this.methodOpt = Optional.of(method);
        }

    }

}
