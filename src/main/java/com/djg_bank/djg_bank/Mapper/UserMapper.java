package com.djg_bank.djg_bank.Mapper;

import com.djg_bank.djg_bank.DTOs.UserDTO;
import com.djg_bank.djg_bank.Models.UserModel;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO(UserModel userModel);

    List<UserDTO> toUserDTOs(List<UserModel> userModels);

    @InheritInverseConfiguration
    UserModel toUserModel(UserDTO userDTO);
}