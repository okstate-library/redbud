package com.okstatelibrary.redbud.folio.entity;

public class Usergroup {

	public String group;

	public String desc;

	public String id;

	@Override
	public String toString() {
		return "id " + this.id + "  - " + this.group + "  -  " + this.desc;
	}

}
