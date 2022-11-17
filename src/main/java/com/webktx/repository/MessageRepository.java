package com.webktx.repository;

public interface MessageRepository {
	String getMessage(Integer id);
	String getMessageByItemCode(String itemCode);
}

