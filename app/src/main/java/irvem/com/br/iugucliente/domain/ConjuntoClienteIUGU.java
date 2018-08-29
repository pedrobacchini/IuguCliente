package irvem.com.br.iugucliente.domain;

import java.util.List;

import lombok.Getter;

public class ConjuntoClienteIUGU {

    @Getter
    private int totalItems;
    @Getter
    private List<ClienteIUGU> items;
}
