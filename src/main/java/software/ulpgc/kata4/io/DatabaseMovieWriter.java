package software.ulpgc.kata4.io;

import software.ulpgc.kata4.model.Movie;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static java.sql.Types.INTEGER;
import static java.sql.Types.NVARCHAR;

public class DatabaseMovieWriter implements MovieWriter {
    private final Connection connection;
    private final PreparedStatement insertMoviePreparedStatement;

    public DatabaseMovieWriter(String connection) throws SQLException {
        this(DriverManager.getConnection(connection));
    }

    public static DatabaseMovieWriter open(File file) throws SQLException {
        return new DatabaseMovieWriter("jdbc:sqlite:"+file.getAbsolutePath());
    }
    public DatabaseMovieWriter(Connection connection) throws SQLException {
        this.connection = connection;
        stopAutoCommit();
        this.createTables();
        this.insertMoviePreparedStatement = this.connection.prepareStatement(InsertMovieStatement);
    }
    private final static String InsertMovieStatement = """
            INSERT OR REPLACE INTO movies (id, type, title, year, duration)
            VALUES (?, ?, ?, ?, ?)
            """;
    private final static String CreateMovieStatement = """
            CREATE TABLE IF NOT EXISTS movies(
            id TEXT PRIMARY KEY,
            type TEXT NOT NULL,
            title TEXT NOT NULL,
            year INTEGER,
            duration INTEGER)
            """;
    private void createTables() throws SQLException {
        connection.createStatement().executeUpdate("DROP table if exists movies");
        connection.createStatement().execute(CreateMovieStatement);
    }

    private void stopAutoCommit() throws SQLException {
        this.connection.setAutoCommit(false);
    }

    @Override
    public void writer(Movie movie) {
        try {
            insertMoviePreparedStatementFor(movie).execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement insertMoviePreparedStatementFor(Movie movie) throws SQLException {
        insertMoviePreparedStatement.clearParameters();
        ParameterOf(movie).forEach(this::define);
        return insertMoviePreparedStatement;
    }

    private void define(Parameter parameter) {
        try{
            if(parameter.value == null)
                insertMoviePreparedStatement.setNull(parameter.index,parameter.type);
            else
                insertMoviePreparedStatement.setObject(parameter.index,parameter.value);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    private List<Parameter> ParameterOf(Movie movie) {
        return List.of(
                new Parameter(1,movie.id(),NVARCHAR),
                new Parameter(2, movie.type(), NVARCHAR),
                new Parameter(3, movie.title(),NVARCHAR),
                new Parameter(4, movie.year() != -1 ? movie.year() : null, INTEGER),
                new Parameter(5, movie.duration() != -1 ? movie.duration() : null, INTEGER)
        );
    }

    private record Parameter(int index, Object value, int type) {}

    @Override
    public void close() throws Exception {
        connection.commit();
        connection.close();
    }
}
