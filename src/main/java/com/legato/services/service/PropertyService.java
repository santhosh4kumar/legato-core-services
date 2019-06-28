/**
 * 
 */
package com.legato.services.service;

import java.util.List;

import com.legato.services.model.Property;
import com.legato.services.view.request.PropertyRequestView;
import com.legato.services.view.response.PropertyResponseView;

/**
 * @author AF83580
 *
 */
public interface PropertyService {

	List<PropertyResponseView> findAll();

	Property findById(long id);

	Property save(PropertyRequestView request);
}