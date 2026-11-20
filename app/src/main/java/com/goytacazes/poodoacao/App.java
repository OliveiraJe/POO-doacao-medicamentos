package com.goytacazes.poodoacao;

import com.goytacazes.poodoacao.View.LoginWindow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {		
		Scene mainWindow = new Scene(new LoginWindow());
		primaryStage.setScene(mainWindow);
		primaryStage.setTitle("Doação de Medicamentos");
		primaryStage.setResizable(false);
		primaryStage.setWidth(512);
		primaryStage.show();
	}
}
