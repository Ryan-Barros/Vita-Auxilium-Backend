package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.UserRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.UserUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.UserResponseDTO;
import com.vitaauxilium.vitaauxilium.models.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponseDTO(User user);

    User toEntity(UserRequestDTO dto);

    List<UserResponseDTO> toResponseDTOList(List<User> users);

    @Mapping(target = "authorities", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(User source, @MappingTarget User target);

    User toEntityFromUpdateDTO(UserUpdateDTO dto);
}
