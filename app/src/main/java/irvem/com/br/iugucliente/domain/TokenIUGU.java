package irvem.com.br.iugucliente.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenIUGU {

    @Getter
    private String id;
    @Getter
    private boolean test;
    @Getter
    private methodType method;
    @Getter
    private DadosCartao data;

    public TokenIUGU(){}

    @Builder
    public TokenIUGU(boolean test, methodType metodo, String account_id,
                     String number, int verification_value, String first_name,
                     String last_name, int month, int year){
        this.test = test;
        this.method = metodo;
        this.data = new DadosCartao();
        this.data.number = number;
        this.data.verification_value = verification_value;
        this.data.first_name = first_name;
        this.data.last_name = last_name;
        this.data.month = month;
        this.data.year = year;
    }

    @ToString
    public class DadosCartao{
        @Getter
        private String number;
        @Getter
        private int verification_value;
        @Getter
        private String first_name;
        @Getter
        private String last_name;
        @Getter
        private int month;
        @Getter
        private int year;
    }

    public enum methodType{
        credit_card
    }
}
