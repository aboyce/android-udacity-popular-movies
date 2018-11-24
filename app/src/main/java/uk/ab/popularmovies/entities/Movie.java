package uk.ab.popularmovies.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDate;

public class Movie implements Parcelable {

    private Integer id;
    private String title;
    private String imagePath;
    private String backdropImagePath;
    private String plotSynopsis;
    private Double userRating;
    private LocalDate releaseDate;

    public Movie(Parcel parcel) {
        this.id = parcel.readInt();
        this.title = parcel.readString();
        this.imagePath = parcel.readString();
        this.backdropImagePath = parcel.readString();
        this.plotSynopsis = parcel.readString();
        this.userRating = parcel.readDouble();
        this.releaseDate = LocalDate.ofEpochDay(parcel.readLong());
    }

    public Movie(Integer id, String title, String imagePath, String backdropImagePath, String plotSynopsis, Double userRating, LocalDate releaseDate) {
        this.id = id;
        this.title = title;
        this.imagePath = imagePath;
        this.backdropImagePath = backdropImagePath;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public Movie(Integer id, String title, String imagePath, String backdropImagePath, String plotSynopsis, Double userRating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.imagePath = imagePath;
        this.backdropImagePath = backdropImagePath;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;

        // Convert the date from a string, must be in the default expected format 'yyyy-MM-dd'.
        this.releaseDate = (releaseDate == null || releaseDate.equals("")) ? null : LocalDate.parse(releaseDate);
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getBackdropImagePath() {
        return backdropImagePath;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public Double getUserRating() {
        return userRating;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Boolean hasImagePath() {
        // Check to see if there is any image that can be used.
        return (this.imagePath != null) || (this.backdropImagePath != null);
    }

    public String getAnyImagePath() {
        // If there is no image path, return null.
        if (!hasImagePath()) {
            return null;
        }
        // Because we have checked that there is at least one image, we know at least one is fine.
        return (this.imagePath != null) ? this.imagePath : this.backdropImagePath;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.id);
        parcel.writeString(this.title);
        parcel.writeString(this.imagePath);
        parcel.writeString(this.backdropImagePath);
        parcel.writeString(this.plotSynopsis);
        parcel.writeDouble(this.userRating);
        parcel.writeLong(this.releaseDate.toEpochDay());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
