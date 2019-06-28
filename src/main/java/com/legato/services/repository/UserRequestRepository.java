/**
 * 
 */
package com.legato.services.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.legato.services.model.UserRequest;

/**
 * @author AF83580
 *
 */
@Repository
public interface UserRequestRepository extends JpaRepository<UserRequest, Long>{
	
}