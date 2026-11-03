package cardapio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Nível 2 (2.4) — teste de VALIDAÇÃO.
//
// Comparando com pytest (Python):
//   - cada método com @Test é como uma função "def test_...()" no pytest;
//   - assertEquals(esperado, obtido) é como "assert obtido == esperado";
//   - assertThrows(IllegalArgumentException.class, () -> ...) é como
//     "with pytest.raises(ValueError): ..." — o teste PASSA se a exceção acontecer.
//   - o "() -> ..." é um lambda: um pedaço de código que o assertThrows executa
//     lá dentro, para poder pegar a exceção (como "lambda: ..." em Python).
//
// No JUnit 5 a classe e os métodos de teste não precisam ser public.
class LancheTest {

    @Test
    void precoNegativoLancaExcecao() {
        // executa + confere: criar um lanche com preço negativo tem que dar erro
        assertThrows(IllegalArgumentException.class, () -> new Lanche("X-Burger", -1.0));
    }

    @Test
    void setPrecoNegativoTambemLancaExcecao() {
        // prepara
        Lanche lanche = LancheFactory.xBurger();

        // executa + confere: a mesma regra vale pelo setter (validação centralizada)
        assertThrows(IllegalArgumentException.class, () -> lanche.setPreco(-5.0));

        // confere: o preço antigo continua lá
        assertEquals(16.0, lanche.getPreco(), 0.001);
    }

    @Test
    void precoZeroEhPermitido() {
        // zero não é negativo, então pode (ex.: um brinde)
        Lanche lanche = new Lanche("Brinde", 0.0);
        assertEquals(0.0, lanche.getPreco(), 0.001);
    }

    @Test
    void nomeVazioLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Lanche("", 10.0));
    }

    @Test
    void nomeSoComEspacosLancaExcecao() {
        // por causa do trim(), "   " conta como vazio
        assertThrows(IllegalArgumentException.class, () -> new Lanche("   ", 10.0));
    }

    @Test
    void nomeNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Lanche(null, 10.0));
    }

    @Test
    void retirarCebolaNaoMudaPreco() {
        Lanche lanche = LancheFactory.xBacon();

        lanche.setCebola(false);

        assertEquals(22.0, lanche.getPreco(), 0.001);
    }

    @Test
    void descricaoMostraIngredientesRetirados() {
        Lanche lanche = LancheFactory.xSalada();

        lanche.setQueijo(false);
        lanche.setTomate(false);

        // a ordem é sempre queijo, alface, tomate, cebola
        assertEquals("X-Salada sem queijo sem tomate", lanche.getDescricao());
    }

    @Test
    void lancheSemRetiradaMostraSoONome() {
        Lanche lanche = LancheFactory.xBurger();

        assertEquals("X-Burger", lanche.getDescricao());
    }
}
