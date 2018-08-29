package irvem.com.br.iugucliente.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import irvem.com.br.iugucliente.R;
import irvem.com.br.iugucliente.application.CustomApplication;
import irvem.com.br.iugucliente.domain.ClienteIUGU;
import irvem.com.br.iugucliente.network.ClienteInterface;
import irvem.com.br.iugucliente.network.ServiceGenerator;
import irvem.com.br.iugucliente.validacao.Validador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NovoCliente extends AppCompatActivity {

    public static final String TAG = NovoCliente.class.getSimpleName();

    private ClienteInterface clienteInterface = ServiceGenerator.getClienteInterface();

    @BindView(R.id.edtNome)
    EditText edtNome;
    @BindView(R.id.edtEmail)
    EditText edtEmail;

    private int indexCliente;

    Validador validadorNome, validadorEmail;

    private CustomApplication customApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_cliente);
        ButterKnife.bind(this);

        customApplication = (CustomApplication) getApplicationContext();

        validadorNome = new Validador(edtNome, Validador.type.NOME, this);
        validadorEmail = new Validador(edtEmail, Validador.type.EMAIL, this);

        indexCliente = this.getIntent().getIntExtra(MainActivity.INDEX_CLIENTE,-1);
        if(indexCliente != -1) {
            setTitle("Atualizar Cliente");
            edtNome.setText(customApplication.getCliente(indexCliente).getName());
            edtEmail.setText(customApplication.getCliente(indexCliente).getEmail());
        }
    }

    public void salvarOrAtualizarButton (View v) {
        if (!validadorNome.isValido() || !validadorEmail.isValido()) {
            if(!validadorNome.isValido())
                validadorNome.validar();
            if (!validadorEmail.isValido())
                validadorEmail.validar();
        }

        if(validadorNome.isValido() && validadorEmail.isValido()){

            Call<ClienteIUGU> call;
            if(indexCliente!=-1) {
                customApplication.getCliente(indexCliente).setName(edtNome.getText().toString());
                customApplication.getCliente(indexCliente).setEmail(edtEmail.getText().toString());
                call = clienteInterface.atualizarCliente(customApplication.getCliente(indexCliente), customApplication.getCliente(indexCliente).getId());
            }
            else {
                ClienteIUGU cliente = new ClienteIUGU();
                cliente.setName(edtNome.getText().toString());
                cliente.setEmail(edtEmail.getText().toString());
                call = clienteInterface.adcionarCliente(cliente);
            }

            final ProgressDialog dialog = ProgressDialog.show(this, "Aviso", "Aguarde, salvando cliente");
            call.enqueue(new Callback<ClienteIUGU>() {
                @Override
                public void onResponse(Call<ClienteIUGU> call, Response<ClienteIUGU> response) {
                    if(response.isSuccessful()){
                        ClienteIUGU novoCliente = response.body();
                        if(indexCliente!=-1)
                            customApplication.setCliente(indexCliente, novoCliente);
                        else
                            customApplication.getClientes().add(0,novoCliente);
                        finish();
                        dialog.dismiss();
                        Toast.makeText(NovoCliente.this, "Cliente salvo com sucesso",Toast.LENGTH_LONG).show();
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
                    dialog.dismiss();
                    Toast.makeText(NovoCliente.this, "Erro ao salvar cliente", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
