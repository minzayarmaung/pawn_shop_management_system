package com.psms.pawn_shop_management_system.features.pawnItem.repository;

import com.psms.pawn_shop_management_system.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByNationalId(String nationalId);

    Optional<Customer> findByPhoneNumber(String phoneNumber);


}
