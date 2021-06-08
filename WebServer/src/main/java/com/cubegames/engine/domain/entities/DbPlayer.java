package com.cubegames.engine.domain.entities;

public class DbPlayer extends BasicNamedDbEntity {

  private int color;

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DbPlayer that = (DbPlayer) o;

    if (color != that.color) return false;
    if (!id.equals(that.id)) return false;
    if (!tenantId.equals(that.tenantId)) return false;
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + color;

    if (tenantId != null) {
      result = 31 * result + tenantId.hashCode();
    }

    if (id != null) {
      result = 31 * result + id.hashCode();
    }

    return result;
  }

}
