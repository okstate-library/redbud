package com.okstatelibrary.redbud.service.impl;

import com.okstatelibrary.redbud.entity.ServicePoint;
import com.okstatelibrary.redbud.repository.ServicePointDao;
import com.okstatelibrary.redbud.service.ServicePointService;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServicePointServiceImpl implements ServicePointService {

	// private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private ServicePointDao ServicePointDao;

	@Override
	public ServicePoint saveServicePoint(ServicePoint servicePoint) {
		return ServicePointDao.save(servicePoint);
	}

	@Override
	public List<ServicePoint> getServicePointList() {
		return ServicePointDao.findAll();
	}

}