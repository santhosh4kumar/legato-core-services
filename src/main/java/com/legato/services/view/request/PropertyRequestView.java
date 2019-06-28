/**
 * 
 */
package com.legato.services.view.request;

/**
 * @author AF83580
 *
 */
public class PropertyRequestView {
	private Long id;
	private String propertyName;
	private String propertyValue;
	
	public PropertyRequestView() {
		// Auto-generated constructor stub
	}

	public PropertyRequestView(Long id, String propertyName, String propertyValue) {
		super();
		this.id = id;
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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