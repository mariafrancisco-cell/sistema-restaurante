package cardapio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Nível 2 (2.4) — teste do PREÇO com adicional (padrão Decorator).
class AdicionaisTest {

    @Test
    void xBaconComCheddarCusta26() {
        // exatamente o exemplo do enunciado
        ItemCardapio lanche = new ComCheddar(LancheFactory.xBacon());
        assertEquals(26.0, lanche.getPreco(), 0.001);
    }

    @Test
    void xBaconComCheddarEBaconExtra() {
        // prepara: cada adicional "embrulha" o item anterior
        ItemCardapio lanche = LancheFactory.xBacon();
        lanche = new ComCheddar(lanche);
        lanche = new ComBaconExtra(lanche);

        // executa
        String descricao = lanche.getDescricao();
        double preco = lanche.getPreco();

        // confere: 22 + 4 + 6 = 32
        assertEquals("X-Bacon + cheddar + bacon extra", descricao);
        assertEquals(32.0, preco, 0.001);
    }

    @Test
    void adicionalMantemIngredientesRetirados() {
        Lanche base = LancheFactory.xBacon();
        base.setCebola(false);

        ItemCardapio lanche = new ComCheddar(base);

        assertEquals("X-Bacon sem cebola + cheddar", lanche.getDescricao());
        assertEquals(26.0, lanche.getPreco(), 0.001);
    }

    @Test
    void adicionalNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new ComCheddar(null));
        assertThrows(IllegalArgumentException.class, () -> new ComBaconExtra(null));
    }

    @Test
    void factoryCriaObjetosDiferentes() {
        Lanche primeiro = LancheFactory.xBacon();
        Lanche segundo = LancheFactory.xBacon();

        // assertNotSame confere que NÃO é o mesmo objeto na memória
        // (como "primeiro is not segundo" em Python)
        assertNotSame(primeiro, segundo);

        // e por isso personalizar um não muda o outro
        primeiro.setCebola(false);
        assertEquals("X-Bacon", segundo.getDescricao());
    }

    @Test
    void nomeBaseIgnoraAdicionais() {
        // BÔNUS: o Decorator repassa getNomeBase() para o item de dentro
        Lanche base = LancheFactory.xBacon();
        base.setCebola(false);
        ItemCardapio lanche = new ComBaconExtra(new ComCheddar(base));

        assertEquals("X-Bacon", lanche.getNomeBase());
    }
}
