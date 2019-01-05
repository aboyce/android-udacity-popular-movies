package uk.ab.popularmovies.entities.database;

import android.arch.persistence.room.TypeConverter;

import java.time.LocalDate;

public class LocalDateConverter {

    @TypeConverter
    public static LocalDate toLocalDate(Long epochDay) {
        return epochDay == null ? null : LocalDate.ofEpochDay(epochDay);
    }

    @TypeConverter
    public static Long toEpochDate(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }
}
