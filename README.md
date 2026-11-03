# Sistema de Restaurante

Projeto 1 da disciplina: sistema de console (Java 17 + Maven + JUnit 5) para um restaurante de lanches.
O sistema registra pedidos, deixa o cliente personalizar o lanche (retirar ingredientes e colocar
adicionais pagos), modifica e cancela pedidos, recebe pagamentos com gorjeta e faz o fechamento do dia.

**Vídeo:** <COLE O LINK AQUI>

## O que foi feito

| Nível | Entrega |
|---|---|
| 1.1 | `Lanche` com `nome` e `preco` privados; o construtor chama os setters, que validam (nome vazio e preço negativo lançam `IllegalArgumentException`). |
| 1.2 | `Pedido` cuida de uma comanda só: número (recebido pronto), cliente validado, lista de itens e `calcularTotal()` com `for`. |
| 1.3 | `GerenciadorPedidos` guarda todos os pedidos num `HashMap<Integer, Pedido>` e gera o número com `contadorPedidos++`. |
| 1.4 | `MenuConsole` é a única classe que usa `Scanner` e `System.out`. |
| 1.5 | `Main` com `while` + `switch`: registrar pedido, listar pedidos, fechar o dia, sair. O `Main` não monta nenhuma frase: cada aviso é um método do menu (`exibirPedidoInexistente(numero)`, `exibirItemRemovido()`, `perguntarRetirar("queijo")`...). |
| 2.1 | Interface `ItemCardapio`; o `Pedido` guarda `ArrayList<ItemCardapio>`. |
| 2.2 | `LancheFactory` cria um objeto `Lanche` **novo** a cada chamada (X-Burger, X-Salada, X-Bacon). |
| 2.3 | Adicionais `ComCheddar` (+R$ 4,00) e `ComBaconExtra` (+R$ 6,00) com o padrão Decorator. |
| 2.4 | Testes JUnit de validação e de preço com adicional (com tolerância `0.001`). |
| 3.1 | Retirar queijo, alface, tomate e cebola (`boolean ... = true`); a descrição mostra "sem X" e o preço não muda. |
| 3.2 | Modificar pedido: acrescentar item, remover item pela lista numerada, cancelar o pedido. |
| 3.3 | Fechamento mostra as combinações diferentes (`HashSet<String>` com as descrições). |
| 3.4 | Toda leitura de número inteiro trata `InputMismatchException` (limpando o `Scanner` com `sc.nextLine()`) e número fora das opções; a gorjeta, lida como texto, trata `NumberFormatException` e recusa valor negativo, `NaN` e `Infinity`. |
| Bônus | Gorjeta + relatório; `Caixa` (Singleton); `Transacao` com `equals`/`hashCode` em `HashSet`; testes extras; `.jar` executável; lanches base diferentes (`getNomeBase()`). |

## Organização dos pacotes

O menu conversa com a pessoa, a fachada coordena e as classes de negócio guardam os dados e fazem o trabalho.

```
src/main/java/
├── app/          conversa com a pessoa e coordena o sistema
│   ├── Main.java                 só o fluxo (while + switch); não imprime, não lê, não calcula
│   ├── MenuConsole.java          toda a entrada (Scanner) e saída (System.out)
│   └── RestauranteFacade.java    porta única para as regras: cardápio, pedidos, caixa
├── cardapio/     o que se pode pedir
│   ├── ItemCardapio.java         interface comum: getDescricao(), getPreco(), getNomeBase()
│   ├── Lanche.java               lanche base + ingredientes que podem ser retirados
│   ├── LancheFactory.java        cria os lanches padrão do cardápio
│   ├── ComCheddar.java           adicional (Decorator) + R$ 4,00
│   └── ComBaconExtra.java        adicional (Decorator) + R$ 6,00
├── pedido/       as comandas
│   ├── Pedido.java               uma comanda: cliente, itens, total
│   └── GerenciadorPedidos.java   todas as comandas: número, busca, cancelamento, fechamento
├── financeiro/   (bônus) o dinheiro que entrou
│   ├── Transacao.java            um pagamento: pedido, valor, gorjeta
│   └── Caixa.java                único caixa do restaurante (Singleton)
└── util/
    └── Formatador.java           formata valores como "R$ 16,00"

src/test/java/    testes JUnit 5, nos mesmos pacotes das classes testadas
├── cardapio/     LancheTest, AdicionaisTest
├── pedido/       PedidoTest, GerenciadorPedidosTest
└── financeiro/   CaixaTest (Caixa e Transacao)
```

