package com.goytacazes.poodoacao.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseController {
	String sqlSchema;

	public DatabaseController(String sqlSchemaFileName) {
		InputStream sqlInputFile = DatabaseController.class.getResourceAsStream("/db/" + sqlSchemaFileName);

		if (sqlInputFile == null) {
			throw new IllegalStateException("schema.sql not found");
		}

		try (sqlInputFile) {
			sqlSchema = new String(sqlInputFile.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public boolean databaseExists() {
		return Files.exists(Path.of("app.db"));
	}

	public void createDatabase() throws SQLException {
		try (Connection con = connect(); Statement statement = con.createStatement()) {
			for (String part : sqlSchema.split(";")) {
				if (!part.isBlank()) {
					statement.execute(part);
				}
			}
		}
	}

	public void insertPerfil(String nome, String email, String senha, String tipoPerfil, String telefone,
							 String cep, String estado, String cidade, String rua, String numero) throws SQLException {
		String sql = "insert into perfil (nome, email, senha_hash, tipo_perfil, telefone, cep, estado, cidade, rua, numero)"
				+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = connect(); PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setString(1, nome);
			statement.setString(2, email);
			statement.setString(3, sha256(senha));
			statement.setString(4, tipoPerfil);
			statement.setString(5, telefone);
			statement.setString(6, cep);
			statement.setString(7, estado);
			statement.setString(8, cidade);
			statement.setString(9, rua);
			statement.setString(10, numero);
			statement.executeUpdate();
		}
	}

	private Connection connect() throws SQLException {
		Connection con = DriverManager.getConnection("jdbc:sqlite:app.db");

		try (Statement statement = con.createStatement()) {
			statement.execute("pragma foreign_keys = on");
		}

		return con;
	}

	private String sha256(String texto) {
		try {
			byte[] hash = MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8));
			StringBuilder hex = new StringBuilder();

			for (byte b : hash) {
				hex.append(String.format("%02x", b));
			}

			return hex.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
}
