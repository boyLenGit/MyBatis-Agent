package boylen.agent.mybatis.agent.core.util;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ReflectUtils {
    public static void getAllVariable(Object obj){
        Field[] fields = obj.getClass().getDeclaredFields();
        for(int i = 0 , len = fields.length; i < len; i++) {
            // 对于每个属性，获取属性名
            String varName = fields[i].getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = fields[i].isAccessible();
                // 修改访问控制权限
                fields[i].setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object o;
                try {
                    o = fields[i].get(obj);
                    System.err.println("传入的对象中包含一个如下的变量：" + varName + " = " + o);
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static List<String> ObjectValueToListString(Object obj){
        List<String> res = new ArrayList<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for(int i = 0 , len = fields.length; i < len; i++) {
            // 对于每个属性，获取属性名
            String varName = fields[i].getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = fields[i].isAccessible();
                // 修改访问控制权限
                fields[i].setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object objVal;
                try {
                    objVal = fields[i].get(obj);
                    if (objVal == null) {
                        res.add("");
                    }else {
                        res.add(objVal.toString());
                    }
//                    System.err.println("传入的对象中包含一个如下的变量：" + varName + " = " + objVal);
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    public static String[] ObjectColNameToListString(Object obj){
        Field[] fields = obj.getClass().getDeclaredFields();
        String[] colNames = new String[fields.length];
        for(int i = 0 , len = fields.length; i < len; i++) {
            // 对于每个属性，获取属性名
            String varName = fields[i].getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = fields[i].isAccessible();
                // 修改访问控制权限
                fields[i].setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object objVal;
                try {
                    objVal = fields[i].get(obj);
                    colNames[i] = varName;
//                    System.err.println("传入的对象中包含一个如下的变量：" + varName + " = " + objVal);
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        return colNames;
    }

    /**
     * 将对象转换成{name:,value:}形式
     *
     * @param obj obj
     * @return {@link List}<{@link ObjectMap}>
     */
    public static List<ObjectMap> ObjectToMapEntity(Object obj){
        Field[] fields = obj.getClass().getDeclaredFields();
        List<ObjectMap> list = new ArrayList<>();
        for(int i = 0 , len = fields.length; i < len; i++) {
            // 对于每个属性，获取属性名
            String varName = fields[i].getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = fields[i].isAccessible();
                // 修改访问控制权限
                fields[i].setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object objVal;
                try {
                    objVal = fields[i].get(obj);
                    list.add(new ObjectMap(varName, objVal));
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 根据方法名查找方法，如果当前类有函数重载，返回找到的第一个方法
     *
     * @param clazz
     * @param name
     * @return
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
