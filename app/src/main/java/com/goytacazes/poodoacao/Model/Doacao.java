package com.goytacazes.poodoacao.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Doacao {
    private final LongProperty codigo;
    private final LongProperty medicamentoId;
    private final StringProperty medicamentoNome;
    private final StringProperty doadorCpf;
    private final StringProperty receptorCnpj;
    private final IntegerProperty quantidade;
    private final StringProperty data;

    public Doacao(long codigo, long medicamentoId, String medicamentoNome, String doadorCpf, String receptorCnpj,
                  int quantidade, String data) {
        this.codigo = new SimpleLongProperty(codigo);
        this.medicamentoId = new SimpleLongProperty(medicamentoId);
        this.medicamentoNome = new SimpleStringProperty(medicamentoNome);
        this.doadorCpf = new SimpleStringProperty(doadorCpf);
        this.receptorCnpj = new SimpleStringProperty(receptorCnpj);
        this.quantidade = new SimpleIntegerProperty(quantidade);
        this.data = new SimpleStringProperty(data);
    }

    // Properties (usadas pela TableView do JavaFX)
    public LongProperty codigoProperty() { return codigo; }
    public LongProperty medicamentoIdProperty() { return medicamentoId; }
    public StringProperty medicamentoNomeProperty() { return medicamentoNome; }
    public StringProperty doadorCpfProperty() { return doadorCpf; }
    public StringProperty receptorCnpjProperty() { return receptorCnpj; }
    public IntegerProperty quantidadeProperty() { return quantidade; }
    public StringProperty dataProperty() { return data; }

    // Getters e Setters
    public long getCodigo() { return codigo.get(); }
    public void setCodigo(long codigo) { this.codigo.set(codigo); }

    public long getMedicamentoId() { return medicamentoId.get(); }
    public void setMedicamentoId(long medicamentoId) { this.medicamentoId.set(medicamentoId); }

    public String getMedicamentoNome() { return medicamentoNome.get(); }
    public void setMedicamentoNome(String medicamentoNome) { this.medicamentoNome.set(medicamentoNome); }

    public String getDoadorCpf() { return doadorCpf.get(); }
    public void setDoadorCpf(String doadorCpf) { this.doadorCpf.set(doadorCpf); }

    public String getReceptorCnpj() { return receptorCnpj.get(); }
    public void setReceptorCnpj(String receptorCnpj) { this.receptorCnpj.set(receptorCnpj); }

    public int getQuantidade() { return quantidade.get(); }
    public void setQuantidade(int quantidade) { this.quantidade.set(quantidade); }

    public String getData() { return data.get(); }
    public void setData(String data) { this.data.set(data); }
}
