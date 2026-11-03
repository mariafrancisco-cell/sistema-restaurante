package pedido;

import cardapio.ComBaconExtra;
import cardapio.ComCheddar;
import cardapio.Lanche;
import cardapio.LancheFactory;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

// BÔNUS — três (ou mais) testes além dos obrigatórios:
// cancelamento, fechamento do dia e filtro de descrições repetidas (HashSet).
class GerenciadorPedidosTest {

    @Test
    void numerosGeradosEmSequencia() {
        // prepara
        GerenciadorPedidos gerenciador = new GerenciadorPedidos();

        // executa: quem gera o número é o gerenciador, não o usuário
        Pedido primeiro = gerenciador.criarPedido("Ana");
        Pedido segundo = gerenciador.criarPedido("Bia");
        Pedido terceiro = gerenciador.criarPedido("Caio");

        // confere
        assertEquals(1, primeiro.getNumero());
        assertEquals(2, segundo.getNumero());
        assertEquals(3, terceiro.getNumero());
    }

    @Test
    void buscarPedidoDevolveOMesmoObjeto() {
        GerenciadorPedidos gerenciador = new GerenciadorPedidos();
        Pedido criado = gerenciador.criarPedido("Ana");

        assertSame(criado, gerenciador.buscarPedido(criado.getNumero()));
        assertTrue(gerenciador.existePedido(criado.getNumero()));
    }

    @Test
    void buscarPedidoInexistenteDevolveNull() {
        GerenciadorPedidos gerenciador = new GerenciadorPedidos();
        gerenciador.criarPedido("Ana");

        assertNull(gerenciador.buscarPedido(99));
        assertFalse(gerenciador.existePedido(99));
    }

    @Test
    void cancelarPedidoRemoveDoTotal() {
        GerenciadorPedidos gerenciador = new GerenciadorPedidos();
        Pedido ana = gerenciador.criarPedido("Ana");
        ana.adicionarItem(LancheFactory.xBurger());   // 16
        Pedido bia = gerenciador.criarPedido("Bia");
        bia.adicionarItem(LancheFactory.xBacon());    // 22

        boolean cancelou = gerenciador.cancelarPedido(bia.getNumero());

        assertTrue(cancelou);
        assertNull(gerenciador.buscarPedido(bia.getNumero()));
        assertEquals(16.0, gerenciador.calcularTotalDoDia(), 0.001);
    }

    @Test
    void cancelarPedidoInexistenteDevolveFalse() {
        GerenciadorPedidos gerenciador = new GerenciadorPedidos();

        assertFalse(gerenciador.cancelarPedido(42));
    }

    @Test
    void fechamentoSomaTodosOsPedidos() {
        GerenciadorPedidos gerenciador = new GerenciadorPedidos();
        Pedido ana = gerenciador.criarPedido("Ana");
        ana.adicionarItem(LancheFactory.xBurger());                         // 16
        ana.adicionarItem(LancheFactory.xSalada());                         // 18
        Pedido bia = gerenciador.criarPedido("Bia");
        bia.adicionarItem(new ComBaconExtra(new ComCheddar(LancheFactory.xBacon())));  // 32

        assertEquals(66.0, gerenciador.calcularTotalDoDia(), 0.001);
    }

    @Test
    void diaSemPedidosTemTotalZero() {
        GerenciadorPedidos gerenciador = new GerenciadorPedidos();

        assertEquals(0.0, gerenciador.calcularTotalDoDia(), 0.001);
    }

    @Test
    void combinacoesRepetidasContamUmaVez() {
        GerenciadorPedidos gerenciador = new GerenciadorPedidos();
        Pedido ana = gerenciador.criarPedido("Ana");
        ana.adicionarItem(LancheFactory.xBacon());
        ana.adicionarItem(new ComCheddar(LancheFactory.xBacon()));
        Pedido bia = gerenciador.criarPedido("Bia");
        bia.adicionarItem(LancheFactory.xBacon());  // repetido: o HashSet ignora

        HashSet<String> combinacoes = gerenciador.getCombinacoesDiferentes();

        // 3 itens vendidos, mas só 2 descrições diferentes
        assertEquals(2, combinacoes.size());
        assertTrue(combinacoes.contains("X-Bacon"));
        assertTrue(combinacoes.contains("X-Bacon + cheddar"));
    }

    @Test
    void ordemDosAdicionaisGeraCombinacoesDiferentes() {
        // a "pergunta para pensar" do 3.3: textos diferentes contam como 2
        GerenciadorPedidos gerenciador = new GerenciadorPedidos();
        Pedido ana = gerenciador.criarPedido("Ana");
        ana.adicionarItem(new ComBaconExtra(new ComCheddar(LancheFactory.xBacon())));
        ana.adicionarItem(new ComCheddar(new ComBaconExtra(LancheFactory.xBacon())));

        assertEquals(2, gerenciador.getCombinacoesDiferentes().size());
    }

    @Test
    void lanchesBaseDiferentes() {
        GerenciadorPedidos gerenciador = new GerenciadorPedidos();
        Pedido ana = gerenciador.criarPedido("Ana");
        ana.adicionarItem(LancheFactory.xBacon());
        ana.adicionarItem(new ComCheddar(LancheFactory.xBacon()));
        Pedido bia = gerenciador.criarPedido("Bia");
        Lanche salada = LancheFactory.xSalada();
        salada.setTomate(false);
        bia.adicionarItem(salada);

        HashSet<String> lanchesBase = gerenciador.getLanchesBaseDiferentes();

        // X-Bacon e X-Bacon + cheddar contam como o mesmo lanche base
        assertEquals(2, lanchesBase.size());
        assertTrue(lanchesBase.contains("X-Bacon"));
        assertTrue(lanchesBase.contains("X-Salada"));
    }
}
