# Registro de mudança — Introdução de Turmas e Histórico do Aluno

> Sistema de Matrículas · Lab02 · 23/09/2026
> Status: **em validação** (implementação após aprovação)

## 1. Novos requisitos do Product Owner (professor)

1. Alunos e professores são associados às **turmas**, e não diretamente às disciplinas.
2. Uma turma deve estar associada a **uma** disciplina.
3. Uma disciplina pode ter **várias turmas**, e essas turmas devem ser **independentes entre si**.
4. Um aluno deve manter seu **histórico** de disciplinas cursadas e da turma correspondente a cada disciplina.

## 2. Impacto nos requisitos (README)

### Requisitos funcionais alterados

| ID | Antes | Depois |
|---|---|---|
| RF02 | Manter disciplinas (nome, créditos, **professor responsável**...) | Manter disciplinas (nome, créditos) — o professor passa a ser definido **na turma** |
| RF06 | Aluno matricula-se em **disciplinas** | Aluno matricula-se em **turmas** (até 4 disciplinas obrigatórias + 2 optativas, **uma turma por disciplina**) |
| RF07 | Cancelar matrícula em disciplina | Cancelar matrícula em **turma** |
| RF09 | Encerrar inscrições da **disciplina** ao atingir 60 alunos | Encerrar inscrições da **turma** ao atingir 60 alunos |
| RF10 | Ativar/cancelar **disciplinas** (≥ 3 alunos) no fim do período | Ativar/cancelar **cada turma individualmente** (≥ 3 alunos na turma) |
| RF12 | Professor consulta alunos da **disciplina** | Professor consulta alunos de cada uma de suas **turmas** |
| RF13 | Aluno consulta disciplinas ofertadas e suas matrículas | Aluno consulta as **turmas ofertadas** (com professor e vagas por turma) e suas matrículas |

### Requisitos funcionais novos

| ID | Requisito | Prioridade |
|---|---|---|
| RF14 | A secretaria deve poder **manter turmas**: criar turmas de uma disciplina (código da turma + professor) e removê-las (se sem matriculados). | Alta |
| RF15 | O aluno deve poder **consultar seu histórico**: disciplinas cursadas e a turma em que cursou cada uma, por semestre. | Alta |

### Regras de negócio alteradas/novas

| ID | Regra |
|---|---|
| RN03 *(alterada)* | Uma **turma** só ocorre no semestre se, ao final do período de matrículas, tiver pelo menos **3 alunos**; caso contrário, **a turma** é cancelada (as demais turmas da mesma disciplina não são afetadas — independência entre turmas). |
| RN04 *(alterada)* | Cada **turma** aceita no máximo **60 alunos**. |
| RN07 *(nova)* | Toda turma pertence a exatamente **uma** disciplina; cada turma tem professor, vagas e ativação **próprios**. |
| RN08 *(nova)* | Um aluno pode estar matriculado em **no máximo uma turma por disciplina** no semestre. |
| RN09 *(nova, proposta)* | Ao encerrar o período de matrículas, as matrículas das turmas **ativas** são lançadas no **histórico** do aluno (disciplina + turma + semestre); matrículas de turmas canceladas são desfeitas e não entram no histórico. |

## 3. Impacto no diagrama de casos de uso (v1 → v2)

| Alteração | Descrição |
|---|---|
| UC02 Realizar matrícula | Passa a ser "matricular-se em uma **turma**" (redação da especificação; a elipse não muda de nome) |
| UC04 | Renomeado: "Consultar alunos **da turma**" |
| UC11 *(novo)* | **Manter turmas** — Secretaria |
| UC12 *(novo)* | **Consultar histórico** — Aluno |
| Legendas dos atores | Professor: "consulta os alunos de suas turmas"; Secretaria inclui turmas |

## 4. Impacto no diagrama de classes (v2 → v3)

Mudança central: a classe **`Turma`** entra entre `Disciplina` e as pessoas.

