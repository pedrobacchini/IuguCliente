package irvem.com.br.iugucliente.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
public class CobrancaRequestIUGU {

    @Getter
    private String customer_payment_method_id;
    @Getter
    private String email;
    @Getter
    private List<Item> items = new ArrayList<>();

    @Builder
    CobrancaRequestIUGU(String customer_payment_method_id, String email,
                        String description, int quantity, int price_cents){
        this.customer_payment_method_id = customer_payment_method_id;
        this.email = email;
        Item item = new Item();
        item.description = description;
        item.quantity = quantity;
        item.price_cents = price_cents;
        this.items.add(item);
    }

    @ToString
    public class Item{
        @Getter
        private String description;
        @Getter
        private int quantity;
        @Getter
        private int price_cents;
    }
}
