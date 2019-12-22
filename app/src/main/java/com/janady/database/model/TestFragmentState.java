package com.janady.database.model;

import com.inuker.bluetooth.library.search.SearchResult;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;
import java.util.List;

@Table("TestFragmentState")
public class TestFragmentState {

    public static final String COL_USERNAME = "username";

    @PrimaryKey(AssignType.BY_MYSELF)
    @Column("username") // 指定列名
    private String username;

    @Mapping(Relation.OneToMany)
    public List<Camera> cameras;

    @Mapping(Relation.OneToMany)
    public List<Bluetooth> bluetooths;

    @Mapping(Relation.OneToMany)
    public List<SearchResult> searchResults;

    @Mapping(Relation.OneToMany)
    public List<WifiRemoter> wifiRemoters;

}
