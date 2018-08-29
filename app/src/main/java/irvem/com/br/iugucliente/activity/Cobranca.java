package irvem.com.br.iugucliente.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import irvem.com.br.iugucliente.BuildConfig;
import irvem.com.br.iugucliente.R;
import irvem.com.br.iugucliente.domain.CobrancaRequestIUGU;
import irvem.com.br.iugucliente.domain.CobrancaResponseIUGU;
import irvem.com.br.iugucliente.network.CobrancaInterface;
import irvem.com.br.iugucliente.network.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cobranca extends AppCompatActivity implements TextWatcher{

    private static final String TAG = Cobranca.class.getSimpleName();

    private CobrancaInterface cobrancaInterface = ServiceGenerator.getCobrancaInterface();

    @BindView(R.id.edtValor)
    EditText edtValor;
    @BindView(R.id.edtDescricao)
    EditText edtDescricao;
    @BindView(R.id.tVNumeroOculto)
    TextView tVNumeroOculto;

    private String idPagamento;
    private String emailCliente;
    private String numeroOculto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobranca);
        ButterKnife.bind(this);

        idPagamento = this.getIntent().getStringExtra(DetalhesCliente.KEY_ID_PAGAMENTO);
        if(idPagamento==null)
            throw new NullPointerException("Id do pagamento nao foi encontrado!");
        emailCliente = this.getIntent().getStringExtra(DetalhesCliente.KEY_EMAIL_CLIENTE);
        if(emailCliente==null)
            throw new NullPointerException("email do cliente nao foi encontrado!");
        numeroOculto = this.getIntent().getStringExtra(DetalhesCliente.KEY_NUMERO_OCULTO);
        if(numeroOculto==null)
            throw new NullPointerException("numero do cartao oculto nao foi encontrado!");

        tVNumeroOculto.setText(numeroOculto);

        Log.i(TAG,idPagamento+" "+emailCliente+" "+numeroOculto);

        edtValor.addTextChangedListener(this);

        if(BuildConfig.DEBUG) {
            edtDescricao.setText("Viajem");
            edtValor.setText("100");
        }
    }


    public void comprarButton (View v) {

        StringBuilder cashAmountBuilder = new StringBuilder(edtValor.getText().toString());

        int indexCifrao = cashAmountBuilder.indexOf("$");
        if(indexCifrao!=-1)
            cashAmountBuilder.deleteCharAt(indexCifrao);

        int indexPonto = cashAmountBuilder.indexOf(".");
        if(indexPonto!=-1)
            cashAmountBuilder.deleteCharAt(indexPonto);

        Log.i(TAG,cashAmountBuilder.toString());

        CobrancaRequestIUGU cobranca = CobrancaRequestIUGU.builder()
                .customer_payment_method_id(idPagamento)
                .email(emailCliente)
                .description(edtDescricao.getText().toString())
                .quantity(1)
                .price_cents(Integer.parseInt(cashAmountBuilder.toString()))
                .build();

        cobrancaInterface.criarCobranca(cobranca).enqueue(criarCallbackCobrancaIUGU());
    }

    private Callback<CobrancaResponseIUGU> criarCallbackCobrancaIUGU(){
        final ProgressDialog dialog = ProgressDialog.show(this, "Aviso", "Aguarde, efetuando compra");
        return new Callback<CobrancaResponseIUGU>() {
            @Override
            public void onResponse(Call<CobrancaResponseIUGU> call, Response<CobrancaResponseIUGU> response) {
                if(response.isSuccessful()){
                    Log.i(TAG,response.body().toString());
                    CobrancaResponseIUGU cobrancaResponse = response.body();
                    dialog.dismiss();
                    Toast.makeText(Cobranca.this, "Compra efetuado com sucesso",Toast.LENGTH_LONG).show();
                }
                else {
                    Log.i(TAG,response.raw().toString());
                    onFailure(call,new Exception(response.message()));
                }
            }
            @Override
            public void onFailure(Call<CobrancaResponseIUGU> call, Throwable t) {
                Log.i(TAG,"onFailure");
                Log.i(TAG,t.getMessage()+" "+call.request().url()+" "+call.request().toString());
                dialog.dismiss();
                Toast.makeText(Cobranca.this, "Erro ao efetuar compra", Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start,
                              int before, int count) {
        if(!s.toString().matches("^\\$(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$"))
        {
            String userInput= ""+s.toString().replaceAll("[^\\d]", "");
            StringBuilder cashAmountBuilder = new StringBuilder(userInput);

            while (cashAmountBuilder.length() > 3 && cashAmountBuilder.charAt(0) == '0') {
                cashAmountBuilder.deleteCharAt(0);
            }
            while (cashAmountBuilder.length() < 3) {
                cashAmountBuilder.insert(0, '0');
            }
            cashAmountBuilder.insert(cashAmountBuilder.length()-2, '.');
            cashAmountBuilder.insert(0, '$');

            edtValor.setTextKeepState(cashAmountBuilder.toString());
            Selection.setSelection(edtValor.getText(), cashAmountBuilder.toString().length());
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
