package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.EnvironmentResponseDTO;
import com.vitaauxilium.vitaauxilium.exception.AccessDeniedException;
import com.vitaauxilium.vitaauxilium.mapper.EnvironmentMapper;
import com.vitaauxilium.vitaauxilium.models.Environment;
import com.vitaauxilium.vitaauxilium.models.EnvironmentMember;
import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class EnvironmentMemberFacadeService {
    private final EnvironmentService environmentService;
    private final EnvironmentMapper environmentMapper;

    @Transactional(readOnly = true)
    public List<EnvironmentResponseDTO> findAllByUser(User user) {
        return environmentMapper.toResponseDTOList(environmentService.findAllByMember(user.getId()));
    }

    @Transactional(readOnly = true)
    public EnvironmentResponseDTO findByIdForUser(User user, UUID id) {
        return environmentMapper.toResponseDTO(environmentService.findByIdForMember(id, user.getId()));
    }

    public EnvironmentResponseDTO createWithRelation(User owner, EnvironmentRequestDTO dto) {
        if (owner.getProfile() != Profile.FAMILY) {
            throw new AccessDeniedException("Precisa ser um familiar para criar um ambiente!");
        }
        Environment env = environmentMapper.toEntity(dto);

        EnvironmentMember member = new EnvironmentMember();
        member.setUser(owner);

        env.addMember(member);

        Environment savedEnv = environmentService.create(env);

        return environmentMapper.toResponseDTO(savedEnv);
    }

    public EnvironmentResponseDTO updateWithRelation(User owner, EnvironmentUpdateDTO dto, UUID envId) {
        if (owner.getProfile() != Profile.FAMILY) {
            throw new AccessDeniedException("Precisa ser um familiar para editar um ambiente!");
        }
        Environment existingEnv = environmentService.findByIdForMember(envId, owner.getId());
        Environment changes = environmentMapper.toEntityFromUpdateDTO(dto);
        return environmentMapper.toResponseDTO(environmentService.update(existingEnv, changes));
    }
}
