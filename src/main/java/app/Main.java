package app;

import cardapio.ItemCardapio;
import pedido.Pedido;

// Nível 1 (1.5): o Main só controla o FLUXO (while + switch).
// Quem conversa com a pessoa é o MenuConsole; quem faz o trabalho é a RestauranteFacade.
// Por isso aqui não tem System.out, nem Scanner, nem contas, nem frases para a pessoa:
// cada aviso é um método do menu com nome de propósito (ex.: menu.exibirItemRemovido()).
public class Main {

    // "static" porque o main é static e usa esses objetos nos métodos auxiliares abaixo.
    private static MenuConsole menu = new MenuConsole();
    private static RestauranteFacade sistema = new RestauranteFacade();

    public static void main(String[] args) {
        menu.exibirBoasVindas();

        boolean rodando = true;
        while (rodando) {
            menu.exibirMenu();
            int opcao = menu.lerOpcao();

            switch (opcao) {
                case 1:
                    registrarPedido();
                    break;
                case 2:
                    menu.exibirPedidos(sistema.listarPedidos());
                    break;
                case 3:
                    fecharRestaurante();
                    rodando = false;
                    break;
                case 4:
                    modificarPedido();   // Nível 3 (3.2)
                    break;
                case 5:
                    pagarPedido();       // BÔNUS
                    break;
                case 0:
                    menu.exibirDespedida();
                    rodando = false;
                    break;
                default:
                    // Não deve acontecer: lerOpcao() só devolve de 0 a 5.
                    menu.exibirOpcaoInvalida();
                    break;
            }
        }
    }

    // Nível 1 (1.5): opção 1
    private static void registrarPedido() {
        String nome = menu.lerNomeCliente();
        Pedido pedido = sistema.criarPedido(nome);
        menu.exibirPedidoAberto(pedido);

        boolean continuar = true;
        while (continuar) {
            acrescentarItem(pedido.getNumero());
            continuar = menu.perguntarMaisUmItem();
        }

        menu.exibirPedido(pedido);
    }

    // Monta um item novo com as escolhas da pessoa e coloca no pedido.
    // Usado ao registrar (opção 1) e ao modificar (opção 4).
    private static void acrescentarItem(int numeroPedido) {
        ItemCardapio item = montarItem();
        if (sistema.adicionarItem(numeroPedido, item)) {
            menu.exibirItemAdicionado(item);
        } else {
            menu.exibirFalhaAoAdicionar();
        }
    }

    // Nível 2 (2.3) e Nível 3 (3.1): pergunta lanche, retiradas e adicionais.
    private static ItemCardapio montarItem() {
        int opcaoLanche = menu.lerOpcaoLanche(sistema.getCardapio());

        boolean semQueijo = false;
        boolean semAlface = false;
        boolean semTomate = false;
        boolean semCebola = false;
        // Só pergunta ingrediente por ingrediente se a pessoa quiser retirar algo.
        if (menu.perguntarRetirarAlgum()) {
            semQueijo = menu.perguntarRetirar("queijo");
            semAlface = menu.perguntarRetirar("alface");
            semTomate = menu.perguntarRetirar("tomate");
            semCebola = menu.perguntarRetirar("cebola");
        }

        boolean comCheddar = menu.perguntarCheddar();
        boolean comBaconExtra = menu.perguntarBaconExtra();

        return sistema.montarItem(opcaoLanche, semQueijo, semAlface, semTomate,
                semCebola, comCheddar, comBaconExtra);
    }

    // Nível 1 (1.5) + Nível 3 (3.3) + BÔNUS: opção 3
    private static void fecharRestaurante() {
        double total = sistema.calcularTotalDoDia();
        menu.exibirFechamentoDoDia(total);
        menu.exibirCombinacoes(sistema.getCombinacoesDiferentes());
        menu.exibirLanchesBase(sistema.getLanchesBaseDiferentes());
        menu.exibirResumoCaixa(sistema.getTotalRecebido(), sistema.getTotalGorjetas(),
                sistema.getQuantidadePagamentos());
        menu.exibirDespedida();
    }

    // Nível 3 (3.2): opção 4
    private static void modificarPedido() {
        int numero = menu.lerNumeroPedido();
        Pedido pedido = sistema.buscarPedido(numero);
        if (pedido == null) {
            menu.exibirPedidoInexistente(numero);
            return;
        }
        if (sistema.pedidoJaPago(numero)) {   // BÔNUS: comanda paga não muda mais
            menu.exibirPedidoJaPago(numero);
            return;
        }

        menu.exibirPedido(pedido);
        int opcao = menu.lerOpcaoModificacao();
        switch (opcao) {
            case 1:
                acrescentarItem(numero);
                menu.exibirPedido(pedido);
                break;
            case 2:
                removerItem(pedido);
                break;
            case 3:
                cancelarPedido(numero);
                break;
            case 0:
                break;
            default:
                menu.exibirOpcaoInvalida();
                break;
        }
    }

    // Nível 3 (3.2): lista os itens numerados e remove o escolhido.
    private static void removerItem(Pedido pedido) {
        if (pedido.getQuantidadeItens() == 0) {
            menu.exibirSemItensParaRemover();
            return;
        }
        menu.exibirItensNumerados(pedido.getItens());
        int posicao = menu.lerPosicaoItem(pedido.getQuantidadeItens());
        if (sistema.removerItem(pedido.getNumero(), posicao)) {
            menu.exibirItemRemovido();
            menu.exibirPedido(pedido);
        } else {
            menu.exibirFalhaAoRemover();
        }
    }

    // Nível 3 (3.2): cancelado sai do mapa, então sai também do total do dia.
    private static void cancelarPedido(int numero) {
        if (!menu.perguntarConfirmaCancelamento(numero)) {
            menu.exibirCancelamentoDesfeito();
            return;
        }
        if (sistema.cancelarPedido(numero)) {
            menu.exibirPedidoCancelado(numero);
        } else {
            menu.exibirFalhaAoCancelar(numero);
        }
    }

    // BÔNUS: opção 5 (caixa + gorjeta)
    private static void pagarPedido() {
        int numero = menu.lerNumeroPedido();
        Pedido pedido = sistema.buscarPedido(numero);
        if (pedido == null) {
            menu.exibirPedidoInexistente(numero);
            return;
        }
        if (sistema.pedidoJaPago(numero)) {
            menu.exibirPedidoJaPago(numero);
            return;
        }
        if (pedido.getQuantidadeItens() == 0) {
            menu.exibirPedidoVazio(numero);
            return;
        }

        menu.exibirPedido(pedido);
        double gorjeta = menu.lerGorjeta();
        if (sistema.pagarPedido(numero, gorjeta)) {
            menu.exibirPagamentoConfirmado(pedido, gorjeta);
        } else {
            menu.exibirFalhaNoPagamento();
        }
    }
}
