package com.epam.gymcrm.domain.mapper;

import com.epam.gymcrm.db.entity.UserEntity;
import com.epam.gymcrm.domain.model.User;

public class UserDomainMapper {

    public static UserEntity toUserEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setActive(user.getActive());
        return entity;
    }

    public static User toUser(UserEntity userEntity) {
        User user = new User();
        user.setId(userEntity.getId());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setUsername(userEntity.getUsername());
        user.setPassword(userEntity.getPassword());
        user.setActive(userEntity.getActive());
        return user;
    }
}
