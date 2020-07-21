package com.btireland.talos.mygroup.myproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * Able to take a string representing query parameters.
 * Parses and transforms this string into a {@link org.springframework.data.jpa.domain.Specification} object.
 * This specification can then be processed as usual by spring data.
 * @param <T>
 */
@NoRepositoryBean
public interface JpaRsqlRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    List<T> findAllByRsqlQuery(String rsqlQuery);

    Page<T> findAllByRsqlQuery(String rsqlQuery, Pageable pageable);

    List<T> findAllByRsqlQuery(String rsqlQuery, Sort sort);
}
