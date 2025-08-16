package com.psms.pawn_shop_management_system.model;

import com.psms.pawn_shop_management_system.common.entity.MasterData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Profile extends MasterData {

    private String name;
    private String nrc;
    private String phone;
    private String dob;
    private String gender;

    @Column(name = "profile_pic")
    private String profilePic;

    @OneToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "user_id", nullable = false , unique = true)
    private User user;

    public Profile(){}
}
