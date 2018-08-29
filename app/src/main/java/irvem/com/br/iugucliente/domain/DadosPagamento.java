package irvem.com.br.iugucliente.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DadosPagamento implements Parcelable {

    @Getter
    private String brand;
    @Getter
    private String holder_name;
    @Getter
    private String display_number;
    @Getter
    private int month;
    @Getter
    private int year;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(brand);
        dest.writeString(holder_name);
        dest.writeString(display_number);
        dest.writeInt(month);
        dest.writeInt(year);
    }

    public static final Parcelable.Creator<DadosPagamento> CREATOR = new Parcelable.Creator<DadosPagamento>() {
        public DadosPagamento createFromParcel(Parcel in) {
            return new DadosPagamento(in);
        }

        public DadosPagamento[] newArray(int size) {
            return new DadosPagamento[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private DadosPagamento(Parcel in) {
        brand = in.readString();
        holder_name = in.readString();
        display_number = in.readString();
        month = in.readInt();
        year = in.readInt();
    }

    public DadosPagamento() {}
}
