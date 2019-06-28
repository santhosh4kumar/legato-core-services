/**
 * 
 */
package com.legato.services.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author AF83580
 *
 */

@Entity
@Table(name = "PROPERTY", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"PROPERTY_NAME"}, name = "UNIQUE_PROPERTY_NAME")}
)
public class Property extends BaseEntity {
	@Column(name = "PROPERTY_NAME")
	private String propertyName;
	@Column(name = "PROPERTY_VALUE")
	private String propertyValue;
	
	public Property() {
		// Auto-generated constructor stub
	}

	public Property(String propertyName, String propertyValue) {
		super();
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
}