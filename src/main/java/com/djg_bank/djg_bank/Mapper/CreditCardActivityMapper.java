package com.djg_bank.djg_bank.Mapper;

import com.djg_bank.djg_bank.DTOs.CreditCardActivityDTO;
import com.djg_bank.djg_bank.Models.CreditCardActivityModel;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CreditCardActivityMapper {

    CreditCardActivityDTO toCreditCardActivityDTO(CreditCardActivityModel creditCardActivityModel);
    List<CreditCardActivityDTO> toCreditCardActivityDTOs(List<CreditCardActivityModel> creditCardActivityModels);
    @InheritInverseConfiguration
    CreditCardActivityModel toCreditCardActivityModel(CreditCardActivityDTO creditCardActivityDTO);
}
