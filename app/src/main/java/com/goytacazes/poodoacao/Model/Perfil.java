package com.goytacazes.poodoacao.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Perfil {
    private final IntegerProperty id;
    private final StringProperty nome;
    private final StringProperty email;
    private final StringProperty tipoPerfil;
    private final StringProperty documento;
    private final StringProperty telefone;
    private final StringProperty cep;
    private final StringProperty estado;
    private final StringProperty cidade;
    private final StringProperty rua;
    private final StringProperty numero;

    public Perfil(int id, String nome, String email, String tipoPerfil, String documento, String telefone,
                  String cep, String estado, String cidade, String rua, String numero) {
        this.id = new SimpleIntegerProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.email = new SimpleStringProperty(email);
        this.tipoPerfil = new SimpleStringProperty(tipoPerfil);
        this.documento = new SimpleStringProperty(documento);
        this.telefone = new SimpleStringProperty(telefone);
        this.cep = new SimpleStringProperty(cep);
        this.estado = new SimpleStringProperty(estado);
        this.cidade = new SimpleStringProperty(cidade);
        this.rua = new SimpleStringProperty(rua);
        this.numero = new SimpleStringProperty(numero);
    }

    // Properties
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomeProperty() { return nome; }
    public StringProperty emailProperty() { return email; }
    public StringProperty tipoPerfilProperty() { return tipoPerfil; }
    public StringProperty documentoProperty() { return documento; }
    public StringProperty telefoneProperty() { return telefone; }
    public StringProperty cepProperty() { return cep; }
    public StringProperty estadoProperty() { return estado; }
    public StringProperty cidadeProperty() { return cidade; }
    public StringProperty ruaProperty() { return rua; }
    public StringProperty numeroProperty() { return numero; }

    // Getters e Setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getNome() { return nome.get(); }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }

    public String getTipoPerfil() { return tipoPerfil.get(); }
    public void setTipoPerfil(String tipoPerfil) { this.tipoPerfil.set(tipoPerfil); }

    public String getDocumento() { return documento.get(); }
    public void setDocumento(String documento) { this.documento.set(documento); }

    public String getTelefone() { return telefone.get(); }
    public void setTelefone(String telefone) { this.telefone.set(telefone); }

    public String getCep() { return cep.get(); }
    public void setCep(String cep) { this.cep.set(cep); }

    public String getEstado() { return estado.get(); }
    public void setEstado(String estado) { this.estado.set(estado); }

    public String getCidade() { return cidade.get(); }
    public void setCidade(String cidade) { this.cidade.set(cidade); }

    public String getRua() { return rua.get(); }
    public void setRua(String rua) { this.rua.set(rua); }

    public String getNumero() { return numero.get(); }
    public void setNumero(String numero) { this.numero.set(numero); }

    @Override
    public String toString() {
        return documento.get() + " - " + nome.get();
    }
}
