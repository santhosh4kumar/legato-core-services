/**
 * 
 */
package com.legato.services.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.legato.services.model.Property;

/**
 * @author AF83580
 *
 */

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>{
	boolean existsByPropertyName(String propertyName);
}