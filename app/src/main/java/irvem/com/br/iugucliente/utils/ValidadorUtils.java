package irvem.com.br.iugucliente.utils;

import org.apache.commons.validator.routines.EmailValidator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;

public class ValidadorUtils {

    public static final String DATE_FORMAT = "dd/MM/yyyy";

    public static boolean isCPFValid(Long CPF) {
        String cpf = Long.toString(CPF);
        return isCPFValid(cpf);
    }

    public static boolean isCPFValid(String CPF) {
// considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF.equals("00000000000") || CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11))
            return(false);

        char dig10, dig11;
        int soma, indice, resto, num, peso;

// "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
// Calculo do 1o. Digito Verificador
            soma = 0;
            peso = 10;
            for (indice=0; indice<9; indice++) {
// converte o i-esimo caractere do CPF em um numero:
// por exemplo, transforma o caractere '0' no inteiro 0
// (48 eh a posicao de '0' na tabela ASCII)
                num = (int)(CPF.charAt(indice) - 48);
                soma = soma + (num * peso);
                peso = peso - 1;
            }

            resto = 11 - (soma % 11);
            if ((resto == 10) || (resto == 11))
                dig10 = '0';
            else dig10 = (char)(resto + 48); // converte no respectivo caractere numerico

// Calculo do 2o. Digito Verificador
            soma = 0;
            peso = 11;
            for(indice=0; indice<10; indice++) {
                num = (int)(CPF.charAt(indice) - 48);
                soma = soma + (num * peso);
                peso = peso - 1;
            }

            resto = 11 - (soma % 11);
            if ((resto == 10) || (resto == 11))
                dig11 = '0';
            else dig11 = (char)(resto + 48);

// Verifica se os digitos calculados conferem com os digitos informados.
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)))
                return(true);
            else return(false);
        } catch (InputMismatchException erro) {
            return(false);
        }
    }

    public static boolean senhaIsValid(String senha){
        if(senha.length()>7)
            return true;
        else
            return false;
    }

    public static boolean emailIsValid(String email){
        return EmailValidator.getInstance().isValid(email);
//        return !email.isEmpty() && email.contains("@") && email.contains(".");
    }

    public static boolean nomeIsValid(String nome){
        String NOME = nome.replaceAll("\\s", "");
        return (NOME.length() > 0);
    }

    public static boolean nomeCompletoIsValid(String nomeCompleto){
        String[] nomeCompletoArray = nomeCompleto.split("\\s");
        return (nomeCompletoArray.length > 1 && nomeCompleto.length() > 0);
    }

    public static Date isDateValid(String date) throws ParseException {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        df.setLenient(false);
        return df.parse(date);
    }

    public static boolean celularIsValid(Long celular){
        String celularString = Long.toString(celular);
        return celularIsValid(celularString);
    }

    public static boolean celularCompletoIsValid(Long celular){
        String celularString = Long.toString(celular);
        return celularIsValid(celularString.substring(2));
    }

    private static boolean celularIsValid(String celularString){
        return (celularString.length() == 11 && celularString.substring(2, 3).equals("9") && celularString.charAt(3) >= 54);//54 é o valor de 6 na tabela ascii
    }
}
