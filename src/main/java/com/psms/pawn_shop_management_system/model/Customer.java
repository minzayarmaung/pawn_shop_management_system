package com.psms.pawn_shop_management_system.model;

import com.psms.pawn_shop_management_system.common.entity.MasterData;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Entity
@Getter
@Setter
public class Customer extends MasterData {

    @Column(nullable = false)
    private String name;

    private String phoneNumber;
    private String nationalId;
    private String address;

}
