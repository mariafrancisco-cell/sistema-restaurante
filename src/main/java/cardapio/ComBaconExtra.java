package cardapio;

// Nível 2 (2.3) - padrão Decorator (mesma ideia do ComCheddar)
// Mais um "embrulho": guarda outro item dentro e repassa as chamadas,
// como em Python um objeto que guarda self.item e chama self.item.metodo().
// Como o item guardado também é um ItemCardapio, dá para embrulhar
// um embrulho: new ComBaconExtra(new ComCheddar(lanche)).
public class ComBaconExtra implements ItemCardapio {

    public static final double PRECO = 6.0;

    private ItemCardapio item;

    public ComBaconExtra(ItemCardapio item) {
        if (item == null) {
            throw new IllegalArgumentException("O adicional precisa de um item para embrulhar.");
        }
        this.item = item;
    }

    @Override
    public String getDescricao() {
        return item.getDescricao() + " + bacon extra";
    }

    @Override
    public double getPreco() {
        return item.getPreco() + PRECO;
    }

    // BÔNUS: repassa para o item de dentro até chegar no Lanche.
    @Override
    public String getNomeBase() {
        return item.getNomeBase();
    }
}