| Classe | Alteração |
|---|---|
| **`Turma`** *(nova)* | `- codigo: String` (ex.: "T1"), `- ativa: boolean`, constantes `MAX_ALUNOS = 60` e `MIN_ALUNOS = 3` (movidas de `Disciplina`), métodos `temVaga()`, `qtdMatriculados()`, `verificarAtivacao()` (movidos de `Disciplina`). Associações: `Disciplina` 1 — 1..* `Turma`; `Professor` 1 — 0..* `Turma` (**leciona** passa a apontar para Turma); `Turma` 1 — 0..60 `Matricula`. |
| **`Disciplina`** | Perde `professor`, `ativa`, `matriculas` e os métodos de lotação/ativação. Fica: código, nome, créditos e suas turmas. |
| **`Professor`** | `disciplinas: List<Disciplina>` → `turmas: List<Turma>`; `listarAlunos(d: Disciplina)` → `listarAlunos(t: Turma)`. |
| **`Matricula`** | `disciplina: Disciplina` → `turma: Turma` (a disciplina é obtida via `turma.getDisciplina()`). |
| **`Aluno`** | Matrícula/cancelamento passam a receber `Turma`; validação nova da RN08 (uma turma por disciplina); ganha `historico: List<ItemHistorico>` e `getHistorico()`. |
| **`ItemHistorico`** *(nova)* | `- disciplina: Disciplina`, `- turma: String` (código), `- semestre: String`. Associação: `Aluno` 1 — 0..* `ItemHistorico`. |
| **`Curriculo`** | Passa a ofertar **turmas** (`turmas: List<Turma>` em vez de `disciplinas`). |
| **`SistemaMatriculas`** | `matricular(a, t, tipo)` e `cancelarMatricula(a, t)` recebem turma; `encerrarPeriodo()` ativa/cancela **por turma** e lança o histórico (RN09); novos: `cadastrarTurma(disciplina, codigo, professor)`, `removerTurma(t)`, `buscarTurma(...)`. |

O diagrama atualizado será versionado como `docs/diagrama-classes-v3.svg` (v1 e v2 permanecem como histórico). O de casos de uso vira `docs/diagrama-casos-de-uso-v2.svg`.

## 5. Impacto no código (arquivos)

| Arquivo | Alteração |
|---|---|
| `Turma.java` *(novo)* | Classe nova conforme a seção 4 |
| `ItemHistorico.java` *(novo)* | Classe nova conforme a seção 4 |
| `Disciplina.java` | Remove professor/lotação/ativação; adiciona lista de turmas |
| `Professor.java` | Turmas no lugar de disciplinas; `listarAlunos(Turma)` |
| `Matricula.java` | Referencia `Turma` |
| `Aluno.java` | RN08, matrícula por turma, histórico |
| `Curriculo.java` | Oferta turmas |
| `SistemaMatriculas.java` | Operações por turma + CRUD de turmas + lançamento de histórico no encerramento |
| `Main.java` | Menus: listar turmas por disciplina (com professor e vagas); aluno escolhe **turma** ao matricular; opção nova "Meu histórico"; secretaria ganha submenu de turmas |
| `RepositorioDados.java` | Ver seção 6 |

## 6. Impacto na persistência (pasta `dados/`)

| Arquivo | Alteração |
|---|---|
| `turmas.csv` *(novo)* | `codigoTurma;codigoDisciplina;professorLogin;ativa` |
| `matriculas.csv` | Passa a referenciar a turma: `alunoLogin;codigoDisciplina;codigoTurma;tipo;data` |
| `historico.csv` *(novo)* | `alunoLogin;codigoDisciplina;codigoTurma;semestre` |
| `disciplinas.csv` | Perde as colunas `ativa` e `professorLogin`: `codigo;nome;creditos` |
| `curriculo.csv` | Lista turmas ofertadas (`disciplina:turma`) em vez de disciplinas |

Como o formato muda, os dados antigos não são compatíveis — a pasta `dados/` deve ser apagada após a atualização (o sistema recria os dados de exemplo, agora com 2 turmas para algumas disciplinas, para demonstrar a independência entre turmas).

## 7. Decisões propostas (validar antes de implementar)

1. **Identificação da turma**: código curto por disciplina ("T1", "T2"), exibido como `ENG101/T1`. Alternativa: número sequencial global.
2. **Histórico (RN09)**: lançado automaticamente no **encerramento do período** para turmas ativas. Alternativa: ter um passo separado de "fechamento do semestre" com aprovação/nota — fora do escopo do enunciado, por isso não proposto.
3. **Limites 4+2 (RN02)**: continuam contados **por disciplina** (via turma), não por turma — mantém o sentido original do enunciado.
