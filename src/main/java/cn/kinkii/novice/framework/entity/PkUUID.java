package cn.kinkii.novice.framework.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 统一定义使用uuid的entity基类.
 *
 * @author TrueOrFalse.Yuan
 */
@MappedSuperclass
public abstract class PkUUID implements Identifiable<String> {

  @Id
  @Column(name = "ID", length = 64)
  @GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
  @GeneratedValue(generator = "system-uuid")
  @Getter
  @Setter
  protected String id;

}
