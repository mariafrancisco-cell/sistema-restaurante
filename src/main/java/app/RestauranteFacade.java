package app;

import cardapio.ComBaconExtra;
import cardapio.ComCheddar;
import cardapio.ItemCardapio;
import cardapio.Lanche;
import cardapio.LancheFactory;
import financeiro.Caixa;
import financeiro.Transacao;
import pedido.GerenciadorPedidos;
import pedido.Pedido;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

// Padrão FACADE (fachada): é o "garçom/gerente" do restaurante.
// Você faz o pedido com ele e ele conversa com a cozinha (cardapio),
// com as comandas (pedido) e com o caixa (financeiro) por você.
// Assim o Main não conhece GerenciadorPedidos, LancheFactory, os adicionais nem o Caixa:
// fala só com a fachada e com o menu (e recebe Pedido/ItemCardapio para repassar ao menu).
// Regra: a fachada coordena, mas NÃO imprime nem lê do teclado (isso é do MenuConsole).
public class RestauranteFacade {

    private GerenciadorPedidos gerenciador = new GerenciadorPedidos();
    private Caixa caixa = Caixa.getInstancia(); // BÔNUS: o único Caixa do sistema (Singleton)

    // Nível 1 / Nível 2 (2.2): o cardápio para exibir na tela.
    // Lista NOVA a cada chamada, com objetos novos vindos da factory.
    public ArrayList<ItemCardapio> getCardapio() {
        ArrayList<ItemCardapio> cardapio = new ArrayList<>();
        cardapio.add(LancheFactory.xBurger());
        cardapio.add(LancheFactory.xSalada());
        cardapio.add(LancheFactory.xBacon());
        return cardapio;
    }

    // Nível 1 (1.5)
    public Pedido criarPedido(String nomeCliente) {
        return gerenciador.criarPedido(nomeCliente);
    }

    // Nível 2 (2.2, 2.3) e Nível 3 (3.1): monta UM item personalizado.
    // Atenção: a numeração abaixo (1, 2, 3) precisa bater com a ordem da lista em getCardapio().
    // A cada chamada a factory cria um Lanche NOVO, então personalizar
    // este item nunca altera um lanche de outro pedido.
    public ItemCardapio montarItem(int opcaoLanche, boolean semQueijo, boolean semAlface, boolean semTomate,
                                   boolean semCebola, boolean comCheddar, boolean comBaconExtra) {
        Lanche lanche;
        if (opcaoLanche == 1) {
            lanche = LancheFactory.xBurger();
        } else if (opcaoLanche == 2) {
            lanche = LancheFactory.xSalada();
        } else if (opcaoLanche == 3) {
            lanche = LancheFactory.xBacon();
        } else {
            throw new IllegalArgumentException("Opção de lanche inválida: " + opcaoLanche);
        }

        // Nível 3 (3.1): retirar ingrediente não muda o preço, só a descrição.
        if (semQueijo) {
            lanche.setQueijo(false);
        }
        if (semAlface) {
            lanche.setAlface(false);
        }
        if (semTomate) {
            lanche.setTomate(false);
        }
        if (semCebola) {
            lanche.setCebola(false);
        }

        // Nível 2 (2.3): Decorator. Cada adicional "embrulha" o item anterior.
        // A variável é do tipo da interface para poder guardar o lanche OU o lanche embrulhado.
        ItemCardapio item = lanche;
        if (comCheddar) {
            item = new ComCheddar(item);
        }
        if (comBaconExtra) {
            item = new ComBaconExtra(item);
        }
        return item;
    }

    public Pedido buscarPedido(int numero) {
        return gerenciador.buscarPedido(numero);
    }

    // Nível 1 (1.5): opção "Listar pedidos do dia". Recebe do gerenciador uma cópia (ver getPedidos).
    public Collection<Pedido> listarPedidos() {
        return gerenciador.getPedidos();
    }

    // Devolve false se o pedido não existe (ou já foi pago: comanda fechada não muda mais).
    public boolean adicionarItem(int numeroPedido, ItemCardapio item) {
        Pedido pedido = gerenciador.buscarPedido(numeroPedido);
        if (pedido == null || pedidoJaPago(numeroPedido)) {
            return false;
        }
        pedido.adicionarItem(item);
        return true;
    }

    // Nível 3 (3.2): remove pela posição que aparece na tela (1, 2, 3...).
    // A lista em Java começa em 0 (igual Python), por isso o "posicao - 1".
    public boolean removerItem(int numeroPedido, int posicao) {
        Pedido pedido = gerenciador.buscarPedido(numeroPedido);
        if (pedido == null || pedidoJaPago(numeroPedido)) {
            return false;
        }
        if (posicao < 1 || posicao > pedido.getQuantidadeItens()) {
            return false;
        }
        pedido.removerItem(posicao - 1);
        return true;
    }

    // Nível 3 (3.2): cancelar tira o pedido do mapa, então ele sai do total do dia.
    public boolean cancelarPedido(int numeroPedido) {
        if (pedidoJaPago(numeroPedido)) {
            return false;
        }
        return gerenciador.cancelarPedido(numeroPedido);
    }

    // Nível 1 (1.5): só repassa. O for que soma os pedidos fica no GerenciadorPedidos.
    public double calcularTotalDoDia() {
        return gerenciador.calcularTotalDoDia();
    }

    // Nível 3 (3.3)
    public HashSet<String> getCombinacoesDiferentes() {
        return gerenciador.getCombinacoesDiferentes();
    }

    // BÔNUS: lanches base diferentes
    public HashSet<String> getLanchesBaseDiferentes() {
        return gerenciador.getLanchesBaseDiferentes();
    }

    // ===== BÔNUS: caixa e gorjeta =====

    public boolean pedidoJaPago(int numeroPedido) {
        return caixa.pedidoJaPago(numeroPedido);
    }

    // Devolve false se o pedido não existe, está vazio, já foi pago ou a gorjeta é negativa.
    public boolean pagarPedido(int numeroPedido, double gorjeta) {
        Pedido pedido = gerenciador.buscarPedido(numeroPedido);
        if (pedido == null || pedido.getQuantidadeItens() == 0 || gorjeta < 0) {
            return false;
        }
        if (caixa.pedidoJaPago(numeroPedido)) {
            return false;
        }
        Transacao transacao = new Transacao(numeroPedido, pedido.calcularTotal(), gorjeta);
        return caixa.registrar(transacao);
    }

    public double getTotalRecebido() {
        return caixa.getTotalRecebido();
    }

    public double getTotalGorjetas() {
        return caixa.getTotalGorjetas();
    }

    public int getQuantidadePagamentos() {
        return caixa.getQuantidadeTransacoes();
    }
}
