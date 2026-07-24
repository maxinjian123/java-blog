package com.example.springboot.service;

import com.example.springboot.dto.TagCreateDTO;
import com.example.springboot.dto.TagUpdateDTO;
import com.example.springboot.entity.Tags;
import com.example.springboot.vo.ArticleVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

public interface TagService extends IService<Tags> {

    List<Tags> listTags();

    Tags createTag(TagCreateDTO createDTO);

    Tags updateTag(String id, TagUpdateDTO updateDTO);

    boolean deleteTag(String id);

    List<Tags> searchTags(String keyword);

}