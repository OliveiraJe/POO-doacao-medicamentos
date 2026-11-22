package com.goytacazes.poodoacao.View;

import com.goytacazes.poodoacao.Controller.DatabaseController;
import com.goytacazes.poodoacao.Model.Doacao;
import com.goytacazes.poodoacao.Model.Medicamento;
import com.goytacazes.poodoacao.Model.Perfil;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReceptorWindow extends Application {
    private final DatabaseController databaseController = new DatabaseController("schema.sql");

    private final ObservableList<Perfil> receptores = FXCollections.observableArrayList();
    private final ObservableList<Medicamento> medicamentos = FXCollections.observableArrayList();
    private final ObservableList<Doacao> doacoes = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Receptor - Doação de Medicamentos");

        Label lblTitulo = new Label("Painel do Receptor");

        ComboBox<Perfil> cbReceptor = new ComboBox<>(receptores);
        cbReceptor.setPromptText("Selecione o receptor (CNPJ)");
        cbReceptor.setPrefWidth(320);

        Button btnAtualizar = new Button("Atualizar");
        Button btnPerfis = new Button("Perfis");

        HBox boxReceptor = new HBox(10, new Label("Receptor:"), cbReceptor, btnAtualizar, btnPerfis);
        boxReceptor.setAlignment(Pos.CENTER_LEFT);

        // Tabela de medicamentos disponíveis
        TableView<Medicamento> tabela = new TableView<>(medicamentos);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Medicamento, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colId.setMaxWidth(50);

        TableColumn<Medicamento, String> colNome = new TableColumn<>("Medicamento");
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

        TableColumn<Medicamento, String> colDoador = new TableColumn<>("CPF Doador");
        colDoador.setCellValueFactory(data -> data.getValue().doadorCpfProperty());

        tabela.getColumns().addAll(colId, colNome, colDesc, colLote, colQtd, colVal, colDoador);

        Spinner<Integer> spnQuantidade = new Spinner<>(1, 1000, 1);
        spnQuantidade.setEditable(true);
        spnQuantidade.setPrefWidth(120);

        Button btnSolicitar = new Button("Solicitar Doação");

        HBox boxSolicitar = new HBox(10, new Label("Quantidade:"), spnQuantidade, btnSolicitar);
        boxSolicitar.setAlignment(Pos.CENTER_RIGHT);

        // Tabela das doações do receptor selecionado
        Label lblDoacoes = new Label("Minhas doações");

        TableView<Doacao> tabelaDoacoes = new TableView<>(doacoes);
        tabelaDoacoes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabelaDoacoes.setPrefHeight(180);

        TableColumn<Doacao, Number> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(data -> data.getValue().codigoProperty());
        colCodigo.setMaxWidth(70);

        TableColumn<Doacao, String> colMedicamento = new TableColumn<>("Medicamento");
        colMedicamento.setCellValueFactory(data -> data.getValue().medicamentoNomeProperty());

        TableColumn<Doacao, Number> colQuantidade = new TableColumn<>("Qtd");
        colQuantidade.setCellValueFactory(data -> data.getValue().quantidadeProperty());
        colQuantidade.setMaxWidth(60);

        TableColumn<Doacao, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(data -> data.getValue().dataProperty());

        TableColumn<Doacao, String> colDoadorCpf = new TableColumn<>("CPF Doador");
        colDoadorCpf.setCellValueFactory(data -> data.getValue().doadorCpfProperty());

        tabelaDoacoes.getColumns().addAll(colCodigo, colMedicamento, colQuantidade, colData, colDoadorCpf);

        Button btnCancelar = new Button("Cancelar Doação");

        HBox boxCancelar = new HBox(btnCancelar);
        boxCancelar.setAlignment(Pos.CENTER_RIGHT);

        // Ações
        btnAtualizar.setOnAction(e -> carregar(cbReceptor, tabelaDoacoes));

        cbReceptor.valueProperty().addListener((obs, antigo, novo) -> carregarDoacoes(novo, tabelaDoacoes));

        btnPerfis.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setTitle("Perfis");
            stage.setScene(new Scene(new PerfisWindow(databaseController), 900, 640));
            stage.show();
        });

        btnSolicitar.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull()
                .or(cbReceptor.valueProperty().isNull()));

        btnSolicitar.setOnAction(e -> {
            Medicamento selecionado = tabela.getSelectionModel().getSelectedItem();
            Perfil receptor = cbReceptor.getValue();

            try {
                databaseController.registrarDoacao(selecionado.getId(), receptor.getDocumento(),
                        spnQuantidade.getValue());

                carregar(cbReceptor, tabelaDoacoes);
            } catch (Exception ex) {
                mostrarAlerta("Atenção", ex.getMessage());
            }
        });

        btnCancelar.disableProperty().bind(tabelaDoacoes.getSelectionModel().selectedItemProperty().isNull());

        btnCancelar.setOnAction(e -> {
            Doacao selecionada = tabelaDoacoes.getSelectionModel().getSelectedItem();

            try {
                databaseController.cancelarDoacao(selecionada.getCodigo());
                carregar(cbReceptor, tabelaDoacoes);
            } catch (Exception ex) {
                mostrarAlerta("Atenção", ex.getMessage());
            }
        });

        VBox root = new VBox(15, lblTitulo, boxReceptor, new Separator(), tabela, boxSolicitar,
                new Separator(), lblDoacoes, tabelaDoacoes, boxCancelar);
        root.setPadding(new Insets(20));

        primaryStage.setScene(new Scene(root, 900, 720));
        primaryStage.show();

        carregar(cbReceptor, tabelaDoacoes);
    }

    private void carregar(ComboBox<Perfil> cbReceptor, TableView<Doacao> tabelaDoacoes) {
        Perfil selecionado = cbReceptor.getValue();

        try {
            if (!databaseController.databaseExists()) {
                mostrarAlerta("Banco de dados", "Nenhum banco encontrado. Cadastre um perfil primeiro.");
                return;
            }

            receptores.setAll(databaseController.listarReceptores());
            medicamentos.setAll(databaseController.listarMedicamentos());

            if (selecionado == null && !receptores.isEmpty()) {
                cbReceptor.setValue(receptores.get(0));
            } else {
                cbReceptor.setValue(selecionado);
                carregarDoacoes(selecionado, tabelaDoacoes);
            }
        } catch (Exception ex) {
            mostrarAlerta("Erro ao carregar", ex.getMessage());
        }
    }

    private void carregarDoacoes(Perfil receptor, TableView<Doacao> tabelaDoacoes) {
        if (receptor == null) {
            doacoes.clear();
            return;
        }

        try {
            doacoes.setAll(databaseController.listarDoacoes(receptor.getDocumento()));
        } catch (Exception ex) {
            mostrarAlerta("Erro ao carregar doações", ex.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
