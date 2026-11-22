package com.goytacazes.poodoacao.View;

import com.goytacazes.poodoacao.Controller.DatabaseController;
import com.goytacazes.poodoacao.Model.Perfil;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginWindow extends BorderPane {
	DatabaseController databaseController;
	Stage stage;

	Label headerLabel = new Label("Painel Doação");
	Label userLabel = new Label("E-mail");
	Label passwordLabel = new Label("Password");
	Label erroLabel = new Label();

	TextField usernameField = new TextField();
	PasswordField passwordField = new PasswordField();

	Button loginBtn = new Button("Login");

	VBox loginVB = new VBox(4);
	HBox loginBtnHB = new HBox();

	public LoginWindow(Stage stage, DatabaseController databaseController) {
		this.stage = stage;
		this.databaseController = databaseController;

		loginBtnHB.setAlignment(Pos.CENTER);
		loginBtnHB.getChildren().add(loginBtn);

		headerLabel.setAlignment(Pos.CENTER);
		headerLabel.setMaxWidth(Double.MAX_VALUE);
		erroLabel.setWrapText(true);

		loginVB.setPadding(new Insets(16));
		loginVB.getChildren().addAll(headerLabel, userLabel, usernameField, passwordLabel, passwordField,
				loginBtnHB, erroLabel);

		loginBtn.setOnAction(e -> entrar());
		passwordField.setOnAction(e -> entrar());

		this.setCenter(loginVB);
	}

	private void entrar() {
		try {
			Perfil perfil = databaseController.autenticar(usernameField.getText().trim(), passwordField.getText());

			if (perfil == null) {
				erroLabel.setText("E-mail ou senha inválidos");
				return;
			}

			if (perfil.getTipoPerfil().equals("doador")) {
				new DonateWindow().start(stage);
			} else {
				new ReceptorWindow().start(stage);
			}
		} catch (Exception e) {
			erroLabel.setText(e.getMessage());
		}
	}
}
