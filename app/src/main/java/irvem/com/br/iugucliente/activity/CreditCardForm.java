package irvem.com.br.iugucliente.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;

import butterknife.BindView;
import butterknife.ButterKnife;
import irvem.com.br.iugucliente.BuildConfig;
import irvem.com.br.iugucliente.R;
import irvem.com.br.iugucliente.domain.PagamentoIUGU;
import irvem.com.br.iugucliente.domain.TokenIUGU;
import irvem.com.br.iugucliente.network.PagamentoInterface;
import irvem.com.br.iugucliente.network.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreditCardForm extends AppCompatActivity {

    private static final String TAG = CreditCardForm.class.getSimpleName();

    private PagamentoInterface pagamentoInterface = ServiceGenerator.getPagamentoInterface();

    public static final String KEY_NOVO_PAGAMENTO = "novo_pagamento";

    @BindView(R.id.card_form)       CardForm cardForm;
    @BindView(R.id.btnBuy)          Button salvar_button;
    @BindView(R.id.edtPrimeiroNome) EditText edtPrimeiroNome;
    @BindView(R.id.edtUltimoNome)   EditText edtUltimoNome;

    private String idCliente;

    private PagamentoIUGU pagamentoIUGU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_form);
        ButterKnife.bind(this);

        idCliente = this.getIntent().getStringExtra(DetalhesCliente.KEY_ID_CLIENTE);
        if(idCliente==null)
            throw new NullPointerException("Id do cliente nao foi encontrado!");

        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .setup(CreditCardForm.this);

        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        if(BuildConfig.DEBUG) {
            cardForm.getCardEditText().setText("4111111111111111");
            cardForm.getCvvEditText().setText("470");
            edtPrimeiroNome.setText("Pedro");
            edtUltimoNome.setText("Bacchini");
            cardForm.getExpirationDateEditText().setText("082018");
        }
    }

    public void salvarCartao (View v) {
        if (cardForm.isValid()) {
            final TokenIUGU tokenIUGU = TokenIUGU.builder()
                    .test(true)
                    .metodo(TokenIUGU.methodType.credit_card)
                    .number(cardForm.getCardNumber())
                    .verification_value(Integer.parseInt(cardForm.getCvv()))
                    .first_name(edtPrimeiroNome.getText().toString())
                    .last_name(edtUltimoNome.getText().toString())
                    .month(Integer.parseInt(cardForm.getExpirationMonth()))
                    .year(Integer.parseInt(cardForm.getExpirationYear()))
                    .build();

            pagamentoInterface.criarToken(tokenIUGU).enqueue(criarCallbackTokenIUGU());
        }else {
            Toast.makeText(CreditCardForm.this, "Por favor complete o formulario", Toast.LENGTH_LONG).show();
        }
    }

    private Callback<TokenIUGU> criarCallbackTokenIUGU(){
        final ProgressDialog dialog = ProgressDialog.show(this, "Aviso", "Aguarde, criando token");
        return new Callback<TokenIUGU>() {
            @Override
            public void onResponse(Call<TokenIUGU> call, Response<TokenIUGU> response) {
                if(response.isSuccessful()){
                    Log.i(TAG,response.body().toString());
                    pagamentoIUGU = new PagamentoIUGU();
                    pagamentoIUGU.setToken(response.body().getId());
                    pagamentoInterface.adicionarPagamento(idCliente, pagamentoIUGU).enqueue(criarCallbackPagamentoIUGU(dialog));
                }
                else {
                    Log.i(TAG,response.raw().toString());
                    onFailure(call,new Exception(response.message()));
                }
            }
            @Override
            public void onFailure(Call<TokenIUGU> call, Throwable t) {
                Log.i(TAG,"onFailure");
                Log.i(TAG,t.getMessage()+" "+call.request().url()+" "+call.request().toString());
                dialog.dismiss();
                Toast.makeText(CreditCardForm.this, "Erro ao criar token", Toast.LENGTH_LONG).show();
            }
        };
    }

    private Callback<PagamentoIUGU> criarCallbackPagamentoIUGU(final ProgressDialog dialog){
        dialog.setMessage("Aguarde, salvando cartao");
        return new Callback<PagamentoIUGU>() {
            @Override
            public void onResponse(Call<PagamentoIUGU> call, Response<PagamentoIUGU> response) {
                if(response.isSuccessful()){
                    Log.i(TAG,response.body().toString());
                    Intent intent = new Intent();
                    intent.putExtra(KEY_NOVO_PAGAMENTO, response.body());
                    setResult(RESULT_OK, intent);
                    finish();
                    dialog.dismiss();
                }
                else {
                    Log.i(TAG,response.raw().toString());
                    onFailure(call,new Exception(response.message()));
                }
            }
            @Override
            public void onFailure(Call<PagamentoIUGU> call, Throwable t) {
                Log.i(TAG,"onFailure");
                Log.i(TAG,t.getMessage()+" "+call.request().url()+" "+call.request().toString());
                dialog.dismiss();
                Toast.makeText(CreditCardForm.this, "Erro ao criar cartao", Toast.LENGTH_LONG).show();
            }
        };
    }
}
