package com.janady.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;

import static com.janady.database.model.Bluetooth.COL_MAC;

@Table("camera")
public class Camera implements Parcelable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id") // 指定列名
    private int id;

    public static final String COL_SN = "sn";
    public static final String COL_DEVID = "devId";

    @Column(COL_SN) // 指定列名
    @Unique
    public String sn;
    @Column(COL_DEVID) // 指定列名
    @Unique
    public int devId;
    public String mac;
    public String serialNo;
    public String devIp;
    public String loginName;
    public String loginPsw;
    public FunDevice camDevice;
    public boolean isFirst = true;
    public int type;
    public String sceneName;
    public boolean isOnline = true;
    @Unique
    public String name;
    @Mapping(Relation.ManyToMany)
    public ArrayList<Door> doorList;

    public Camera() {
    }

    protected Camera(Parcel in) {
        id = in.readInt();
        sn = in.readString();
        devId = in.readInt();
        mac = in.readString();
        serialNo = in.readString();
        devIp = in.readString();
        loginName = in.readString();
        loginPsw = in.readString();
        isFirst = in.readByte() != 0;
        sceneName = in.readString();
        isOnline = in.readByte() != 0;
        name = in.readString();
    }

    public static final Creator<Camera> CREATOR = new Creator<Camera>() {
        @Override
        public Camera createFromParcel(Parcel in) {
            return new Camera(in);
        }

        @Override
        public Camera[] newArray(int size) {
            return new Camera[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(sn);
        dest.writeInt(devId);
        dest.writeString(mac);
        dest.writeString(serialNo);
        dest.writeString(devIp);
        dest.writeString(loginName);
        dest.writeString(loginPsw);
        dest.writeByte((byte) (isFirst ? 1 : 0));
        dest.writeString(sceneName);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeString(name);
    }
}
