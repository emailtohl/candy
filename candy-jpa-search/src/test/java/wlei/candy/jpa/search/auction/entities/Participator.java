package wlei.candy.jpa.search.auction.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import wlei.candy.jpa.UsualEntity;

import java.util.LinkedHashSet;
import java.util.Set;

@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
@Indexed
@Table(name = "AUCTION_PARTICIPATOR")
@Entity
public class Participator extends UsualEntity<Participator> {

  @GenericField(sortable = Sortable.YES)
  @NotNull
  protected String name;

  protected boolean activated;

  protected Address homeAddress;

  @FullTextField
  protected String remark;

  @JoinTable(name = "AUCTION_PARTICIPATOR_LOGIN_NAMES")
  @ElementCollection
  protected Set<String> loginNames = new LinkedHashSet<>();

  public Participator() {
  }

  public Participator(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Participator setName(String name) {
    this.name = name;
    return this;
  }

  public boolean isActivated() {
    return activated;
  }

  public Participator setActivated(boolean activated) {
    this.activated = activated;
    return this;
  }

  public Address getHomeAddress() {
    return homeAddress;
  }

  public Participator setHomeAddress(Address homeAddress) {
    this.homeAddress = homeAddress;
    return this;
  }

  public String getRemark() {
    return remark;
  }

  public Participator setRemark(String remark) {
    this.remark = remark;
    return this;
  }

  public Set<String> getLoginNames() {
    return loginNames;
  }

  public Participator setLoginNames(Set<String> loginNames) {
    this.loginNames = loginNames;
    return this;
  }
}