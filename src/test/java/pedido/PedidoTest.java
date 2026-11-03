package pedido;

import cardapio.ComCheddar;
import cardapio.ItemCardapio;
import cardapio.LancheFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoTest {

    @Test
    void totalSomaOsPrecosDosItens() {
        // prepara
        Pedido pedido = new Pedido(1, "Ana");
        pedido.adicionarItem(LancheFactory.xBurger());                  // 16
        pedido.adicionarItem(new ComCheddar(LancheFactory.xBacon()));  // 22 + 4

        // executa
        double total = pedido.calcularTotal();

        // confere
        assertEquals(42.0, total, 0.001);
    }

    @Test
    void pedidoVazioTemTotalZero() {
        Pedido pedido = new Pedido(1, "Ana");

        assertEquals(0.0, pedido.calcularTotal(), 0.001);
        assertEquals(0, pedido.getQuantidadeItens());
    }

    @Test
    void removerItemAtualizaTotal() {
        Pedido pedido = new Pedido(1, "Ana");
        pedido.adicionarItem(LancheFactory.xBurger());  // posição 0
        pedido.adicionarItem(LancheFactory.xSalada());  // posição 1
        pedido.adicionarItem(LancheFactory.xBacon());   // posição 2

        // removerItem usa índice começando em 0, como lista em Python
        ItemCardapio removido = pedido.removerItem(1);

        assertEquals("X-Salada", removido.getDescricao());
        assertEquals(2, pedido.getQuantidadeItens());
        assertEquals(38.0, pedido.calcularTotal(), 0.001);
    }

    @Test
    void removerPosicaoInvalidaLancaExcecao() {
        Pedido pedido = new Pedido(1, "Ana");
        pedido.adicionarItem(LancheFactory.xBurger());

        assertThrows(IllegalArgumentException.class, () -> pedido.removerItem(1));
        assertThrows(IllegalArgumentException.class, () -> pedido.removerItem(-1));
    }

    @Test
    void nomeDoClienteVazioLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(1, ""));
        assertThrows(IllegalArgumentException.class, () -> new Pedido(1, "   "));
    }

    @Test
    void nomeDoClienteNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(1, null));
    }

    @Test
    void adicionarItemNuloLancaExcecao() {
        Pedido pedido = new Pedido(1, "Ana");

        assertThrows(IllegalArgumentException.class, () -> pedido.adicionarItem(null));
    }

    @Test
    void getItensDevolveCopiaDaLista() {
        Pedido pedido = new Pedido(1, "Ana");
        pedido.adicionarItem(LancheFactory.xBurger());

        // mexer na cópia não pode mudar o pedido de verdade
        ArrayList<ItemCardapio> copia = pedido.getItens();
        copia.clear();

        assertEquals(1, pedido.getQuantidadeItens());
        assertEquals(16.0, pedido.calcularTotal(), 0.001);
    }
}
