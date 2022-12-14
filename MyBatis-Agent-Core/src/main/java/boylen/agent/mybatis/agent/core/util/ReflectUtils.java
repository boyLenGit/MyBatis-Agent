package boylen.agent.mybatis.agent.core.util;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ReflectUtils {
    public static void getAllVariable(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            try {
                boolean accessFlag = fields[i].isAccessible();
                fields[i].setAccessible(true);
                Object o;
                try {
                    o = fields[i].get(obj);
                    System.err.println("传入的对象中包含一个如下的变量：" + varName + " = " + o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static List<String> ObjectValueToListString(Object obj) {
        List<String> res = new ArrayList<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            try {
                boolean accessFlag = fields[i].isAccessible();
                fields[i].setAccessible(true);
                Object objVal;
                try {
                    objVal = fields[i].get(obj);
                    if (objVal == null) {
                        res.add("");
                    } else {
                        res.add(objVal.toString());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    public static String[] ObjectColNameToListString(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        String[] colNames = new String[fields.length];
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            try {
                boolean accessFlag = fields[i].isAccessible();
                fields[i].setAccessible(true);
                Object objVal;
                try {
                    objVal = fields[i].get(obj);
                    colNames[i] = varName;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        return colNames;
    }

    /**
     * 将对象转换成{name:,value:}形式
     */
    public static List<ObjectMap> ObjectToMapEntity(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<ObjectMap> list = new ArrayList<>();
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            try {
                boolean accessFlag = fields[i].isAccessible();
                fields[i].setAccessible(true);
                Object objVal;
                try {
                    objVal = fields[i].get(obj);
                    list.add(new ObjectMap(varName, objVal));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 根据方法名查找方法，如果当前类有函数重载，返回找到的第一个方法
     */
    public static Method getMethodByName(Class clazz, String name) {
        if (clazz == null) {
            return null;
        }
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    public static Class getClass(String fullName) throws ClassNotFoundException {
        return Class.forName(fullName);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ObjectMap {
    private String name;

    private Object value;
}
