package financeiro;

import java.util.HashSet;

// BÔNUS
// Padrão Singleton: o sistema inteiro usa UM único Caixa.
// O construtor é privado (ninguém faz new Caixa() de fora) e o único jeito
// de pegar o caixa é Caixa.getInstancia(), que sempre devolve o mesmo objeto.
public class Caixa {

    // static: pertence à classe, não a um objeto. Guarda o único Caixa que vai existir.
    private static Caixa instancia;

    // HashSet como um set do Python: não aceita duas transações "iguais" (mesmo pedido).
    private HashSet<Transacao> transacoes = new HashSet<>();

    private Caixa() {
    }

    public static Caixa getInstancia() {
        if (instancia == null) {
            instancia = new Caixa(); // só cria na primeira vez
        }
        return instancia;
    }

    // É AQUI que o equals/hashCode da Transacao trabalham: o HashSet usa os dois para ver
    // se já existe uma transação "igual" (mesmo pedido). add devolve false = pedido já pago.
    public boolean registrar(Transacao t) {
        return transacoes.add(t);
    }

    // Procura com um for simples, como "any(...)" em Python.
    public boolean pedidoJaPago(int numeroPedido) {
        for (Transacao t : transacoes) {
            if (t.getNumeroPedido() == numeroPedido) {
                return true;
            }
        }
        return false;
    }

    // Soma só o valor dos pedidos (sem gorjeta).
    public double getTotalRecebido() {
        double total = 0;
        for (Transacao t : transacoes) {
            total += t.getValor();
        }
        return total;
    }

    public double getTotalGorjetas() {
        double total = 0;
        for (Transacao t : transacoes) {
            total += t.getGorjeta();
        }
        return total;
    }

    public int getQuantidadeTransacoes() {
        return transacoes.size();
    }

    // Como só existe um Caixa, ele "lembra" de tudo. Este método limpa as transações
    // para começar um novo dia e para cada teste começar do zero.
    public void zerar() {
        transacoes.clear();
    }
}
