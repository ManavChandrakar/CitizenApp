package com.pris.citizenapp.adapters;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by manav on 13/4/17.
 */

public class Replies implements Parcelable {

     public String  id;
     public String grievance_id;
     public String file;
     public String reply;
     public String status;
     public String username;
     public String timestamp;

     public Replies(Parcel in) {
          id = in.readString();
          grievance_id = in.readString();
          reply = in.readString();
          status = in.readString();
          username = in.readString();
          timestamp = in.readString();
          file=in.readString();
     }

     public Replies()
     {

     }

     public static final Creator<Replies> CREATOR = new Creator<Replies>() {
          @Override
          public Replies createFromParcel(Parcel in) {
               return new Replies(in);
          }

          @Override
          public Replies[] newArray(int size) {
               return new Replies[size];
          }
     };

     @Override
     public int describeContents() {
          return 0;
     }

     @Override
     public void writeToParcel(Parcel parcel, int i) {
          parcel.writeString(id);
          parcel.writeString(grievance_id);
          parcel.writeString(reply);
          parcel.writeString(status);
          parcel.writeString(username);
          parcel.writeString(timestamp);
          parcel.writeString(file);
     }
}
