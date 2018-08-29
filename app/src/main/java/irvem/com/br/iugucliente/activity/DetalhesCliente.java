package irvem.com.br.iugucliente.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import irvem.com.br.iugucliente.R;
import irvem.com.br.iugucliente.application.CustomApplication;
import irvem.com.br.iugucliente.domain.ClienteIUGU;
import irvem.com.br.iugucliente.domain.PagamentoIUGU;
import irvem.com.br.iugucliente.network.ClienteInterface;
import irvem.com.br.iugucliente.network.PagamentoInterface;
import irvem.com.br.iugucliente.network.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalhesCliente extends AppCompatActivity {

    private static final String TAG = DetalhesCliente.class.getSimpleName();

    private PagamentoInterface pagamentoInterface = ServiceGenerator.getPagamentoInterface();
    private ClienteInterface clienteInterface = ServiceGenerator.getClienteInterface();

    //Para tela de cartao de credito
    public static final String KEY_ID_CLIENTE = "id_cliente";

    //Para tela de cobranca
    public static final String KEY_ID_PAGAMENTO = "id_pagamento";
    public static final String KEY_EMAIL_CLIENTE = "email_cliente";
    public static final String KEY_NUMERO_OCULTO = "numero_oculto";

    private static final int NOVO_CARTAO_RESULT_CODE = 2000;

    @BindView(R.id.textView_Nome)
    TextView textViewNome;
    @BindView(R.id.textView_Email)
    TextView textViewEmail;
    @BindView(R.id.textView_nenhum_pagamento)
    TextView tvNenhumPagamento;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.list_payments)
    ListView listView;

    MenuItem menuItemCobranca;

    private int indexCliente;

    private CustomApplication customApplication;

    private List<PagamentoIUGU> formasPagamento = new ArrayList<PagamentoIUGU>();

    private PagamentoIUGU formaPagamentoPadrao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_cliente);
        ButterKnife.bind(this);

        customApplication = (CustomApplication) getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        indexCliente = this.getIntent().getIntExtra(MainActivity.INDEX_CLIENTE,-1);
        if(indexCliente != -1) {
            Log.i(TAG,"Cliente index: "+indexCliente);
            configurarActivity();
        }
        else
            throw new NullPointerException("Cliente não foi selecionado");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListenerNovoCartao());

        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new OnClickListenerBackPressed());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalhes_cliente, menu);
        menuItemCobranca = menu.findItem(R.id.miCobranca);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.miEditar) {
            configurarBotaoEditar();
            return true;
        } else if (id == R.id.miCobranca) {
            configurarBotaoCobranca();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configurarBotaoEditar(){
        Intent intent = new Intent(this, NovoCliente.class);
        intent.putExtra(MainActivity.INDEX_CLIENTE, indexCliente);
        startActivity(intent);
    }

    private void configurarBotaoCobranca(){
        Intent intent = new Intent(this, Cobranca.class);
        intent.putExtra(KEY_ID_PAGAMENTO,customApplication.getCliente(indexCliente).getDefault_payment_method_id());
        intent.putExtra(KEY_EMAIL_CLIENTE,customApplication.getCliente(indexCliente).getEmail());
        intent.putExtra(KEY_NUMERO_OCULTO,formaPagamentoPadrao.getData().getDisplay_number());
        startActivity(intent);
    }

    class OnClickListenerNovoCartao implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(DetalhesCliente.this, CreditCardForm.class);
            intent.putExtra(KEY_ID_CLIENTE, customApplication.getCliente(indexCliente).getId());
            startActivityForResult(intent, NOVO_CARTAO_RESULT_CODE);
        }
    }

    class OnClickListenerBackPressed implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==NOVO_CARTAO_RESULT_CODE && resultCode==RESULT_OK){
            Log.i(TAG,"onActivityResult criacao de cartao");
            PagamentoIUGU pagamentoIUGU = data.getParcelableExtra(CreditCardForm.KEY_NOVO_PAGAMENTO);
            customApplication.getCliente(indexCliente).setDefault_payment_method_id(pagamentoIUGU.getId());
            adicionarFormaPagamento(0, pagamentoIUGU);
        }
    }

    private void configurarActivity(){
        Log.i(TAG,"configurarActivity");
        atualizarCliente();
        if(customApplication.getCliente(indexCliente).getDefault_payment_method_id()!=null)
        {
            Call<List<PagamentoIUGU>> call = pagamentoInterface.listaPagamentos(customApplication.getCliente(indexCliente).getId());
            call.enqueue(new Callback<List<PagamentoIUGU>>() {
                @Override
                public void onResponse(Call<List<PagamentoIUGU>> call, Response<List<PagamentoIUGU>> response) {
                    if(response.isSuccessful()){
                        formasPagamento = response.body();
                        atualizarListaFormasPagamento();
                    }
                    else {
                        Log.i(TAG,response.raw().toString());
                        onFailure(call,new Exception(response.message()));
                    }
                }
                @Override
                public void onFailure(Call<List<PagamentoIUGU>> call, Throwable t) {
                    Log.i(TAG,"onFailure");
                    Log.i(TAG,t.getMessage()+" "+call.request().url()+" "+call.request().toString());
                    progressBar.setVisibility(View.INVISIBLE);
                    tvNenhumPagamento.setVisibility(View.VISIBLE);
                    tvNenhumPagamento.setText("Erro ao buscar formas de pagamento");
                }
            });
        }
        else {
            atualizarListaFormasPagamento();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
        atualizarCliente();
    }

    private void atualizarCliente(){
        textViewNome.setText(customApplication.getCliente(indexCliente).getName());
        textViewEmail.setText(customApplication.getCliente(indexCliente).getEmail());
    }

    private void adicionarFormaPagamento(int position,PagamentoIUGU pagamento){
        formasPagamento.add(position,pagamento);
        atualizarListaFormasPagamento();
    }

    private void removerFormaPagamento(int position){
        formasPagamento.remove(position);
        atualizarListaFormasPagamento();
    }

    private void atualizarListaFormasPagamento(){
        if(formasPagamento.size()==0){
            progressBar.setVisibility(View.INVISIBLE);
            tvNenhumPagamento.setVisibility(View.VISIBLE);
            listView.setAdapter(null);
        }
        else {
            tvNenhumPagamento.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            listView.setAdapter(criarAdaptadorDePagamentos());
        }
    }

    private ArrayAdapter<PagamentoIUGU> criarAdaptadorDePagamentos(){
        final PagamentoIUGU[] pagamentosArray = formasPagamento.toArray(new PagamentoIUGU[formasPagamento.size()]);
        return new ArrayAdapter<PagamentoIUGU>(DetalhesCliente.this, 0, pagamentosArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.custom_list_item_pagamento, null, false);

                ImageView tipoCartao        = (ImageView) convertView.findViewById(R.id.imagem_cartao);
                TextView numeroCartao       = (TextView) convertView.findViewById(R.id.label_numero_cartao);
                TextView dataExpiraCartao   = (TextView) convertView.findViewById(R.id.label_data_expira);

                if(pagamentosArray[position].getData().getBrand().equals("VISA"))
                    tipoCartao.setImageDrawable(tipoCartao.getResources().getDrawable(com.braintreepayments.cardform.R.drawable.bt_ic_visa));
                else
                    tipoCartao.setImageDrawable( tipoCartao.getResources().getDrawable(com.braintreepayments.cardform.R.drawable.bt_ic_mastercard));

                numeroCartao.setText(pagamentosArray[position].getData().getDisplay_number());
                dataExpiraCartao.setText(pagamentosArray[position].getData().getMonth()+"/"+pagamentosArray[position].getData().getYear());

                ImageView cartaoPadrao = (ImageView) convertView.findViewById(R.id.imageView_cartao_padrao);

                if(pagamentosArray[position].getId().equals(customApplication.getCliente(indexCliente).getDefault_payment_method_id())) {
                    menuItemCobranca.setVisible(true);
                    formaPagamentoPadrao = pagamentosArray[position];
                    cartaoPadrao.setVisibility(View.VISIBLE);
                }

                ImageButton buttonOpcoes = (ImageButton) convertView.findViewById(R.id.buttonOpcoes);
                buttonOpcoes.setOnClickListener(criarOnClickListenerPagamento(position));

                return convertView;
            }
        };
    }

    private View.OnClickListener criarOnClickListenerPagamento(final int position){
        return new View.OnClickListener() {
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(DetalhesCliente.this, v);
                popup.setOnMenuItemClickListener(criarOnMenuItemClickListenerPagamento(position));
                popup.inflate(R.menu.menu_pagamento);
                popup.show();
            }
        };
    }

    private PopupMenu.OnMenuItemClickListener criarOnMenuItemClickListenerPagamento(final int position){
        return new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_padrao:
                        configurarBotaoCartaoPadrao(formasPagamento.get(position).getId());
                        return true;
                    case R.id.menu_deletar:
                        configurarBotaoRemover(position);
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private void configurarBotaoCartaoPadrao(final String idFormaPagamento){
        if(!idFormaPagamento.equals(customApplication.getCliente(indexCliente).getDefault_payment_method_id())){
            final ProgressDialog dialog = ProgressDialog.show(this, "Aviso", "Aguarde, definindo cartao padrao");
            customApplication.getCliente(indexCliente).setDefault_payment_method_id(idFormaPagamento);
            Call<ClienteIUGU> call = clienteInterface.atualizarCliente(customApplication.getCliente(indexCliente), customApplication.getCliente(indexCliente).getId());
            call.enqueue(new Callback<ClienteIUGU>() {
                @Override
                public void onResponse(Call<ClienteIUGU> call, Response<ClienteIUGU> response) {
                    if(response.isSuccessful()){
                        atualizarListaFormasPagamento();
                        dialog.dismiss();
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
                    Toast.makeText(DetalhesCliente.this, "Erro definir cartao padrao", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetalhesCliente.this);
            builder.setTitle("definir cartão padrão");
            builder.setMessage("este já o seu cartão padrão");
            builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    private void configurarBotaoRemover(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetalhesCliente.this);
        builder.setTitle("remover cartão");
        if(formasPagamento.get(position).getId().equals(customApplication.getCliente(indexCliente).getDefault_payment_method_id())){
            builder.setMessage("voce não pode remover um cartão padrão");
            builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
        }
        else {
            builder.setMessage("deseja remover o cartão: \n " + formasPagamento.get(position).getData().getDisplay_number() + " ?");
            builder.setPositiveButton("OK", criarOnClickListenerOKDeletar(position));
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
        }
        builder.show();
    }

    private DialogInterface.OnClickListener criarOnClickListenerOKDeletar(final int position) {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, final int index) {
                final ProgressDialog mainDialog = ProgressDialog.show(DetalhesCliente.this, "Aviso", "Aguarde, Deletando cartão");
                Call<PagamentoIUGU> call =  pagamentoInterface.deletarPagamento(customApplication.getCliente(indexCliente).getId(),formasPagamento.get(position).getId());
                call.enqueue(new Callback<PagamentoIUGU>() {
                    @Override
                    public void onResponse(Call<PagamentoIUGU> call, Response<PagamentoIUGU> response) {
                        if(response.isSuccessful()) {
                            mainDialog.dismiss();
                            Toast.makeText(DetalhesCliente.this, "Cartão deletado com sucesso",Toast.LENGTH_LONG).show();
                            removerFormaPagamento(position);
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
                        mainDialog.dismiss();
                        Toast.makeText(DetalhesCliente.this, "Erro ao deletar cartão", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
    }
}
