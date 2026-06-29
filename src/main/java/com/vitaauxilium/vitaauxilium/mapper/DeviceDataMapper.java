package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.DeviceDataRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.DeviceDataResponseDTO;
import com.vitaauxilium.vitaauxilium.models.DeviceData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceDataMapper {
   DeviceData toEntity(DeviceDataRequestDTO dto);

   DeviceDataResponseDTO toResponseDTO(DeviceData entity);
}
