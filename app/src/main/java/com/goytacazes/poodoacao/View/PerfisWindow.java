package com.goytacazes.poodoacao.View;

import com.goytacazes.poodoacao.Controller.DatabaseController;
import com.goytacazes.poodoacao.Model.Perfil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PerfisWindow extends VBox {
    private final DatabaseController databaseController;

    private final ObservableList<Perfil> perfis = FXCollections.observableArrayList();

    private Integer perfilEmEdicao;

    private final Label lblTitulo = new Label("Perfis");
    private final Label lblDocumento = new Label("CPF:");

    private final ChoiceBox<String> cbTipo = new ChoiceBox<>(FXCollections.observableArrayList("doador", "receptor"));
    private final TextField txtNome = new TextField();
    private final TextField txtEmail = new TextField();
    private final PasswordField txtSenha = new PasswordField();
    private final TextField txtDocumento = new TextField();
    private final TextField txtTelefone = new TextField();
    private final TextField txtCep = new TextField();
    private final TextField txtEstado = new TextField();
    private final TextField txtCidade = new TextField();
    private final TextField txtRua = new TextField();
    private final TextField txtNumero = new TextField();

    private final TableView<Perfil> tabela = new TableView<>(perfis);

    private final Button btnNovo = new Button("Novo");
    private final Button btnSalvar = new Button("Criar Perfil");
    private final Button btnExcluir = new Button("Excluir Selecionado");

    public PerfisWindow(DatabaseController databaseController) {
        this.databaseController = databaseController;

        GridPane formulario = montarFormulario();
        montarTabela();

        cbTipo.setValue("doador");

        cbTipo.valueProperty().addListener((obs, antigo, novo) -> atualizarRotuloDocumento());
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> selecionar(novo));

        btnNovo.setOnAction(e -> limparFormulario());
        btnSalvar.setOnAction(e -> salvar());
        btnExcluir.setOnAction(e -> excluir());

        HBox boxBotoes = new HBox(10, btnNovo, btnExcluir, btnSalvar);
        boxBotoes.setAlignment(Pos.CENTER_RIGHT);

        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.getChildren().addAll(lblTitulo, formulario, boxBotoes, new Separator(), tabela);

        VBox.setVgrow(tabela, Priority.ALWAYS);

        btnExcluir.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull());

        carregar();
    }

    private GridPane montarFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        grid.add(new Label("Perfil:"), 0, 0);
        grid.add(cbTipo, 1, 0);
        grid.add(new Label("Nome:"), 2, 0);
        grid.add(txtNome, 3, 0);

        grid.add(new Label("E-mail:"), 0, 1);
        grid.add(txtEmail, 1, 1);
        grid.add(new Label("Senha:"), 2, 1);
        grid.add(txtSenha, 3, 1);

        grid.add(lblDocumento, 0, 2);
        grid.add(txtDocumento, 1, 2);
        grid.add(new Label("Telefone:"), 2, 2);
        grid.add(txtTelefone, 3, 2);

        grid.add(new Label("CEP:"), 0, 3);
        grid.add(txtCep, 1, 3);
        grid.add(new Label("Estado:"), 2, 3);
        grid.add(txtEstado, 3, 3);

        grid.add(new Label("Cidade:"), 0, 4);
        grid.add(txtCidade, 1, 4);
        grid.add(new Label("Rua:"), 2, 4);
        grid.add(txtRua, 3, 4);

        grid.add(new Label("Número:"), 0, 5);
        grid.add(txtNumero, 1, 5);

        ColumnConstraints colLabel = new ColumnConstraints();
        ColumnConstraints colInput = new ColumnConstraints();
        colInput.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(colLabel, colInput, colLabel, colInput);

        return grid;
    }

    private void montarTabela() {
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Perfil, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colId.setMaxWidth(50);

        TableColumn<Perfil, String> colTipo = new TableColumn<>("Perfil");
        colTipo.setCellValueFactory(data -> data.getValue().tipoPerfilProperty());
        colTipo.setMaxWidth(90);

        TableColumn<Perfil, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(data -> data.getValue().nomeProperty());

        TableColumn<Perfil, String> colDocumento = new TableColumn<>("CPF/CNPJ");
        colDocumento.setCellValueFactory(data -> data.getValue().documentoProperty());

        TableColumn<Perfil, String> colEmail = new TableColumn<>("E-mail");
        colEmail.setCellValueFactory(data -> data.getValue().emailProperty());

        TableColumn<Perfil, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(data -> data.getValue().telefoneProperty());

        TableColumn<Perfil, String> colCidade = new TableColumn<>("Cidade");
        colCidade.setCellValueFactory(data -> data.getValue().cidadeProperty());

        tabela.getColumns().addAll(colId, colTipo, colNome, colDocumento, colEmail, colTelefone, colCidade);
    }

    private void carregar() {
        Perfil atual = tabela.getSelectionModel().getSelectedItem();
        int idSelecionado = atual == null ? -1 : atual.getId();

        try {
            perfis.setAll(databaseController.listarPerfis());
        } catch (Exception ex) {
            mostrarAlerta(ex.getMessage());
            return;
        }

        for (Perfil perfil : perfis) {
            if (perfil.getId() == idSelecionado) {
                tabela.getSelectionModel().select(perfil);
                return;
            }
        }
    }

    private void selecionar(Perfil perfil) {
        if (perfil == null) {
            return;
        }

        perfilEmEdicao = perfil.getId();

        cbTipo.setValue(perfil.getTipoPerfil());
        cbTipo.setDisable(true);
        txtNome.setText(perfil.getNome());
        txtEmail.setText(perfil.getEmail());
        txtSenha.clear();
        txtDocumento.setText(perfil.getDocumento());
        txtTelefone.setText(perfil.getTelefone());
        txtCep.setText(perfil.getCep());
        txtEstado.setText(perfil.getEstado());
        txtCidade.setText(perfil.getCidade());
        txtRua.setText(perfil.getRua());
        txtNumero.setText(perfil.getNumero());

        btnSalvar.setText("Salvar Alterações");
    }

    private void limparFormulario() {
        perfilEmEdicao = null;

        cbTipo.setDisable(false);
        cbTipo.setValue("doador");
        txtNome.clear();
        txtEmail.clear();
        txtSenha.clear();
        txtDocumento.clear();
        txtTelefone.clear();
        txtCep.clear();
        txtEstado.clear();
        txtCidade.clear();
        txtRua.clear();
        txtNumero.clear();

        tabela.getSelectionModel().clearSelection();
        btnSalvar.setText("Criar Perfil");
    }

    private void salvar() {
        if (cbTipo.getValue() == null || txtNome.getText().isBlank() || txtEmail.getText().isBlank()
                || txtDocumento.getText().isBlank() || txtTelefone.getText().isBlank()
                || txtCep.getText().isBlank() || txtEstado.getText().isBlank() || txtCidade.getText().isBlank()
                || txtRua.getText().isBlank() || txtNumero.getText().isBlank()) {
            mostrarAlerta("Preencha todos os campos");
            return;
        }

        if (perfilEmEdicao == null && txtSenha.getText().isBlank()) {
            mostrarAlerta("A senha é obrigatória para um novo perfil");
            return;
        }

        Perfil perfil = new Perfil(
                perfilEmEdicao == null ? 0 : perfilEmEdicao,
                txtNome.getText().trim(),
                txtEmail.getText().trim(),
                cbTipo.getValue(),
                txtDocumento.getText().trim(),
                txtTelefone.getText().trim(),
                txtCep.getText().trim(),
                txtEstado.getText().trim(),
                txtCidade.getText().trim(),
                txtRua.getText().trim(),
                txtNumero.getText().trim());

        try {
            if (perfilEmEdicao == null) {
                databaseController.criarPerfil(perfil, txtSenha.getText());
            } else {
                databaseController.atualizarPerfil(perfil);
            }

            limparFormulario();
            carregar();
        } catch (Exception ex) {
            mostrarAlerta(ex.getMessage());
        }
    }

    private void excluir() {
        Perfil selecionado = tabela.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            return;
        }

        try {
            databaseController.excluirPerfil(selecionado.getId());
            limparFormulario();
            carregar();
        } catch (Exception ex) {
            mostrarAlerta(ex.getMessage());
        }
    }

    private void atualizarRotuloDocumento() {
        lblDocumento.setText(cbTipo.getValue() != null && cbTipo.getValue().equals("receptor") ? "CNPJ:" : "CPF:");
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
