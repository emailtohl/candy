package wlei.candy.jpa.auction.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Embeddable
public class Address implements Serializable, Cloneable {

  @NotNull
  protected String street;

  @NotNull
  protected String zipcode;

  @NotNull
  protected String city;

  public Address() {
  }

  public Address(String street, String zipcode, String city) {
    this.street = street;
    this.zipcode = zipcode;
    this.city = city;
  }

  @Override
  public Address clone() {
    try {
      return (Address) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException(e);
    }
  }

  public String getStreet() {
    return street;
  }

  public Address setStreet(String street) {
    this.street = street;
    return this;
  }

  public String getZipcode() {
    return zipcode;
  }

  public Address setZipcode(String zipcode) {
    this.zipcode = zipcode;
    return this;
  }

  public String getCity() {
    return city;
  }

  public Address setCity(String city) {
    this.city = city;
    return this;
  }
}