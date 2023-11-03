package com.djg_bank.djg_bank.Mapper;

import com.djg_bank.djg_bank.DTOs.IpAdressDTO;
import com.djg_bank.djg_bank.Models.IpAdressModel;
import org.mapstruct.Mapper;
import org.mapstruct.InheritInverseConfiguration;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IpAdressMapper {

    IpAdressDTO toIpAdressDTO(IpAdressModel ipAdressModel);

    List<IpAdressDTO> toIpAdressDTOs(List<IpAdressModel> ipAdressModels);

    @InheritInverseConfiguration
    IpAdressModel toIpAdressModel(IpAdressDTO ipAdressDTO);

}
