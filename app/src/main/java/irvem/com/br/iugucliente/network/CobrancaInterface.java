package irvem.com.br.iugucliente.network;

import irvem.com.br.iugucliente.domain.CobrancaRequestIUGU;
import irvem.com.br.iugucliente.domain.CobrancaResponseIUGU;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CobrancaInterface {

    @POST("charge")
    Call<CobrancaResponseIUGU> criarCobranca(@Body CobrancaRequestIUGU cobranca);
}
