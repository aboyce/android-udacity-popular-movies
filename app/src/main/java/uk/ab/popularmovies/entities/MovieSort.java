
package uk.ab.popularmovies.entities;

public enum MovieSort {

    POPULARITY_DESCENDING("popularity.desc"),
    POPULARITY_ASCENDING("popularity.asc"),
    RATED_COUNT_DESCENDING("vote_average.asc"),
    RATED_DESCENDING("vote_average.desc");

    private final String apiValue;

    MovieSort(String value) {
        this.apiValue = value;
    }

    @Override
    public String toString() {
        return apiValue;
    }
}
