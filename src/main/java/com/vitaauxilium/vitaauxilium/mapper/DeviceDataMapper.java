package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.DeviceDataRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.DeviceDataResponseDTO;
import com.vitaauxilium.vitaauxilium.models.DeviceData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DeviceDataMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "device", ignore = true)
    DeviceData toEntity(DeviceDataRequestDTO dto);

    @Mapping(target = "deviceId", source = "device.id")
    DeviceDataResponseDTO toResponseDTO(DeviceData entity);

    List<DeviceDataResponseDTO> toResponseDTOList(List<DeviceData> entities);
}
