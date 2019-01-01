package uk.ab.popularmovies.entities;

import android.os.Parcel;
import android.os.Parcelable;

import uk.ab.popularmovies.entities.enums.MovieTrailerType;

public class MovieTrailer implements Parcelable {

    private String id;
    private String key;
    private String name;
    private String site;
    private Integer size;
    private MovieTrailerType type;

    public MovieTrailer(Parcel parcel) {
        this.id = parcel.readString();
        this.key = parcel.readString();
        this.name = parcel.readString();
        this.site = parcel.readString();
        this.size = parcel.readInt();
        this.type = MovieTrailerType.getMovieTrailerType(parcel.readString());
    }

    public MovieTrailer(String id, String key, String name, String site, Integer size, MovieTrailerType type) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    public MovieTrailer(String id, String key, String name, String site, Integer size, String type) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = MovieTrailerType.getMovieTrailerType(type);
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public Integer getSize() {
        return size;
    }

    public MovieTrailerType getType() {
        return type;
    }

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.id);
        parcel.writeString(this.key);
        parcel.writeString(this.name);
        parcel.writeString(this.site);
        parcel.writeInt(this.size);
        parcel.writeString(this.type.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
