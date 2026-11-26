package com.goytacazes.poodoacao.View;

import com.goytacazes.poodoacao.Controller.DatabaseController;
import com.goytacazes.poodoacao.Model.Medicamento;
import com.goytacazes.poodoacao.Model.Perfil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class DonateWindow {
    private final DatabaseController databaseController;
    private final Perfil doador;
    private final ObservableList<Medicamento> medicamentos = FXCollections.observableArrayList();

    public DonateWindow(DatabaseController databaseController, Perfil doador) {
        this.databaseController = databaseController;
        this.doador = doador;
    }

    public void start(Stage stage) {
        stage.setTitle("Doação de Medicamentos");

        Label titulo = new Label("Olá, " + doador.getNome() + " - Cadastro de medicamentos");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField nome = new TextField();
        nome.setPromptText("Nome do medicamento");
        TextField descricao = new TextField();
        descricao.setPromptText("Descrição");
        TextField lote = new TextField();
        lote.setPromptText("Lote");
        Spinner<Integer> quantidade = new Spinner<>(1, 1000, 1);
        quantidade.setEditable(true);
        DatePicker validade = new DatePicker();

        Button adicionar = new Button("Cadastrar medicamento");
        Button atualizar = new Button("Atualizar selecionado");
        Button remover = new Button("Remover selecionado");
        Button voltar = new Button("Voltar ao login");
        atualizar.setDisable(true);

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(8);
        formulario.addRow(0, new Label("Medicamento:"), nome, new Label("Quantidade:"), quantidade);
        formulario.addRow(1, new Label("Descrição:"), descricao, new Label("Validade:"), validade);
        formulario.addRow(2, new Label("Lote:"), lote);
        ColumnConstraints label1 = new ColumnConstraints();
        ColumnConstraints campo1 = new ColumnConstraints(); campo1.setHgrow(Priority.ALWAYS);
        ColumnConstraints label2 = new ColumnConstraints();
        ColumnConstraints campo2 = new ColumnConstraints(); campo2.setHgrow(Priority.ALWAYS);
        formulario.getColumnConstraints().addAll(label1, campo1, label2, campo2);

        TableView<Medicamento> tabela = new TableView<>(medicamentos);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPlaceholder(new Label("Nenhum medicamento cadastrado."));

        TableColumn<Medicamento, Number> id = new TableColumn<>("ID");
        id.setCellValueFactory(d -> d.getValue().idProperty());
        TableColumn<Medicamento, String> colNome = new TableColumn<>("Medicamento");
        colNome.setCellValueFactory(d -> d.getValue().nomeProperty());
        TableColumn<Medicamento, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(d -> d.getValue().descricaoProperty());
        TableColumn<Medicamento, String> colLote = new TableColumn<>("Lote");
        colLote.setCellValueFactory(d -> d.getValue().loteProperty());
        TableColumn<Medicamento, Number> colQtd = new TableColumn<>("Quantidade");
        colQtd.setCellValueFactory(d -> d.getValue().quantidadeProperty());
        TableColumn<Medicamento, String> colValidade = new TableColumn<>("Validade");
        colValidade.setCellValueFactory(d -> d.getValue().validadeFormatadaProperty());
        tabela.getColumns().addAll(id, colNome, colDescricao, colLote, colQtd, colValidade);
        tabela.setPrefHeight(300);
        VBox.setVgrow(tabela, Priority.ALWAYS);

        Runnable carregar = () -> {
            try {
                medicamentos.setAll(databaseController.listarMedicamentosDoador(doador.getDocumento()));
            } catch (Exception ex) {
                alerta(ex.getMessage());
            }
        };

        tabela.getSelectionModel().selectedItemProperty().addListener((obs, anterior, selecionado) -> {
            boolean temSelecao = selecionado != null;
            atualizar.setDisable(!temSelecao);
            remover.setDisable(!temSelecao);
            if (temSelecao) {
                nome.setText(selecionado.getNome());
                descricao.setText(selecionado.getDescricao());
                lote.setText(selecionado.getLote());
                quantidade.getValueFactory().setValue(Math.max(1, selecionado.getQuantidade()));
                validade.setValue(selecionado.getValidade());
            }
        });

        adicionar.setOnAction(e -> {
            if (!camposValidos(nome, descricao, lote, quantidade, validade)) return;
            try {
                databaseController.cadastrarMedicamento(new Medicamento(0, doador.getDocumento(),
                        nome.getText().trim(), descricao.getText().trim(), lote.getText().trim(),
                        quantidade.getValue(), validade.getValue()));
                limparFormulario(nome, descricao, lote, quantidade, validade);
                carregar.run();
            } catch (Exception ex) {
                alerta(ex.getMessage());
            }
        });

        atualizar.setOnAction(e -> {
            Medicamento selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null || !camposValidos(nome, descricao, lote, quantidade, validade)) return;
            try {
                Medicamento atualizado = new Medicamento(selecionado.getId(), doador.getDocumento(),
                        nome.getText().trim(), descricao.getText().trim(), lote.getText().trim(),
                        quantidade.getValue(), validade.getValue());
                databaseController.atualizarMedicamento(atualizado, doador.getDocumento());
                carregar.run();
                tabela.getSelectionModel().clearSelection();
                limparFormulario(nome, descricao, lote, quantidade, validade);
                alertaInformacao("Medicamento atualizado com sucesso.");
            } catch (Exception ex) {
                alerta(ex.getMessage());
            }
        });

        remover.setDisable(true);
        remover.setOnAction(e -> {
            Medicamento selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) return;
            try {
                databaseController.removerMedicamento(selecionado.getId(), doador.getDocumento());
                carregar.run();
                tabela.getSelectionModel().clearSelection();
                limparFormulario(nome, descricao, lote, quantidade, validade);
            } catch (Exception ex) {
                alerta(ex.getMessage());
            }
        });

        voltar.setOnAction(e -> voltarLogin(stage));

        HBox botoes = new HBox(8, adicionar, atualizar, remover);
        botoes.setAlignment(Pos.CENTER_RIGHT);
        HBox rodape = new HBox(voltar);
        rodape.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(10, titulo, new Separator(), formulario, botoes,
                new Label("Medicamentos cadastrados"), tabela, rodape);
        root.setPadding(new Insets(16));
        root.setFillWidth(true);

        stage.setResizable(false);
        stage.setScene(new Scene(root, 920, 650));
        stage.sizeToScene();
        stage.show();
        stage.centerOnScreen();
        carregar.run();
    }

    private boolean camposValidos(TextField nome, TextField descricao, TextField lote,
                                  Spinner<Integer> quantidade, DatePicker validade) {
        if (nome.getText().isBlank() || descricao.getText().isBlank() || lote.getText().isBlank() || validade.getValue() == null) {
            alerta("Preencha todos os campos.");
            return false;
        }
        if (quantidade.getValue() == null || quantidade.getValue() <= 0 || validade.getValue().isBefore(LocalDate.now())) {
            alerta("Informe uma quantidade válida e uma data de validade igual ou posterior à data atual.");
            return false;
        }
        return true;
    }

    private void limparFormulario(TextField nome, TextField descricao, TextField lote,
                                  Spinner<Integer> quantidade, DatePicker validade) {
        nome.clear(); descricao.clear(); lote.clear();
        quantidade.getValueFactory().setValue(1);
        validade.setValue(null);
    }

    private void voltarLogin(Stage stage) {
        Tela.abrirAjustada(stage, new LoginWindow(stage, databaseController), Tela.LOGIN_L);
    }

    private void alerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(mensagem == null ? "Ocorreu um erro." : mensagem);
        alert.showAndWait();
    }

    private void alertaInformacao(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Concluído");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
