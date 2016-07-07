package com.ym.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.ym.common.dao.BaseMapper;
import com.ym.common.service.BaseService;

public class BaseServiceImpl<T> implements BaseService<T> {

	@Autowired
	private BaseMapper<T> mapper;
	
	@Override
	public int deleteByPrimaryKey(Long id) {
		return mapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(T record) {
		return mapper.insert(record);
	}

	@Override
	public int insertSelective(T record) {
		return mapper.insertSelective(record);
	}

	@Override
	public T selectByPrimaryKey(Long id) {
		return mapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(T record) {
		return mapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(T record) {
		return mapper.updateByPrimaryKey(record);
	}
}
