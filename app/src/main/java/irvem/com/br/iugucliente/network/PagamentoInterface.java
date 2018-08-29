package irvem.com.br.iugucliente.network;

import java.util.List;

import irvem.com.br.iugucliente.domain.PagamentoIUGU;
import irvem.com.br.iugucliente.domain.TokenIUGU;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PagamentoInterface {

    @GET("customers/{idCliente}/payment_methods")
    Call<List<PagamentoIUGU>> listaPagamentos(@Path("idCliente") String idCliente);

    @DELETE("customers/{idCliente}/payment_methods/{idPagamento}")
    Call<PagamentoIUGU> deletarPagamento(@Path("idCliente") String idCliente, @Path("idPagamento") String idPagamento);

    @POST("payment_token")
    Call<TokenIUGU> criarToken(@Body TokenIUGU token);

    @POST("customers/{idCliente}/payment_methods")
    Call<PagamentoIUGU> adicionarPagamento(@Path("idCliente") String idCliente, @Body PagamentoIUGU pagamento);
}
