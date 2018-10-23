package com.jizhangyl.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.Interests;

/**
 * 
 * @author 陈栋
 * @date 2018年9月17日  
 * @description
 */
public interface InterestsRepository extends JpaRepository<Interests, Integer>{
	
	
	Interests findByUserGrade(Integer userGrade);
	
	
	

}
