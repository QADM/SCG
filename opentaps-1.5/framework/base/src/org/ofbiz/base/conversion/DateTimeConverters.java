/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.base.conversion;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.TimeDuration;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;

import com.ibm.icu.util.Calendar;

/** Date/time Converter classes. */
public class DateTimeConverters implements ConverterLoader {
    public static class CalendarToLong extends AbstractConverter<Calendar, Long> {
        public CalendarToLong() {
            super(Calendar.class, Long.class);
        }

        public Long convert(Calendar obj) throws ConversionException {
            return obj.getTimeInMillis();
        }
    }

    public static class CalendarToString extends AbstractConverter<Calendar, String> {
        public CalendarToString() {
            super(Calendar.class, String.class);
        }

        public String convert(Calendar obj) throws ConversionException {
            Locale locale = obj.getLocale(com.ibm.icu.util.ULocale.VALID_LOCALE).toLocale();
            TimeZone timeZone = UtilDateTime.toTimeZone(obj.getTimeZone().getID());
            DateFormat df = UtilDateTime.toDateTimeFormat(UtilDateTime.DATE_TIME_FORMAT, timeZone, locale);
            return df.format(obj);
        }
    }

    public static class DateToLong extends AbstractConverter<java.util.Date, Long> {
        public DateToLong() {
            super(java.util.Date.class, Long.class);
        }

        public Long convert(java.util.Date obj) throws ConversionException {
             return obj.getTime();
        }
    }

    public static class DateToSqlDate extends AbstractConverter<java.util.Date, java.sql.Date> {
        public DateToSqlDate() {
            super(java.util.Date.class, java.sql.Date.class);
        }

        public java.sql.Date convert(java.util.Date obj) throws ConversionException {
            return new java.sql.Date(obj.getTime());
        }
    }

    public static class DateToString extends GenericLocalizedConverter<java.util.Date, String> {
        public DateToString() {
            super(java.util.Date.class, String.class);
        }

        @Override
        public String convert(Date obj) throws ConversionException {
            return obj.toString();
        }

        public String convert(java.util.Date obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
            DateFormat df = null;
            if (UtilValidate.isEmpty(formatString)) {
                df = UtilDateTime.toDateTimeFormat(UtilDateTime.DATE_TIME_FORMAT, timeZone, locale);
            } else {
                df = UtilDateTime.toDateTimeFormat(formatString, timeZone, locale);
            }
            return df.format(obj);
        }
    }

    public static class DateToTimestamp extends AbstractConverter<java.util.Date, java.sql.Timestamp> {
        public DateToTimestamp() {
            super(java.util.Date.class, java.sql.Timestamp.class);
        }

        public java.sql.Timestamp convert(java.util.Date obj) throws ConversionException {
            return new java.sql.Timestamp(obj.getTime());
        }
    }

    public static class DurationToBigDecimal extends AbstractConverter<TimeDuration, java.math.BigDecimal> {
        public DurationToBigDecimal() {
            super(TimeDuration.class, java.math.BigDecimal.class);
        }

        public java.math.BigDecimal convert(TimeDuration obj) throws ConversionException {
             return new java.math.BigDecimal(TimeDuration.toLong(obj));
        }
    }

    public static class DurationToDouble extends AbstractConverter<TimeDuration, Double> {
        public DurationToDouble() {
            super(TimeDuration.class, Double.class);
        }

        public Double convert(TimeDuration obj) throws ConversionException {
             return Double.valueOf(TimeDuration.toLong(obj));
        }
    }

    public static class DurationToFloat extends AbstractConverter<TimeDuration, Float> {
        public DurationToFloat() {
            super(TimeDuration.class, Float.class);
        }

        public Float convert(TimeDuration obj) throws ConversionException {
             return Float.valueOf(TimeDuration.toLong(obj));
        }
    }

    public static class DurationToLong extends AbstractConverter<TimeDuration, Long> {
        public DurationToLong() {
            super(TimeDuration.class, Long.class);
        }

        public Long convert(TimeDuration obj) throws ConversionException {
             return TimeDuration.toLong(obj);
        }
    }

