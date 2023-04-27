package com.cydeo.service.impl;

import com.cydeo.dto.RoleDTO;
import com.cydeo.entity.Role;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.mapper.RoleMapper;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final MapperUtil mapperUtil;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper, MapperUtil mapperUtil) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public List<RoleDTO> listAllRoles() {
// REVIEW:
    // Controller requests all RoleDTOs, so it can show in the UI
    // Need to access DB to get all Roles from the Table
    // go to Repo and find Service which gives me Roles from DB
    // inject this Service (RoleRepository)

        List<Role> roleList = roleRepository.findAll();

    // need to convert Role Entities to RoleDTOs
    // need to use ModelMapper methods
    // RoleMapper class created for these conversion methods
    // ^^ class injected here

        return roleList.stream()
//                .map(roleMapper::convertToDto).collect(Collectors.toList());
//                .map(role -> mapperUtil.convert(role, new RoleDTO())).collect(Collectors.toList());
                .map(role -> mapperUtil.convert(role, RoleDTO.class)).collect(Collectors.toList());
    }

    @Override
    public RoleDTO findById(Long id) {
        return roleMapper.convertToDto(roleRepository.findById(id).get());
    }
}
