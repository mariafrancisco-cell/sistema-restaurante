package cardapio;

// Nível 1 (1.1) + Nível 2 (2.1: implementa ItemCardapio) + Nível 3 (3.1: retirar ingredientes)
public class Lanche implements ItemCardapio {

    private String nome;
    private double preco;

    // Nível 3 (3.1)
    // O "= true" é obrigatório: em Java, um atributo boolean sem valor vira false,
    // e o lanche nasceria "sem queijo, sem alface...". Como o construtor só recebe
    // nome e preço, estes campos começam com o valor escrito aqui.
    private boolean queijo = true;
    private boolean alface = true;
    private boolean tomate = true;
    private boolean cebola = true;

    // O construtor reutiliza os setters: assim a validação fica num lugar só.
    public Lanche(String nome, double preco) {
        setNome(nome);
        setPreco(preco);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        // trim() tira os espaços das pontas, então "   " também é recusado
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do lanche não pode ser vazio.");
        }
        this.nome = nome;
    }

    @Override
    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        if (preco < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo.");
        }
        this.preco = preco;
    }

    // Nível 3 (3.1) - getters e setters dos ingredientes (sem validação: são booleanos).
    // Para boolean, o costume em Java é "isAlgo()" em vez de "getAlgo()".
    public boolean isQueijo() {
        return queijo;
    }

    public void setQueijo(boolean queijo) {
        this.queijo = queijo;
    }

    public boolean isAlface() {
        return alface;
    }

    public void setAlface(boolean alface) {
        this.alface = alface;
    }

    public boolean isTomate() {
        return tomate;
    }

    public void setTomate(boolean tomate) {
        this.tomate = tomate;
    }

    public boolean isCebola() {
        return cebola;
    }

    public void setCebola(boolean cebola) {
        this.cebola = cebola;
    }

    // Nível 3 (3.1) - retirar ingrediente muda a descrição, mas NÃO o preço.
    @Override
    public String getDescricao() {
        String desc = nome;

        if (!queijo) {
            desc += " sem queijo";
        }
        if (!alface) {
            desc += " sem alface";
        }
        if (!tomate) {
            desc += " sem tomate";
        }
        if (!cebola) {
            desc += " sem cebola";
        }
        return desc;
    }

    // BÔNUS: o lanche puro é a "base", então devolve só o nome.
    @Override
    public String getNomeBase() {
        return nome;
    }
}