    public static class DurationToList extends GenericSingletonToList<TimeDuration> {
        public DurationToList() {
            super(TimeDuration.class);
        }
    }

    public static class DurationToSet extends GenericSingletonToSet<TimeDuration> {
        public DurationToSet() {
            super(TimeDuration.class);
        }
    }

    public static class DurationToString extends AbstractConverter<TimeDuration, String> {
        public DurationToString() {
            super(TimeDuration.class, String.class);
        }

        public String convert(TimeDuration obj) throws ConversionException {
             return obj.toString();
        }
    }

    public static abstract class GenericLocalizedConverter<S, T> extends AbstractLocalizedConverter<S, T> {
        protected GenericLocalizedConverter(Class<S> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public T convert(S obj) throws ConversionException {
            return convert(obj, Locale.getDefault(), TimeZone.getDefault(), null);
        }

        public T convert(S obj, Locale locale, TimeZone timeZone) throws ConversionException {
            return convert(obj, locale, timeZone, null);
        }
    }

    public static class LongToCalendar extends AbstractLocalizedConverter<Long, Calendar> {
        public LongToCalendar() {
            super(Long.class, Calendar.class);
        }

        public Calendar convert(Long obj) throws ConversionException {
            return convert(obj, Locale.getDefault(), TimeZone.getDefault());
        }

        public Calendar convert(Long obj, Locale locale, TimeZone timeZone) throws ConversionException {
            return UtilDateTime.toCalendar(new java.util.Date(obj.longValue()), timeZone, locale);
        }

        public Calendar convert(Long obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
            return convert(obj, Locale.getDefault(), TimeZone.getDefault());
        }
    }

    public static class NumberToDate extends AbstractConverter<Number, java.util.Date> {
        public NumberToDate() {
            super(Number.class, java.util.Date.class);
        }

        public java.util.Date convert(Number obj) throws ConversionException {
             return new java.util.Date(obj.longValue());
        }
    }

    public static class NumberToDuration extends AbstractConverter<Number, TimeDuration> {
        public NumberToDuration() {
            super(Number.class, TimeDuration.class);
        }

        public TimeDuration convert(Number obj) throws ConversionException {
             return TimeDuration.fromNumber(obj);
        }
    }

    public static class NumberToSqlDate extends AbstractConverter<Number, java.sql.Date> {
        public NumberToSqlDate() {
            super(Number.class, java.sql.Date.class);
        }

        public java.sql.Date convert(Number obj) throws ConversionException {
             return new java.sql.Date(obj.longValue());
        }
    }

    public static class NumberToSqlTime extends AbstractConverter<Number, java.sql.Time> {
        public NumberToSqlTime() {
            super(Number.class, java.sql.Time.class);
        }

        public java.sql.Time convert(Number obj) throws ConversionException {
             return new java.sql.Time(obj.longValue());
        }
    }

    public static class NumberToTimestamp extends AbstractConverter<Number, java.sql.Timestamp> {
        public NumberToTimestamp() {
            super(Number.class, java.sql.Timestamp.class);
        }

        public java.sql.Timestamp convert(Number obj) throws ConversionException {
             return new java.sql.Timestamp(obj.longValue());
        }
    }

    public static class SqlDateToDate extends AbstractConverter<java.sql.Date, java.util.Date> {
        public SqlDateToDate() {
            super(java.sql.Date.class, java.util.Date.class);
        }

        public java.util.Date convert(java.sql.Date obj) throws ConversionException {
            return new java.util.Date(obj.getTime());
        }
    }

    public static class SqlDateToList extends GenericSingletonToList<java.sql.Date> {
        public SqlDateToList() {
            super(java.sql.Date.class);
        }
    }

    public static class SqlDateToSet extends GenericSingletonToSet<java.sql.Date> {
        public SqlDateToSet() {
            super(java.sql.Date.class);
        }
    }

    public static class SqlDateToString extends GenericLocalizedConverter<java.sql.Date, String> {
        public SqlDateToString() {
            super(java.sql.Date.class, String.class);
        }

