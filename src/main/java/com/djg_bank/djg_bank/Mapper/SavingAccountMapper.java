package com.djg_bank.djg_bank.Mapper;

import com.djg_bank.djg_bank.DTOs.SavingsAccountDTO;
import com.djg_bank.djg_bank.Models.SavingsAccountModel;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SavingAccountMapper {

    SavingsAccountDTO toSavingsAccountDTO(SavingsAccountModel savingsAccountModel);

    List<SavingsAccountDTO> toSavingsAccountDTOs(List<SavingsAccountModel> savingsAccountModels);

    @InheritInverseConfiguration
    SavingsAccountModel toSavingsAccountModel(SavingsAccountDTO savingsAccountDTO);

}
