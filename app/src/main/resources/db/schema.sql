pragma foreign_keys = on;

create table perfil (
    id integer primary key autoincrement,
    nome text not null,
    email text not null unique,
    senha_hash text not null,
    tipo_perfil text not null check (tipo_perfil in ('doador', 'receptor')),
    telefone text not null,
    cep text not null,
    estado text not null,
    cidade text not null,
    rua text not null,
    numero text not null,
    criado_em text default current_timestamp
);

create table doador (
    cpf text primary key,
    perfil_id integer not null unique,
    data_nascimento text,
    foreign key (perfil_id) references perfil(id) on delete cascade
);

create table receptor (
    cnpj text primary key,
    perfil_id integer not null unique,
    foreign key (perfil_id) references perfil(id) on delete cascade
);

create table medicamento (
    id integer primary key autoincrement,
    doador_cpf text not null,
    nome text not null,
    descricao text not null,
    lote text not null,
    quantidade integer not null check (quantidade >= 0),
    validade text not null,
    status integer not null default 0,
    foreign key (doador_cpf) references doador(cpf) on delete cascade
);

create table doacao (
    codigo integer primary key autoincrement,
    doador_cpf text not null,
    receptor_cnpj text not null,
    medicamento_id integer not null,
    data_doacao text not null,
    quantidade_doada integer not null check (quantidade_doada > 0),
    foreign key (doador_cpf) references doador(cpf) on delete restrict,
    foreign key (receptor_cnpj) references receptor(cnpj) on delete restrict,
    foreign key (medicamento_id) references medicamento(id) on delete restrict
);
