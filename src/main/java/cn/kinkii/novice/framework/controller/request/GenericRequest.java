package cn.kinkii.novice.framework.controller.request;

import cn.kinkii.novice.framework.utils.KGenericsUtils;

public abstract class GenericRequest<T> extends AnnotatedRequest {

  private Class<T> targetClass;

  @SuppressWarnings("unchecked")
  public GenericRequest() {
    this.targetClass = KGenericsUtils.getSuperclassGenericType(getClass());
  }

  public T toTarget(T targetObject) {
    return to(targetObject, targetClass);
  }

  public T newTarget() {
    return to(targetClass);
  }

}
