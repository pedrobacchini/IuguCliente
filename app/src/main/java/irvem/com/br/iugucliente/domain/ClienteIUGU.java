package irvem.com.br.iugucliente.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClienteIUGU implements Parcelable {

    @Getter
    private String id;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String default_payment_method_id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(default_payment_method_id);
    }

    public static final Parcelable.Creator<ClienteIUGU> CREATOR = new Parcelable.Creator<ClienteIUGU>() {
        public ClienteIUGU createFromParcel(Parcel in) {
            return new ClienteIUGU(in);
        }

        public ClienteIUGU[] newArray(int size) {
            return new ClienteIUGU[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private ClienteIUGU(Parcel in) {
        id = in.readString();
        email = in.readString();
        name = in.readString();
        default_payment_method_id = in.readString();
    }

    public ClienteIUGU() {};

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClienteIUGU other = (ClienteIUGU) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
