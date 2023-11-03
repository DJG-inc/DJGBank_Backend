package com.djg_bank.djg_bank.Repositories;

import com.djg_bank.djg_bank.Models.IpAdressModel;
import com.djg_bank.djg_bank.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IipAdressRepository extends JpaRepository<IpAdressModel, Long> {
    IpAdressModel findById(long id);
    IpAdressModel findByIp(String ip);
    IpAdressModel findByUser(UserModel user);

}
