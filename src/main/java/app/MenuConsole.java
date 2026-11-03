package app;

import cardapio.ComBaconExtra;
import cardapio.ComCheddar;
import cardapio.ItemCardapio;
import pedido.Pedido;
import util.Formatador;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;

// Nível 1 (1.4): a ÚNICA classe que conversa com a pessoa (System.out e Scanner).
// Ela lê, valida a digitação e mostra resultados, mas não faz regra de negócio.
public class MenuConsole {

    private Scanner sc = new Scanner(System.in);

    // ===================== MENSAGENS GERAIS =====================

    public void exibirBoasVindas() {
        System.out.println("Bem-vindo ao sistema do restaurante!");
    }

    public void exibirMenu() {
        System.out.println();
        System.out.println("=== RESTAURANTE ===");
        System.out.println("1 - Registrar novo pedido");
        System.out.println("2 - Listar pedidos do dia");
        System.out.println("3 - Fechar restaurante");
        System.out.println("4 - Modificar pedido");
        System.out.println("5 - Pagar pedido (bônus: gorjeta)");
        System.out.println("0 - Sair");
    }

    // private: só o próprio menu usa. O Main chama os métodos com nome de propósito (abaixo).
    private void exibirErro(String msg) {
        System.out.println("[!] " + msg);
    }

    public void exibirOpcaoInvalida() {
        exibirErro("Opção inválida.");
    }

    public void exibirDespedida() {
        System.out.println("Até amanhã! Sistema encerrado.");
    }

    // ===================== LEITURAS =====================

    // Nível 1 (1.5)
    public int lerOpcao() {
        return lerInteiro("Escolha uma opção: ", 0, 5);
    }

    // Nível 1 (1.5) / Nível 2 (2.2): mostra o cardápio numerado com preço.
    public int lerOpcaoLanche(ArrayList<ItemCardapio> cardapio) {
        System.out.println("--- Cardápio ---");
        for (int i = 0; i < cardapio.size(); i++) {
            ItemCardapio item = cardapio.get(i);
            System.out.println((i + 1) + " - " + item.getDescricao() + " - " + Formatador.dinheiro(item.getPreco()));
        }
        return lerInteiro("Escolha o lanche: ", 1, cardapio.size());
    }

    // Repete até a pessoa digitar um nome que não seja vazio.
    public String lerNomeCliente() {
        while (true) {
            System.out.print("Nome do cliente: ");
            verificarFimDaEntrada();
            String nome = sc.nextLine().trim();
            if (!nome.isEmpty()) {
                return nome;
            }
            exibirErro("O nome não pode ficar vazio.");
        }
    }

    // Aceita s/sim ou n/nao/não (maiúscula ou minúscula). Repete se for outra coisa.
    // private: o Main usa as perguntas prontas abaixo, sem montar texto.
    private boolean perguntarSimOuNao(String pergunta) {
        while (true) {
            System.out.print(pergunta + " (s/n): ");
            verificarFimDaEntrada();
            String resposta = sc.nextLine().trim().toLowerCase();
            if (resposta.equals("s") || resposta.equals("sim")) {
                return true;
            }
            if (resposta.equals("n") || resposta.equals("nao") || resposta.equals("não")) {
                return false;
            }
            exibirErro("Responda com s ou n.");
        }
    }

    // Nível 1 (1.5)
    public boolean perguntarMaisUmItem() {
        return perguntarSimOuNao("Adicionar mais um item?");
    }

    // Nível 3 (3.1)
    public boolean perguntarRetirarAlgum() {
        return perguntarSimOuNao("Deseja retirar algum ingrediente?");
    }

    // Nível 3 (3.1): o Main passa só o nome ("queijo"); a frase é montada aqui.
    public boolean perguntarRetirar(String ingrediente) {
        return perguntarSimOuNao("Retirar " + ingrediente + "?");
    }

    // Nível 3 (3.2)
    public boolean perguntarConfirmaCancelamento(int numeroPedido) {
        return perguntarSimOuNao("Tem certeza que deseja cancelar o pedido #" + numeroPedido + "?");
    }

    // Nível 2 (2.3): o preço do adicional vem da constante da própria classe,
    // assim se o preço mudar lá, a pergunta já mostra o valor certo.
    public boolean perguntarCheddar() {
        return perguntarSimOuNao("Adicionar cheddar (+ " + Formatador.dinheiro(ComCheddar.PRECO) + ")?");
    }

