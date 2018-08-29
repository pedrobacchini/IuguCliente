package irvem.com.br.iugucliente.network;

import irvem.com.br.iugucliente.domain.ClienteIUGU;
import irvem.com.br.iugucliente.domain.ConjuntoClienteIUGU;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ClienteInterface {

    @GET("customers")
    Call<ConjuntoClienteIUGU> listaClientes();

    @DELETE("customers/{idCliente}")
    Call<ClienteIUGU> deletarCliente(@Path("idCliente") String idCliente);

//    @GET("customers/{idCliente}")
//    Call<ClienteIUGU> pegarCliente(@Path("idCliente") String idCliente);

    @POST("customers")
    Call<ClienteIUGU> adcionarCliente(@Body ClienteIUGU cliente);

    @PUT("customers/{idCliente}")
    Call<ClienteIUGU> atualizarCliente(@Body ClienteIUGU cliente, @Path("idCliente") String idCliente);
}
