package com.djg_bank.djg_bank.Mapper;
import com.djg_bank.djg_bank.DTOs.TransactionsDTO;
import com.djg_bank.djg_bank.Models.TransactionsModel;
import org.mapstruct.*;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionsMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "amount", target = "amount"),
            @Mapping(target = "date_of_transaction", source = "date_of_transaction"),
            @Mapping(target = "description", source = "description")
    })
    TransactionsDTO toTransactionsDTO(TransactionsModel transactionsModel);

    List<TransactionsDTO> toTransactionsDTOs(List<TransactionsModel> transactionsModels);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "user", ignore = true)
    })
    TransactionsModel toTransactionsModel(TransactionsDTO transactionsDTO);

}
