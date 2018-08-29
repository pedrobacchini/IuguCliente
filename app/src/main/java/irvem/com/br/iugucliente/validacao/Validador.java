package irvem.com.br.iugucliente.validacao;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import irvem.com.br.iugucliente.R;
import irvem.com.br.iugucliente.utils.ValidadorUtils;
import lombok.Getter;

public class Validador {

    private EditText editText;
    private type info;
    @Getter
    private boolean valido;
    private boolean isUpdating = false;
    private Context context;

    String textBefore = "";
    String currentText = "";

    public Validador(EditText editText, type info, Context context){
        this.editText = editText;
        this.info = info;
        this.valido = false;
        this.context = context;
        AddValidationEvent();
    }

    public String getText (boolean formatted) {
        switch (info){
            case CPF:
                return formatted ?  FormatTools.FormatCPF(editText.getText().toString()) : FormatTools.OnlyDigits (editText.getText().toString());
            case DATA:
                return formatted ?  FormatTools.FormatData(editText.getText().toString()) : FormatTools.OnlyDigits (editText.getText().toString());
            case TELEFONE:
                return formatted ?  FormatTools.FormatPhoneNumber(editText.getText().toString()) : FormatTools.OnlyDigits (editText.getText().toString());
            case SENHA:
            case EMAIL:
            case SMS:
            case NOME_COMPLETO:
            case NOME:
                return editText.getText().toString();
        }

        return "";
    }

    public void validar (){
        handler.removeCallbacks(runnable);
        switch (info){
            case CPF: {
                valido = ValidadorUtils.isCPFValid(FormatTools.OnlyDigits(editText.getText().toString()));
                break;
            }
            case DATA: {
                try {
                    Date dateConverted = ValidadorUtils.isDateValid(editText.getText().toString());
                    Date currentDate = new Date();
                    valido = editText.getText().toString().length() >= 10 && currentDate.after(dateConverted);
                }catch (ParseException ex){
                    valido = false;
                }
                break;
            }
            case NOME_COMPLETO: {
                valido = ValidadorUtils.nomeCompletoIsValid(editText.getText().toString());
                break;
            }
            case NOME: {
                // tirando todos os espaços (para evitar que o usuario digite só espaço no nome)
                String NOME = editText.getText().toString().replaceAll("\\s", "");
                valido = (NOME.length() > 0);
                break;
            }
            case TELEFONE: {
                try {
                    String PhoneNumber = FormatTools.OnlyPhoneNumberAndDDD(editText.getText().toString());
                    valido = ValidadorUtils.celularIsValid(Long.parseLong(PhoneNumber));
                }catch (NumberFormatException e){
                    valido = false;
                }
                break;
            }
            case SMS: {
                valido = editText.getText().toString().length() == 4;
                break;
            }
            case EMAIL: {
                valido = ValidadorUtils.emailIsValid(editText.getText().toString());
                break;
            }
            case SENHA: {
                valido = !editText.getText().toString().isEmpty()
                        && editText.getText().toString().length() > 7
                        && editText.getText().toString().length() < 21;
                break;
            }
        }
        if(!valido) {
            editText.setError(info.getMensagemErro());
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editText.startAnimation(shake);
            Vibrator rr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            rr.vibrate(50);//'30' é o tempo em milissegundos, é basicamente o tempo de duração da vibração. portanto, quanto maior este numero, mais tempo de vibração você irá tremer
        }
    }

    private final int interval = 2000; // 1 Second
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable(){
        public void run() {
            validar();
        }
    };

    private void AddValidationEvent (){
        List<InputFilter> filtersList = new ArrayList<InputFilter>();
        for (InputFilter filter : editText.getFilters()) {
            filtersList.add(filter);
        }

        filtersList.add(new EmojiExcludeFilter());

        InputFilter[] filtersArray = new InputFilter[filtersList.size()];
        filtersArray = filtersList.toArray(filtersArray);

        editText.setFilters(filtersArray);
        editText.addTextChangedListener(getValidationTextWatcher(this));
    }

    private TextWatcher getValidationTextWatcher (final Validador validador){
        return new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                isUpdating = true;

                if (info == type.CPF)
                { editText.setText(FormatTools.FormatCPF(editText.getText().toString())); }
                else if (info == type.DATA)
                { editText.setText(FormatTools.FormatData(editText.getText().toString())); }
                else if (info == type.EMAIL)
                { editText.setText(FormatTools.FormatEmail(editText.getText().toString().toLowerCase())); }
                else if (info == type.TELEFONE)
                { editText.setText(FormatTools.FormatPhoneNumber(editText.getText().toString())); }
                else { isUpdating = false; }

                //Adjust cursor
                currentText = editText.getText().toString();
                editText.setSelection(FormatTools.getPositionOfCursor(start, before, currentText, textBefore));
                textBefore = currentText;

                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, interval);
            }
        };
    }

    private class EmojiExcludeFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    }

    public enum type {
        CPF("CPF invalido!"),
        DATA("Data invalida!"),
        NOME_COMPLETO("Nome invalido!"),
        NOME("Nome invalido!"),
        TELEFONE("Telefone invalido!"),
        SMS("SMS invalido!"),
        EMAIL("Email invalido"),
        SENHA("Senha invalida");

        private String mensagemErro;

        type(String mensagemErro) { this.mensagemErro = mensagemErro; }

        public String getMensagemErro() { return mensagemErro; }
    }
}
