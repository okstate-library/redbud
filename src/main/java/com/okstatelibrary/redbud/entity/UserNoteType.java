package com.okstatelibrary.redbud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_note_type")
public class UserNoteType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;
    private String userNoteTypeId;
    private String name;


    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id=id;
    }

    public String getUserNoteTypeId(){
        return userNoteTypeId;
    }

    public void setUserNoteTypeId(String userNoteTypeId){
        this.userNoteTypeId=userNoteTypeId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }
}
