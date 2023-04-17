package com.webktx.repository;

import com.webktx.entity.Post;
import com.webktx.entity.Relative;

public interface IRelativeRepository {
	Integer add (Relative relative);
	Integer edit (Relative relative);
	Integer deleteById (Integer relativeId);
}
