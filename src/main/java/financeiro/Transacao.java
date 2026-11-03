package financeiro;

// BÔNUS
// Um pagamento de pedido. Depois de criada, não muda (por isso os atributos são final).
public class Transacao {

    private final int numeroPedido;
    private final double valor;
    private final double gorjeta;

    public Transacao(int numeroPedido, double valor, double gorjeta) {
        if (valor < 0) {
            throw new IllegalArgumentException("O valor não pode ser negativo.");
        }
        if (gorjeta < 0) {
            throw new IllegalArgumentException("A gorjeta não pode ser negativa.");
        }
        this.numeroPedido = numeroPedido;
        this.valor = valor;
        this.gorjeta = gorjeta;
    }

    public int getNumeroPedido() {
        return numeroPedido;
    }

    public double getValor() {
        return valor;
    }

    public double getGorjeta() {
        return gorjeta;
    }

    public double getTotal() {
        return valor + gorjeta;
    }

    // Duas transações são "iguais" se são do MESMO pedido: um pedido só pode ser pago uma vez.
    // É o mesmo papel do __eq__ do Python.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true; // é literalmente o mesmo objeto
        }
        if (o == null || getClass() != o.getClass()) {
            return false; // nada ou outro tipo de objeto
        }
        Transacao outra = (Transacao) o; // cast: agora o Java sabe que é uma Transacao
        return numeroPedido == outra.numeroPedido;
    }

    // Contrato: se equals diz que dois objetos são iguais, o hashCode deles TEM de ser igual.
    // O HashSet usa o hashCode para achar a "gaveta" e o equals para confirmar se já existe.
    // Em Python é a mesma regra entre __eq__ e __hash__. Por isso usamos o mesmo campo do equals.
    @Override
    public int hashCode() {
        return numeroPedido; // o próprio número já serve de hash (mesmo campo do equals)
    }

    @Override
    public String toString() {
        return "Transacao{pedido=" + numeroPedido + ", valor=" + valor + ", gorjeta=" + gorjeta + "}";
    }
}
