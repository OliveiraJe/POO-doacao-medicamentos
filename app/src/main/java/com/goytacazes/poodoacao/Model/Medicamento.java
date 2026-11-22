package com.goytacazes.poodoacao.Model;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Medicamento {
    private final LongProperty id;
    private final StringProperty doadorCpf;
    private final StringProperty nome;
    private final StringProperty descricao;
    private final StringProperty lote;
    private final IntegerProperty quantidade;
    private final ObjectProperty<LocalDate> validade;

    public Medicamento(long id, String doadorCpf, String nome, String descricao, String lote, int quantidade, LocalDate validade) {
        this.id = new SimpleLongProperty(id);
        this.doadorCpf = new SimpleStringProperty(doadorCpf);
        this.nome = new SimpleStringProperty(nome);
        this.descricao = new SimpleStringProperty(descricao);
        this.lote = new SimpleStringProperty(lote);
        this.quantidade = new SimpleIntegerProperty(quantidade);
        this.validade = new SimpleObjectProperty<>(validade);
    }

    // Properties (usadas pela TableView do JavaFX)
    public LongProperty idProperty() { return id; }
    public StringProperty doadorCpfProperty() { return doadorCpf; }
    public StringProperty nomeProperty() { return nome; }
    public StringProperty descricaoProperty() { return descricao; }
    public StringProperty loteProperty() { return lote; }
    public IntegerProperty quantidadeProperty() { return quantidade; }
    public ObjectProperty<LocalDate> validadeProperty() { return validade; }

    public StringProperty validadeFormatadaProperty() {
        String dataStr = validade.get() != null
                ? validade.get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "";
        return new SimpleStringProperty(dataStr);
    }

    // Getters e Setters
    public long getId() { return id.get(); }
    public void setId(long id) { this.id.set(id); }

    public String getDoadorCpf() { return doadorCpf.get(); }
    public void setDoadorCpf(String doadorCpf) { this.doadorCpf.set(doadorCpf); }

    public String getNome() { return nome.get(); }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getDescricao() { return descricao.get(); }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }

    public String getLote() { return lote.get(); }
    public void setLote(String lote) { this.lote.set(lote); }

    public int getQuantidade() { return quantidade.get(); }
    public void setQuantidade(int quantidade) { this.quantidade.set(quantidade); }

    public LocalDate getValidade() { return validade.get(); }
    public void setValidade(LocalDate validade) { this.validade.set(validade); }
}