## Padrões de projeto

| Padrão | Onde | Para quê |
|---|---|---|
| Factory | `cardapio.LancheFactory` | O resto do sistema pede "um X-Bacon" sem saber nome, preço e ingredientes; e cada pedido recebe um objeto novo. |
| Decorator | `cardapio.ComCheddar`, `cardapio.ComBaconExtra` | Acrescentar adicionais (em qualquer quantidade e combinação) sem mudar a classe `Lanche` e sem criar uma classe para cada combinação. |
| Facade | `app.RestauranteFacade` | O `Main` não conhece `GerenciadorPedidos`, `LancheFactory`, os adicionais nem o `Caixa`: fala só com a fachada e o menu (e recebe `Pedido`/`ItemCardapio` para repassar ao menu). |
| Singleton | `financeiro.Caixa` | Garantir que existe um único caixa no sistema (`getInstancia()`, construtor privado). |

## Como rodar

**Pré-requisito:** JDK 17 ou mais novo.

### 1. Pela IDE (IntelliJ)
`File > Open` e escolha o arquivo `pom.xml` (abrir como projeto). Espere o Maven carregar.
Rode `app.Main` pelo botão ▶ ao lado do `main`; rode os testes com botão direito em `src/test/java > Run 'All Tests'`.

### 2. Pelo Maven (terminal)
```bash
mvn test                                  # roda os testes
mvn package                               # roda os testes e gera target/sistema-restaurante.jar
java -jar target/sistema-restaurante.jar  # abre o sistema
```

### 3. Pelo Docker (não precisa ter Java nem Maven instalados)
```bash
docker compose run --rm restaurante   # abre o sistema (interativo)
docker compose run --rm testes        # roda os testes
```
Use `run` e não `up`: o sistema lê o teclado, e o `run` liga o terminal ao container.
Depois de mudar o código, acrescente `--build` (ex.: `docker compose run --rm --build restaurante`).

> **Windows (cmd/PowerShell):** se os acentos aparecerem trocados (ex.: "op??o"), rode `chcp 65001`
> antes de abrir o sistema, ou rode pela IDE.

## Como gerar o zip de entrega

Os scripts rodam os testes, geram o `.jar` executável e criam `sistema-restaurante-entrega.zip` com
**só** o que a entrega pede: `pom.xml`, `src/`, `README.md`, arquivos do Docker e os próprios scripts
(+ `sistema-restaurante.jar`, se o Maven estiver instalado). `target/`, `.idea/`, `.git/` e outros arquivos ficam de fora.

```bash
bash empacotar.sh                                          # Linux, macOS ou Git Bash
powershell -ExecutionPolicy Bypass -File .\empacotar.ps1   # Windows (PowerShell)
```
- Se algum teste falhar, o script para e **não** gera o zip.
- Se o README ainda não tiver o link do vídeo, o script avisa e pergunta se quer continuar.
- Sem Maven no PATH (comum quando só se usa o IntelliJ): o zip é gerado sem o `.jar`; rode os testes pela IDE antes.
  Para pular os testes de propósito: `bash empacotar.sh --sem-testes` ou `.\empacotar.ps1 -SemTestes`.

## Pergunta para pensar (3.3): a ordem dos adicionais deveria importar?

Para o restaurante, não: "X-Bacon + cheddar + bacon extra" e "X-Bacon + bacon extra + cheddar" são o mesmo
lanche, com o mesmo preço. Mas o `HashSet<String>` compara textos, e os textos são diferentes, então conta
duas combinações (há um teste mostrando isso: `ordemDosAdicionaisGeraCombinacoesDiferentes`).
Não foi pedido implementar; duas soluções possíveis:

1. **Ordenar os adicionais antes de montar a chave:** separar o nome base e os nomes dos adicionais,
   ordenar os adicionais em ordem alfabética e só então juntar o texto. As duas versões viram
   "X-Bacon + bacon extra + cheddar", e o `HashSet<String>` conta uma vez.
2. **Usar um objeto como chave:** uma classe `Combinacao` com o lanche base (com as retiradas) e um
   `HashSet<String>` de adicionais, implementando `equals`/`hashCode` com esses dois campos. Como conjuntos
   não têm ordem, `{cheddar, bacon extra}` é igual a `{bacon extra, cheddar}`, e o `HashSet<Combinacao>`
   conta uma combinação só (mesma ideia do `equals`/`hashCode` da `Transacao`).
