package com.goytacazes.poodoacao.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoginWindow extends BorderPane {
	Label headerLabel = new Label("Painel Doação");
	Label userLabel = new Label("Username");
	Label passwordLabel = new Label("Password");
	
	TextField usernameField = new TextField();
	PasswordField passwordField = new PasswordField();
	
	Button loginBtn = new Button("Login");
	
	VBox loginVB = new VBox(4);
	HBox loginBtnHB = new HBox();

	public LoginWindow() {
		loginBtnHB.setAlignment(Pos.CENTER);
		loginBtnHB.getChildren().add(loginBtn);
		
		headerLabel.setAlignment(Pos.CENTER);
		headerLabel.setMaxWidth(Double.MAX_VALUE);
		
		loginVB.setPadding(new Insets(16));
		loginVB.getChildren().addAll(headerLabel, userLabel, usernameField, passwordLabel, passwordField, loginBtnHB);
		
		this.setCenter(loginVB);
	}
}
