package cn.kinkii.novice.framework.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class KGenericsUtils {

  /**
   * 通过反射,获得指定类父类的泛型参数的实际类型. 如BuyerDao extends DaoSupport<Buyer>
   * 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
   *
   * @param clazz clazz 需要反射的类,该类必须继承范型父类
   * @param index 泛型参数所在索引,从0开始
   * @return 范型参数的实际类型
   */
  public static Class getSuperclassGenericType(Class clazz, int index) {
    // 得到泛型父类
    Type genType = clazz.getGenericSuperclass();
    if (genType instanceof ParameterizedType) {
      Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
      if (index < params.length) {
        return (Class) params[index];
      }
    }
    return null;
  }

  /**
   * 通过反射,获得指定类父类的第一个泛型参数的实际类型. 如BuyerDao extends DaoSupport<Buyer>
   * 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回 <code>Object.class</code>
   *
   * @param clazz clazz 需要反射的类,该类必须继承泛型父类
   * @return 泛型参数的实际类型
   */
  public static Class getSuperclassGenericType(Class clazz) {
    return getSuperclassGenericType(clazz, 0);
  }
}
