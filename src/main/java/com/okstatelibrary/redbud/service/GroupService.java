package com.okstatelibrary.redbud.service;

import java.util.List;

import com.okstatelibrary.redbud.entity.PatronGroup;

public interface GroupService {

	PatronGroup saveGroup(PatronGroup group);

	List<PatronGroup> getGroupList();

	List<PatronGroup> getGroupListByInstituteCode(String instituteCode);

}