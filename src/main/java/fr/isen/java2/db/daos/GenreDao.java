package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;

public class GenreDao {

	public List<Genre> listGenres() {
		// create a list of genres that will be used for return
		List<Genre> genres = new ArrayList<>();
		String cmdSQL = "SELECT * FROM genre";

		// less code than in class w/ triple try in one
		try (Connection connection = getDataSource().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(cmdSQL)) {

			// go trough all results
			while (resultSet.next()) {
				// create a new genre & set its values
				Genre genre = new Genre();
				genre.setId(resultSet.getInt("idgenre"));
				genre.setName(resultSet.getString("name"));
				// add genre to list
				genres.add(genre);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return genres;
	}

	public Genre getGenre(String name) {
		// create a genre that will be used for return
		Genre genre = null;
		String cmdSQL = "SELECT * FROM genre WHERE name = ?";

		// less code than in class w/ double try in one
		try (Connection connection = getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement(cmdSQL)) {

			statement.setString(1, name);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				// create a new genre & set its values
				genre = new Genre();
				genre.setId(resultSet.getInt("idgenre"));
				genre.setName(resultSet.getString("name"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return genre;
	}

	//not too complicated, no need of extensive comments
	public void addGenre(String name) {
		String cmdSQL = "INSERT INTO genre(name) VALUES(?)";

		// less code than in class w/ double try in one
		try (Connection connection = getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement(cmdSQL)) {

			statement.setString(1, name);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
