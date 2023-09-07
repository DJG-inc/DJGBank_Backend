package com.djg_bank.djg_bank.Mapper;

import com.djg_bank.djg_bank.DTOs.CreditCardDTO;
import com.djg_bank.djg_bank.Models.CreditCardModel;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CreditCardMapper {

    CreditCardDTO toCreditCardDTO(CreditCardModel creditCardModel);

    List<CreditCardDTO> toCreditCardDTOs(List<CreditCardModel> creditCardModels);

    @InheritInverseConfiguration
    CreditCardModel toCreditCardModel(CreditCardDTO creditCardDTO);
}
