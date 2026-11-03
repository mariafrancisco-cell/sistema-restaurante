package cardapio;

// Nível 2 (2.2) - padrão Factory
// O resto do sistema pede o lanche aqui e não precisa saber nome nem preço.
// Se o preço do X-Bacon mudar, só esta classe muda.
//
// Ingredientes de cada lanche:
//   X-Burger: pão, hambúrguer, queijo, alface e tomate.
//   X-Salada: pão, hambúrguer, queijo, alface, tomate e cebola.
//   X-Bacon:  pão, hambúrguer, queijo, bacon, alface, tomate e cebola.
//
// Os métodos são static: chamamos LancheFactory.xBacon() sem criar uma LancheFactory.
public class LancheFactory {

    // Cada chamada faz "new Lanche(...)", ou seja, cria um objeto NOVO.
    // Por quê? O Lanche pode ser alterado (ex.: setCebola(false)). Se todos os
    // pedidos usassem o mesmo objeto, tirar a cebola de um tiraria de todos.

    public static Lanche xBurger() {
        return new Lanche("X-Burger", 16.0);
    }

    public static Lanche xSalada() {
        return new Lanche("X-Salada", 18.0);
    }

    public static Lanche xBacon() {
        return new Lanche("X-Bacon", 22.0);
    }
}
