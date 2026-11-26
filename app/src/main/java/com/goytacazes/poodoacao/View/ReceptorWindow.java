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
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReceptorWindow extends Application {
    private DatabaseController databaseController;
    private Perfil receptorLogado;
    private final ObservableList<Perfil> receptores = FXCollections.observableArrayList();
    private final ObservableList<Medicamento> medicamentos = FXCollections.observableArrayList();
    private final ObservableList<Doacao> doacoes = FXCollections.observableArrayList();

    public ReceptorWindow() {
        this.databaseController = new DatabaseController("schema.sql");
    }

    public ReceptorWindow(DatabaseController databaseController, Perfil receptorLogado) {
        this.databaseController = databaseController;
        this.receptorLogado = receptorLogado;
    }

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Receptor - Doação de Medicamentos");

        Label titulo = new Label("Painel do Receptor");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ComboBox<Perfil> cbReceptor = new ComboBox<>(receptores);
        cbReceptor.setPromptText("Selecione o receptor (CNPJ)");
        cbReceptor.setPrefWidth(270);
        if (receptorLogado != null) cbReceptor.setDisable(true);

        Button btnAtualizar = new Button("Atualizar lista");
        Button btnPerfis = new Button("Perfis");
        Button btnVoltar = new Button("Voltar ao login");
        if (receptorLogado != null) {
            btnPerfis.setVisible(false);
            btnPerfis.setManaged(false);
        }

        HBox boxReceptor = new HBox(8, new Label("Receptor:"), cbReceptor, btnAtualizar, btnPerfis);
        boxReceptor.setAlignment(Pos.CENTER_LEFT);

        TableView<Medicamento> tabela = new TableView<>(medicamentos);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPlaceholder(new Label("Nenhum medicamento disponível no momento."));
        TableColumn<Medicamento, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> data.getValue().idProperty());
        TableColumn<Medicamento, String> colNome = new TableColumn<>("Medicamento");
        colNome.setCellValueFactory(data -> data.getValue().nomeProperty());
        TableColumn<Medicamento, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setCellValueFactory(data -> data.getValue().descricaoProperty());
        TableColumn<Medicamento, String> colLote = new TableColumn<>("Lote");
        colLote.setCellValueFactory(data -> data.getValue().loteProperty());
        TableColumn<Medicamento, Number> colQtd = new TableColumn<>("Qtd");
        colQtd.setCellValueFactory(data -> data.getValue().quantidadeProperty());
        TableColumn<Medicamento, String> colVal = new TableColumn<>("Validade");
        colVal.setCellValueFactory(data -> data.getValue().validadeFormatadaProperty());
        TableColumn<Medicamento, String> colDoador = new TableColumn<>("CPF do doador");
        colDoador.setCellValueFactory(data -> data.getValue().doadorCpfProperty());
        tabela.getColumns().addAll(colId, colNome, colDesc, colLote, colQtd, colVal, colDoador);
        tabela.setPrefHeight(205);
        tabela.setMinHeight(165);
        VBox.setVgrow(tabela, Priority.ALWAYS);

        Spinner<Integer> spnQuantidade = new Spinner<>(1, 1000, 1);
        spnQuantidade.setEditable(true);
        spnQuantidade.setPrefWidth(100);
        Button btnSolicitar = new Button("Solicitar doação");
        HBox boxSolicitar = new HBox(8, new Label("Quantidade:"), spnQuantidade, btnSolicitar);
        boxSolicitar.setAlignment(Pos.CENTER_RIGHT);

        Label lblDoacoes = new Label("Minhas doações");
        TableView<Doacao> tabelaDoacoes = new TableView<>(doacoes);
        tabelaDoacoes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabelaDoacoes.setPlaceholder(new Label("Você ainda não possui doações registradas."));
        TableColumn<Doacao, Number> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(data -> data.getValue().codigoProperty());
        TableColumn<Doacao, String> colMedicamento = new TableColumn<>("Medicamento");
        colMedicamento.setCellValueFactory(data -> data.getValue().medicamentoNomeProperty());
        TableColumn<Doacao, Number> colQuantidade = new TableColumn<>("Qtd");
        colQuantidade.setCellValueFactory(data -> data.getValue().quantidadeProperty());
        TableColumn<Doacao, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(data -> data.getValue().dataProperty());
        TableColumn<Doacao, String> colDoadorCpf = new TableColumn<>("CPF do doador");
        colDoadorCpf.setCellValueFactory(data -> data.getValue().doadorCpfProperty());
        tabelaDoacoes.getColumns().addAll(colCodigo, colMedicamento, colQuantidade, colData, colDoadorCpf);
        tabelaDoacoes.setPrefHeight(145);
        tabelaDoacoes.setMinHeight(115);

        Button btnCancelar = new Button("Cancelar doação selecionada");
        btnCancelar.disableProperty().bind(tabelaDoacoes.getSelectionModel().selectedItemProperty().isNull());
        HBox boxAcoesDoacao = new HBox(btnCancelar);
        boxAcoesDoacao.setAlignment(Pos.CENTER_RIGHT);

        cbReceptor.valueProperty().addListener((obs, antigo, novo) -> carregarDoacoes(novo));

        btnAtualizar.setOnAction(e -> carregar(cbReceptor));
        btnPerfis.setOnAction(e -> {
            Stage janelaPerfis = new Stage();
            janelaPerfis.setTitle("Perfis");
            janelaPerfis.setScene(new Scene(new PerfisWindow(databaseController), 900, 600));
            janelaPerfis.setResizable(false);
            janelaPerfis.show();
            janelaPerfis.centerOnScreen();
        });

        btnSolicitar.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull()
                .or(cbReceptor.valueProperty().isNull()));
        btnSolicitar.setOnAction(e -> {
            Medicamento selecionado = tabela.getSelectionModel().getSelectedItem();
            Perfil receptor = cbReceptor.getValue();
            if (selecionado == null || receptor == null) return;
            if (spnQuantidade.getValue() == null || spnQuantidade.getValue() < 1
                    || spnQuantidade.getValue() > selecionado.getQuantidade()) {
                mostrarAlerta("Quantidade inválida", "Informe uma quantidade disponível para este medicamento.");
                return;
            }
            try {
                databaseController.registrarDoacao(selecionado.getId(), receptor.getDocumento(), spnQuantidade.getValue());
                carregar(cbReceptor);
                mostrarInformacao("Solicitação registrada com sucesso.");
            } catch (Exception ex) {
                mostrarAlerta("Atenção", ex.getMessage());
            }
        });

        btnCancelar.setOnAction(e -> {
            Doacao selecionada = tabelaDoacoes.getSelectionModel().getSelectedItem();
            if (selecionada == null) return;
            try {
                databaseController.cancelarDoacao(selecionada.getCodigo());
                carregar(cbReceptor);
            } catch (Exception ex) {
                mostrarAlerta("Atenção", ex.getMessage());
            }
        });

        btnVoltar.setOnAction(e -> {
            Tela.abrirAjustada(stage, new LoginWindow(stage, databaseController), Tela.LOGIN_L);
        });

        VBox root = new VBox(8, titulo, boxReceptor, new Separator(), new Label("Medicamentos disponíveis"),
                tabela, boxSolicitar, new Separator(), lblDoacoes, tabelaDoacoes, boxAcoesDoacao, btnVoltar);
        root.setPadding(new Insets(14));
        root.setFillWidth(true);

        stage.setResizable(false);
        stage.setScene(new Scene(root, 980, 690));
        stage.sizeToScene();
        stage.show();
        stage.centerOnScreen();
        carregar(cbReceptor);
    }

    private void carregar(ComboBox<Perfil> cbReceptor) {
        String documentoSelecionado = cbReceptor.getValue() == null ? null : cbReceptor.getValue().getDocumento();
        try {
            if (!databaseController.databaseExists()) {
                mostrarAlerta("Banco de dados", "Nenhum banco encontrado. Cadastre um perfil primeiro.");
                return;
            }
            receptores.setAll(receptorLogado != null ? FXCollections.observableArrayList(receptorLogado)
                    : databaseController.listarReceptores());
            medicamentos.setAll(databaseController.listarMedicamentos());

            Perfil receptorSelecionado = null;
            if (documentoSelecionado != null) {
                for (Perfil perfil : receptores) {
                    if (perfil.getDocumento().equals(documentoSelecionado)) {
                        receptorSelecionado = perfil;
                        break;
                    }
                }
            }
            if (receptorSelecionado == null && !receptores.isEmpty()) receptorSelecionado = receptores.get(0);
            cbReceptor.getSelectionModel().select(receptorSelecionado);
            carregarDoacoes(receptorSelecionado);
        } catch (Exception ex) {
            mostrarAlerta("Erro ao carregar", ex.getMessage());
        }
    }

    private void carregarDoacoes(Perfil receptor) {
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
        alert.setContentText(mensagem == null ? "Ocorreu um erro." : mensagem);
        alert.showAndWait();
    }

    private void mostrarInformacao(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Concluído");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
