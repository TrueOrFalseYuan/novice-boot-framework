package cn.kinkii.novice.framework.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.UUID;

/**
 * 运行时UUID标识，主要用于作为包装类的唯一标识.
 * 不可用于@Entity实体
 *
 * @author TrueOrFalse.Yuan
 */
@MappedSuperclass
public abstract class RtUUID implements Identifiable<String> {

  @Getter
  @Setter
  @Transient
  protected String id;

  public RtUUID() {
    this.id = UUID.randomUUID().toString();
  }
}
