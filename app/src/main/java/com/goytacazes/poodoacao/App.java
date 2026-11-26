package com.goytacazes.poodoacao;

import com.goytacazes.poodoacao.Controller.DatabaseController;
import com.goytacazes.poodoacao.View.LoginWindow;
import com.goytacazes.poodoacao.View.RegisterWindow;
import com.goytacazes.poodoacao.View.Tela;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		DatabaseController dbControler = new DatabaseController("schema.sql");
		dbControler.createDatabase();
		BorderPane root = dbControler.listarPerfis().isEmpty()
				? new RegisterWindow(primaryStage, dbControler)
				: new LoginWindow(primaryStage, dbControler);

		primaryStage.setTitle("Doação de Medicamentos");
		Tela.abrirAjustada(primaryStage, root,
				root instanceof RegisterWindow ? Tela.CADASTRO_L : Tela.LOGIN_L);
		primaryStage.show();
		primaryStage.centerOnScreen();
	}
}
