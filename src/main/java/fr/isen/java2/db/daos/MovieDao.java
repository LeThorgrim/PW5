package fr.isen.java2.db.daos;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;

public class MovieDao {

	public List<Movie> listMovies() {
		List<Movie> movies = new ArrayList<>();
		// a bit complex querry (i forgot to use the queries on java210, but it works anyway)
		String sql = "SELECT m.idmovie, m.title, m.release_date, g.idgenre, g.name, " +
				"m.duration, m.director, m.summary " +
				"FROM movie m JOIN genre g ON m.genre_id = g.idgenre";

		// less code than in class w/ triple try in one
		try (Connection connection = getDataSource().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				// date format
				String dateString = resultSet.getString("release_date");
				LocalDate releaseDate = LocalDate.parse(dateString.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);

				Genre genre = new Genre(resultSet.getInt("idgenre"), resultSet.getString("name"));
				Movie movie = new Movie(
						resultSet.getInt("idmovie"),
						resultSet.getString("title"),
						releaseDate,
						genre,
						resultSet.getInt("duration"),
						resultSet.getString("director"),
						resultSet.getString("summary")
				);
				movies.add(movie);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return movies;
	}

	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> movies = new ArrayList<>();
		// same as listMovies()
		String sql = "SELECT m.idmovie, m.title, m.release_date, g.idgenre, g.name, " +
				"m.duration, m.director, m.summary " +
				"FROM movie m JOIN genre g ON m.genre_id = g.idgenre " +
				"WHERE g.name = ?";

		// less code than in class w/ triple try in one
		try (Connection connection = getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, genreName);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				// date format
				String dateString = resultSet.getString("release_date");
				LocalDate releaseDate = LocalDate.parse(dateString.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);

				Genre genre = new Genre(resultSet.getInt("idgenre"), resultSet.getString("name"));
				Movie movie = new Movie(
						resultSet.getInt("idmovie"),
						resultSet.getString("title"),
						releaseDate,
						genre,
						resultSet.getInt("duration"),
						resultSet.getString("director"),
						resultSet.getString("summary")
				);
				movies.add(movie);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return movies;
	}

	public Movie addMovie(Movie movie) {
		// again, same as listMovies()
		String sql = "INSERT INTO movie (title, release_date, genre_id, duration, director, summary) " +
				"VALUES (?, ?, ?, ?, ?, ?)";

		// less code than in class w/ double try in one
		try (Connection connection = getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			statement.setString(1, movie.getTitle());
			statement.setString(2, movie.getReleaseDate().toString() + " 00:00:00"); // Stocker au format 'YYYY-MM-DD HH:MM:SS'
			statement.setInt(3, movie.getGenre().getId());
			statement.setInt(4, movie.getDuration());
			statement.setString(5, movie.getDirector());
			statement.setString(6, movie.getSummary());

			statement.executeUpdate();

			ResultSet generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next()) {
				movie.setId(generatedKeys.getInt(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return movie;
	}
}
