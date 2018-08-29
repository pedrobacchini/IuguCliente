package irvem.com.br.iugucliente.validacao;


public class FormatTools {

//    static private boolean IsCelPhone (String Phone){
//        return (Phone.substring(2,3) == "6" ||
//                Phone.substring(2,3) == "7" ||
//                Phone.substring(2,3) == "8" ||
//                Phone.substring(2,3) == "9");
//    }
    static public boolean hasPrefix (String Phone){
        return (Phone.startsWith("012") || // Algar Telecom / CTBC
                Phone.startsWith("015") || // VIVO
                Phone.startsWith("021") || // Claro
                Phone.startsWith("014") || // OI
                Phone.startsWith("041") || // TIM
                Phone.startsWith("023") || // Intelig-TIM
                Phone.startsWith("032") || // Convergia
                Phone.startsWith("043") || // Sercomtel
                Phone.startsWith("031") || // OI-Telemar
                Phone.startsWith("017")); // Aerotech
    }
    static public String OnlyDigits (String input){
        input = input.replaceAll("\\+55", "");
        return input.replaceAll("[^\\d]", "");
    }
    static public String OnlyPhoneNumberAndDDD (String PhoneNumber){
        PhoneNumber = OnlyDigits(PhoneNumber);

        if(PhoneNumber.length() < 3)
            return PhoneNumber;

        // prefixos operadoras
        if(hasPrefix(PhoneNumber)){
            PhoneNumber = PhoneNumber.substring(3);
        }

        if(PhoneNumber.startsWith("0")){
            PhoneNumber = PhoneNumber.substring(1);
        }
        if(PhoneNumber.startsWith("90")){
            PhoneNumber = PhoneNumber.substring(4);
        }

        return PhoneNumber;
    }
    static public String FormatPhoneNumber (String PhoneNumber){
        PhoneNumber = OnlyPhoneNumberAndDDD(PhoneNumber);

        if(PhoneNumber.length() < 3)
            return PhoneNumber;

        if(PhoneNumber.length() > 7){
            String FormattedPhoneNumber = PhoneNumber;
            // celular
//            if(IsCelPhone(PhoneNumber)){
            FormattedPhoneNumber = "(" + PhoneNumber.substring(0,2) + ") " + PhoneNumber.substring(2, 3) + " " + PhoneNumber.substring(3, 7) + "-" + PhoneNumber.substring(7);
//            }
//            // fixo
//            else{
//                FormattedPhoneNumber = "(" + PhoneNumber.substring(0,2) + ") " + PhoneNumber.substring(2, 6) + "-" + PhoneNumber.substring(6);
//            }
            PhoneNumber = FormattedPhoneNumber;

        }else {
            PhoneNumber = "(" + PhoneNumber.substring(0,2) + ") " + PhoneNumber.substring(2);
        }

        return PhoneNumber;
    }
    static public String FormatCPF(String CPF){
        if(CPF.length() <= 3)
            return CPF;

        CPF = CPF.replaceAll("\\.", "");
        CPF = CPF.replaceAll("\\-", "");

        String FormattedCPF = CPF;

        if(CPF.length() <= 6){
            FormattedCPF = CPF.substring(0,3) + "." + CPF.substring(3);
            return FormattedCPF;
        }

        if(CPF.length() <= 9){
            FormattedCPF = CPF.substring(0,3) + "." + CPF.substring(3, 6) + "." + CPF.substring(6);
            return FormattedCPF;
        }

        FormattedCPF = CPF.substring(0,3) + "." + CPF.substring(3, 6) + "." + CPF.substring(6, 9) + "-" + CPF.substring(9);
        return FormattedCPF;
    }
    static public String FormatData(String DATA){
        if(DATA.length() <= 2)
            return DATA;

        DATA = OnlyDigits(DATA);

        String FormattedDATA = DATA;

        if(DATA.length() <= 4){
            FormattedDATA = DATA.substring(0,2) + "/" + DATA.substring(2);
            return FormattedDATA;
        }

        FormattedDATA = DATA.substring(0,2) + "/" + DATA.substring(2, 4) + "/" + DATA.substring(4);
        return FormattedDATA;
    }
    static public String FormatEmail(String EMAIL){
        return (EMAIL.replaceAll("\\s", ""));
    }


    public static int getPositionOfCursor(int start, int before, CharSequence current, CharSequence previous) {
        if (current == null || previous == null)
            return 0;

        int diff = current.length() - previous.length();
        start += (diff + before);
        start = start > current.length() ? current.length() : start;
        start = start < 0 ? 0 : start;

        return start;
    }
}
