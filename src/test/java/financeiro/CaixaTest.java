package financeiro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// BÔNUS — Caixa (Singleton) e Transacao (equals/hashCode).
class CaixaTest {

    private Caixa caixa;

    // @BeforeEach roda ANTES de cada teste (como uma fixture do pytest).
    // Por que zerar? O Caixa é um Singleton: existe um único objeto durante
    // toda a execução, então as transações de um teste continuariam lá no
    // próximo. Zerando, cada teste começa com o caixa vazio.
    @BeforeEach
    void zerarCaixa() {
        caixa = Caixa.getInstancia();
        caixa.zerar();
    }

    @Test
    void singletonDevolveOMesmoObjeto() {
        // executa
        Caixa primeiro = Caixa.getInstancia();
        Caixa segundo = Caixa.getInstancia();

        // confere: assertSame = é o MESMO objeto (como "is" em Python)
        assertSame(primeiro, segundo);
    }

    @Test
    void transacoesComMesmoNumeroSaoIguais() {
        // prepara: mesmo pedido, valores diferentes
        Transacao a = new Transacao(7, 30.0, 0.0);
        Transacao b = new Transacao(7, 50.0, 5.0);

        // confere: objetos diferentes, mas "iguais" pela regra do equals
        assertNotSame(a, b);
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void transacoesComNumerosDiferentesNaoSaoIguais() {
        Transacao a = new Transacao(1, 30.0, 0.0);
        Transacao b = new Transacao(2, 30.0, 0.0);

        assertFalse(a.equals(b));
    }

    @Test
    void transacaoNaoEhIgualANulo() {
        Transacao a = new Transacao(1, 30.0, 0.0);

        assertFalse(a.equals(null));
    }

    @Test
    void transacaoComValorOuGorjetaNegativaLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Transacao(1, -10.0, 0.0));
        assertThrows(IllegalArgumentException.class, () -> new Transacao(1, 10.0, -1.0));
    }

    @Test
    void totalDaTransacaoSomaValorEGorjeta() {
        Transacao t = new Transacao(1, 40.0, 4.0);

        assertEquals(44.0, t.getTotal(), 0.001);
    }

    @Test
    void caixaNaoRegistraOMesmoPedidoDuasVezes() {
        boolean primeira = caixa.registrar(new Transacao(3, 22.0, 0.0));
        boolean segunda = caixa.registrar(new Transacao(3, 22.0, 2.0));

        assertTrue(primeira);
        assertFalse(segunda);
        assertEquals(1, caixa.getQuantidadeTransacoes());
        assertEquals(22.0, caixa.getTotalRecebido(), 0.001);
    }

    @Test
    void pedidoJaPagoConsultaPeloNumero() {
        caixa.registrar(new Transacao(5, 16.0, 0.0));

        assertTrue(caixa.pedidoJaPago(5));
        assertFalse(caixa.pedidoJaPago(6));
    }

    @Test
    void somaDeGorjetas() {
        caixa.registrar(new Transacao(1, 16.0, 2.0));
        caixa.registrar(new Transacao(2, 32.0, 3.5));
        caixa.registrar(new Transacao(3, 18.0, 0.0));

        assertEquals(5.5, caixa.getTotalGorjetas(), 0.001);
        // o total recebido NÃO inclui a gorjeta
        assertEquals(66.0, caixa.getTotalRecebido(), 0.001);
    }

    @Test
    void zerarLimpaAsTransacoes() {
        caixa.registrar(new Transacao(1, 16.0, 2.0));

        caixa.zerar();

        assertEquals(0, caixa.getQuantidadeTransacoes());
        assertEquals(0.0, caixa.getTotalGorjetas(), 0.001);
    }
}
