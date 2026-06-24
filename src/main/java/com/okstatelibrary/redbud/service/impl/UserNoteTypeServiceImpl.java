package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.UserNoteType;
import com.okstatelibrary.redbud.repository.UserNoteTypeDao;
import com.okstatelibrary.redbud.service.UserNoteTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserNoteTypeServiceImpl implements UserNoteTypeService {

	// private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserNoteTypeDao userNoteType;

	@Override
	public List<UserNoteType> getUserNoteTypeList() {
		return userNoteType.findAll();
	}

}