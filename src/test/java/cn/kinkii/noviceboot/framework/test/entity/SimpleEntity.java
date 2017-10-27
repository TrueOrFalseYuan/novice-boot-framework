package cn.kinkii.noviceboot.framework.test.entity;

import cn.kinkii.noviceboot.framework.entity.RtUUID;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SimpleEntity extends RtUUID {

  @NonNull
  private String testString;

  @NonNull
  private Integer testInteger;

  private Date testDate;

}
