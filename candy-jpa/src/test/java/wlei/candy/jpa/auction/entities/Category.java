package wlei.candy.jpa.auction.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import wlei.candy.jpa.UsualEntity;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
@Table(name = "AUCTION_CATEGORY")
@Entity
public class Category extends UsualEntity<Category> {

  @NotNull
  protected String name;

  // The root of the tree has no parent, column has to be nullable!
  @ManyToOne
  @JoinColumn(
      name = "PARENT_ID",
      foreignKey = @ForeignKey(name = "FK_CATEGORY_PARENT_ID")
  )
  protected Category parent;

  @ManyToMany(targetEntity = Item.class, cascade = CascadeType.PERSIST)
  @JoinTable(name = "AUCTION_CATEGORY_ITEM",
      joinColumns = @JoinColumn(
          name = "CATEGORY_ID",
          foreignKey = @ForeignKey(name = "FK_CATEGORY_ITEM_CATEGORY_ID")
      ),
      inverseJoinColumns = @JoinColumn(
          name = "ITEM_ID",
          foreignKey = @ForeignKey(name = "FK_CATEGORY_ITEM_ITEM_ID")
      ))
  protected Set<Item> items = new HashSet<>();

  public Category() {
  }

  public Category(String name) {
    this.name = name;
  }

  public Category(String name, Category parent) {
    this.name = name;
    this.parent = parent;
  }

  public String getName() {
    return name;
  }

  public Category setName(String name) {
    this.name = name;
    return this;
  }

  public Category getParent() {
    return parent;
  }

  public Category setParent(Category parent) {
    this.parent = parent;
    return this;
  }

  public Set<Item> getItems() {
    return items;
  }

  public Category setItems(Set<Item> items) {
    this.items = items;
    return this;
  }

}
