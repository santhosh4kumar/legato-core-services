package com.legato.services.enums;

public enum UserStatus {
	REQUESTED(0, "Requested"),
    REJECTED(1, "Rejected"),
    APPROVED(2, "Activated");
	
	private int id;
	private String name;
	private UserStatus(int id, String name){
		this.id = id;
		this.name = name;
	}
	public static UserStatus getById(int id){
		return UserStatus.values()[id];
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
}