        @Override
        public String convert(java.sql.Date obj) throws ConversionException {
            return obj.toString();
        }

        public String convert(java.sql.Date obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
            DateFormat df = null;
            if (UtilValidate.isEmpty(formatString)) {
                df = UtilDateTime.toDateFormat(UtilDateTime.getDateFormat(locale), timeZone, locale);
            } else {
                df = UtilDateTime.toDateFormat(formatString, timeZone, locale);
            }
            return df.format(obj);
        }
    }

    public static class SqlDateToTime extends AbstractConverter<java.sql.Date, java.sql.Time> {
        public SqlDateToTime() {
            super(java.sql.Date.class, java.sql.Time.class);
        }

        public java.sql.Time convert(java.sql.Date obj) throws ConversionException {
            throw new ConversionException("Conversion from Date to Time not supported");
       }
    }

    public static class SqlTimeToSqlDate extends AbstractConverter<java.sql.Time, java.sql.Date> {
        public SqlTimeToSqlDate() {
            super(java.sql.Time.class, java.sql.Date.class);
        }

        public java.sql.Date convert(java.sql.Time obj) throws ConversionException {
            throw new ConversionException("Conversion from Time to Date not supported");
        }
    }

    public static class SqlTimeToList extends GenericSingletonToList<java.sql.Time> {
        public SqlTimeToList() {
            super(java.sql.Time.class);
        }
    }

    public static class SqlDateToTimestamp extends AbstractConverter<java.sql.Date, java.sql.Timestamp> {
        public SqlDateToTimestamp() {
            super(java.sql.Date.class, java.sql.Timestamp.class);
        }

        public java.sql.Timestamp convert(java.sql.Date obj) throws ConversionException {
            return new java.sql.Timestamp(obj.getTime());
       }
    }

    public static class SqlTimeToSet extends GenericSingletonToSet<java.sql.Time> {
        public SqlTimeToSet() {
            super(java.sql.Time.class);
        }
    }

    public static class SqlTimeToString extends GenericLocalizedConverter<java.sql.Time, String> {
        public SqlTimeToString() {
            super(java.sql.Time.class, String.class);
        }

        public String convert(java.sql.Time obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
            DateFormat df = null;
            if (UtilValidate.isEmpty(formatString)) {
                df = UtilDateTime.toTimeFormat(UtilDateTime.getTimeFormat(locale), timeZone, locale);
            } else {
                df = UtilDateTime.toTimeFormat(formatString, timeZone, locale);
            }
            return df.format(obj);
        }
    }

    public static class StringToCalendar extends AbstractLocalizedConverter<String, Calendar> {
        public Calendar convert(String obj) throws ConversionException {
            return convert(obj, Locale.getDefault(), TimeZone.getDefault(), null);
        }

        public Calendar convert(String obj, Locale locale, TimeZone timeZone) throws ConversionException {
            return convert(obj, Locale.getDefault(), TimeZone.getDefault(), null);
        }

        public StringToCalendar() {
            super(String.class, Calendar.class);
        }

        public Calendar convert(String obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
            String trimStr = obj.trim();
            if (trimStr.length() == 0) {
                return null;
            }
            DateFormat df = null;
            if (UtilValidate.isEmpty(formatString)) {
                df = UtilDateTime.toDateTimeFormat(UtilDateTime.DATE_TIME_FORMAT, timeZone, locale);
            } else {
                df = UtilDateTime.toDateTimeFormat(formatString, timeZone, locale);
            }
            try {
                java.util.Date date = df.parse(trimStr);
                return UtilDateTime.toCalendar(date, timeZone, locale);
            } catch (ParseException e) {
                throw new ConversionException(e);
            }
        }
    }

    public static class StringToDate extends GenericLocalizedConverter<String, java.util.Date> {
        public StringToDate() {
            super(String.class, java.util.Date.class);
        }

