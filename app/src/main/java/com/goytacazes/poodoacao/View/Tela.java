package com.goytacazes.poodoacao.View;

import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Define o tamanho das telas de login e cadastro: largura fixa e altura
 * calculada pelo conteúdo, para nenhum campo ficar cortado. Se o conteúdo for
 * mais alto que o monitor, a janela é limitada à área útil da tela.
 */
public final class Tela {
	private Tela() {
	}

	public static final double LOGIN_L = 420;
	public static final double CADASTRO_L = 720;

	public static void abrirAjustada(Stage stage, Parent conteudo, double largura) {
		Rectangle2D area = Screen.getPrimary().getVisualBounds();
		if (conteudo instanceof Region) {
			((Region) conteudo).setPrefWidth(Math.min(largura, area.getWidth() * 0.95));
		}

		stage.setResizable(false);
		stage.setScene(new Scene(conteudo));

		Runnable ajustar = () -> {
			stage.sizeToScene();
			double maxAltura = area.getHeight() * 0.95;
			if (stage.getHeight() > maxAltura) {
				stage.setHeight(maxAltura);
			}
			stage.centerOnScreen();
		};

		if (stage.isShowing()) {
			ajustar.run();
		} else {
			// primeira exibição: ajusta assim que a janela aparece
			stage.setOnShown(e -> {
				stage.setOnShown(null);
				ajustar.run();
			});
		}
	}
}
