package com.okstatelibrary.redbud.service;

import java.util.List;

import com.okstatelibrary.redbud.entity.ServicePoint;

public interface ServicePointService {

	ServicePoint saveServicePoint(ServicePoint servicePoint);

	List<ServicePoint> getServicePointList();

}