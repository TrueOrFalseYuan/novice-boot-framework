package cn.kinkii.noviceboot.framework.controller.response;

import cn.kinkii.noviceboot.framework.utils.KGenericsUtils;
import com.google.common.collect.Lists;

import java.util.List;


public abstract class GenericResponse<T> extends AnnotatedResponse {

  @SuppressWarnings("unchecked")
  @Override
  protected List<Class<?>> buildSourceClasses() {
    return Lists.newArrayList(KGenericsUtils.getSuperclassGenericType(getClass()));
  }

  public void from(T original) {
    super.from(original);
  }

}
