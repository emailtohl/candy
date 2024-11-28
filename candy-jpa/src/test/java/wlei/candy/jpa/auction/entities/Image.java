package wlei.candy.jpa.auction.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
@Embeddable
public class Image implements Serializable, Cloneable {

  @NotNull
  @Column(nullable = false)
  protected String name;

  @NotNull
  @Column(nullable = false)
  protected String filename;

  @NotNull
  protected int width;

  @NotNull
  protected int height;

  public Image() {
  }

  public Image(String name, String filename, int width, int height) {
    this.name = name;
    this.filename = filename;
    this.width = width;
    this.height = height;
  }
  // Whenever value-types are managed in collections, overriding equals/hashCode is a good idea!

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Image image = (Image) o;

    if (width != image.width) {
      return false;
    }
    if (height != image.height) {
      return false;
    }
    if (!filename.equals(image.filename)) {
      return false;
    }
    return name.equals(image.name);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + filename.hashCode();
    result = 31 * result + width;
    result = 31 * result + height;
    return result;
  }

  @Override
  public Image clone() {
    try {
      return (Image) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException(e);
    }
  }

  public String getName() {
    return name;
  }

  public Image setName(String name) {
    this.name = name;
    return this;
  }

  public String getFilename() {
    return filename;
  }

  public Image setFilename(String filename) {
    this.filename = filename;
    return this;
  }

  public int getWidth() {
    return width;
  }

  public Image setWidth(int width) {
    this.width = width;
    return this;
  }

  public int getHeight() {
    return height;
  }

  public Image setHeight(int height) {
    this.height = height;
    return this;
  }
}
