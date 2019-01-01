package uk.ab.popularmovies.entities.enums;

public enum MovieTrailerType {

    TRAILER("Trailer"),
    TEASER("Teaser"),
    CLIP("Clip"),
    FEATURETTE("Featurette");

    private final String apiValue;

    MovieTrailerType(String value) {
        this.apiValue = value;
    }

    public static MovieTrailerType getMovieTrailerType(String value) {
        switch (value.toLowerCase()) {
            case "trailer":
                return TRAILER;
            case "teaser":
                return TEASER;
            case "clip":
                return CLIP;
            case "featurette":
                return FEATURETTE;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return apiValue;
    }
}
