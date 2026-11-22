package com.goytacazes.poodoacao.Controller;

import com.goytacazes.poodoacao.Model.Medicamento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class MedicamentoController {

    private final ObservableList<Medicamento> listaMedicamentos = FXCollections.observableArrayList();
    private long proximoId = 1;

    public ObservableList<Medicamento> getListaMedicamentos() {
        return listaMedicamentos;
    }

    public void adicionarMedicamento(String cpf, String nome, String descricao, String lote, int quantidade, LocalDate validade) {
        // Regras de negócio e validações
        if (cpf == null || cpf.trim().length() != 11) {
            throw new IllegalArgumentException("O CPF do doador deve conter exatamente 11 dígitos.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do medicamento é obrigatório.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }
        if (lote == null || lote.trim().isEmpty()) {
            throw new IllegalArgumentException("O lote é obrigatório.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (validade == null || validade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Não é permitido doar medicamentos com validade expirada.");
        }

        Medicamento novo = new Medicamento(
                proximoId++,
                cpf.trim(),
                nome.trim(),
                descricao.trim(),
                lote.trim(),
                quantidade,
                validade
        );

        // MedicamentoDAO.inserir(novo)
        listaMedicamentos.add(novo);
    }

    public void removerMedicamento(Medicamento medicamento) {
        if (medicamento == null) {
            throw new IllegalArgumentException("Selecione um medicamento na tabela para remover.");
        }
        // MedicamentoDAO.deletar(medicamento.getId())
        listaMedicamentos.remove(medicamento);
    }
}