package com.goytacazes.poodoacao;

import javafx.application.Application;

/**
 * Ponto de entrada do JAR executável.
 *
 * Precisa existir uma classe que NÃO estenda Application: quando a classe main
 * herda de javafx.application.Application e o JavaFX vem do classpath (caso do
 * fat jar), o launcher da JVM aborta com "JavaFX runtime components are missing".
 */
public final class Launcher {
	private Launcher() {
	}

	public static void main(String[] args) {
		Application.launch(App.class, args);
	}
}
