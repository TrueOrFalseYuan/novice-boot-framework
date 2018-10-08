package cn.kinkii.novice.framework.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class AssignableID<ID extends Serializable> implements Identifiable<ID> {

  @Id
  @Column(name = "ID", length = 128)
  @Getter
  @Setter
  protected ID id;
}
