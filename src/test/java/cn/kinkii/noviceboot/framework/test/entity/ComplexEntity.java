package cn.kinkii.noviceboot.framework.test.entity;

import cn.kinkii.noviceboot.framework.entity.AssignableID;
import lombok.*;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ComplexEntity extends AssignableID<String> {

  @NonNull
  private List<Long> testList;
  @NonNull
  private Map<String, Long> testMap;

  public ComplexEntity(String id, List<Long> testList, Map<String, Long> testMap) {
    this.id = id;
    this.testList = testList;
    this.testMap = testMap;
  }

}
