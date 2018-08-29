package irvem.com.br.iugucliente.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import irvem.com.br.iugucliente.R;
import irvem.com.br.iugucliente.application.CustomApplication;
import irvem.com.br.iugucliente.domain.ClienteIUGU;
import irvem.com.br.iugucliente.domain.ConjuntoClienteIUGU;
import irvem.com.br.iugucliente.network.ClienteInterface;
import irvem.com.br.iugucliente.network.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ClienteInterface clienteInterface = ServiceGenerator.getClienteInterface();

    public static final String INDEX_CLIENTE = "index_cliente";

//    public static final String KEY_CLIENTE = "cliente";

//    private static final int NOVO_CLIENTE_RESULT_CODE = 1000;
//    private static final int DETALHES_CLIENTE_RESULT_CODE = 2000;

    @BindView(R.id.list)
    ListView lView;

//    private List<ClienteIUGU> clientes;

    private CustomApplication customApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        customApplication = (CustomApplication) getApplicationContext();
        buscarClientes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.miRefresh) {
            buscarClientes();
            return true;
        } else if (id == R.id.miNovo) {
            Intent intent = new Intent(this, NovoCliente.class);
//            startActivityForResult(intent, NOVO_CLIENTE_RESULT_CODE);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
        atualizarListaClientes();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.i(TAG,requestCode+" "+resultCode+" "+data);
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==NOVO_CLIENTE_RESULT_CODE && resultCode==RESULT_OK) {
//            Log.i(TAG,"NOVO_CLIENTE_RESULT_CODE");
//            ClienteIUGU novoCliente = data.getParcelableExtra(NovoCliente.KEY_NOVO_CLIENTE);
//            adicionarCliente(0, novoCliente);
//        }
//        if(requestCode==DETALHES_CLIENTE_RESULT_CODE && resultCode==RESULT_OK) {
//            Log.i(TAG,"DETALHES_CLIENTE_RESULT_CODE");
//            ClienteIUGU novoCliente = data.getParcelableExtra(NovoCliente.KEY_NOVO_CLIENTE);
//            int indexCliente = clientes.indexOf(novoCliente);
//            if(indexCliente>=0) {
//                clientes.remove(indexCliente);
//                adicionarCliente(indexCliente, novoCliente);
//            }
//            else
//                throw new NullPointerException("Cliente nao encontrado!");
//        }
//    }

    private void buscarClientes() {
        final ProgressDialog mainDialog = ProgressDialog.show(this, "Aviso", "Aguarde, buscando clientes");
        Call<ConjuntoClienteIUGU> call = clienteInterface.listaClientes();
        call.enqueue(new Callback<ConjuntoClienteIUGU>() {
            @Override
            public void onResponse(Call<ConjuntoClienteIUGU> call, Response<ConjuntoClienteIUGU> response) {
                if(response.isSuccessful()) {
                    customApplication.setClientes(response.body().getItems());
                    atualizarListaClientes();
                    mainDialog.dismiss();
                }
                else {
                    Log.i(TAG,response.raw().toString());
                    onFailure(call,new Exception(response.message()));
                }
            }

            @Override
            public void onFailure(Call<ConjuntoClienteIUGU> call, Throwable t) {
                Log.i(TAG,"onFailure");
                Log.i(TAG,t.getMessage()+" "+call.request().url()+" "+call.request().toString());
                mainDialog.dismiss();
                Toast.makeText(MainActivity.this, "Erro ao buscar clientes", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void atualizarListaClientes(){
        lView.setAdapter(criarAdaptadorDeClientes());
        lView.setOnItemClickListener(new OnItemClickListenerCliente());
        lView.setOnItemLongClickListener(new OnItemLongClickListenerCliente());
    }

    private ArrayAdapter<ClienteIUGU> criarAdaptadorDeClientes(){
        final ClienteIUGU[] clienteArray = customApplication.getClientes().toArray(new ClienteIUGU[customApplication.getClientes().size()]);
        return new ArrayAdapter<ClienteIUGU>(MainActivity.this, 0, clienteArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.custom_list_item_clientes, null, false);

                TextView nomeCliente = (TextView) convertView.findViewById(R.id.cliente_nome);
                TextView emailCliente = (TextView) convertView.findViewById(R.id.cliente_email);

                nomeCliente.setText(clienteArray[position].getName());
                emailCliente.setText(clienteArray[position].getEmail());

                return convertView;
            }
        };
    }

    class OnItemClickListenerCliente implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, DetalhesCliente.class);
            intent.putExtra(INDEX_CLIENTE,position);
//            intent.putExtra(KEY_CLIENTE, customApplication.getClientes().get(position));
//            startActivityForResult(intent, DETALHES_CLIENTE_RESULT_CODE);
            startActivity(intent);
        }
    }

    class OnItemLongClickListenerCliente implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapter, View view, final int position, long arg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Deseja excluir o cliente "+customApplication.getClientes().get(position).getName()+" ?");
            builder.setTitle("Deletar Cliente");
            builder.setPositiveButton("OK", new OnClickListenerOKDeletar(position));
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.show();
            return true;
        }
    }

    class OnClickListenerOKDeletar implements DialogInterface.OnClickListener {

        int position;

        OnClickListenerOKDeletar(int position){
            this.position = position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            final ProgressDialog mainDialog = ProgressDialog.show(MainActivity.this, "Aviso", "Aguarde, Deletando cliente");
            Call<ClienteIUGU> call =  clienteInterface.deletarCliente(customApplication.getClientes().get(position).getId());
            call.enqueue(new Callback<ClienteIUGU>() {
                @Override
                public void onResponse(Call<ClienteIUGU> call, Response<ClienteIUGU> response) {
                    if(response.isSuccessful()) {
                        mainDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Cliente deletado com sucesso",Toast.LENGTH_LONG).show();
                        customApplication.getClientes().remove(position);
                        atualizarListaClientes();
                    }
                    else {
                        Log.i(TAG,response.raw().toString());
                        onFailure(call,new Exception(response.message()));
                    }
                }

                @Override
                public void onFailure(Call<ClienteIUGU> call, Throwable t) {
                    Log.i(TAG,"onFailure");
                    Log.i(TAG,t.getMessage()+" "+call.request().url()+" "+call.request().toString());
                    mainDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Erro ao deletar clientes", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
