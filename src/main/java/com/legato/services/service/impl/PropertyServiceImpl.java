/**
 * 
 */
package com.legato.services.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.legato.services.constants.MessageConstants;
import com.legato.services.exception.DuplicateFieldException;
import com.legato.services.model.Property;
import com.legato.services.repository.PropertyRepository;
import com.legato.services.service.PropertyService;
import com.legato.services.view.request.PropertyRequestView;
import com.legato.services.view.response.PropertyResponseView;

/**
 * @author AF83580
 *
 */

@Service
public class PropertyServiceImpl implements PropertyService {
	@Autowired
	private PropertyRepository propertyRepository;
	
	@Override
	public List<PropertyResponseView> findAll() {
		List<PropertyResponseView> responses = new ArrayList<>();
		propertyRepository.findAll().forEach(property -> {
			PropertyResponseView view = getResponse(property);
			responses.add(view);
		});
		return responses;
	}
	
	@Override
	@Transactional
	public Property findById(long id){
		return propertyRepository.findById(id).orElse(null);
	}
	
	@Override
	@Transactional
	public Property save(PropertyRequestView request) {
		if (propertyRepository.existsByPropertyName(request.getPropertyName())) {
			throw new DuplicateFieldException(MessageConstants.DUPLICATE_PROPERTY_NAME_MSG);
		}
		Property property = new Property();
		property.setPropertyName(request.getPropertyName());
		property.setPropertyValue(request.getPropertyValue());
		return propertyRepository.save(property);
	}
	
	private PropertyResponseView getResponse(Property property) {
		return new PropertyResponseView(property.getId(), 
				property.getPropertyName(), 
				property.getPropertyValue());
	}
}