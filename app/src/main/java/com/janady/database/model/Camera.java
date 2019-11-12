package com.janady.database.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
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
public class Camera {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id") // 指定列名
    private int id;

    public static final String COL_SN = "sn";
    public static final String COL_DEVID = "devId";
    public static final String COL_MAC = "mac";

    @Column(COL_SN) // 指定列名
    @Unique
    public String sn;
    @Column(COL_DEVID) // 指定列名
    @Unique
    public int devId;
    @Column(COL_MAC) // 指定列名
    @Unique
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


    public String toJson() {
        String json = JSON.toJSONString(this);
        Log.d("Camera Model", "Camera Json:\n"+json);
        return json;
    }
}
