package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.EnvironmentResponseDTO;
import com.vitaauxilium.vitaauxilium.exception.AccessDeniedException;
import com.vitaauxilium.vitaauxilium.exception.DuplicateResourceException;
import com.vitaauxilium.vitaauxilium.mapper.EnvironmentMapper;
import com.vitaauxilium.vitaauxilium.models.Environment;
import com.vitaauxilium.vitaauxilium.models.EnvironmentMember;
import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import com.vitaauxilium.vitaauxilium.dto.response.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<EnvironmentResponseDTO> findAllByUser(User user) {
        return environmentMapper.toResponseDTOList(environmentService.findAllByMember(user.getId()));
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<EnvironmentResponseDTO> findAllByUser(User user, Pageable pageable) {
        Page<Environment> page = environmentService.findAllByMember(user.getId(), pageable);
        return PageResponseDTO.from(page, environmentMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public EnvironmentResponseDTO findByIdForUser(User user, UUID id) {
        return environmentMapper.toResponseDTO(environmentService.findByIdForMember(id, user.getId()));
    }

    @Transactional
    public void setActiveEnvironment(User user, UUID envId) {
        environmentService.findByIdForMember(envId, user.getId());
        user.setActiveAmbient(envId);
        if (userRepository != null) {
            userRepository.save(user);
        }
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
        if (owner.getActiveAmbient() == null && savedEnv != null && savedEnv.getId() != null && userRepository != null) {
            owner.setActiveAmbient(savedEnv.getId());
            userRepository.save(owner);
        }

        return environmentMapper.toResponseDTO(savedEnv);
    }

    public EnvironmentResponseDTO updateWithRelation(User owner, EnvironmentUpdateDTO dto, UUID envId) {
        if (owner.getProfile() != Profile.FAMILY) {
            throw new AccessDeniedException("Precisa ser um familiar para editar um ambiente!");
        }
        Environment env = environmentService.findByIdForMember(envId, owner.getId());
        return environmentMapper.toResponseDTO(environmentService.update(env, dto));
    }

    public void deleteWithRelation(User owner, UUID envId) {
        if (owner.getProfile() != Profile.FAMILY) {
            throw new AccessDeniedException("Precisa ser um familiar para excluir um ambiente!");
        }
        Environment env = environmentService.findByIdForMember(envId, owner.getId());
        environmentService.deactivate(env);
        if (envId.equals(owner.getActiveAmbient()) && userRepository != null) {
            owner.setActiveAmbient(null);
            userRepository.save(owner);
        }
    }

    public EnvironmentResponseDTO joinByCode(User user, String rawCode) {
        Environment env = environmentService.findByActiveInviteCode(rawCode);
        boolean alreadyMember = env.getMembers().stream()
                .anyMatch(m -> m.getUser() != null && m.getUser().getId().equals(user.getId()));
        if (alreadyMember) {
            throw new DuplicateResourceException("Você já é membro deste ambiente!");
        }
        EnvironmentMember member = new EnvironmentMember();
        member.setUser(user);
        env.addMember(member);
        Environment saved = environmentService.save(env);
        if (user.getActiveAmbient() == null && userRepository != null) {
            user.setActiveAmbient(saved.getId());
            userRepository.save(user);
        }
        return environmentMapper.toResponseDTO(saved);
    }
}
