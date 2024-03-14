package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.Library;
import com.okstatelibrary.redbud.repository.LibraryDao;
import com.okstatelibrary.redbud.service.LibraryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LibraryServiceImpl implements LibraryService {

	// private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private LibraryDao libraryDao;

	@Override
	public Library saveLibrary(Library library) {
		return libraryDao.save(library);
	}

	@Cacheable(cacheNames = "getLibraryList")
	@Override
	public List<Library> getLibraryList() {
		return libraryDao.findAll();
	}

	@Override
	public List<Library> getLibraryListByCampusId(String campusId) {

		return getLibraryList().stream().filter(u -> u.getCampus_id().equals(campusId)).collect(Collectors.toList());

	}

}