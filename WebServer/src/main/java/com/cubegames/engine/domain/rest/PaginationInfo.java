package com.cubegames.engine.domain.rest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationInfo implements Pageable {

  private String sortBy;
  private Sort.Direction sortOrder;
  private int offset;
  private int limit;

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public Sort.Direction getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(Sort.Direction sortOrder) {
    this.sortOrder = sortOrder;
  }

  @Override
  public int getPageNumber() {
    return 0;
  }

  @Override
  public int getPageSize() {
    return limit;
  }

  @Override
  public int getOffset() {
    return offset;
  }

  @Override
  public Sort getSort() {
    if (sortBy == null || sortOrder == null) {
      return null;
    }
    return new Sort(sortOrder, sortBy);
  }

  @Override
  public Pageable next() {
    return null;
  }

  @Override
  public Pageable previousOrFirst() {
    return null;
  }

  @Override
  public Pageable first() {
    return null;
  }

  @Override
  public boolean hasPrevious() {
    return false;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

}
