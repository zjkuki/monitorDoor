package com.janady.database.model;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lib.funsdk.support.models.FunDevType;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;

@Table("bluetooth")
public class Bluetooth {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id") // 指定列名
    private int id;

    public static final String COL_MAC = "mac";

    @Column(COL_MAC) // 指定列名
    @Unique
    public String mac;
    public int devType = FunDevType.EE_DEV_BLUETOOTH.getDevIndex();
    public String uuid;
    public String serviceUuid;
    public String writeUuid;
    public String notifyUuid;
    public String password;
    public boolean isFirst = true;
    public String name;
    public String sceneName;
    public boolean isOnline = true;

    @Mapping(Relation.OneToOne)
    public Door door;

    @Override public String toString() {
        return "Bluetooth{"+
                "mac = " + mac +
                ", uuid = " + uuid +
                ", serviceUuid = " + serviceUuid +
                ", writeUuid = " + writeUuid +
                ", notifyUuid = " + notifyUuid +
                ", password = " + password +
                ", isFirst = " + isFirst +
                ", name = " + name +
                ", sceneName = " + sceneName +
                ", isOnline = " + isOnline
                + "} ";
    }

    public String toJson() {
        String json = JSON.toJSONString(this);
        Log.d("Bluetooth Model", "Bluetooth Json:\n"+json);
        return json;
    }

}
