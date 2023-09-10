package com.djg_bank.djg_bank.Repositories;

import com.djg_bank.djg_bank.Models.UserModel;
import org.mapstruct.control.MappingControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<UserModel, Long> {
    UserModel findByEmail(String email);
    @Query("SELECT u FROM UserModel u WHERE u.user_id = ?1")
    UserModel findByUser_id(String user_id);
}