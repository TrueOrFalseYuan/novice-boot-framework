package cn.kinkii.novice.framework.test.entity;

import cn.kinkii.novice.framework.entity.RtUUID;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SimpleEntity extends RtUUID {

  @NonNull private String testString;

  @NonNull private Integer testInteger;

  private Date testDate;

  private Integer testIntResult;

  private Integer sum;

  public Integer getTestIntegerMethod() {
    return this.testIntResult;
  }

  public void setTestIntegerMethod(Integer testInt) {
    this.testIntResult = (testInteger == null ? 0 : testInteger) + testInt;
  }
}