        public java.util.Date convert(String obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
        	DateFormat df = null;
            String str = obj.trim();
            if (str.length() == 0) {
                return null;
            }

            // check str in old format yyyy-MM-dd
            if (str.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            	formatString = "yyyy-MM-dd";
            }

            String dateFormat = UtilValidate.isEmpty(formatString) ? UtilDateTime.getDateFormat(locale) : formatString;
            if (!UtilValidate.isDateTime(str, dateFormat, locale, timeZone)) {
                throw new ConversionException("Invalid format. Could not convert " + str + " to Date");
            }
            df = UtilDateTime.toDateFormat(dateFormat, timeZone, locale);
           try {
        	   Date fieldDate = df.parse(str);
               return new java.sql.Date(fieldDate.getTime());
            } catch (ParseException e) {
                throw new ConversionException(e);
            }
        }
    }

    public static class StringToDuration extends AbstractLocalizedConverter<String, TimeDuration> {
        public StringToDuration() {
            super(String.class, TimeDuration.class);
        }

        public TimeDuration convert(String obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
            return convert(obj, locale, timeZone);
        }

        public TimeDuration convert(String obj, Locale locale, TimeZone timeZone) throws ConversionException {
            if (!obj.contains(":")) {
                // Encoded duration
                try {
                    NumberFormat nf = NumberFormat.getNumberInstance(locale);
                    nf.setMaximumFractionDigits(0);
                    Number number = nf.parse(obj);
                    return TimeDuration.fromNumber(number);
                } catch (ParseException e) {
                    throw new ConversionException(e);
                }
            }
            return convert(obj);
        }

        public TimeDuration convert(String obj) throws ConversionException {
            return TimeDuration.parseDuration(obj);
        }
    }

    public static class StringToSqlDate extends GenericLocalizedConverter<String, java.sql.Date> {
        public StringToSqlDate() {
            super(String.class, java.sql.Date.class);
        }

        public java.sql.Date convert(String obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
        	DateFormat df = null;
            String str = obj.trim();
            if (str.length() == 0) {
                return null;
            }

            // check str in old format yyyy-MM-dd
            if (str.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            	formatString = "yyyy-MM-dd";
            }

            String dateFormat = UtilValidate.isEmpty(formatString) ? UtilDateTime.getDateFormat(locale) : formatString;
            if (!UtilValidate.isDateTime(str, dateFormat, locale, timeZone)) {
                throw new ConversionException("Invalid format. Could not convert " + str + " to Date");
            }
            df = UtilDateTime.toDateFormat(dateFormat, timeZone, locale);
           try {
        	   Date fieldDate = df.parse(str);
               return new java.sql.Date(fieldDate.getTime());
            } catch (ParseException e) {
                throw new ConversionException(e);
            }
        }
    }

    public static class StringToSqlTime extends GenericLocalizedConverter<String, java.sql.Time> {
        public StringToSqlTime() {
            super(String.class, java.sql.Time.class);
        }

        public java.sql.Time convert(String obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
            String trimStr = obj.trim();
            if (trimStr.length() == 0) {
                return null;
            }
            DateFormat df = null;
            if (UtilValidate.isEmpty(formatString)) {
                df = UtilDateTime.toTimeFormat(UtilDateTime.getTimeFormat(locale), timeZone, locale);
            } else {
                df = UtilDateTime.toTimeFormat(formatString, timeZone, locale);
            }
            try {
                return new java.sql.Time(df.parse(trimStr).getTime());
            } catch (ParseException e) {
                throw new ConversionException(e);
            }
        }
    }

    public static class StringToTimestamp extends GenericLocalizedConverter<String, java.sql.Timestamp> {
        public StringToTimestamp() {
            super(String.class, java.sql.Timestamp.class);
        }

