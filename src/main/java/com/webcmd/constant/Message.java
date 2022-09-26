package com.webcmd.constant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webcmd.repository.impl.MessageRepositoryImpl;

@Service
public class Message {
	@Autowired
	MessageRepositoryImpl messageRepositoryImpl;
	
	public String getMessage(Integer id) {
		return messageRepositoryImpl.getMessage(id);
	}
	public String getMessageByItemCode(String itemCode) {
		return messageRepositoryImpl.getMessageByItemCode(itemCode);
	}
	
	
}