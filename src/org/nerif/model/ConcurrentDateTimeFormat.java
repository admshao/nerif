package org.nerif.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConcurrentDateTimeFormat {

 private ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat> () {

  @Override
  public DateFormat get() {
   return super.get();
  }

  @Override
  protected DateFormat initialValue() {
   return new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
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
 
 public String convertDateToString(Date date) throws ParseException {
  return df.get().format(date);
 }

}