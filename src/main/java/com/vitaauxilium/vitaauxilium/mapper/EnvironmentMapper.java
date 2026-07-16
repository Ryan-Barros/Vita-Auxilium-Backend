package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.EnvironmentResponseDTO;
import com.vitaauxilium.vitaauxilium.models.Environment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnvironmentMapper {
    Environment toEntity(EnvironmentRequestDTO dto);

    EnvironmentResponseDTO toResponseDTO(Environment environment);

    List<EnvironmentResponseDTO> toResponseDTOList(List<Environment> environments);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEnvironmentFromEntity(Environment source, @MappingTarget Environment target);

    Environment toEntityFromUpdateDTO(EnvironmentUpdateDTO dto);
}
