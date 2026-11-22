package com.goytacazes.poodoacao.View;

import com.goytacazes.poodoacao.Controller.MedicamentoController;
import com.goytacazes.poodoacao.Model.Medicamento;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class DonateWindow extends Application {

    private final MedicamentoController controller = new MedicamentoController();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Doação de Medicamentos");

        Label lblTitulo = new Label("Cadastro de Medicamento para Doação");
        lblTitulo.getStyleClass().add("titulo");

        // Campos do formulário
        TextField txtDoadorCpf = new TextField();
        txtDoadorCpf.setPromptText("Apenas números");

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome do medicamento");

        TextField txtDescricao = new TextField();
        txtDescricao.setPromptText("");

        TextField txtLote = new TextField();
        txtLote.setPromptText("Ex: L2026A");

        Spinner<Integer> spnQuantidade = new Spinner<>(1, 1000, 1);
        spnQuantidade.setEditable(true);
        spnQuantidade.setPrefWidth(120);

        DatePicker dpValidade = new DatePicker();
        dpValidade.setPromptText("dd/mm/aaaa");

        // Limite de caracteres
        limitarTamanho(txtDoadorCpf, 11);
        limitarTamanho(txtNome, 80);
        limitarTamanho(txtDescricao, 120);
        limitarTamanho(txtLote, 20);

        // Apenas números no CPF
        txtDoadorCpf.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtDoadorCpf.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        // Grid do Formulário
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        grid.add(new Label("CPF do Doador:"), 0, 0);
        grid.add(txtDoadorCpf, 1, 0);

        grid.add(new Label("Medicamento:"), 2, 0);
        grid.add(txtNome, 3, 0);

        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(txtDescricao, 1, 1, 3, 1);

        grid.add(new Label("Lote:"), 0, 2);
        grid.add(txtLote, 1, 2);

        grid.add(new Label("Quantidade:"), 2, 2);
        grid.add(spnQuantidade, 3, 2);

        grid.add(new Label("Validade:"), 0, 3);
        grid.add(dpValidade, 1, 3);

        ColumnConstraints colLabel = new ColumnConstraints();
        ColumnConstraints colInput = new ColumnConstraints();
        colInput.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(colLabel, colInput, colLabel, colInput);

        // Botões
        Button btnLimpar = new Button("Limpar");
        Button btnRemover = new Button("Remover Selecionado");
        btnRemover.getStyleClass().add("btn-remover");

        Button btnAdicionar = new Button("Adicionar Medicamento");
        btnAdicionar.getStyleClass().add("btn-adicionar");

        BooleanBinding camposInvalidos = txtDoadorCpf.textProperty().length().lessThan(11)
                .or(txtNome.textProperty().isEmpty())
                .or(txtDescricao.textProperty().isEmpty())
                .or(txtLote.textProperty().isEmpty())
                .or(dpValidade.valueProperty().isNull());

        btnAdicionar.disableProperty().bind(camposInvalidos);

        // TableView
        TableView<Medicamento> tabela = new TableView<>(controller.getListaMedicamentos());
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Medicamento, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colId.setMaxWidth(50);

        TableColumn<Medicamento, String> colCpf = new TableColumn<>("CPF Doador");
        colCpf.setCellValueFactory(data -> data.getValue().doadorCpfProperty());

        TableColumn<Medicamento, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(data -> data.getValue().nomeProperty());

        TableColumn<Medicamento, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setCellValueFactory(data -> data.getValue().descricaoProperty());

        TableColumn<Medicamento, String> colLote = new TableColumn<>("Lote");
        colLote.setCellValueFactory(data -> data.getValue().loteProperty());

        TableColumn<Medicamento, Number> colQtd = new TableColumn<>("Qtd");
        colQtd.setCellValueFactory(data -> data.getValue().quantidadeProperty());
        colQtd.setMaxWidth(60);

        TableColumn<Medicamento, String> colVal = new TableColumn<>("Validade");
        colVal.setCellValueFactory(data -> data.getValue().validadeFormatadaProperty());

        tabela.getColumns().addAll(colId, colCpf, colNome, colDesc, colLote, colQtd, colVal);
        btnRemover.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull());

        // Ações dos botões

        // Botão Adicionar
        btnAdicionar.setOnAction(e -> {
            try {
                controller.adicionarMedicamento(
                        txtDoadorCpf.getText(),
                        txtNome.getText(),
                        txtDescricao.getText(),
                        txtLote.getText(),
                        spnQuantidade.getValue(),
                        dpValidade.getValue()
                );

                // Limpa os campos do remédio e mantém o CPF
                txtNome.clear();
                txtDescricao.clear();
                txtLote.clear();
                spnQuantidade.getValueFactory().setValue(1);
                dpValidade.setValue(null);
                txtNome.requestFocus();
            } catch (IllegalArgumentException ex) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", ex.getMessage());
            }
        });

        // Botão Remover
        btnRemover.setOnAction(e -> {
            try {
                Medicamento selecionado = tabela.getSelectionModel().getSelectedItem();
                controller.removerMedicamento(selecionado);
            } catch (IllegalArgumentException ex) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", ex.getMessage());
            }
        });


        // Botão Limpar
        btnLimpar.setOnAction(e -> {
            txtDoadorCpf.clear();
            txtNome.clear();
            txtDescricao.clear();
            txtLote.clear();
            spnQuantidade.getValueFactory().setValue(1);
            dpValidade.setValue(null);
        });

        HBox boxBotoes = new HBox(10, btnLimpar, btnRemover, btnAdicionar);
        boxBotoes.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(15, lblTitulo, grid, boxBotoes, new Separator(), tabela);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 780, 520);
        if (getClass().getResource("/donate.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/donate.css").toExternalForm());
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void limitarTamanho(TextField campo, int max) {
        campo.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > max) {
                campo.setText(oldVal);
            }
        });
    }


    // Alerta
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}