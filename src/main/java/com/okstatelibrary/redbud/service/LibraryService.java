package com.okstatelibrary.redbud.service;

import java.util.List;

import com.okstatelibrary.redbud.entity.Library;

public interface LibraryService {

	Library saveLibrary(Library library);

	List<Library> getLibraryList();

	List<Library> getLibraryListByCampusId(String campusId);

}