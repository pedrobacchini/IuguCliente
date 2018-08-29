package irvem.com.br.iugucliente.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagamentoIUGU implements Parcelable {

    @Getter
    private String id;
    @Getter
    private String description = "irvem_cartao";
    @Getter
    private DadosPagamento data;
    @Getter @Setter
    private String token;
    @Getter
    private boolean set_as_default = true;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeParcelable(data,flags);
        dest.writeString(token);
    }

    public static final Parcelable.Creator<PagamentoIUGU> CREATOR = new Parcelable.Creator<PagamentoIUGU>() {
        public PagamentoIUGU createFromParcel(Parcel in) {
            return new PagamentoIUGU(in);
        }

        public PagamentoIUGU[] newArray(int size) {
            return new PagamentoIUGU[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private PagamentoIUGU(Parcel in) {
        id = in.readString();
        description = in.readString();
        data = in.readParcelable(DadosPagamento.class.getClassLoader());
        token = in.readString();
    }

    public PagamentoIUGU() {};
}