    public boolean perguntarBaconExtra() {
        return perguntarSimOuNao("Adicionar bacon extra (+ " + Formatador.dinheiro(ComBaconExtra.PRECO) + ")?");
    }

    // Nível 3 (3.2): qualquer número a partir de 1 (se o pedido existe, quem confere é o Main).
    public int lerNumeroPedido() {
        while (true) {
            int numero = lerQualquerInteiro("Número do pedido: ");
            if (numero >= 1) {
                return numero;
            }
            exibirErro("O número do pedido deve ser positivo.");
        }
    }

    // Nível 3 (3.2): posição como aparece na tela (1 até quantidade).
    public int lerPosicaoItem(int quantidade) {
        return lerInteiro("Número do item: ", 1, quantidade);
    }

    // Nível 3 (3.2): submenu de modificação.
    public int lerOpcaoModificacao() {
        System.out.println("--- Modificar pedido ---");
        System.out.println("1 - Acrescentar item");
        System.out.println("2 - Remover item");
        System.out.println("3 - Cancelar pedido");
        System.out.println("0 - Voltar");
        return lerInteiro("Escolha uma opção: ", 0, 3);
    }

    // BÔNUS: gorjeta. Aceita "2,50" ou "2.50"; Enter vazio = sem gorjeta.
    public double lerGorjeta() {
        while (true) {
            System.out.print("Gorjeta (Enter para nenhuma): R$ ");
            verificarFimDaEntrada();
            String texto = sc.nextLine().trim().replace(',', '.');
            if (texto.isEmpty()) {
                return 0.0;
            }
            try {
                double gorjeta = Double.parseDouble(texto); // como float("2.50") em Python
                // isFinite: o parseDouble também aceita "NaN" e "Infinity", que não são dinheiro.
                if (!Double.isFinite(gorjeta)) {
                    exibirErro("Valor inválido. Exemplo: 2,50");
                } else if (gorjeta < 0) {
                    exibirErro("A gorjeta não pode ser negativa.");
                } else {
                    return gorjeta;
                }
            } catch (NumberFormatException e) {
                exibirErro("Valor inválido. Exemplo: 2,50");
            }
        }
    }

    // Repete até a pessoa digitar um número entre min e max.
    private int lerInteiro(String mensagem, int min, int max) {
        while (true) {
            int valor = lerQualquerInteiro(mensagem);
            if (valor >= min && valor <= max) {
                return valor;
            }
            exibirErro("Opção inválida. Digite um número de " + min + " a " + max + ".");
        }
    }

