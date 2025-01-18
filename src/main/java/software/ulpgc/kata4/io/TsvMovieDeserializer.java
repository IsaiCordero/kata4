package software.ulpgc.kata4.io;

import software.ulpgc.kata4.model.Movie;

public class TsvMovieDeserializer implements MovieDeserializer {
    @Override
    public Movie deserialize(String text) {
        return deserialize(text.split("\t"));
    }

    private Movie deserialize(String[] split) {
        return new Movie(split[0], split[1], split[2], toInt(split[5]), toInt(split[7]));
    }

    private int toInt(String field) {
        if(field != null && !field.equals("\\N")) return Integer.parseInt(field);
        return -1;
    }
}
