package util;

import java.util.Locale;

// Utilitário: métodos static que qualquer classe pode usar sem criar objeto.
// (Parecido com uma função solta de um módulo em Python.)
public class Formatador {

    // Locale brasileiro: usa vírgula nos centavos -> "R$ 16,00" (e não "R$ 16.00").
    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    // Classe só com métodos static: o construtor privado impede "new Formatador()".
    private Formatador() {
    }

    // Formata um valor em dinheiro, ex.: 16.0 -> "R$ 16,00".
    // %.2f = número com 2 casas decimais (como f"{valor:.2f}" em Python).
    public static String dinheiro(double valor) {
        return String.format(BRASIL, "R$ %.2f", valor);
    }
}
