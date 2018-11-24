package uk.ab.popularmovies.entities;

import java.time.LocalDate;

public class Movie {

    private Integer id;
    private String title;
    private String imagePath;
    private String backdropImagePath;
    private String plotSynopsis;
    private Double userRating;
    private LocalDate releaseDate;

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

    public String getbackdropImagePath() {
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
}
