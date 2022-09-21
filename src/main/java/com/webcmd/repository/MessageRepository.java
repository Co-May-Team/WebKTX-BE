package com.webcmd.repository;

public interface MessageRepository {
	String getMessage(Integer id);
	String getMessageByItemCode(String itemCode);
}

