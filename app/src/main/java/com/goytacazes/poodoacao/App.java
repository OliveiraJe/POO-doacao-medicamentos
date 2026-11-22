package com.goytacazes.poodoacao;

import com.goytacazes.poodoacao.Controller.DatabaseController;
import com.goytacazes.poodoacao.View.LoginWindow;
import com.goytacazes.poodoacao.View.RegisterWindow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		DatabaseController dbControler = new DatabaseController("schema.sql");
		BorderPane root = dbControler.databaseExists()
				? new LoginWindow()
				: new RegisterWindow(primaryStage, dbControler);

		Scene mainWindow = new Scene(root);
		primaryStage.setScene(mainWindow);
		primaryStage.setTitle("Doação de Medicamentos");
		primaryStage.setResizable(false);
		primaryStage.setWidth(512);
		primaryStage.show();
	}
}
