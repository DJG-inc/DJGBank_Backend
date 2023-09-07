package com.djg_bank.djg_bank.Mapper;

import com.djg_bank.djg_bank.DTOs.LoanDTO;
import com.djg_bank.djg_bank.Models.LoanModel;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    LoanDTO toLoanDTO(LoanModel loanModel);

    List<LoanDTO> toLoanDTOs(List<LoanModel> loanModels);

    @InheritInverseConfiguration
    LoanModel toLoanModel(LoanDTO loanDTO);

}
