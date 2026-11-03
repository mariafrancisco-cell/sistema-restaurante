package pedido;

import java.util.ArrayList;

import cardapio.ItemCardapio;

// Nível 1 (1.2)
// O Pedido cuida de UMA comanda; quem guarda todos é o GerenciadorPedidos (S do SOLID).
public class Pedido {

    private int numero;
    private String nomeCliente;

    // Nível 2 (2.1): lista do tipo da interface, para aceitar lanche puro E lanche com adicionais.
    private ArrayList<ItemCardapio> itens = new ArrayList<>();

    // O número chega pronto: quem gera é o GerenciadorPedidos.
    public Pedido(int numero, String nomeCliente) {
        this.numero = numero;
        setNomeCliente(nomeCliente); // reaproveita a validação do setter
    }

    public int getNumero() {
        return numero;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        if (nomeCliente == null || nomeCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do cliente não pode ser vazio.");
        }
        this.nomeCliente = nomeCliente;
    }

    public void adicionarItem(ItemCardapio item) {
        if (item == null) {
            throw new IllegalArgumentException("O item não pode ser nulo.");
        }
        itens.add(item);
    }

    // Devolve uma CÓPIA da lista: quem receber pode olhar, mas não mexe na lista original por fora.
    // Em Python seria como devolver itens[:] em vez da própria lista.
    public ArrayList<ItemCardapio> getItens() {
        return new ArrayList<>(itens);
    }

    // Nível 3 (3.2): índice começa em 0, como em Python.
    public ItemCardapio removerItem(int indice) {
        if (indice < 0 || indice >= itens.size()) {
            throw new IllegalArgumentException("Não existe item na posição " + indice + ".");
        }
        return itens.remove(indice);
    }

    public int getQuantidadeItens() {
        return itens.size();
    }

    // Recalcula sempre a partir da lista: se um item sai ou entra, o total já acompanha.
    public double calcularTotal() {
        double total = 0;
        for (ItemCardapio item : itens) {
            total += item.getPreco();
        }
        return total;
    }
}
