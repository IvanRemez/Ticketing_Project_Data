package com.cydeo.mapper;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import org.modelmapper.ModelMapper;

public class BaseMapper<R, T> {

    private final ModelMapper modelMapper;

    public BaseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

//    public R convertToEntity(T dto) {
//        return modelMapper.map(dto, R.class);
//    }
//
//    public R convertToDto(T entity) {
//        return modelMapper.map(entity, R.class);
//    }
}
