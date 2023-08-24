package com.djg_bank.djg_bank.Mapper;

import com.djg_bank.djg_bank.DTOs.UserDTO;
import com.djg_bank.djg_bank.Models.UserModel;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(source = "user_id", target = "user_id"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "first_name", target = "first_name"),
            @Mapping(source = "last_name", target = "last_name"),
            @Mapping(source = "date_of_birth", target = "date_of_birth"),
            @Mapping(source = "address", target = "address"),
            @Mapping(source = "phone_number", target = "phone_number")
    })
    UserDTO toUserDTO(UserModel userModel);

    List<UserDTO> toUserDTOs(List<UserModel> userModels);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "loans", ignore = true),
            @Mapping(target = "credit_cards", ignore = true),
            @Mapping(target = "savings_account", ignore = true)
    })
    UserModel toUSerModel(UserDTO userDTO);

}
