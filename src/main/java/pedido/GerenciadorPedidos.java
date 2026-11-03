package pedido;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import cardapio.ItemCardapio;

// Nível 1 (1.3)
// Guarda TODOS os pedidos do dia. Não imprime nada: só devolve dados para quem for exibir.
public class GerenciadorPedidos {

    // HashMap é como um dict do Python: chave = número do pedido, valor = o Pedido.
    private HashMap<Integer, Pedido> pedidos = new HashMap<>();
    private int contadorPedidos = 1;

    // Atenção: o HashMap não recusa chave repetida, ele SUBSTITUI o valor antigo (igual ao dict).
    // Por isso o número nunca vem do usuário: é o contador que gera, e ele nunca repete.
    public Pedido criarPedido(String nomeCliente) {
        Pedido novo = new Pedido(contadorPedidos, nomeCliente);
        pedidos.put(novo.getNumero(), novo);
        contadorPedidos++;
        return novo;
    }

    // get devolve null se a chave não existe (parecido com dict.get() do Python, que devolve None).
    public Pedido buscarPedido(int numero) {
        return pedidos.get(numero);
    }

    // Como "numero in pedidos" no Python.
    public boolean existePedido(int numero) {
        return pedidos.containsKey(numero);
    }

    // Collection = tipo geral de "grupo de objetos" (é o que values() devolve).
    // Devolve uma CÓPIA, como o Pedido.getItens(): quem recebe olha, mas não mexe no mapa por fora.
    public Collection<Pedido> getPedidos() {
        return new ArrayList<>(pedidos.values());
    }

    // Nível 3 (3.2): ao sair do mapa, o pedido sai também do total do dia.
    public boolean cancelarPedido(int numero) {
        // remove devolve o valor removido, ou null se a chave não existia.
        return pedidos.remove(numero) != null;
    }

    public double calcularTotalDoDia() {
        double total = 0;
        for (Pedido p : pedidos.values()) {
            total += p.calcularTotal();
        }
        return total;
    }

    // Nível 3 (3.3)
    // HashSet é como um set do Python: descrições repetidas entram uma vez só.
    // Pergunta para pensar (do enunciado): "X-Bacon + cheddar + bacon extra" e
    // "X-Bacon + bacon extra + cheddar" têm os mesmos adicionais e o mesmo preço, mas são
    // textos diferentes, então contam como DUAS combinações. A ordem deveria importar?
    // Como você resolveria isso?
    public HashSet<String> getCombinacoesDiferentes() {
        HashSet<String> combinacoes = new HashSet<>();
        for (Pedido p : pedidos.values()) {
            for (ItemCardapio item : p.getItens()) {
                combinacoes.add(item.getDescricao());
            }
        }
        return combinacoes;
    }

    // BÔNUS: "X-Bacon" e "X-Bacon + cheddar" contam como o mesmo lanche base.
    public HashSet<String> getLanchesBaseDiferentes() {
        HashSet<String> lanchesBase = new HashSet<>();
        for (Pedido p : pedidos.values()) {
            for (ItemCardapio item : p.getItens()) {
                lanchesBase.add(item.getNomeBase());
            }
        }
        return lanchesBase;
    }
}
