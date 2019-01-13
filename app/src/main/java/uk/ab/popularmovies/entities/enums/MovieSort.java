
package uk.ab.popularmovies.entities.enums;

public enum MovieSort {

    POPULARITY("popularity"),
    POPULARITY_DESCENDING("popularity.desc"),
    POPULARITY_ASCENDING("popularity.asc"),
    RATED("rated"),
    RATED_DESCENDING("vote_average.desc"),
    RATED_ASCENDING("vote_average.asc"),

    FAVOURITES("favourites");

    private final String apiValue;

    MovieSort(String value) {
        this.apiValue = value;
    }

    @Override
    public String toString() {
        return apiValue;
    }
}