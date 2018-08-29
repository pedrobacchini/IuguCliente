package irvem.com.br.iugucliente.application;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import irvem.com.br.iugucliente.domain.ClienteIUGU;
import lombok.Getter;
import lombok.Setter;

public class CustomApplication extends Application {

    private static final String TAG = CustomApplication.class.getSimpleName();

    @Getter @Setter
    private List<ClienteIUGU> clientes;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate()");
        clientes = new ArrayList<ClienteIUGU>();
    }

    public void setCliente(int index, ClienteIUGU cliente){
        clientes.set(index, cliente);
    }

    public ClienteIUGU getCliente(int index){
        return clientes.get(index);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG,"onLowMemory()");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i(TAG,"onTrimMemory("+level+")");
    }
}
