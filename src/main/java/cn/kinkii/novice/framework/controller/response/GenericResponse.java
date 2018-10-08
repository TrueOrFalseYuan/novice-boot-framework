package cn.kinkii.novice.framework.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class GenericResponse<T> extends AnnotatedResponse {

  public GenericResponse<T> from(T original) {
    super.from(original);
    return this;
  }

  private Integer errorCode;

  private String errorMessage;

  public GenericResponse(Integer code, String message) {
    this.errorCode = code;
    this.errorMessage = message;
  }
}
