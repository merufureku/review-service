package com.merufureku.aromatica.review_service.dao.repository;

import com.merufureku.aromatica.review_service.dao.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {}
