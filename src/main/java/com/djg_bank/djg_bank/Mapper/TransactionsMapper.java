package com.djg_bank.djg_bank.Mapper;

import com.djg_bank.djg_bank.DTOs.TransactionsDTO;
import com.djg_bank.djg_bank.Models.TransactionsModel;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionsMapper {

    TransactionsDTO toTransactionsDTO(TransactionsModel transactionsModel);

    List<TransactionsDTO> toTransactionsDTOs(List<TransactionsModel> transactionsModels);

    @InheritInverseConfiguration
    TransactionsModel toTransactionsModel(TransactionsDTO transactionsDTO);

}