    // Nível 3 (3.4): TODA leitura de número inteiro passa por aqui.
    // Repete até a pessoa digitar um número (qualquer um); quem confere o intervalo é quem chamou.
    private int lerQualquerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            verificarFimDaEntrada();
            try {
                int valor = sc.nextInt();
                sc.nextLine(); // consome o Enter que sobrou depois do número
                return valor;
            } catch (InputMismatchException e) {
                // Sem este nextLine() o texto errado continua "preso" no Scanner:
                // o próximo nextInt() falha de novo, e de novo... loop infinito.
                sc.nextLine();
                exibirErro("Entrada inválida. Digite apenas números.");
            }
        }
    }

    // Extra (não faz parte do enunciado): se a entrada acaba (rodando com um arquivo de entrada
    // ou Ctrl+D), o Scanner não teria mais o que ler e lançaria exceção; aqui encerramos com aviso.
    private void verificarFimDaEntrada() {
        if (!sc.hasNextLine()) {
            System.out.println();
            System.out.println("Fim da entrada. Encerrando o sistema.");
            System.exit(0);
        }
    }

    // ===================== EXIBIÇÃO DE PEDIDOS =====================

    // Nível 1 (1.4)
    public void exibirPedido(Pedido p) {
        System.out.println("Pedido #" + p.getNumero() + " - Cliente: " + p.getNomeCliente());
        exibirItensNumerados(p.getItens());
        System.out.println("   Total: " + Formatador.dinheiro(p.calcularTotal()));
    }

    // Nível 1 (1.5): opção "Listar pedidos do dia".
    // Collection = tipo geral de "grupo de objetos"; aqui só precisamos percorrer com for.
    public void exibirPedidos(Collection<Pedido> pedidos) {
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido registrado hoje.");
            return;
        }
        System.out.println("--- Pedidos do dia ---");
        for (Pedido p : pedidos) {
            exibirPedido(p);
            System.out.println();
        }
    }

    // Nível 3 (3.2): itens numerados a partir de 1 (a pessoa escolhe por esse número).
    public void exibirItensNumerados(ArrayList<ItemCardapio> itens) {
        if (itens.isEmpty()) {
            System.out.println("   (nenhum item)");
            return;
        }
        for (int i = 0; i < itens.size(); i++) {
            ItemCardapio item = itens.get(i);
            System.out.println("   " + (i + 1) + ". " + item.getDescricao() + " - " + Formatador.dinheiro(item.getPreco()));
        }
    }

    // ===================== AVISOS DO FLUXO (o Main só escolhe qual chamar) =====================

    // Nível 1 (1.5)
    public void exibirPedidoAberto(Pedido p) {
        System.out.println("Pedido #" + p.getNumero() + " aberto para " + p.getNomeCliente() + ".");
    }

    public void exibirItemAdicionado(ItemCardapio item) {
        System.out.println("Item adicionado: " + item.getDescricao() + " - " + Formatador.dinheiro(item.getPreco()));
    }

    public void exibirFalhaAoAdicionar() {
        exibirErro("Não foi possível adicionar o item.");
    }

    // Nível 3 (3.2) e BÔNUS
    public void exibirPedidoInexistente(int numeroPedido) {
        exibirErro("Pedido #" + numeroPedido + " não existe.");
    }

    // BÔNUS: comanda paga não muda mais e não é paga de novo.
    public void exibirPedidoJaPago(int numeroPedido) {
        exibirErro("Pedido #" + numeroPedido + " já foi pago; a comanda está fechada.");
    }

    // Nível 3 (3.2)
    public void exibirSemItensParaRemover() {
        exibirErro("Este pedido não tem itens para remover.");
    }

    public void exibirItemRemovido() {
        System.out.println("Item removido.");
    }

    public void exibirFalhaAoRemover() {
        exibirErro("Não foi possível remover o item.");
    }

    public void exibirCancelamentoDesfeito() {
        System.out.println("Cancelamento desfeito.");
    }

    public void exibirPedidoCancelado(int numeroPedido) {
        System.out.println("Pedido #" + numeroPedido + " cancelado.");
    }

    public void exibirFalhaAoCancelar(int numeroPedido) {
        exibirErro("Não foi possível cancelar o pedido #" + numeroPedido + ".");
    }

    // BÔNUS
    public void exibirPedidoVazio(int numeroPedido) {
        exibirErro("Pedido #" + numeroPedido + " está vazio; não há o que pagar.");
    }

    public void exibirFalhaNoPagamento() {
        exibirErro("Não foi possível registrar o pagamento.");
    }

    // BÔNUS
    public void exibirPagamentoConfirmado(Pedido p, double gorjeta) {
        System.out.println("Pagamento do pedido #" + p.getNumero() + " registrado!");
        System.out.println("   Pedido: " + Formatador.dinheiro(p.calcularTotal())
                + " | Gorjeta: " + Formatador.dinheiro(gorjeta));
    }

    // ===================== FECHAMENTO =====================

    // Nível 1 (1.5): só mostra o total que já veio calculado.
    public void exibirFechamentoDoDia(double total) {
        System.out.println();
        System.out.println("=== FECHAMENTO DO DIA ===");
        System.out.println("Total vendido: " + Formatador.dinheiro(total));
    }

    // Nível 3 (3.3): o HashSet não guarda repetidos, então size() = combinações diferentes.
    public void exibirCombinacoes(HashSet<String> combinacoes) {
        System.out.println("Combinações diferentes: " + combinacoes.size());
        for (String c : combinacoes) {
            System.out.println("   - " + c);
        }
    }

    // BÔNUS
    public void exibirLanchesBase(HashSet<String> lanchesBase) {
        System.out.println("Lanches base diferentes: " + lanchesBase.size());
        for (String nome : lanchesBase) {
            System.out.println("   - " + nome);
        }
    }

    // BÔNUS
    public void exibirResumoCaixa(double totalRecebido, double totalGorjetas, int quantidadePagamentos) {
        System.out.println("--- Caixa ---");
        System.out.println("Pagamentos registrados: " + quantidadePagamentos);
        System.out.println("Total recebido (sem gorjeta): " + Formatador.dinheiro(totalRecebido));
        System.out.println("Total de gorjetas: " + Formatador.dinheiro(totalGorjetas));
    }
}
