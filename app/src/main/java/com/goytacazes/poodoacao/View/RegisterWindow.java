package com.goytacazes.poodoacao.View;

import com.goytacazes.poodoacao.Controller.DatabaseController;
import com.goytacazes.poodoacao.Model.Perfil;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterWindow extends BorderPane {
	DatabaseController databaseController;
	Stage stage;

	Label headerLabel = new Label("Cadastro inicial");
	Label nomeLabel = new Label("Nome");
	Label emailLabel = new Label("E-mail");
	Label senhaLabel = new Label("Senha");
	Label tipoLabel = new Label("Perfil");
	Label documentoLabel = new Label("CPF");
	Label telefoneLabel = new Label("Telefone");
	Label cepLabel = new Label("CEP");
	Label estadoLabel = new Label("Estado");
	Label cidadeLabel = new Label("Cidade");
	Label ruaLabel = new Label("Rua");
	Label numeroLabel = new Label("Número");
	Label erroLabel = new Label();

	TextField nomeField = new TextField();
	TextField emailField = new TextField();
	PasswordField senhaField = new PasswordField();
	ChoiceBox<String> tipoField = new ChoiceBox<>();
	TextField documentoField = new TextField();
	TextField telefoneField = new TextField();
	TextField cepField = new TextField();
	TextField estadoField = new TextField();
	TextField cidadeField = new TextField();
	TextField ruaField = new TextField();
	TextField numeroField = new TextField();

	Button cadastrarBtn = new Button("Cadastrar");

	GridPane formulario = new GridPane();
	VBox registerVB = new VBox(4);
	HBox cadastrarBtnHB = new HBox();

	public RegisterWindow(Stage stage, DatabaseController databaseController) {
		this.stage = stage;
		this.databaseController = databaseController;

		tipoField.setItems(FXCollections.observableArrayList("doador", "receptor"));
		tipoField.setValue("doador");
		tipoField.setMaxWidth(Double.MAX_VALUE);
		tipoField.valueProperty().addListener((obs, antigo, novo) ->
				documentoLabel.setText("receptor".equals(novo) ? "CNPJ" : "CPF"));

		cadastrarBtnHB.setAlignment(Pos.CENTER);
		cadastrarBtnHB.getChildren().add(cadastrarBtn);

		headerLabel.setAlignment(Pos.CENTER);
		headerLabel.setMaxWidth(Double.MAX_VALUE);
		erroLabel.setWrapText(true);

		registerVB.setPadding(new Insets(16));
		// campos em duas colunas para a tela caber em monitores menores
		formulario.setHgap(12);
		formulario.setVgap(10);
		formulario.addRow(0, tipoLabel, tipoField, documentoLabel, documentoField);
		formulario.addRow(1, nomeLabel, nomeField, emailLabel, emailField);
		formulario.addRow(2, senhaLabel, senhaField, telefoneLabel, telefoneField);
		formulario.addRow(3, cepLabel, cepField, estadoLabel, estadoField);
		formulario.addRow(4, cidadeLabel, cidadeField, ruaLabel, ruaField);
		formulario.addRow(5, numeroLabel, numeroField);

		ColumnConstraints rotulo1 = new ColumnConstraints(70);
		ColumnConstraints campo1 = new ColumnConstraints();
		campo1.setHgrow(Priority.ALWAYS);
		ColumnConstraints rotulo2 = new ColumnConstraints(70);
		ColumnConstraints campo2 = new ColumnConstraints();
		campo2.setHgrow(Priority.ALWAYS);
		formulario.getColumnConstraints().addAll(rotulo1, campo1, rotulo2, campo2);

		registerVB.setSpacing(10);
		registerVB.getChildren().addAll(headerLabel, formulario, cadastrarBtnHB, erroLabel);

		cadastrarBtn.setOnAction(e -> cadastrar());

		this.setCenter(registerVB);
	}


	private void cadastrar() {
		if (tipoField.getValue() == null || nomeField.getText().isBlank() || emailField.getText().isBlank()
				|| senhaField.getText().isBlank() || documentoField.getText().isBlank()
				|| telefoneField.getText().isBlank() || cepField.getText().isBlank()
				|| estadoField.getText().isBlank() || cidadeField.getText().isBlank()
				|| ruaField.getText().isBlank() || numeroField.getText().isBlank()) {
			erroLabel.setText("Preencha todos os campos.");
			return;
		}

		String documento = documentoField.getText().replaceAll("\\D", "");
		int tamanhoDocumento = "doador".equals(tipoField.getValue()) ? 11 : 14;
		if (documento.length() != tamanhoDocumento) {
			erroLabel.setText("Informe um " + ("doador".equals(tipoField.getValue()) ? "CPF" : "CNPJ")
					+ " com " + tamanhoDocumento + " dígitos.");
			return;
		}
		if (!emailField.getText().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
			erroLabel.setText("Informe um e-mail válido.");
			return;
		}

		Perfil perfil = new Perfil(0, nomeField.getText().trim(), emailField.getText().trim(), tipoField.getValue(),
				documento, telefoneField.getText().trim(), cepField.getText().trim(),
				estadoField.getText().trim(), cidadeField.getText().trim(), ruaField.getText().trim(),
				numeroField.getText().trim());

		try {
			databaseController.createDatabase();
			databaseController.criarPerfil(perfil, senhaField.getText());
			Tela.abrirAjustada(stage, new LoginWindow(stage, databaseController), Tela.LOGIN_L);
		} catch (Exception e) {
			erroLabel.setText(e.getMessage());
		}
	}

}
