package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.EnvironmentResponseDTO;
import com.vitaauxilium.vitaauxilium.models.Environment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = EnvironmentMemberMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EnvironmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "environmentCode", ignore = true)
    @Mapping(target = "expirationDate", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "members", ignore = true)
    Environment toEntity(EnvironmentRequestDTO dto);

    EnvironmentResponseDTO toResponseDTO(Environment environment);

    List<EnvironmentResponseDTO> toResponseDTOList(List<Environment> environments);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "environmentCode", ignore = true)
    void updateFromDTO(EnvironmentUpdateDTO dto, @MappingTarget Environment target);
}
