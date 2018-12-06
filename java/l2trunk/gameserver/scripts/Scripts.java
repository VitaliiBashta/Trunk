package l2trunk.gameserver.scripts;

import l2trunk.commons.lang.FileUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;

public enum Scripts {
    INSTANCE;
    public static final Map<Integer, List<ScriptClassAndMethod>> dialogAppends = new HashMap<>();
    public static final Map<String, ScriptClassAndMethod> onAction = new HashMap<>();
    public static final Map<String, ScriptClassAndMethod> onActionShift = new HashMap<>();
    private static final Logger _log = LoggerFactory.getLogger(Scripts.class);
    private final Map<String, Class<?>> _classes = new TreeMap<>();

    public static void main(String[] args) {
        Scripts.INSTANCE.init2();
        Scripts.INSTANCE.load2();
    }

    /**
     * Adding Handlers like DialogAppend, OnAction and OnActionShif if they exists in Class
     *
     * @param clazz Class to look for Handlers
     */
    private void addHandlers(Class<?> clazz) {
        try {
            for (Method method : clazz.getMethods())
                if (method.getName().contains("DialogAppend_")) {
                    Integer id = Integer.parseInt(method.getName().substring(13));
                    List<ScriptClassAndMethod> handlers = dialogAppends.computeIfAbsent(id, k -> new ArrayList<>());
                    handlers.add(new ScriptClassAndMethod(clazz.getName(), method.getName()));
                } else if (method.getName().contains("OnAction_")) {
                    String name = method.getName().substring(9);
                    onAction.put(name, new ScriptClassAndMethod(clazz.getName(), method.getName()));
                } else if (method.getName().contains("OnActionShift_")) {
                    String name = method.getName().substring(14);
                    onActionShift.put(name, new ScriptClassAndMethod(clazz.getName(), method.getName()));
                }
        } catch (NumberFormatException | SecurityException e) {
            _log.error("Exception while adding Handlers ", e);
        } catch (Exception e) {
            _log.error("something went wrong: " + e);
        }
    }

    /**
     * Calling Method in Script file with variables, argument and Player caller as NULL
     *
     * @param caller     Player calling class(can be used later with getSelf())
     * @param className  Class to call
     * @param methodName Method to call
     * @param args       Arguments that method takes
     * @return Object returned from method
     */
    public Object callScripts(Player caller, String className, String methodName, Object[] args) {
        return callScripts(caller, className, methodName, args, null);
    }

    /**
     * Calling Method in Script file with variables, argument and Player caller as NULL
     *
     * @param caller     Player calling class(can be used later with getSelf())
     * @param className  Class to call
     * @param methodName Method to call
     * @param variables  Additional arguments that may be called in Script file
     * @return Object returned from method
     */
    public Object callScripts(Player caller, String className, String methodName, Map<String, Object> variables) {
        return callScripts(caller, className, methodName, new Object[0], variables);
    }

    /**
     * Calling Method in Script file with variables, argument and Player caller as NULL
     *
     * @param caller     Player calling class(can be used later with getSelf())
     * @param className  Class to call
     * @param methodName Method to call
     * @param args       Arguments that method takes
     * @param variables  Additional arguments that may be called in Script file
     * @return Object returned from method
     */
    public Object callScripts(Player caller, String className, String methodName, Object[] args, Map<String, Object> variables) {
        Object o;
        Class<?> clazz = null;

        try {
            clazz = Class.forName("l2trunk.scripts." + className);
        } catch (ClassNotFoundException e) {
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }
//                _classes.get(className);
        if (clazz == null) {
            _log.error("Script class " + className + " not found!");
            return null;
        }

        try {
            o = clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            _log.error("Scripts: Failed creating instance of " + clazz.getName(), e);
            return null;
        }

        if (variables != null && !variables.isEmpty())
            for (Map.Entry<String, Object> param : variables.entrySet())
                try {
                    FieldUtils.writeField(o, param.getKey(), param.getValue());
                } catch (IllegalAccessException e) {
                    _log.error("Scripts: Failed setting fields for " + clazz.getName(), e);
                }

        if (caller != null)
            try {
                Field field;
                if ((field = FieldUtils.getField(clazz, "self")) != null)
                    FieldUtils.writeField(field, o, caller.getRef());
            } catch (IllegalAccessException e) {
                _log.error("Scripts: Failed setting field for " + clazz.getName(), e);
            }

        Object ret = null;
        try {
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++)
                parameterTypes[i] = args[i] != null ? args[i].getClass() : null;

            ret = MethodUtils.invokeMethod(o, methodName, args, parameterTypes);
        } catch (NoSuchMethodException nsme) {
            _log.error("Scripts: No such method " + clazz.getName() + "." + methodName + "()!", nsme);
        } catch (InvocationTargetException ite) {
            _log.error("Scripts: Error while calling " + clazz.getName() + "." + methodName + "()", ite.getTargetException());
        } catch (IllegalAccessException e) {
            _log.error("Scripts: Failed calling " + clazz.getName() + "." + methodName + "()", e);
        }

        return ret;
    }

    public void init2() {
        Path path = Config.CONFIG.resolve("scripts.txt");
        String[] scripts = FileUtils.readFileToString(path).split("\r\n");
//        Map <String, Class<?>> classes = new HashMap<>();
        for (String script : scripts) {
            Class<?> clazz;
            int i = 0;
            try {
                clazz = Class.forName("l2trunk.scripts." + script);
                _classes.put(script, clazz);
                i++;
                if (i > 50)
                    System.out.println("loaded: " + i);
            } catch (Exception e) {
                _log.error("not found class for " + script);
                e.printStackTrace();
            }
        }
        for (Class<?> clazz : _classes.values()) {
            addHandlers(clazz);
            if (ClassUtils.isAssignable(clazz, ScriptFile.class)) {
                if (Modifier.isAbstract(clazz.getModifiers())) continue;
                try {
                    ((ScriptFile) clazz.newInstance()).onLoad();
                } catch (IllegalAccessException | InstantiationException e) {
                    _log.error("Scripts: Failed running " + clazz.getName() + ".onLoad()", e);
                }
            }
        }


    }

    public void load2() {
        for (Class<?> clazz : _classes.values()) {
            if (ClassUtils.isAssignable(clazz, ScriptFile.class))
                try {
                    ((ScriptFile) clazz.newInstance()).onLoad();
                } catch (IllegalAccessException | InstantiationException e) {
                    _log.error("Scripts: Failed running " + clazz.getName() + ".onLoad()", e);
                } catch (Exception e) {
                    _log.error("some error: " + e);
                }
        }
    }

    public class ScriptClassAndMethod {
        public final String className;
        public final String methodName;

        ScriptClassAndMethod(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }
    }
}