package irvem.com.br.iugucliente.network;

import android.text.TextUtils;

import irvem.com.br.iugucliente.domain.TokenIUGU;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class ServiceGenerator {

    private static final String TokenIUGU = "Token IUGU vai aqui";

    public static final String BASE_URL = "https://api.iugu.com/v1/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(JacksonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static ClienteInterface getClienteInterface() {
        return createService(ClienteInterface.class,TokenIUGU,"");
    }

    public static PagamentoInterface getPagamentoInterface(){
        return createService(PagamentoInterface.class,TokenIUGU,"");
    }

    public static CobrancaInterface getCobrancaInterface(){
        return createService(CobrancaInterface.class,TokenIUGU,"");
    }

    public static <S> S createService(Class<S> serviceClass, String userName, String password){
        if(!TextUtils.isEmpty(userName)){
            String authToken = Credentials.basic(userName,password);
            return createService(serviceClass, authToken);
        }
        return createService(serviceClass, null);
    }

    public static <S> S createService(Class<S> serviceClass, String authToken){
        if(!TextUtils.isEmpty(authToken)){
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);

            if(!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        return retrofit.create(serviceClass);
    }
}
