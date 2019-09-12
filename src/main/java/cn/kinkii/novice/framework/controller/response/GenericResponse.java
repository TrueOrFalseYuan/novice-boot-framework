package cn.kinkii.novice.framework.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class GenericResponse<T> extends AnnotatedResponse {

  public void from(T original) {
    super.from(original);
  }

}
