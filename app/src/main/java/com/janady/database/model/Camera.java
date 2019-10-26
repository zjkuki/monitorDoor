package com.janady.database.model;

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

    @Column(COL_SN) // 指定列名
    @Unique
    public String sn;
    public int devId;
    public String mac;
    public String serialNo;
    public String devIp;
    public String loginName;
    public String loginPsw;
    public FunDevice camDevice;
    public boolean isFirst = true;
    public FunDevType type;
    public String sceneName;
    public boolean isOnline = true;
    @Unique
    public String name;
    @Mapping(Relation.ManyToMany)
    public ArrayList<Door> doorList;
}
