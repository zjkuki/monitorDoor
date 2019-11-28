package com.janady.database.model;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.funsdkdemo.MyApplication;
import com.lib.funsdk.support.models.FunDevType;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;

@Table("remote")
public class WifiRemoter {// 指定一对多关系
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id") // 指定列名
    private int id;

    public static final String COL_NAME = "name";
    public static final String COL_SN = "devName";
    public static final String COL_MAC = "mac";

    @Column(COL_NAME)
    @Unique
    public String name;

    @Column(COL_SN)
    @Unique
    public String devName;

    @Column(COL_MAC) // 指定列名
    @Unique
    public String mac;
    public int devTypeId=FunDevType.EE_DEV_OW_REMOTER.getDevIndex();
    public String loginPsw;
    public String hostUrl;
    public String hostPort;
    public String hostUsername;
    public String hostPassword;
    public String clientid;
    public String devIpAddr;
    public String devPort;
    public String publictopic;
    public String subscribetopic;
    public String sceneName;
    public String devType = "One_Way_Smart_Lock";

    public int defaultDoorId = 0;

    @Mapping(Relation.OneToOne)
    public Camera camera;

    @Mapping(Relation.OneToMany)
    public ArrayList<WifiRemoteLocker> Lockers;

    @Mapping(Relation.OneToMany)
    public ArrayList<Door> doorList;

    @Override public String toString() {
        return "Classes{"
                + super.toString() +
                " Camera= " + camera +
                "} ";
    }

    public String toJson() {
        String json = JSON.toJSONString(this);
        Log.d("WifiRemoter Model", "WifiRemoter Json:\n"+json);
        return json;
    }
}
