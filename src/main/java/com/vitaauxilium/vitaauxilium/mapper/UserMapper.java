package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.UserRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.UserUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.UserResponseDTO;
import com.vitaauxilium.vitaauxilium.models.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {
    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "picture", ignore = true)
    @Mapping(target = "cpf", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "activeAmbient", ignore = true)
    @Mapping(target = "environments", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(UserRequestDTO dto);

    List<UserResponseDTO> toResponseDTOList(List<User> users);

    @Mapping(target = "authorities", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(User source, @MappingTarget User target);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "picture", ignore = true)
    @Mapping(target = "cpf", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "activeAmbient", ignore = true)
    @Mapping(target = "environments", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "profile", ignore = true)
    User toEntityFromUpdateDTO(UserUpdateDTO dto);
}
