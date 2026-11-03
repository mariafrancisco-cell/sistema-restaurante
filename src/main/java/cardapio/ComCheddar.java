package cardapio;

// Nível 2 (2.3) - padrão Decorator
// Pense em embrulhar um presente: o presente (o lanche) continua lá dentro,
// e cada embrulho (adicional) acrescenta algo por fora.
// Em Python seria um objeto que guarda outro em self.item e repassa as
// chamadas para ele, somando a sua parte.
//
// Exemplo: new ComCheddar(LancheFactory.xBacon())
//   descrição: "X-Bacon" + " + cheddar"   preço: 22.0 + 4.0
public class ComCheddar implements ItemCardapio {

    // final = constante, não pode mudar depois
    public static final double PRECO = 4.0;

    // O item que está "dentro do embrulho" (pode ser um Lanche ou outro adicional)
    private ItemCardapio item;

    public ComCheddar(ItemCardapio item) {
        if (item == null) {
            throw new IllegalArgumentException("O adicional precisa de um item para embrulhar.");
        }
        this.item = item;
    }

    @Override
    public String getDescricao() {
        return item.getDescricao() + " + cheddar";
    }

    @Override
    public double getPreco() {
        return item.getPreco() + PRECO;
    }

    // BÔNUS: o adicional não muda o lanche base, só repassa a pergunta para dentro.
    @Override
    public String getNomeBase() {
        return item.getNomeBase();
    }
}
