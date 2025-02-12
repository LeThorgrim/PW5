package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;

public class GenreDao {

	public List<Genre> listGenres() {
		List<Genre> genres = new ArrayList<>();
		String cmdSQL = "SELECT * FROM genre";

		// less code than in class w/ triple try in one
		try (Connection connection = getDataSource().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(cmdSQL)) {

			while (resultSet.next()) {
				Genre genre = new Genre();
				genre.setId(resultSet.getInt("idgenre"));
				genre.setName(resultSet.getString("name"));
				genres.add(genre);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return genres;
	}

	public Genre getGenre(String name) {
		Genre genre = null;
		String cmdSQL = "SELECT * FROM genre WHERE name = ?";

		// less code than in class w/ double try in one
		try (Connection connection = getDataSource().getConnection();
			 PreparedStatement statement = connection.prepareStatement(cmdSQL)) {

			statement.setString(1, name);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				genre = new Genre();
				genre.setId(resultSet.getInt("idgenre"));
				genre.setName(resultSet.getString("name"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return genre;
	}

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
