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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.goytacazes.poodoacao.Model.Doacao;
import com.goytacazes.poodoacao.Model.Medicamento;
import com.goytacazes.poodoacao.Model.Perfil;

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

	public List<Perfil> listarPerfis() throws SQLException {
		String sql = "select p.id, p.nome, p.email, p.tipo_perfil, p.telefone, p.cep, p.estado, p.cidade, p.rua, p.numero,"
				+ " coalesce(d.cpf, r.cnpj, '') as documento"
				+ " from perfil p"
				+ " left join doador d on d.perfil_id = p.id"
				+ " left join receptor r on r.perfil_id = p.id"
				+ " order by p.id";

		List<Perfil> perfis = new ArrayList<>();

		try (Connection con = connect();
			 Statement statement = con.createStatement();
			 ResultSet result = statement.executeQuery(sql)) {
			while (result.next()) {
				perfis.add(montarPerfil(result));
			}
		}

		return perfis;
	}

	public List<Perfil> listarReceptores() throws SQLException {
		List<Perfil> receptores = new ArrayList<>();

		for (Perfil perfil : listarPerfis()) {
			if (perfil.getTipoPerfil().equals("receptor")) {
				receptores.add(perfil);
			}
		}

		return receptores;
	}

	public void criarPerfil(Perfil perfil, String senha) throws SQLException {
		String sql = "insert into perfil (nome, email, senha_hash, tipo_perfil, telefone, cep, estado, cidade, rua, numero)"
				+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = connect()) {
			con.setAutoCommit(false);

			try {
				int perfilId;

				try (PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
					statement.setString(1, perfil.getNome());
					statement.setString(2, perfil.getEmail());
					statement.setString(3, sha256(senha));
					statement.setString(4, perfil.getTipoPerfil());
					statement.setString(5, perfil.getTelefone());
					statement.setString(6, perfil.getCep());
					statement.setString(7, perfil.getEstado());
					statement.setString(8, perfil.getCidade());
					statement.setString(9, perfil.getRua());
					statement.setString(10, perfil.getNumero());
					statement.executeUpdate();

					try (ResultSet chaves = statement.getGeneratedKeys()) {
						chaves.next();
						perfilId = chaves.getInt(1);
					}
				}

				if (perfil.getTipoPerfil().equals("doador")) {
					gravarDocumento(con, "doador", "cpf", perfil.getDocumento(), perfilId);
				} else {
					gravarDocumento(con, "receptor", "cnpj", perfil.getDocumento(), perfilId);
				}

				con.commit();
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}
		}
	}

	public void atualizarPerfil(Perfil perfil) throws SQLException {
		String sql = "update perfil set nome = ?, email = ?, telefone = ?, cep = ?, estado = ?, cidade = ?, rua = ?, numero = ?"
				+ " where id = ?";

		try (Connection con = connect()) {
			con.setAutoCommit(false);

			try {
				try (PreparedStatement statement = con.prepareStatement(sql)) {
					statement.setString(1, perfil.getNome());
					statement.setString(2, perfil.getEmail());
					statement.setString(3, perfil.getTelefone());
					statement.setString(4, perfil.getCep());
					statement.setString(5, perfil.getEstado());
					statement.setString(6, perfil.getCidade());
					statement.setString(7, perfil.getRua());
					statement.setString(8, perfil.getNumero());
					statement.setInt(9, perfil.getId());
					statement.executeUpdate();
				}

				if (perfil.getTipoPerfil().equals("doador")) {
					try (PreparedStatement statement = con.prepareStatement("update doador set cpf = ? where perfil_id = ?")) {
						statement.setString(1, perfil.getDocumento());
						statement.setInt(2, perfil.getId());
						statement.executeUpdate();
					}
				} else {
					try (PreparedStatement statement = con.prepareStatement("update receptor set cnpj = ? where perfil_id = ?")) {
						statement.setString(1, perfil.getDocumento());
						statement.setInt(2, perfil.getId());
						statement.executeUpdate();
					}
				}

				con.commit();
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}
		}
	}

	public void excluirPerfil(int perfilId) throws SQLException {
		try (Connection con = connect(); PreparedStatement statement = con.prepareStatement("delete from perfil where id = ?")) {
			statement.setInt(1, perfilId);
			statement.executeUpdate();
		}
	}

	public List<Medicamento> listarMedicamentos() throws SQLException {
		String sql = "select id, doador_cpf, nome, descricao, lote, quantidade, validade from medicamento"
				+ " where quantidade > 0 order by nome";

		List<Medicamento> medicamentos = new ArrayList<>();

		try (Connection con = connect();
			 Statement statement = con.createStatement();
			 ResultSet result = statement.executeQuery(sql)) {
			while (result.next()) {
				medicamentos.add(new Medicamento(
						result.getLong("id"),
						result.getString("doador_cpf"),
						result.getString("nome"),
						result.getString("descricao"),
						result.getString("lote"),
						result.getInt("quantidade"),
						LocalDate.parse(result.getString("validade"))));
			}
		}

		return medicamentos;
	}

	public void registrarDoacao(long medicamentoId, String receptorCnpj, int quantidade) throws SQLException {
		try (Connection con = connect()) {
			con.setAutoCommit(false);

			try {
				String doadorCpf;

				try (PreparedStatement statement = con.prepareStatement("select doador_cpf, quantidade from medicamento where id = ?")) {
					statement.setLong(1, medicamentoId);

					try (ResultSet result = statement.executeQuery()) {
						if (!result.next()) {
							throw new SQLException("Medicamento não encontrado");
						}

						doadorCpf = result.getString("doador_cpf");

						if (result.getInt("quantidade") < quantidade) {
							throw new SQLException("Quantidade indisponível para este medicamento");
						}
					}
				}

				try (PreparedStatement statement = con.prepareStatement(
						"insert into doacao (doador_cpf, receptor_cnpj, medicamento_id, data_doacao, quantidade_doada)"
								+ " values (?, ?, ?, date('now'), ?)")) {
					statement.setString(1, doadorCpf);
					statement.setString(2, receptorCnpj);
					statement.setLong(3, medicamentoId);
					statement.setInt(4, quantidade);
					statement.executeUpdate();
				}

				try (PreparedStatement statement = con.prepareStatement(
						"update medicamento set quantidade = quantidade - ? where id = ?")) {
					statement.setInt(1, quantidade);
					statement.setLong(2, medicamentoId);
					statement.executeUpdate();
				}

				con.commit();
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}
		}
	}

	public List<Doacao> listarDoacoes(String receptorCnpj) throws SQLException {
		String sql = "select d.codigo, d.medicamento_id, m.nome, d.doador_cpf, d.receptor_cnpj, d.quantidade_doada, d.data_doacao"
				+ " from doacao d join medicamento m on m.id = d.medicamento_id"
				+ " where d.receptor_cnpj = ? order by d.codigo desc";

		List<Doacao> doacoes = new ArrayList<>();

		try (Connection con = connect(); PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setString(1, receptorCnpj);

			try (ResultSet result = statement.executeQuery()) {
				while (result.next()) {
					doacoes.add(new Doacao(
							result.getLong("codigo"),
							result.getLong("medicamento_id"),
							result.getString("nome"),
							result.getString("doador_cpf"),
							result.getString("receptor_cnpj"),
							result.getInt("quantidade_doada"),
							result.getString("data_doacao")));
				}
			}
		}

		return doacoes;
	}

	public void cancelarDoacao(long codigo) throws SQLException {
		try (Connection con = connect()) {
			con.setAutoCommit(false);

			try {
				long medicamentoId;
				int quantidade;

				try (PreparedStatement statement = con.prepareStatement("select medicamento_id, quantidade_doada from doacao where codigo = ?")) {
					statement.setLong(1, codigo);

					try (ResultSet result = statement.executeQuery()) {
						if (!result.next()) {
							throw new SQLException("Doação não encontrada");
						}

						medicamentoId = result.getLong("medicamento_id");
						quantidade = result.getInt("quantidade_doada");
					}
				}

				try (PreparedStatement statement = con.prepareStatement(
						"update medicamento set quantidade = quantidade + ? where id = ?")) {
					statement.setInt(1, quantidade);
					statement.setLong(2, medicamentoId);
					statement.executeUpdate();
				}

				try (PreparedStatement statement = con.prepareStatement("delete from doacao where codigo = ?")) {
					statement.setLong(1, codigo);
					statement.executeUpdate();
				}

				con.commit();
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}
		}
	}

	private void gravarDocumento(Connection con, String tabela, String coluna, String documento, int perfilId) throws SQLException {
		try (PreparedStatement statement = con.prepareStatement(
				"insert into " + tabela + " (" + coluna + ", perfil_id) values (?, ?)")) {
			statement.setString(1, documento);
			statement.setInt(2, perfilId);
			statement.executeUpdate();
		}
	}

	private Perfil montarPerfil(ResultSet result) throws SQLException {
		return new Perfil(
				result.getInt("id"),
				result.getString("nome"),
				result.getString("email"),
				result.getString("tipo_perfil"),
				result.getString("documento"),
				result.getString("telefone"),
				result.getString("cep"),
				result.getString("estado"),
				result.getString("cidade"),
				result.getString("rua"),
				result.getString("numero"));
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
