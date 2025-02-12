package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDaoTestCase {

	private MovieDao movieDao = new MovieDao();
	private GenreDao genreDao = new GenreDao();

	@BeforeEach
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS movie (\r\n"
						+ "  idmovie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
						+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
						+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
						+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM movie");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='movie'");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='genre'");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00', 1, 120, 'director 1', 'summary of the first movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00', 2, 114, 'director 2', 'summary of the second movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00', 2, 176, 'director 3', 'summary of the third movie')");
		stmt.close();
		connection.close();
	}

	// I kinda copied the tests code from GenreDaoTestCase (little changes)

	@Test
	public void shouldListMovies() {
		//WHEN
		List<Movie> movies = movieDao.listMovies();
		//THEN
		assertThat(movies).hasSize(3);
		assertThat(movies)
				.extracting("title")
				.containsExactlyInAnyOrder("Title 1", "My Title 2", "Third title");
		assertThat(movies)
				.extracting("releaseDate")
				.containsExactlyInAnyOrder(
						LocalDate.parse("2015-11-26", DateTimeFormatter.ISO_LOCAL_DATE),
						LocalDate.parse("2015-11-14", DateTimeFormatter.ISO_LOCAL_DATE),
						LocalDate.parse("2015-12-12", DateTimeFormatter.ISO_LOCAL_DATE)
				);
	}

	@Test
	public void shouldListMoviesByGenre() {
		// WHEN
		Genre comedyGenre = genreDao.getGenre("Comedy");
		List<Movie> comedyMovies = movieDao.listMoviesByGenre("Comedy");
		// THEN
		assertThat(comedyMovies).hasSize(2);
		assertThat(comedyMovies)
				.extracting("title")
				.containsExactlyInAnyOrder("My Title 2", "Third title");
		assertThat(comedyMovies)
				.allMatch(movie -> movie.getGenre().getName().equals("Comedy"));
	}

	@Test
	public void shouldAddMovie() {
		// WHEN
		Genre dramaGenre = genreDao.getGenre("Drama");
		Movie newMovie = new Movie("Inception",
				LocalDate.of(2010, 7, 16),
				dramaGenre,
				148,
				"Christopher Nolan",
				"Science-fiction et rêve lucide.");
		movieDao.addMovie(newMovie);
		List<Movie> movies = movieDao.listMovies();
		// THEN
		assertThat(movies).hasSize(4);
		assertThat(movies)
				.extracting("title")
				.contains("Inception");
		Movie inception = movies.stream()
				.filter(m -> m.getTitle().equals("Inception"))
				.findFirst()
				.orElse(null);
		assertThat(inception).isNotNull();
		assertThat(inception.getReleaseDate()).isEqualTo(LocalDate.of(2010, 7, 16));
		assertThat(inception.getGenre().getName()).isEqualTo("Drama");
	}
}
