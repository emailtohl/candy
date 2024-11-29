package wlei.candy.jpa.search.auction.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.springframework.format.annotation.DateTimeFormat;
import wlei.candy.jpa.UsualEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static wlei.candy.share.util.DateUtil.GMT_8;
import static wlei.candy.share.util.DateUtil.TIME_PATTERN;

@Indexed
@Table(name = "AUCTION_ITEM")
@Entity
public class Item extends UsualEntity<Item> {

  @NotNull
  @FullTextField
  @Column(unique = true)
  protected String name;

  @NotNull
  @DateTimeFormat(pattern = TIME_PATTERN)
  @JsonFormat(pattern = TIME_PATTERN, timezone = GMT_8)
  protected Date auctionEnd;

  @NotNull
  @Enumerated(EnumType.STRING)
  protected AuctionType auctionType = AuctionType.HIGHEST_BID;

  protected boolean approved = true;

  protected BigDecimal buyNowPrice;

  @IndexedEmbedded
  @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_ITEM_SELLER_ID"))
  protected Participator seller;

  @JsonBackReference("categories")
  @ManyToMany(mappedBy = "items")
  protected Set<Category> categories = new HashSet<>();

  @JsonBackReference("bids")
  @IndexedEmbedded
  @OneToMany(mappedBy = "item")
  protected Set<Bid> bids = new HashSet<>();

  @ElementCollection
  @JoinTable(name = "AUCTION_ITEM_IMAGES", joinColumns = @JoinColumn(foreignKey = @ForeignKey(name = "FK_ITEM_IMAGES_ITEM_ID")))
  protected Set<Image> images = new HashSet<>();

  @FullTextField
  protected String description;

  public Item() {
  }

  public Item(String name, Date auctionEnd, Participator seller) {
    this.name = name;
    this.auctionEnd = auctionEnd;
    this.seller = seller;
  }

  public String getName() {
    return name;
  }

  public Item setName(String name) {
    this.name = name;
    return this;
  }

  public Date getAuctionEnd() {
    return auctionEnd;
  }

  public Item setAuctionEnd(Date auctionEnd) {
    this.auctionEnd = auctionEnd;
    return this;
  }

  public AuctionType getAuctionType() {
    return auctionType;
  }

  public Item setAuctionType(AuctionType auctionType) {
    this.auctionType = auctionType;
    return this;
  }

  public boolean isApproved() {
    return approved;
  }

  public Item setApproved(boolean approved) {
    this.approved = approved;
    return this;
  }

  public BigDecimal getBuyNowPrice() {
    return buyNowPrice;
  }

  public Item setBuyNowPrice(BigDecimal buyNowPrice) {
    this.buyNowPrice = buyNowPrice;
    return this;
  }

  public Participator getSeller() {
    return seller;
  }

  public Item setSeller(Participator seller) {
    this.seller = seller;
    return this;
  }

  public Set<Category> getCategories() {
    return categories;
  }

  public Item setCategories(Set<Category> categories) {
    this.categories = categories;
    return this;
  }

  public Set<Bid> getBids() {
    return bids;
  }

  public Item setBids(Set<Bid> bids) {
    this.bids = bids;
    return this;
  }

  public Set<Image> getImages() {
    return images;
  }

  public Item setImages(Set<Image> images) {
    this.images = images;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public Item setDescription(String description) {
    this.description = description;
    return this;
  }
}