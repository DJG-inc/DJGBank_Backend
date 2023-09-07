package com.djg_bank.djg_bank.Mapper;

import com.djg_bank.djg_bank.DTOs.DebitCardsDTO;
import com.djg_bank.djg_bank.Models.DebitCardsModel;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DebitCardsMapper {

    DebitCardsDTO toDebitCardsDTO(DebitCardsModel debitCardsModel);

    List<DebitCardsDTO> toDebitCardsDTOs(List<DebitCardsModel> debitCardsModels);

    @InheritInverseConfiguration
    DebitCardsModel toDebitCardsModel(DebitCardsDTO debitCardsDTO);
}
