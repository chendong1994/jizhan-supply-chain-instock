package com.jizhangyl.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jizhangyl.application.dataobject.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Integer>{
	
	
	Wallet findByOpenId(String openId);
	
	
	List<Wallet> findByStatus(Integer status);
	
	

}
