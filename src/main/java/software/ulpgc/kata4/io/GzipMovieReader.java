package software.ulpgc.kata4.io;

import software.ulpgc.kata4.model.Movie;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class GzipMovieReader implements MovieReader {
    private final BufferedReader reader;
    private final MovieDeserializer deserializer;

    public GzipMovieReader(File file, MovieDeserializer deserializer) throws IOException {
        this.reader = readerOf(file);
        this.deserializer = deserializer;
        skipHeader();
    }

    private String skipHeader() throws IOException {
        return this.reader.readLine();
    }

    private static BufferedReader readerOf(File file) throws IOException {
        return new BufferedReader(new InputStreamReader(GZIPInputStream(file)));
    }

    private static GZIPInputStream GZIPInputStream(File file) throws IOException {
        return new GZIPInputStream(new FileInputStream(file));
    }

    @Override
    public Movie read() throws IOException {
        return deserialize(reader.readLine());
    }

    private Movie deserialize(String line) {
        return line != null ? deserializer.deserialize(line) : null;
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}
