package com.goytacazes.poodoacao.View;

import com.goytacazes.poodoacao.Controller.DatabaseController;
import com.goytacazes.poodoacao.Model.Perfil;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
		registerVB.getChildren().addAll(headerLabel,
				nomeLabel, nomeField,
				emailLabel, emailField,
				senhaLabel, senhaField,
				tipoLabel, tipoField,
				documentoLabel, documentoField,
				telefoneLabel, telefoneField,
				cepLabel, cepField,
				estadoLabel, estadoField,
				cidadeLabel, cidadeField,
				ruaLabel, ruaField,
				numeroLabel, numeroField,
				cadastrarBtnHB, erroLabel);

		cadastrarBtn.setOnAction(e -> cadastrar());

		this.setCenter(registerVB);
	}

	private void cadastrar() {
		if (nomeField.getText().isBlank() || emailField.getText().isBlank() || senhaField.getText().isBlank()
				|| documentoField.getText().isBlank() || telefoneField.getText().isBlank()
				|| cepField.getText().isBlank() || estadoField.getText().isBlank()
				|| cidadeField.getText().isBlank() || ruaField.getText().isBlank()
				|| numeroField.getText().isBlank()) {
			erroLabel.setText("Preencha todos os campos");
			return;
		}

		Perfil perfil = new Perfil(0, nomeField.getText().trim(), emailField.getText().trim(), tipoField.getValue(),
				documentoField.getText().trim(), telefoneField.getText().trim(), cepField.getText().trim(),
				estadoField.getText().trim(), cidadeField.getText().trim(), ruaField.getText().trim(),
				numeroField.getText().trim());

		try {
			databaseController.createDatabase();
			databaseController.criarPerfil(perfil, senhaField.getText());

			stage.setScene(new Scene(new LoginWindow(stage, databaseController)));
		} catch (Exception e) {
			erroLabel.setText(e.getMessage());
		}
	}
}
