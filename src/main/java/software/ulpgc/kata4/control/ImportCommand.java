package software.ulpgc.kata4.control;

import software.ulpgc.kata4.io.*;
import software.ulpgc.kata4.model.Movie;
import software.ulpgc.kata4.ui.ImportDialog;

import java.io.File;
import java.io.IOException;

public class ImportCommand implements Command {
    private final ImportDialog dialog;

    public ImportCommand(ImportDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void execute() {
        try(MovieReader reader = new GzipMovieReader(dialog.get(), new TsvMovieDeserializer());
            MovieWriter writer = DatabaseMovieWriter.open(new File("movies.db"))){
            doExecute(reader,writer);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doExecute(MovieReader reader, MovieWriter writer) throws IOException {
        while (true){
            Movie movie = reader.read();
            if (movie == null) break;
            writer.writer(movie);
        }
    }
}
