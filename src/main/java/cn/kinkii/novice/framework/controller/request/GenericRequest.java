package cn.kinkii.novice.framework.controller.request;

import cn.kinkii.novice.framework.utils.KGenericsUtils;

public abstract class GenericRequest<T> extends AnnotatedRequest {

  private Class<T> targetClass;

  @SuppressWarnings({"unchecked", "CastCanBeRemovedNarrowingVariableType"})
  public GenericRequest() {
    Class<?> requestClass = getClass();
    Class<?> valueClass = KGenericsUtils.getSuperclassGenericType(requestClass);
    while(valueClass == null && requestClass.getSuperclass() != Object.class) {
      requestClass = requestClass.getSuperclass();
      valueClass = KGenericsUtils.getSuperclassGenericType(requestClass);
    }
    if(valueClass == null) {
      throw new IllegalStateException("Fail to get the target class!");
    }
    this.targetClass = (Class<T>) valueClass;
  }

  public T toTarget(T targetObject) {
    return to(targetObject, targetClass);
  }

  public T newTarget() {
    return to(targetClass);
  }

}
