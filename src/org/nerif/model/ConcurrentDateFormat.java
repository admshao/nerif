package org.nerif.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConcurrentDateFormat {

 private ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat> () {

  @Override
  public DateFormat get() {
   return super.get();
  }

  @Override
  protected DateFormat initialValue() {
   return new SimpleDateFormat("yyyy-MM-dd");
  }

  @Override
  public void remove() {
   super.remove();
  }

  @Override
  public void set(DateFormat value) {
   super.set(value);
  }

 };

 public Date convertStringToDate(String dateString) throws ParseException {
  return df.get().parse(dateString);
 }

}