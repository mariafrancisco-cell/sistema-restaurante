package cardapio;

// Nível 2 (2.1)
// Interface = um "contrato": quem implementa promete ter estes métodos.
// Assim o Pedido pode guardar lanches puros E lanches com adicionais
// na mesma lista, como o Pet[] que guardava Cachorro e Gato.
// (Em Python seria parecido com "duck typing", só que aqui o Java confere.)
public interface ItemCardapio {

    String getDescricao();

    double getPreco();

    // BÔNUS: nome do lanche base, sem adicionais nem "sem X".
    // Ex.: "X-Bacon sem cebola + cheddar" tem nome base "X-Bacon".
    String getNomeBase();
}