        public java.sql.Timestamp convert(String obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
            String str = obj.trim();
            if (str.length() == 0) {
                return null;
            }
            DateFormat df = null;
            
            /* check if str in timestamp format */
            if (str.matches("^\\d{4}-\\d{2}-\\d{2} \\d{1,2}:\\d{2}:\\d{1,2}$") || str.matches("^\\d{4}-\\d{2}-\\d{2} \\d{1,2}:\\d{2}:\\d{1,2}.\\d+$")) {
            	formatString = "yyyy-MM-dd HH:mm:ss.S";
                // hack to mimic Timestamp.valueOf() method
                if (str.length() > 0 && !str.contains(".")) {
                    str += ".0";
                } else {
                    // DateFormat has a funny way of parsing milliseconds:
                    // 00:00:00.2 parses to 00:00:00.002
                    // so we'll add zeros to the end to get 00:00:00.200
                    String[] timeSplit = str.split("[.]");
                    if (timeSplit.length > 1 && timeSplit[1].length() < 3) {
                        str = str + "000".substring(timeSplit[1].length());
                    }
                }
            } else if (str.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            	formatString = "yyyy-MM-dd HH:mm:ss.S";
                str += " 00:00:00.0";
            }
            try {
                if (UtilValidate.isEmpty(formatString)) {
                    try {
                        String dateTimeFormat = UtilDateTime.getDateTimeFormat(locale);
                        if (!UtilValidate.isDateTime(str, dateTimeFormat, locale, timeZone)) {
                            throw new ParseException("Invalid format. Could not convert " + str + " to Timestamp", 0);
                        }
                        df = UtilDateTime.toDateTimeFormat(dateTimeFormat, timeZone, null);
                    } catch (ParseException e) {
                        String dateFormat = UtilDateTime.getDateFormat(locale);
                        if (!UtilValidate.isDateTime(str, dateFormat, locale, timeZone)) {
                        	throw new ConversionException("Invalid format. Could not convert " + str + " to Timestamp");
                        }
                        df = UtilDateTime.toDateTimeFormat(dateFormat, timeZone, locale);
                    }
                } else {
                    if (!UtilValidate.isDateTime(str, formatString, locale, timeZone)) {
                    	throw new ConversionException("Invalid format. Could not convert " + str + " to Timestamp");
                    }
                    df = UtilDateTime.toDateTimeFormat(formatString, timeZone, null);
                }
                return new java.sql.Timestamp(df.parse(str).getTime());
            } catch (ParseException e) {
                throw new ConversionException(e);
            }
        }
    }

    public static class StringToTimeZone extends AbstractConverter<String, TimeZone> {
        public StringToTimeZone() {
            super(String.class, TimeZone.class);
        }

        public TimeZone convert(String obj) throws ConversionException {
            TimeZone tz = UtilDateTime.toTimeZone(obj);
            if (tz != null) {
                return tz;
            } else {
                throw new ConversionException("Could not convert " + obj + " to TimeZone: ");
            }
        }
    }

    public static class TimestampToDate extends AbstractConverter<java.sql.Timestamp, java.util.Date> {
        public TimestampToDate() {
            super(java.sql.Timestamp.class, java.util.Date.class);
        }

        public java.util.Date convert(java.sql.Timestamp obj) throws ConversionException {
            return new java.sql.Timestamp(obj.getTime());
        }
    }

    public static class TimestampToSqlDate extends AbstractConverter<java.sql.Timestamp, java.sql.Date> {
        public TimestampToSqlDate() {
            super(java.sql.Timestamp.class, java.sql.Date.class);
        }

        public java.sql.Date convert(java.sql.Timestamp obj) throws ConversionException {
            return new java.sql.Date(obj.getTime());
        }
    }

    public static class TimeZoneToString extends AbstractConverter<TimeZone, String> {
        public TimeZoneToString() {
            super(TimeZone.class, String.class);
        }

        public String convert(TimeZone obj) throws ConversionException {
            return obj.getID();
        }
    }

    public static class TimestampToList extends GenericSingletonToList<java.sql.Timestamp> {
        public TimestampToList() {
            super(java.sql.Timestamp.class);
        }
    }

    public static class TimestampToSet extends GenericSingletonToSet<java.sql.Timestamp> {
        public TimestampToSet() {
            super(java.sql.Timestamp.class);
        }
    }

    public void loadConverters() {
        Converters.loadContainedConverters(DateTimeConverters.class);
    }
}
