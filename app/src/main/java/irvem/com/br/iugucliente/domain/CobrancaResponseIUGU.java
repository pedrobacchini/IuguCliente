package irvem.com.br.iugucliente.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CobrancaResponseIUGU {

    @Getter
    private String message;
    @Getter
    private boolean success;
}
